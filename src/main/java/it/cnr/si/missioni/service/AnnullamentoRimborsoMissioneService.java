/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 */

package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoRimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.AnnullamentoRimborsoMissioneRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneRepository;
import it.cnr.si.missioni.repository.specification.BaseSpecification;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.CommonService;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnullamentoRimborsoMissioneService {

    private final Logger log = LoggerFactory.getLogger(AnnullamentoRimborsoMissioneService.class);

    @Autowired(required = false)
    CronService cronService;
    @Autowired
    CMISRimborsoMissioneService cmisRimborsoMissioneService;

    @Autowired
    private Environment env;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;
    @Autowired
    private AnnullamentoRimborsoMissioneRepository annullamentoRimborsoMissioneRepository;
    @Autowired
    private RimborsoMissioneRepository rimborsoMissioneRepository;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired
    private DatiSedeService datiSedeService;

    @Autowired(required = false)
    private MailService mailService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UoService uoService;

    @Value("${spring.mail.messages.invioAnnullamentoRimborsoMissione.oggetto}")
    private String subjectSendToAdministrative;

    @Value("${spring.mail.messages.annullamentoRimborsoMittente.oggetto}")
    private String subjectUndo;

    @Autowired
    private SecurityService securityService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public AnnullamentoRimborsoMissione getAnnullamentoRimborsoMissione(Long idAnnullamento) throws AwesomeException {
        RimborsoMissioneFilter filter = new RimborsoMissioneFilter();
        filter.setDaId(idAnnullamento);
        filter.setaId(idAnnullamento);
        AnnullamentoRimborsoMissione annullamento = null;
        List<AnnullamentoRimborsoMissione> listaAnnullamentiMissione = getAnnullamenti(filter, false, true);
        if (listaAnnullamentiMissione != null && !listaAnnullamentiMissione.isEmpty()) {
            annullamento = listaAnnullamentiMissione.get(0);
            RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById((Long) annullamento.getRimborsoMissione().getId()).orElse(null);
            if (rimborsoMissione != null) {
                Uo datiUo = uoService.recuperoUoSigla(annullamento.getRimborsoMissione().getUoSpesa());
                if (datiUo != null && Utility.nvl(datiUo.getOrdineDaValidare(), "N").equals("N")) {
                    annullamento.setIsUoDaValidare("N");
                } else {
                    annullamento.setIsUoDaValidare("S");
                }
            }
        }
        return annullamento;
    }

    public List<AnnullamentoRimborsoMissione> getAnnullamentiForValidateFlows(RimborsoMissioneFilter filter, Boolean isServiceRest) throws AwesomeException {
        filter.setStato(Costanti.STATO_INSERITO);
        List<AnnullamentoRimborsoMissione> lista = getAnnullamenti(filter, isServiceRest, true);
        if (lista != null) {
            List<AnnullamentoRimborsoMissione> listaNew = new ArrayList<>();
            for (AnnullamentoRimborsoMissione annullamento : lista) {
                if (annullamento.isMissioneInserita()) {
                    listaNew.add(annullamento);
                }
            }
            return listaNew;
        }
        return lista;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoRimborsoMissione updateAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, String basePath) throws AwesomeException {
        return updateAnnullamentoRimborsoMissione(annullamento, false, basePath);
    }

    private AnnullamentoRimborsoMissione updateAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, Boolean fromFlows, String basePath) throws AwesomeException {
        return updateAnnullamentoRimborsoMissione(annullamento, fromFlows, false, basePath);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoRimborsoMissione updateAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, Boolean fromFlows, Boolean confirm, String basePath) throws AwesomeException {

        AnnullamentoRimborsoMissione annullamentoDB = annullamentoRimborsoMissioneRepository.findById((Long) annullamento.getId()).orElse(null);
        boolean isRitornoMissioneMittente = false;

        if (annullamentoDB == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Annullamento Rimborso Missione da aggiornare inesistente.");
        }

        try {
            entityManager.lock(annullamentoDB, LockModeType.PESSIMISTIC_WRITE);
        } catch (OptimisticLockException e) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Annullamento rimborso in modifica. Ripetere l'operazione. ID: " + annullamentoDB.getId());
        }

        if (annullamentoDB.getRimborsoMissione() != null) {
            RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById((Long) annullamentoDB.getRimborsoMissione().getId()).orElse(null);
            if (rimborsoMissione != null) {
                annullamento.setRimborsoMissione(rimborsoMissione);
            }
        }

        if (confirm) {
            if (annullamentoDB.isMissioneConfermata()) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione già annullato.");
            }
            List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsAnnullamentoRimborsoMissione(annullamento.getRimborsoMissione(), Long.valueOf(annullamento.getId().toString()));
            if (lista == null || lista.isEmpty()) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario allegare almeno un documento prima di procedere all'annullamento del rimborso.");
            }
            annullamento.setStato(Costanti.STATO_CONFERMATO);
        } else {
            if (!accountService.isUserEnableToWorkUo(annullamentoDB.getRimborsoMissione().getUoSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato ad inserire gli annullamenti rimborso missione per la uo " + annullamentoDB.getRimborsoMissione().getUoSpesa() + ".");
            }
        }

        aggiornaDatiAnnullamentoRimborsoMissione(annullamento, confirm, annullamentoDB);
        annullamentoDB.setToBeUpdated();
        annullamentoDB = annullamentoRimborsoMissioneRepository.save(annullamentoDB);

        if (confirm) {
            RimborsoMissione rimborsoMissione = annullamento.getRimborsoMissione();
            rimborsoMissione.setStato(Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE);
            rimborsoMissione.setStatoFlusso(Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE);
            rimborsoMissione.setToBeUpdated();
            rimborsoMissione = rimborsoMissioneRepository.save(rimborsoMissione);

            if (rimborsoMissione.getPgMissioneSigla() != null) {
                JSONBody body = new JSONBody();

                String app = Costanti.APP_SIGLA;
                String url = Costanti.REST_COMUNICA_RIMBORSO_SIGLA + "/" + rimborsoMissione.getId();
                commonService.process(body, app, url, true, HttpMethod.DELETE);
            }

            sendMail(annullamentoDB);
        }

        log.debug("Updated Information for Annullamento Rimborso Missione: {}", annullamentoDB);

        return annullamentoDB;
    }

    private String getTextMail(AnnullamentoRimborsoMissione annullamento) {
        return "Il rimborso missione approvato " + annullamento.getRimborsoMissione().getAnno() + "-" + annullamento.getRimborsoMissione().getNumero() + " di " + getNominativo(annullamento.getRimborsoMissione().getUid()) + " per la missione a " + annullamento.getRimborsoMissione().getDestinazione() + " dal " + DateUtils.getDefaultDateAsString(annullamento.getRimborsoMissione().getDataInizioMissione()) + " al " + DateUtils.getDefaultDateAsString(annullamento.getRimborsoMissione().getDataFineMissione()) + " avente per oggetto " + annullamento.getRimborsoMissione().getOggetto() + " è stato annullato da " + getNominativo(securityService.getCurrentUserLogin());
    }

    private void sendMail(AnnullamentoRimborsoMissione annullamento) {
        DatiIstituto dati = datiIstitutoService.getDatiIstituto(annullamento.getRimborsoMissione().getUoSpesa(), annullamento.getRimborsoMissione().getAnno());
        String subjectMail = subjectUndo + " " + getNominativo(annullamento.getUid());
        String testoMail = getTextMail(annullamento);

        Account account = accountService.loadAccountFromUsername(annullamento.getRimborsoMissione().getUid());

        List<String> listaMail = new ArrayList<>();
        listaMail.add(account.getEmail_comunicazioni());
        if (dati != null && dati.getMailNotificheRimborso() != null && !dati.getMailNotificheRimborso().equals("N")) {
            listaMail.add(dati.getMailNotificheRimborso());
        } else {
            List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(annullamento.getRimborsoMissione().getUoSpesa());
            listaMail.addAll(mailService.preparaListaMail(lista));
        }
        sendMail(listaMail, testoMail, subjectMail);
    }

    private void sendMail(List<String> lista, String testoMail, String oggetto) {
        if (lista != null && lista.size() > 0) {
            String[] elencoMail = mailService.preparaElencoMail(lista);
            if (elencoMail != null && elencoMail.length > 0) {
                mailService.sendEmail(oggetto, testoMail, false, true, elencoMail);
            }
        }
    }

    private void aggiornaDatiAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, Boolean confirm,
                                                          AnnullamentoRimborsoMissione annullamentoDB) {
        annullamentoDB.setStato(annullamento.getStato());
        annullamentoDB.setMotivoAnnullamento(annullamento.getMotivoAnnullamento());
    }

    private String getNominativo(String user) {
        Account utente = accountService.loadAccountFromUsername(user);
        return utente.getCognome() + " " + utente.getNome();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnnullamento(Long idAnnullamento) throws AwesomeException {
        AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.findById(idAnnullamento).orElse(null);
        if (annullamento != null) {
            controlloOperazioniCRUDDaGui(annullamento);
            annullamento.setStato(Costanti.STATO_ANNULLATO);
            annullamento.setToBeUpdated();
            annullamentoRimborsoMissioneRepository.save(annullamento);
        }
    }

    public void controlloOperazioniCRUDDaGui(AnnullamentoRimborsoMissione annullamento) {
        if (!annullamento.isMissioneInserita()) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile effettuare l'operazione su un Annullamento Rimborso issione che non si trova in uno stato " + Costanti.STATO.get(Costanti.STATO_INSERITO));
        }
    }

    @Transactional(readOnly = true)
    public AnnullamentoRimborsoMissione getAnnullamentoMissione(Long idMissione) throws AwesomeException {
        return getAnnullamentoRimborsoMissione(idMissione);
    }

    @Transactional(readOnly = true)
    public List<AnnullamentoRimborsoMissione> getAnnullamenti(RimborsoMissioneFilter filter, Boolean isServiceRest) throws AwesomeException {
        return getAnnullamenti(filter, isServiceRest, false);
    }

    @Transactional(readOnly = true)
    public List<AnnullamentoRimborsoMissione> getAnnullamenti(RimborsoMissioneFilter filter,
                                                              Boolean isServiceRest,
                                                              Boolean isForValidateFlows) throws AwesomeException {

        Specification<AnnullamentoRimborsoMissione> spec = Specification.where(null);
        Sort sort = Sort.by(Sort.Order.asc("dataInserimento"))
                .and(Sort.by(Sort.Order.asc("anno")))
                .and(Sort.by(Sort.Order.asc("numero")));

        if (filter != null) {
            // Filtri sull'annullamento
            spec = spec.and(BaseSpecification.eq("anno", filter.getAnno()));
            spec = spec.and(BaseSpecification.ge("id", filter.getDaId()));
            spec = spec.and(BaseSpecification.le("id", filter.getaId()));
            spec = spec.and(BaseSpecification.eq("stato", filter.getStato()));
            if (filter.getListaStatiMissione() != null && !filter.getListaStatiMissione().isEmpty()) {
                spec = spec.and(BaseSpecification.in("stato", filter.getListaStatiMissione()));
            }
            spec = spec.and(BaseSpecification.ge("numero", filter.getDaNumero()));
            if (filter.getDaData() != null) {
                LocalDate daData = DateUtils.parseLocalDate(filter.getDaData(), DateUtils.PATTERN_DATE);
                spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataInserimento"), daData));
            }
            if (filter.getaData() != null) {
                LocalDate aData = DateUtils.parseLocalDate(filter.getaData(), DateUtils.PATTERN_DATE);
                spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dataInserimento"), aData));
            }

            // Filtri sul rimborso collegato
            if (filter.getUoRich() != null) {
                if (accountService.isUserEnableToWorkUo(filter.getUoRich()) && !filter.isDaCron()) {
                    String uoRich = filter.getUoRich();
                    spec = spec.and((root, query, cb) -> cb.or(
                            cb.equal(root.get("rimborsoMissione").get("uoRich"), uoRich),
                            cb.equal(root.get("rimborsoMissione").get("uoSpesa"), uoRich)));
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente " + securityService.getCurrentUserLogin() + "  non è abilitato a vedere i dati della uo " + filter.getUoRich());
                }
            }
            if (filter.getAnnoOrdine() != null) {
                Integer annoOrdine = filter.getAnnoOrdine();
                spec = spec.and((root, query, cb) -> cb.equal(root.get("rimborsoMissione").get("anno"), annoOrdine));
            }
            if (filter.getDaNumeroOrdine() != null) {
                Long daNumeroOrdine = filter.getDaNumeroOrdine();
                spec = spec.and((root, query, cb) -> cb.ge(root.get("rimborsoMissione").get("numero"), daNumeroOrdine));
            }
            if (filter.getaNumeroOrdine() != null) {
                Long aNumeroOrdine = filter.getaNumeroOrdine();
                spec = spec.and((root, query, cb) -> cb.le(root.get("rimborsoMissione").get("numero"), aNumeroOrdine));
            }
            spec = spec.and(BaseSpecification.eq("statoInvioSigla", filter.getStatoInvioSigla()));
        }

        if (filter != null && filter.isDaCron()) {
            return annullamentoRimborsoMissioneRepository.findAll(spec, sort);
        }

        String currentUser = securityService.getCurrentUserLogin();
        if (!isForValidateFlows) {
            if (filter != null && !StringUtils.isEmpty(filter.getUser())) {
                spec = spec.and(BaseSpecification.eq("uid", filter.getUser()));
            } else if (filter == null || StringUtils.isEmpty(filter.getUoRich())) {
                spec = spec.and(BaseSpecification.eq("uid", currentUser));
            }
        } else {
            UsersSpecial userSpecial = accountService.getUoForUsersSpecial(currentUser);
            if (userSpecial != null) {
                if (!"S".equals(userSpecial.getAll())) {
                    if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
                        // uoRich sempre; uoSpesa solo se la uo e l'utente prevedono la validazione
                        List<String> uoRichiedenti = new ArrayList<>();
                        List<String> uoSpesa = new ArrayList<>();
                        for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
                            Uo uo = uoService.recuperoUo(uoUser.getCodice_uo());
                            if (uo != null) {
                                String uoFilter = uoService.getUoSigla(uoUser);
                                uoRichiedenti.add(uoFilter);
                                if (Utility.nvl(uo.getOrdineDaValidare(), "N").equals("S")
                                        && Utility.nvl(uoUser.getOrdine_da_validare(), "N").equals("S")) {
                                    uoSpesa.add(uoFilter);
                                }
                            }
                        }
                        if (!uoRichiedenti.isEmpty()) {
                            spec = spec.and((root, query, cb) -> {
                                Predicate perUoRich = root.get("rimborsoMissione").get("uoRich").in(uoRichiedenti);
                                return uoSpesa.isEmpty() ? perUoRich
                                        : cb.or(perUoRich, root.get("rimborsoMissione").get("uoSpesa").in(uoSpesa));
                            });
                        }
                    } else {
                        spec = spec.and(BaseSpecification.eq("uid", currentUser));
                    }
                }
            } else {
                spec = spec.and(BaseSpecification.eq("uid", currentUser));
            }
        }

        if (filter == null || (!Utility.nvl(filter.getIncludiMissioniAnnullate()).equals("S")
                && !(filter.getDaId() != null && filter.getaId() != null && filter.getDaId().compareTo(filter.getaId()) == 0))) {
            spec = spec.and((root, query, cb) -> cb.notEqual(root.get("stato"), Costanti.STATO_ANNULLATO));
        }

        if (Boolean.TRUE.equals(isServiceRest) && Boolean.TRUE.equals(isForValidateFlows)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("stato"), Costanti.STATO_INSERITO));
        }

        return annullamentoRimborsoMissioneRepository.findAll(spec, sort);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoRimborsoMissione createAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento) throws AwesomeException {
        controlloDatiObbligatoriDaGUI(annullamento);
        inizializzaCampiPerInserimento(annullamento);
        validaCRUD(annullamento);
        annullamento = annullamentoRimborsoMissioneRepository.save(annullamento);
        log.info("Creato Annullamento Rimborso Missione", annullamento.getId());
        return annullamento;
    }

    private void inizializzaCampiPerInserimento(AnnullamentoRimborsoMissione annullamento) throws AwesomeException {
        annullamento.setUidInsert(securityService.getCurrentUserLogin());
        annullamento.setUser(securityService.getCurrentUserLogin());
        Integer anno = recuperoAnno(annullamento);
        annullamento.setAnno(anno);
        annullamento.setNumero(datiIstitutoService.getNextPG(annullamento.getRimborsoMissione().getUoSpesa(), anno, Costanti.TIPO_ANNULLAMENTO_RIMBORSO_MISSIONE));

        annullamento.setStato(Costanti.STATO_INSERITO);
        if (annullamento.getRimborsoMissione() != null) {
            RimborsoMissione rimborsoMissione = rimborsoMissioneRepository
                    .findById((Long) annullamento.getRimborsoMissione().getId())
                    .orElse(null);
            if (rimborsoMissione != null) {
                AnnullamentoRimborsoMissione ann = annullamentoRimborsoMissioneRepository.getAnnullamentoRimborsoMissione(rimborsoMissione);
                if (ann != null) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Esiste già un annullamento per il rimborso missione " + annullamento.getRimborsoMissione().getAnno() + "-" + annullamento.getRimborsoMissione().getNumero());
                }
                annullamento.setRimborsoMissione(rimborsoMissione);
            } else {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Il rimborso missione con ID: " + annullamento.getRimborsoMissione().getId() + " non esiste");
            }
        }
        annullamento.setToBeCreated();
    }

    private Integer recuperoAnno(AnnullamentoRimborsoMissione annullamento) {
        if (annullamento.getDataInserimento() == null) {
            annullamento.setDataInserimento(LocalDate.now());
        }
        return annullamento.getDataInserimento().getYear();
    }

    private void controlloDatiObbligatoriDaGUI(AnnullamentoRimborsoMissione annullamento) {
        if (annullamento != null) {
            if (StringUtils.isEmpty(annullamento.getMotivoAnnullamento())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Motivo di Annullamento");
            }
            if (annullamento.isMissioneDipendente()) {
                if (StringUtils.isEmpty(annullamento.getComuneResidenzaRich())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Comune di Residenza del Richiedente");
                } else if (StringUtils.isEmpty(annullamento.getIndirizzoResidenzaRich())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Indirizzo di Residenza del Richiedente");
                }
            }
        }
    }

    private void validaCRUD(AnnullamentoRimborsoMissione annullamento) {
        if (annullamento != null) {
            controlloCampiObbligatori(annullamento);
        }
    }


    private void controlloCampiObbligatori(AnnullamentoRimborsoMissione annullamento) {
        if (!annullamento.isToBeCreated()) {
            controlloDatiObbligatoriDaGUI(annullamento);
        }
        if (StringUtils.isEmpty(annullamento.getAnno())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Anno");
        } else if (StringUtils.isEmpty(annullamento.getUid())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Utente");
        } else if (StringUtils.isEmpty(annullamento.getStato())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Stato");
        } else if (StringUtils.isEmpty(annullamento.getNumero())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Numero");
        }
    }

    public List<CMISFileAttachment> getAttachments(Long idAnnullamentoRimborsoMissione) throws AwesomeException {
        if (idAnnullamentoRimborsoMissione != null) {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.findById(idAnnullamentoRimborsoMissione).orElse(null);
            if (annullamento != null) {
                RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById((Long) annullamento.getRimborsoMissione().getId()).orElse(null);
                if (rimborsoMissione != null) {
                    return cmisRimborsoMissioneService.getAttachmentsAnnullamentoRimborsoMissione(rimborsoMissione, idAnnullamentoRimborsoMissione);
                }
            }
        }
        return null;
    }

    public List<CMISFileAttachment> getAttachmentsFromRimborso(Long idRimborsoMissione) throws AwesomeException {
        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById(idRimborsoMissione).orElse(null);
        if (rimborsoMissione != null) {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.getAnnullamentoRimborsoMissione(rimborsoMissione);
            return getAttachments(Long.valueOf(annullamento.getId().toString()));
        }
        return null;
    }

    public CMISFileAttachment uploadAllegato(Long idAnnullamentoRimborsoMissione, InputStream inputStream, String name, MimeTypes mimeTypes) throws AwesomeException {
        if (idAnnullamentoRimborsoMissione != null) {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.findById(idAnnullamentoRimborsoMissione).orElse(null);
            if (annullamento != null) {
                RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById((Long) annullamento.getRimborsoMissione().getId()).orElse(null);
                if (rimborsoMissione != null) {
                    return cmisRimborsoMissioneService.uploadAttachmentAnnullamentoRimborsoMissione(rimborsoMissione, idAnnullamentoRimborsoMissione,
                            inputStream, name, mimeTypes);
                }
            }
        }
        return null;
    }

    public void gestioneCancellazioneAllegati(String idNodo, Long idAnnullamentoRimborsoMissione) {
        if (idAnnullamentoRimborsoMissione != null) {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.findById(idAnnullamentoRimborsoMissione).orElse(null);
            if (annullamento != null) {
                if (annullamento.isMissioneConfermata()) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione già annullato.");
                }
                missioniCMISService.deleteNode(idNodo);
            }
        }
    }
}