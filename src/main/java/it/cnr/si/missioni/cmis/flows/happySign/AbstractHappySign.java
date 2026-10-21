package it.cnr.si.missioni.cmis.flows.happySign;

import feign.FeignException;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartSignatureFlowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.FlussiToHappySign;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.service.DatiIstitutoService;
import it.cnr.si.missioni.service.MissioniAceService;
import it.cnr.si.missioni.service.MissioniAceServiceIss;
import it.cnr.si.missioni.service.UoService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.proxy.json.object.*;
import it.cnr.si.missioni.util.proxy.json.service.*;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.service.AceService;
import it.iss.si.service.HappySignService;
import it.iss.si.service.UtilAce;
import it.iss.si.signature.flow.flow.model.SignatureDocument;
import it.iss.si.signature.flow.flow.model.SignatureRequest;
import it.iss.si.signature.flow.flow.service.SignatureService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static it.cnr.si.missioni.cmis.flows.happySign.UtilHappySign.formatCdsCode;
import static it.cnr.si.missioni.cmis.flows.happySign.UtilHappySign.formatUoCode;
import static it.cnr.si.missioni.util.Costanti.ACE_SIGLA_PRESIDENTE_DESC;

public abstract class AbstractHappySign implements FlussiToHappySign {

    private static final Log logger = LogFactory.getLog(AbstractHappySign.class);

    @Value("${codiciUo.drue:#{null}}")
    private String uoCodeDrue;
    @Value("${codiciUo.direzioneGenerale:#{null}}")
    private String uoCodeDirGenerale;
    @Value("${codiciUo.presidenza:#{null}}")
    private String uoCodePresidenza;
    @Value("${codiciUo.drag:#{null}}")
    private String uoCodeDrag;
    @Value("${keyUo.dirIfascia:#{null}}")
    private String keyUoDirIFascia;
    @Value("${keyUo.dirIIfascia:#{null}}")
    private String keyUoDirIIFascia;
    @Value("${keyUo.direzioneGenerale:#{null}}")
    private String keyUoDirGenerale;
    @Value("${keyUo.presidenza:#{null}}")
    private String keyUoPresidenza;
    @Value("${codiciCds.dirCentrale:#{null}}")
    private String codeCdsDirCentrale;
    @Value("${codiciCds.cns:#{null}}")
    private String codeCdsCns;
    @Value("${codiciCds.cnt:#{null}}")
    private String codeCdsCnt;
    @Value("${idUo.dirUffEcoGiur:#{null}}")
    private String dirUffEcoGiur;

    @Autowired
    protected HappySignService happySignService;
    @Autowired
    ProgettoService progettoService;
    @Autowired
    GaeService gaeService;
    @Autowired
    AceService aceService;
    @Autowired
    UnitaOrganizzativaService unitaOrganizzativaService;
    @Autowired
    MissioniCMISService missioniCMISService;
    @Autowired
    TerzoService terzoService;
    @Autowired
    UoService uoService;
    @Autowired
    DatiIstitutoService datiIstitutoService;
    @Autowired
    MissioniAceServiceIss missioniAceServiceIss;
    @Autowired
    AccountService accountService;
    @Autowired
    private MissioniAceService missioniAceService;

    @Autowired
    protected SignatureService signatureService;

    /**
     * Ottiene il progetto associato all'ordine missione
     */
    public Progetto getProgetto(OrdineMissione ordineMissione) {
        Integer anno = DateUtils.getCurrentYear();
        return progettoService.loadModulo(ordineMissione.getPgProgetto(), anno, ordineMissione.getUoSpesa());
    }

    /**
     * Verifica se il responsabile del progetto deve firmare
     */
    protected Boolean signRespProgetto(OrdineMissione ordineMissione) {
        if (ordineMissione == null || ordineMissione.getPgProgetto() == null || ordineMissione.getPgProgetto() <= 0) {
            return Boolean.FALSE;
        }

        Progetto progetto = getProgetto(ordineMissione);
        return progetto.getCd_responsabile_terzo() != null;
    }

    /**
     * Ottiene la GAE associata all'ordine missione
     */
    public Gae getGae(OrdineMissione ordineMissione) {
        return gaeService.loadGae(ordineMissione);
    }

