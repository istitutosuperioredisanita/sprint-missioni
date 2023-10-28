package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.StoragePath;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.service.AbstractAccountService;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;

import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;
import it.iss.si.dto.happysign.response.UserListForTemplateResponse;
import it.iss.si.service.AceService;
import it.iss.si.service.HappySignService;
import it.iss.si.service.UtilAce;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public abstract  class AbstractHappySign implements FlussiToHappySign{

    private static final Log logger = LogFactory.getLog(AbstractHappySign.class);
    @Autowired
    protected HappySignService happySignService;
    @Autowired
    ProgettoService progettoService;
    @Autowired
    AceService aceService;
    @Autowired
    UnitaOrganizzativaService unitaOrganizzativaService;
    @Autowired
    MissioniCMISService missioniCMISService;
    @Autowired
    AccountService accountService;


    public  Progetto getProgetto(OrdineMissione ordineMissione){
        Integer anno = DateUtils.getCurrentYear();
        return progettoService.loadModulo(ordineMissione.getPgProgetto(), anno, ordineMissione.getUoSpesa());
    }
    protected Boolean signRespProgetto(OrdineMissione ordineMissione) {
        if ( ordineMissione==null)
            return Boolean.FALSE;
        if(ordineMissione.getPgProgetto()==null)
            return Boolean.FALSE;
        if(ordineMissione.getPgProgetto()<=0)
            return Boolean.FALSE;


        Progetto progetto = getProgetto(ordineMissione);
        if (progetto.getCd_responsabile_terzo() != null)
            return Boolean.TRUE;

        return Boolean.FALSE;
    }
    protected Boolean signRespUoAfferente(OrdineMissione ordineMissione) {
        if ( ordineMissione==null)
            return Boolean.FALSE;
        if ( !ordineMissione.getUoRich().equalsIgnoreCase(ordineMissione.getUoSpesa()))
            return Boolean.FALSE;
        return Boolean.FALSE;
    }

    public EmployeeDetails getUserFeaByCf(String codiceFiscale){
        return aceService.getPersonaByCodiceFiscale( codiceFiscale);

    }
    public EmployeeDetails getResponsabile( String uo){
        UnitaOrganizzativa unitaOrganizzativa = unitaOrganizzativaService.loadUo(uo,null,DateUtils.getCurrentYear());
        return aceService.findResponsabileBySigla(unitaOrganizzativa.getSigla_int_ente());

    }

    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }

    public File getFile( StorageObject modulo, List<StorageObject> allegati) throws IOException {
        File f = new File();
        f.setFilename(getNomeFile(modulo.getKey()));
        f.setPdf(getDocumento(modulo));
        return f;
    }
    public String getNomeFile ( String fileName){

        return missioniCMISService.parseFilename(fileName);
    }

    @Override
    public String send(String templateName, List<String> signerList, List<String> approvedList, File fileToSign) throws Exception {
        logger.info("UploadToComplexResponse send(UploadToComplexRequest request)");
        return happySignService.startFlowToSignSigleDocument(templateName,signerList,approvedList,fileToSign);
    }


}
