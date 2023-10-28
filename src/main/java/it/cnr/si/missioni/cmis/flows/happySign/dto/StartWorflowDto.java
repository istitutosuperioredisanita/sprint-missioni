package it.cnr.si.missioni.cmis.flows.happySign.dto;

import it.iss.si.dto.happysign.base.File;

import java.util.ArrayList;
import java.util.List;

public class StartWorflowDto {

    private String templateName;

    private List<String> signers =new ArrayList<String>();

    private List<String> approvers= new ArrayList<String>();

    private File fileToSign;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<String> getSigners() {
        return signers;
    }

    public void setSigners(List<String> signers) {
        this.signers = signers;
    }

    public List<String> getApprovers() {
        return approvers;
    }

    public void setApprovers(List<String> approvers) {
        this.approvers = approvers;
    }

    public void setFileToSign(File fileToSign) {
        this.fileToSign = fileToSign;
    }

    public File getFileToSign() {
        return fileToSign;
    }

    public void addSigner(String signer){
        signers.add(signer);
    }
    public void addApprover(String approver){
        approvers.add(approver);
    }


}
