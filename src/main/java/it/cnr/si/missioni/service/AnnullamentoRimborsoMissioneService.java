/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
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
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilterMapper;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
import java.util.List;


/**
 * Service class for managing users.
 */
@Service
public class AnnullamentoRimborsoMissioneService {

    private final Logger log = LoggerFactory.getLogger(AnnullamentoRimborsoMissioneService.class);
    //	@Autowired
//	private PrintAnnullamentoOrdineMissioneService printAnnullamentoMissioneService;
//
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
            List<AnnullamentoRimborsoMissione> listaNew = new ArrayList<AnnullamentoRimborsoMissione>();
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
        LocalDate data = LocalDate.now();
        int anno = data.getYear();

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
//			if (annullamento.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(annullamento.getIdFlusso())){
//				cmisRimborsoMissioneService.annullaFlusso(annullamento);
//			}
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

        if (filter != null) {

            // --- Filtri base su RimborsoMissione tramite mapper ---
            Specification<RimborsoMissione> rimSpec = RimborsoMissioneFilterMapper.mapBaseFilters(filter);

            // Join tra AnnullamentoRimborsoMissione e RimborsoMissione
            spec = spec.and((root, query, cb) -> {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<RimborsoMissione> rim = sub.from(RimborsoMissione.class);  // Root<RimborsoMissione> ✓
                sub.select(rim.get("id"))
                        .where(cb.and(
                                cb.equal(rim.get("id"), root.get("rimborsoMissione").get("id")),
                                rimSpec.toPredicate(rim, query, cb)  // ora funziona
                        ));
                return cb.exists(sub);
            });

            // --- Filtri specifici AnnullamentoRimborsoMissione ---
            spec = spec
                    .and(BaseSpecification.eq("statoInvioSigla", filter.getStatoInvioSigla()));

            // --- Logica utente / UO ---
            String currentUser = securityService.getCurrentUserLogin();

            if ("S".equals(Utility.nvl(filter.getDaCron(), "N"))) {
                // Nessun filtro utente aggiuntivo
            } else {
                if (!isForValidateFlows) {
                    if (!StringUtils.isEmpty(filter.getUser())) {
                        spec = spec.and(BaseSpecification.eq("uid", filter.getUser()));
                    } else if (StringUtils.isEmpty(filter.getUoRich())) {
                        spec = spec.and(BaseSpecification.eq("uid", currentUser));
                    }
                } else {
                    UsersSpecial userSpecial = accountService.getUoForUsersSpecial(currentUser);
                    Specification<AnnullamentoRimborsoMissione> uoSpec = Specification.where(null);

                    if (userSpecial != null && !"S".equals(userSpecial.getAll()) &&
                            userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {

                        for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
                            String uoSigla = uoService.getUoSigla(uoUser);
                            uoSpec = uoSpec.or((root, query, cb) -> cb.equal(root.join("rimborsoMissione").get("uoRich"), uoSigla));
                            uoSpec = uoSpec.or((root, query, cb) -> cb.equal(root.join("rimborsoMissione").get("uoSpesa"), uoSigla));
                        }
                        spec = spec.and(uoSpec);
                    } else {
                        spec = spec.and(BaseSpecification.eq("uid", currentUser));
                    }
                }
            }

            // --- Escludi missioni annullate, salvo override ---
            if (!"S".equals(Utility.nvl(filter.getIncludiMissioniAnnullate())) &&
                    !(filter.getDaId() != null && filter.getaId() != null && filter.getDaId().compareTo(filter.getaId()) == 0)) {
                spec = spec.and((root, query, cb) -> cb.notEqual(root.get("stato"), Costanti.STATO_ANNULLATO));
            }

            // --- Filtro REST + validateFlows ---
            if (Boolean.TRUE.equals(isServiceRest) && Boolean.TRUE.equals(isForValidateFlows)) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("stato"), Costanti.STATO_INSERITO));
            }
        }

        Sort sort = Sort.by(Sort.Order.asc("dataInserimento"))
                .and(Sort.by(Sort.Order.asc("anno")))
                .and(Sort.by(Sort.Order.asc("numero")));

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

    private void inizializzaCampiPerInserimento(
            AnnullamentoRimborsoMissione annullamento) throws AwesomeException {
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

    public List<CMISFileAttachment> getAttachments(Long idAnnullamentoRimborsoMissione)
            throws AwesomeException {
        if (idAnnullamentoRimborsoMissione != null) {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.findById(idAnnullamentoRimborsoMissione).orElse(null);
            if (annullamento != null) {
                RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById((Long) annullamento.getRimborsoMissione().getId()).orElse(null);
                if (rimborsoMissione != null) {
                    List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsAnnullamentoRimborsoMissione(rimborsoMissione, idAnnullamentoRimborsoMissione);
                    return lista;
                }
            }
        }
        return null;
    }

    public List<CMISFileAttachment> getAttachmentsFromRimborso(Long idRimborsoMissione)
            throws AwesomeException {
        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository.findById(idRimborsoMissione).orElse(null);
        if (rimborsoMissione != null) {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.getAnnullamentoRimborsoMissione(rimborsoMissione);
            return getAttachments(Long.valueOf(annullamento.getId().toString()));
        }
        return null;
    }

    public CMISFileAttachment uploadAllegato(Long idAnnullamentoRimborsoMissione,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws AwesomeException {
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

