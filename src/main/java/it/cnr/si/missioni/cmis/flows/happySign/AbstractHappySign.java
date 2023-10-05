package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.service.application.FlowsService;
import it.iss.si.dto.happysign.base.UserFea;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;
import it.iss.si.dto.happysign.response.UserListForTemplateResponse;
import it.iss.si.service.HappySignService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract  class AbstractHappySign implements FlussiToHappySign{
    @Autowired
    private HappySignService happySignService;
    protected UserFea getUserFeaByMail(String email){
        return happySignService.getUserFeaByMail(email);
    }
    protected UserListForTemplateResponse getUserListForTemplate(String templateName){
        return happySignService.getUserListForTemplate(templateName);
    }

    protected UploadToComplexResponse UploadToComplexTemplate(UploadToComplexRequest request){
        return happySignService.uploadToComplexTemplate(request);
    }
}