    /**
     * Verifica se il responsabile della GAE deve firmare
     */
    protected Boolean signGae(OrdineMissione ordineMissione) {
        if (ordineMissione == null || StringUtils.isEmpty(ordineMissione.getGae())) {
            return Boolean.FALSE;
        }

        Gae gae = getGae(ordineMissione);
        return gae.getCd_responsabile_terzo() != null;
    }

    /**
     * Verifica se l'UO richiedente è uguale all'UO della GAE
     */
    protected Boolean signUoRichEqUoGae(OrdineMissione ordineMissione) {
        if (ordineMissione == null) {
            return Boolean.FALSE;
        }

        String uoGae = getUoGAE(ordineMissione);
        return ordineMissione.getUoRich().equalsIgnoreCase(uoGae);
    }

    /**
     * Verifica se il responsabile del dipartimento UO è uguale all'UO della GAE
     */
    protected Boolean signRespDipUoEqUoGae(OrdineMissione ordineMissione) {
        if (ordineMissione == null) {
            return Boolean.FALSE;
        }

        String uoGae = getUoGAE(ordineMissione);
        Account direttore = uoService.getDirettore(ordineMissione.getUoRich());
        return direttore.getCodice_uo().equalsIgnoreCase(uoGae);
    }

    /**
     * Verifica se è un incarico per il presidente
     */
    protected Boolean isIncarico_VociPresidente(OrdineMissione ordineMissione) {
        if (ordineMissione == null) {
            return Boolean.FALSE;
        }

        return Optional.ofNullable(ordineMissione.getPresidente())
                .filter(p -> p.equals("S"))
                .isPresent();
    }

    /**
     * Ottiene i dettagli dell'utente FEA tramite codice fiscale
     */
    public EmployeeDetails getUserFeaByCf(String codiceFiscale) {
        return aceService.getPersonaByCodiceFiscale(codiceFiscale);
    }

    /**
     * Ottiene il responsabile di una UO
     */
    public EmployeeDetails getResponsabile(String uo) {
        UnitaOrganizzativa unitaOrganizzativa = unitaOrganizzativaService.loadUo(uo, null, DateUtils.getCurrentYear());
        return aceService.findResponsabileBySigla(unitaOrganizzativa.getSigla_int_ente());
    }

