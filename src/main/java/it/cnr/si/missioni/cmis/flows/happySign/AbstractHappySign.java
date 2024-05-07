package it.cnr.si.missioni.cmis.flows.happySign;

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
import it.cnr.si.missioni.util.data.UsersSpecial;
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

public abstract class AbstractHappySign implements FlussiToHappySign {

    private static final Log logger = LogFactory.getLog(AbstractHappySign.class);

    @Value("${codiciUo.drue:#{null}}")
    private String uoCodeDrue;
    @Value("${codiciUo.direzioneGenerale:#{null}}")
    private String uoCodeDirGenerale;
    @Value("${codiciUo.presidenza:#{null}}")
    private String uoCodePresidenza;
    @Value("${keyUo.direzione:#{null}}")
    private String keyUoDirezione;
    @Value("${keyUo.direzioneGenerale:#{null}}")
    private String keyUoDirGenerale;
    @Value("${keyUo.presidenza:#{null}}")
    private String keyUoPresidenza;

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

    protected Boolean isVociBilancioPresidente(OrdineMissione ordineMissione) {

        if (ordineMissione == null)
            return Boolean.FALSE;
        else {
            if (ordineMissione.getVoce().contains("2089") || ordineMissione.getVoce().contains("2090")) {
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

    public EmployeeDetails getRespScientifico(OrdineMissione ordineMissione) {
        String cdResponsabileTerzo = getGae(ordineMissione).getCd_responsabile_terzo();
        TerzoInfo terzoInfo = terzoService.loadUserInfoByCdTerzo(cdResponsabileTerzo);
        return getUserFeaByCf(terzoInfo.getCodice_fiscale());
    }

    public EmployeeDetails getPresidente() {

        Account direttore = uoService.getDirettore(formatUoCode(uoCodePresidenza));
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }

    public EmployeeDetails getDirGenerale() {

        Account direttore = uoService.getDirettore(formatUoCode(uoCodeDirGenerale));
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }


    public EmployeeDetails getDirDRUE() {

        Account direttore = uoService.getDirettore(formatUoCode(uoCodeDrue));
        return getUserFeaByCf(direttore.getCodice_fiscale());
    }


    @Override
    public String send(String templateName, List<String> signerList, List<String> approvedList, File fileToSign) throws Exception {
        logger.info("UploadToComplexResponse send(UploadToComplexRequest request)");
        return happySignService.startFlowToSignSingleDocument(templateName, signerList, approvedList, fileToSign);
    }


    public Boolean isDirGenerale(OrdineMissione ordineMissione) {
        EmployeeDetails dirGenerale = getPersonaByUsmFromAce(ordineMissione.getUid());

        if ((Objects.equals(dirGenerale.getRapporto().getQualifica().getKey(), keyUoDirGenerale))
                && (dirGenerale.getRapporto().getQualifica().getValue().equalsIgnoreCase(Costanti.SIGLA_ACE_DIR_GENERALE_VALUE))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }

    }

    public Boolean isPresidente(OrdineMissione ordineMissione) {
        EmployeeDetails presidente = getPersonaByUsmFromAce(ordineMissione.getUid());

        if (Objects.equals(presidente.getRapporto().getQualifica().getKey(), keyUoPresidenza)
                && presidente.getRapporto().getQualifica().getValue().equalsIgnoreCase(Costanti.ACE_SIGLA_PRESIDENTE_DESC)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }


    public Boolean isDirIFascia(OrdineMissione ordineMissione) {

        EmployeeDetails dir = getPersonaByUsmFromAce(ordineMissione.getUid());

        if (Objects.equals(dir.getRapporto().getQualifica().getKey(), keyUoDirezione)
                && dir.getRapporto().getQualifica().getValue().contains(Costanti.ACE_SIGLA_DIR_I_FASCIA)
                && !ordineMissione.getUoRich().equals(formatUoCode(uoCodeDrue))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }


    public Boolean isDirDipartimento(OrdineMissione ordineMissione) {

        EmployeeDetails richDetails = getPersonaByUsmFromAce(ordineMissione.getUid());
        EmployeeDetails respDetails = getResponsabile(ordineMissione.getUoRich());

        if (UtilAce.getEmail(richDetails).equals(UtilAce.getEmail(respDetails))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
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
        if (Optional.ofNullable(ordineMissione.getMissioneGratuita()).isPresent()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isDirDRUE(OrdineMissione ordineMissione) {

        EmployeeDetails dirDRUE = getPersonaByUsmFromAce(ordineMissione.getUid());

        if (Objects.equals(dirDRUE.getRapporto().getQualifica().getKey(), keyUoDirezione)
                && ordineMissione.getUoRich().equalsIgnoreCase(formatUoCode(uoCodeDrue))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private String getUoGAE(OrdineMissione ordineMissione) {
        Gae gae = getGae(ordineMissione);
        String codCompletoCDR = gae.getCd_centro_responsabilita();
        return codCompletoCDR.substring(4);
    }


    private String formatUoCode(String uoCode) {
        if (uoCode == null) {
            return null;
        }

        String[] parts = uoCode.split("\\.");
        StringBuilder formattedUoCode = new StringBuilder();

        for (String part : parts) {
            formattedUoCode.append(String.format("%03d", Integer.parseInt(part))).append(".");
        }

        return formattedUoCode.deleteCharAt(formattedUoCode.length() - 1).toString();
    }


}
