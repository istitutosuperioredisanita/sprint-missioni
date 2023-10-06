package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.StoragePath;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;

import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;
import it.iss.si.dto.happysign.response.UserListForTemplateResponse;
import it.iss.si.service.AceService;
import it.iss.si.service.HappySignService;
import it.iss.si.service.UtilAce;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public abstract  class AbstractHappySign implements FlussiToHappySign{
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


    public UserFea getUserFea( String mail){
        return happySignService.getUserFeaByMail(mail);
    }

    private UserFea getUserFea(EmployeeDetails detail){
        return getUserFea(UtilAce.getEmail(detail));
    }
    public UserFea getUserFeaByCf(String codiceFiscale){
        EmployeeDetails detail= aceService.getPersonaByCodiceFiscale( codiceFiscale);
        return getUserFea(detail);
    }
    public UserFea getResponsabile( String uo){
        UnitaOrganizzativa unitaOrganizzativa = unitaOrganizzativaService.loadUo(uo,null,DateUtils.getCurrentYear());
        EmployeeDetails detail= aceService.findResponsabileBySigla(unitaOrganizzativa.getSigla_int_ente());
        return getUserFea(detail);
    }

    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }

    @Override
    public UploadToComplexResponse send(UploadToComplexRequest request) {
        return happySignService.uploadToComplexTemplate(request);
    }


}
