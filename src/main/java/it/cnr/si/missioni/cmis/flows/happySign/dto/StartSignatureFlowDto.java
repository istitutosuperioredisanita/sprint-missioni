package it.cnr.si.missioni.cmis.flows.happySign.dto;

import it.iss.si.signature.flow.flow.model.SignatureDocument;

import java.util.ArrayList;
import java.util.List;

public class StartSignatureFlowDto {

    private String templateName;

    private List<String> signers = new ArrayList<>();

    private List<String> approvers = new ArrayList<>();

    private List<SignatureDocument> filesToSign = new ArrayList<>();

    private List<SignatureDocument> attachments = new ArrayList<>();

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

    public List<SignatureDocument> getFilesToSign() {
        return filesToSign;
    }

    public void setFilesToSign(List<SignatureDocument> filesToSign) {
        this.filesToSign = filesToSign;
    }

    public List<SignatureDocument> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SignatureDocument> attachments) {
        this.attachments = attachments;
    }

    public void addSigner(String signer) {
        signers.add(signer);
    }

    public void addApprover(String approver) {
        approvers.add(approver);
    }

}