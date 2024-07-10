package it.cnr.si.missioni.cmis.flows.happySign;

import feign.FeignException;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
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
import it.iss.si.dto.happysign.base.AttachedFile;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.service.AceService;
import it.iss.si.service.HappySignService;
import it.iss.si.service.UtilAce;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    @Value("${dirUffEcoGiur.username:#{null}}")
    private String dirUffEcoGiur;
    @Value("${vociPresidenza.voce2089:#{null}}")
    private String voce2089;
    @Value("${vociPresidenza.voce2090:#{null}}")
    private String voce2090;
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

    public Progetto getProgetto(OrdineMissione ordineMissione) {
        Integer anno = DateUtils.getCurrentYear();
        return progettoService.loadModulo(ordineMissione.getPgProgetto(), anno, ordineMissione.getUoSpesa());
    }

    protected Boolean signRespProgetto(OrdineMissione ordineMissione) {
        if (ordineMissione == null)
            return Boolean.FALSE;
        if (ordineMissione.getPgProgetto() == null)
            return Boolean.FALSE;
        if (ordineMissione.getPgProgetto() <= 0)
            return Boolean.FALSE;


        Progetto progetto = getProgetto(ordineMissione);
        if (progetto.getCd_responsabile_terzo() != null)
            return Boolean.TRUE;

        return Boolean.FALSE;
    }

    public Gae getGae(OrdineMissione ordineMissione) {
        return gaeService.loadGae(ordineMissione);
    }

    protected Boolean signGae(OrdineMissione ordineMissione) {
        if (ordineMissione == null)
            return Boolean.FALSE;
        if (ordineMissione.getGae() == null)
            return Boolean.FALSE;

        Gae gae = getGae(ordineMissione);
        if (gae.getCd_responsabile_terzo() != null)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    protected Boolean signUoRichEqUoGae(OrdineMissione ordineMissione) {
        String uoGae = getUoGAE(ordineMissione);

        if (ordineMissione == null)
            return Boolean.FALSE;
        if (ordineMissione.getUoRich().equalsIgnoreCase(uoGae)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    protected Boolean signRespDipUoEqUoGae(OrdineMissione ordineMissione) {
        String uoGae = getUoGAE(ordineMissione);

        if (ordineMissione == null)
            return Boolean.FALSE;
        else {
            Account direttore = uoService.getDirettore(ordineMissione.getUoRich());
            if (direttore.getCodice_uo().equalsIgnoreCase(uoGae)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }

    protected Boolean isIncarico_VociPresidente(OrdineMissione ordineMissione) {

        if (ordineMissione == null)
            return Boolean.FALSE;
        else {
            if (Optional.ofNullable(ordineMissione.getPresidente()).isPresent()
                    && ordineMissione.getVoce().contains(voce2089) || ordineMissione.getVoce().contains(voce2090)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }


    public EmployeeDetails getUserFeaByCf(String codiceFiscale) {
        return aceService.getPersonaByCodiceFiscale(codiceFiscale);

    }

    public EmployeeDetails getResponsabile(String uo) {
        UnitaOrganizzativa unitaOrganizzativa = unitaOrganizzativaService.loadUo(uo, null, DateUtils.getCurrentYear());
        return aceService.findResponsabileBySigla(unitaOrganizzativa.getSigla_int_ente());

    }

    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }

    public File getFile(StorageObject modulo, List<StorageObject> allegati) throws IOException {
        File f = new File();
        String fileName = modulo.getPropertyValue(StoragePropertyNames.NAME.value());
        f.setFilename(fileName);
        f.setPdf(getDocumento(modulo));
        if (allegati != null && allegati.size() > 0) {
            for (StorageObject so : allegati) {
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setAttached(false);
                attachedFile.setContent(getDocumento(so));
                attachedFile.setFilename(so.getPropertyValue(StoragePropertyNames.NAME.value()));
                f.addAttachedFile(attachedFile);
            }
        }

        return f;
    }

    public String getNomeFile(String fileName) {

        return missioniCMISService.parseFilename(fileName);
    }

    public EmployeeDetails getRespScientifico(OrdineMissione ordineMissione) throws FeignException {
        String cdResponsabileTerzo = getGae(ordineMissione).getCd_responsabile_terzo();
        TerzoInfo terzoInfo = terzoService.loadUserInfoByCdTerzo(cdResponsabileTerzo);
        return getUserFeaByCf(terzoInfo.getCodice_fiscale());
    }

    public EmployeeDetails getPresidente() {
        String uoFormatted = formatUoCode(uoCodePresidenza);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    public EmployeeDetails getDirGenerale() {
        String uoFormatted = formatUoCode(uoCodeDirGenerale);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }


    public EmployeeDetails getDirDRUE() {
        String uoFormatted = formatUoCode(uoCodeDrue);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    public EmployeeDetails getDirDRAG() {
        String uoFormatted = formatUoCode(uoCodeDrag);
        Account direttore = uoService.getDirettore(uoFormatted);
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    public void setDirUffEcoGiur(OrdineMissione ordineMissione, StartWorflowDto startInfo) {

        if (!ordineMissione.getUid().equalsIgnoreCase(dirUffEcoGiur)) {
            startInfo.addSigner(dirUffEcoGiur);
        }
    }


    @Override
    public String send(String templateName, List<String> signerList, List<String> approvedList, File fileToSign) throws Exception {
        logger.info("UploadToComplexResponse send(UploadToComplexRequest request)");
        return happySignService.startFlowToSignSingleDocument(templateName, signerList, approvedList, fileToSign);
    }


    public Boolean isDirGenerale(OrdineMissione ordineMissione) {
        EmployeeDetails dirGenerale = getPersonaByUsmFromAce(ordineMissione.getUid());
        return Objects.equals(dirGenerale.getRapporto().getQualifica().getKey(), convertStringToInteger(keyUoDirGenerale))
                && dirGenerale.getRapporto().getQualifica().getValue().equalsIgnoreCase(Costanti.SIGLA_ACE_DIR_GENERALE_VALUE);
    }


    public Boolean isPresidente(OrdineMissione ordineMissione) {
        EmployeeDetails presidente = getPersonaByUsmFromAce(ordineMissione.getUid());
        return Objects.equals(presidente.getRapporto().getQualifica().getKey(), convertStringToInteger(keyUoPresidenza))
                && Objects.equals(presidente.getRapporto().getQualifica().getValue(), ACE_SIGLA_PRESIDENTE_DESC);
    }


    public Boolean isDirIFascia(OrdineMissione ordineMissione) {
        EmployeeDetails dir = getPersonaByUsmFromAce(ordineMissione.getUid());
        return Objects.equals(dir.getRapporto().getQualifica().getKey(), convertStringToInteger(keyUoDirIFascia));
    }


    public Boolean isDirIIFascia(OrdineMissione ordineMissione) {
        EmployeeDetails dir = getPersonaByUsmFromAce(ordineMissione.getUid());
        return Objects.equals(dir.getRapporto().getQualifica().getKey(), convertStringToInteger(keyUoDirIIFascia));
    }


    public Boolean isDirDipartimento(OrdineMissione ordineMissione) {
        EmployeeDetails richDetails = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails respDetails = getResponsabile(ordineMissione.getUoRich());
        return UtilAce.getEmail(richDetails).equals(UtilAce.getEmail(respDetails));
    }


    public void setRepScientificoToSign(StartWorflowDto startInfo, OrdineMissione ordineMissione) {
        EmployeeDetails richiedente = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails respScientifico = getRespScientifico(ordineMissione);

        if (!UtilAce.getEmail(richiedente).equals(UtilAce.getEmail(respScientifico))) {
            startInfo.addSigner(UtilAce.getEmail(respScientifico));
        }
    }


    public void setDirDipToSign(StartWorflowDto startInfo, OrdineMissione ordineMissione) {

        EmployeeDetails richiedente = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails dirUoRich = getResponsabile(ordineMissione.getUoRich());

        if (!UtilAce.getEmail(richiedente).equals(UtilAce.getEmail(dirUoRich))) {
            startInfo.addSigner(UtilAce.getEmail(dirUoRich));
        }

    }

    public EmployeeDetails getPersonaByUsmFromAce(String username) {
        return aceService.getPersonaByUsername(username);
    }

    public Boolean isMissioneNoCaricoEnte(OrdineMissione ordineMissione) {
        return Optional.ofNullable(ordineMissione.getMissioneGratuita()).isPresent();
    }


    public Boolean isDirDRUE(OrdineMissione ordineMissione) {
        EmployeeDetails dirDRUE = getPersonaByUsmFromAce(ordineMissione.getUid());
        return Objects.equals(dirDRUE.getRapporto().getQualifica().getKey(), convertStringToInteger(keyUoDirIFascia))
                && ordineMissione.getUoRich().equalsIgnoreCase(formatUoCode(uoCodeDrue));
    }


    public Boolean uoGaeSuDirCentrale(OrdineMissione ordineMissione) {
        String cdr = getCDR(ordineMissione);
        String cdsDirCentraleFormatted = formatCdsCode(codeCdsDirCentrale);
        return cdr.startsWith(cdsDirCentraleFormatted);
    }

    public Boolean uoGaeSuCNSOrCNT(OrdineMissione ordineMissione) {
        String cdr = getCDR(ordineMissione);
        String cdsCNSFormatted = formatCdsCode(codeCdsCns);
        String cdsCNTFormatted = formatCdsCode(codeCdsCnt);
        return cdr.startsWith(cdsCNSFormatted) || cdr.startsWith(cdsCNTFormatted);
    }


    private String getUoGAE(OrdineMissione ordineMissione) {
        String cdr = getCDR(ordineMissione);
        return cdr.substring(4);
    }


    private String getCDR(OrdineMissione ordineMissione) {
        Gae gae = getGae(ordineMissione);
        return gae.getCd_centro_responsabilita();
    }

    public Boolean checkIsDirDipartimento(OrdineMissione ordineMissione) {
        Boolean result;
        if (!isPresidente(ordineMissione) && !isDirGenerale(ordineMissione) &&
                !isDirIFascia(ordineMissione) && !isDirIIFascia(ordineMissione) && !isDirDRUE(ordineMissione)) {
            result = isDirDipartimento(ordineMissione);
        } else {
            result = Boolean.FALSE;
        }
        return result;
    }

    private Integer convertStringToInteger(String s) {
        return Integer.valueOf(s);
    }


    protected Boolean setSignersToMissioni(OrdineMissione ordineMissione, String s) {
        boolean signGae = signGae(ordineMissione);
        boolean uoGaeSuDirCentrale = uoGaeSuDirCentrale(ordineMissione);

        Predicate<OrdineMissione> condition;
            switch (s) {
                case Costanti.IS_PRESIDENTE:
                    condition = this::isPresidente;
                    break;
                case Costanti.CHECK_IS_DIR_DIPARTIMENTO:
                    condition = this::checkIsDirDipartimento;
                    break;
                case Costanti.IS_DIR_GENERALE:
                    condition = this::isDirGenerale;
                    break;
                case Costanti.IS_DIR_II_FASCIA:
                    condition = this::isDirIIFascia;
                    break;
                case Costanti.IS_DIR_I_FASCIA:
                    condition = this::isDirIFascia;
                    break;
                case Costanti.IS_DIR_DRUE:
                    condition = this::isDirDRUE;
                    break;
                case Costanti.IS_INCARICO_VOCI_PRESIDENTE:
                    condition = this::isIncarico_VociPresidente;
                    break;
                case Costanti.IS_MISSIONE_NO_CARICO_ENTE:
                    condition = this::isMissioneNoCaricoEnte;
                    break;
                default:
                    condition = ordine -> false;
                    break;
            }

        Predicate<OrdineMissione> allNegatedConditions = ordine ->
                !isPresidente(ordine) &&
                        !checkIsDirDipartimento(ordine) &&
                        !isDirGenerale(ordine) &&
                        !isDirIIFascia(ordine) &&
                        !isDirIFascia(ordine) &&
                        !isDirDRUE(ordine) &&
                        !isIncarico_VociPresidente(ordine) &&
                        !isMissioneNoCaricoEnte(ordine);

        if (Costanti.OLD_AUTH.equals(s)) {
            return !signGae && !uoGaeSuDirCentrale && allNegatedConditions.test(ordineMissione);
        }

        if (s.isEmpty()) {
            return signGae && uoGaeSuDirCentrale && allNegatedConditions.test(ordineMissione);
        }

        return signGae && uoGaeSuDirCentrale && condition.test(ordineMissione);
    }

}