    /**
     * Ottiene il documento come array di byte
     */
    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }

    /**
     * Imposta sul DTO il modulo da firmare e gli allegati non firmabili
     */
    protected void setDocumenti(StartSignatureFlowDto startInfo, StorageObject modulo, List<StorageObject> allegati) throws IOException {
        startInfo.setFilesToSign(List.of(getDocumentoFirmabile(modulo)));
        startInfo.setAttachments(getAllegati(allegati));
    }

    /**
     * Crea il documento da firmare a partire dal modulo
     */
    public SignatureDocument getDocumentoFirmabile(StorageObject modulo) throws IOException {
        String fileName = modulo.getPropertyValue(StoragePropertyNames.NAME.value());
        return new SignatureDocument(fileName, MimeTypes.PDF.mimetype(), new ByteArrayInputStream(getDocumento(modulo)));
    }

    /**
     * Crea gli allegati non firmabili a partire dagli storage object
     */
    public List<SignatureDocument> getAllegati(List<StorageObject> allegati) throws IOException {
        List<SignatureDocument> result = new ArrayList<>();
        if (allegati == null) {
            return result;
        }
        for (StorageObject so : allegati) {
            String fileName = so.getPropertyValue(StoragePropertyNames.NAME.value());
            result.add(new SignatureDocument(fileName, UtilHappySign.mimeTypeOf(fileName), new ByteArrayInputStream(getDocumento(so))));
        }
        return result;
    }

    /**
     * Ottiene il nome del file da un percorso
     */
    public String getNomeFile(String fileName) {
        return missioniCMISService.parseFilename(fileName);
    }

    /**
     * Ottiene il responsabile scientifico per un ordine missione
     */
    public EmployeeDetails getRespScientifico(OrdineMissione ordineMissione) throws FeignException {
        String cdResponsabileTerzo = getGae(ordineMissione).getCd_responsabile_terzo();
        TerzoInfo terzoInfo = terzoService.loadUserInfoByCdTerzo(cdResponsabileTerzo);
        return getUserFeaByCf(terzoInfo.getCodice_fiscale());
    }

    /**
     * Ottiene i dettagli del presidente
     */
    public EmployeeDetails getPresidente() {
        String uoFormatted = formatUoCode(uoCodePresidenza);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    /**
     * Ottiene i dettagli del direttore generale
     */
    public EmployeeDetails getDirGenerale() {
        String uoFormatted = formatUoCode(uoCodeDirGenerale);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    /**
     * Ottiene i dettagli del direttore DRUE
     */
    public EmployeeDetails getDirDRUE() {
        String uoFormatted = formatUoCode(uoCodeDrue);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    /**
     * Ottiene i dettagli del direttore DRAG
     */
    public EmployeeDetails getDirDRAG() {
        String uoFormatted = formatUoCode(uoCodeDrag);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    /**
     * Imposta il direttore dell'ufficio economico giuridico come firmatario
     */
    public void setDirUffEcoGiur(OrdineMissione ordineMissione, StartSignatureFlowDto startInfo) {
        String dirUffEcoEmail = getDirUffEcoEGiud();
        if (!ordineMissione.getUid().equalsIgnoreCase(dirUffEcoEmail)) {
            startInfo.addSigner(dirUffEcoEmail);
        }
    }

    /**
     * Invia la richiesta di firma tramite signature-flow
     */
    @Override
    public String send(StartSignatureFlowDto startInfo) {
        List<SignatureDocument> filesToSign = startInfo.getFilesToSign();
        String subject = filesToSign.isEmpty() ? null : filesToSign.get(0).fileName();
        SignatureRequest request = new SignatureRequest(
                filesToSign,
                subject,
                null,
                startInfo.getSigners(),
                startInfo.getApprovers(),
                startInfo.getAttachments(),
                null,
                List.of(),
                startInfo.getTemplateName());
        logger.info("Invio richiesta di firma: template=" + startInfo.getTemplateName() + ", firmatari=" + startInfo.getSigners().size());
        return signatureService.sendToSign(request);
    }

    /**
     * Verifica se la persona è il direttore generale
     */
    public Boolean isDirGenerale(OrdineMissione ordineMissione) {
        EmployeeDetails dirGenerale = getPersonaByUsmFromAce(ordineMissione.getUid());
        return hasQualifica(dirGenerale, keyUoDirGenerale, Costanti.SIGLA_ACE_DIR_GENERALE_VALUE);
    }

    /**
     * Verifica se la persona è il presidente
     */
    public Boolean isPresidente(OrdineMissione ordineMissione) {
        EmployeeDetails presidente = getPersonaByUsmFromAce(ordineMissione.getUid());
        return hasQualifica(presidente, keyUoPresidenza, ACE_SIGLA_PRESIDENTE_DESC);
    }

    /**
     * Verifica se la persona è un direttore di I fascia
     */
    public Boolean isDirIFascia(OrdineMissione ordineMissione) {
        EmployeeDetails dirIFascia = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails dirDip = getResponsabile(ordineMissione.getUoSpesa());

//        /* x fare i test con direttori dip e dirigenti I/II fascia gli passo la uo del richiedente
//        perchè ho le voci di bilancio solo su DRUE in collaudo*/
//        EmployeeDetails dirDip = getResponsabile(ordineMissione.getUoRich());

        return ordineMissione.getUid().equals(getEmailFromEmployee(dirDip)) &&
                hasQualifica(dirIFascia, keyUoDirIFascia, null);
    }

    /**
     * Verifica se la persona è un direttore di II fascia
     */
    public Boolean isDirIIFascia(OrdineMissione ordineMissione) {
        EmployeeDetails dir = getPersonaByUsmFromAce(ordineMissione.getUid());
        return hasQualifica(dir, keyUoDirIIFascia, null);
    }

    /**
     * Verifica se la persona è un direttore di dipartimento
     */
    public Boolean isDirDipartimento(OrdineMissione ordineMissione) {
        EmployeeDetails richDetails = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails respDetails = getResponsabile(ordineMissione.getUoSpesa());

//        /* x fare i test con direttori dip e dirigenti I/II fascia gli passo la uo del richiedente
//        perchè ho le voci di bilancio solo su DRUE in collaudo*/
//        EmployeeDetails respDetails = getResponsabile(ordineMissione.getUoRich());
        return getEmailFromEmployee(richDetails).equals(getEmailFromEmployee(respDetails)) &&
                !hasQualifica(richDetails, keyUoDirIFascia, null);
    }

    /**
     * Imposta il responsabile scientifico come firmatario
     */
    public void setRepScientificoToSign(StartSignatureFlowDto startInfo, OrdineMissione ordineMissione) {
        EmployeeDetails richiedente = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails respScientifico = getRespScientifico(ordineMissione);

        if (!getEmailFromEmployee(richiedente).equals(getEmailFromEmployee(respScientifico))) {
            startInfo.addSigner(getEmailFromEmployee(respScientifico));
        }
    }

    /**
     * Imposta il direttore del dipartimento come firmatario
     */
    public void setDirDipToSign(StartSignatureFlowDto startInfo, OrdineMissione ordineMissione) {
        EmployeeDetails richiedente = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails dirUoRich = getResponsabile(ordineMissione.getUoSpesa());

//        /* x fare i test con direttori dip e dirigenti I/II fascia gli passo la uo del richiedente
//        perchè ho le voci di bilancio solo su DRUE in collaudo*/
//        EmployeeDetails dirUoRich = getResponsabile(ordineMissione.getUoRich());
        if (!getEmailFromEmployee(richiedente).equals(getEmailFromEmployee(dirUoRich))) {
            startInfo.addSigner(getEmailFromEmployee(dirUoRich));
        }
    }

    /**
     * Ottiene una persona tramite nome utente da ACE
     */
    public EmployeeDetails getPersonaByUsmFromAce(String username) {
        return aceService.getPersonaByUsername(username);
    }

    /**
     * Verifica se la missione non è a carico dell'ente
     */
    public Boolean isMissioneNoCaricoEnte(OrdineMissione ordineMissione) {
        return ordineMissione != null && ordineMissione.isMissioneGratuita();
    }

    /**
     * Verifica se la persona è il direttore DRUE
     */
    public Boolean isDirDRUE(OrdineMissione ordineMissione) {
        EmployeeDetails dirDRUE = getPersonaByUsmFromAce(ordineMissione.getUid());
        String uoFormatted = formatUoCode(uoCodeDrue);

        return hasQualifica(dirDRUE, keyUoDirIFascia, null) &&
                ordineMissione.getUoRich().equalsIgnoreCase(uoFormatted);
    }

    /**
     * Verifica se l'UO GAE è sotto la direzione centrale
     */
    public Boolean uoGaeSuDirCentrale(OrdineMissione ordineMissione) {
        String cdr = getCDR(ordineMissione);
        String cdsDirCentraleFormatted = formatCdsCode(codeCdsDirCentrale);
        return cdr.startsWith(cdsDirCentraleFormatted);
    }

    /**
     * Verifica se l'UO GAE è sotto CNS o CNT
     */
    public Boolean uoGaeSuCNSOrCNT(OrdineMissione ordineMissione) {
        String cdr = getCDR(ordineMissione);
        String cdsCNSFormatted = formatCdsCode(codeCdsCns);
        String cdsCNTFormatted = formatCdsCode(codeCdsCnt);
        return cdr.startsWith(cdsCNSFormatted) || cdr.startsWith(cdsCNTFormatted);
    }

    /**
     * Ottiene l'email del direttore dell'ufficio economico giuridico
     */
    private String getDirUffEcoEGiud() {
        EmployeeDetails dir = aceService.findResponsabile(Integer.valueOf(dirUffEcoGiur));
        return getEmailFromEmployee(dir);
    }

    /**
     * Ottiene l'UO della GAE
     */
    private String getUoGAE(OrdineMissione ordineMissione) {
        String cdr = getCDR(ordineMissione);
        return cdr.substring(4);
    }

    /**
     * Ottiene il CDR dall'ordine missione
     */
    private String getCDR(OrdineMissione ordineMissione) {
        Gae gae = getGae(ordineMissione);
        return gae.getCd_centro_responsabilita();
    }

    /**
     * Verifica se la persona è un direttore di dipartimento (con esclusioni)
     */
    public Boolean checkIsDirDipartimento(OrdineMissione ordineMissione) {
        return !isPresidente(ordineMissione) &&
                !isDirGenerale(ordineMissione) &&
                !isDirIFascia(ordineMissione) &&
                !isDirIIFascia(ordineMissione) &&
                isDirDipartimento(ordineMissione);
    }

    /**
     * Converte una stringa in intero
     *
     * @param s La stringa da convertire in intero
     * @return L'intero risultante dalla conversione o null se la conversione fallisce
     */
    private Integer convertStringToInteger(String s) {
        if (StringUtils.isEmpty(s)) {
            logger.warn("Tentativo di conversione di una stringa vuota o null in intero");
            return null;
        }

        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            logger.error("Errore nella conversione a intero della stringa: " + s, e);
            return null;
        }
    }

    /**
     * Verifica se un impiegato ha una determinata qualifica
     */
    private boolean hasQualifica(EmployeeDetails employee, String keyQualifica, String valueQualifica) {
        if (employee == null || employee.getRapporto() == null || employee.getRapporto().getQualifica() == null) {
            return false;
        }

        boolean keyMatch = Objects.equals(employee.getRapporto().getQualifica().getKey(),
                convertStringToInteger(keyQualifica));

        if (StringUtils.isEmpty(valueQualifica)) {
            return keyMatch;
        }

        return keyMatch && employee.getRapporto().getQualifica().getValue().equalsIgnoreCase(valueQualifica);
    }

    /**
     * Ottiene l'email da un oggetto EmployeeDetails
     */
    private String getEmailFromEmployee(EmployeeDetails employee) {
        return UtilAce.getEmail(employee);
    }

    /**
     * Determina i firmatari per un ordine missione
     */
    protected Boolean setSignersToMissioni(OrdineMissione ordineMissione, String s) {
        if (ordineMissione == null) {
            return Boolean.FALSE;
        }

        boolean signGae = signGae(ordineMissione);
        boolean uoGaeSuDirCentrale = uoGaeSuDirCentrale(ordineMissione);

        // Mappa delle condizioni per tipo
        Map<String, Predicate<OrdineMissione>> conditionMap = new HashMap<>();
        conditionMap.put(Costanti.IS_PRESIDENTE, this::isPresidente);
        conditionMap.put(Costanti.IS_DIR_I_FASCIA, this::isDirIFascia);
        conditionMap.put(Costanti.CHECK_IS_DIR_DIPARTIMENTO, this::checkIsDirDipartimento);
        conditionMap.put(Costanti.IS_DIR_GENERALE, this::isDirGenerale);
        conditionMap.put(Costanti.IS_DIR_II_FASCIA, this::isDirIIFascia);
        conditionMap.put(Costanti.IS_DIR_DRUE, this::isDirDRUE);
        conditionMap.put(Costanti.IS_INCARICO_VOCI_PRESIDENTE, this::isIncarico_VociPresidente);
        conditionMap.put(Costanti.IS_MISSIONE_NO_CARICO_ENTE, this::isMissioneNoCaricoEnte);

        // Predicate per la condizione selezionata
        Predicate<OrdineMissione> condition = conditionMap.getOrDefault(s, ordine -> false);

        // Predicate per tutte le condizioni negate
        Predicate<OrdineMissione> allNegatedConditions = ordine ->
                !isPresidente(ordine) &&
                        !checkIsDirDipartimento(ordine) &&
                        !isDirGenerale(ordine) &&
                        !isDirIIFascia(ordine) &&
                        !isDirIFascia(ordine) &&
                        !isDirDRUE(ordine) &&
                        !isIncarico_VociPresidente(ordine) &&
                        !isMissioneNoCaricoEnte(ordine);

        // Gestione casi di autorizzazione vecchi (*SenzaProgStessaUo,*SenzaProgDifUo,*ProgStessaUo,*ProgDifUo)
        if (Costanti.OLD_AUTH.equals(s)) {
            return !signGae && !uoGaeSuDirCentrale && allNegatedConditions.test(ordineMissione);
        }

        if (StringUtils.isEmpty(s)) {
            return signGae && uoGaeSuDirCentrale && allNegatedConditions.test(ordineMissione);
        }

        // Caso standard
        return signGae && uoGaeSuDirCentrale && condition.test(ordineMissione);
    }
}