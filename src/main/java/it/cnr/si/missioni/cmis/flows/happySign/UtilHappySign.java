package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class UtilHappySign {

    @Value("${flows.templateFirme.1Firma:#{null}}")
    private static String template1Firma;
    @Value("${flows.templateFirme.2Firme:#{null}}")
    private static String template2Firme;
    @Value("${flows.templateFirme.3Firme:#{null}}")
    private static String template3Firme;
    @Value("${flows.templateFirme.4Firme:#{null}}")
    private static String template4Firme;
    @Value("${flows.templateFirme.5Firme:#{null}}")
    private static String template5Firme;

    @Autowired(required = false)
    private MailService mailService;
    @Value("${spring.mail.messages.firmatariMissioneInProd.oggetto}")
    private String firmatariMissioneInProd;


    protected static String formatUoCode(String uoCode) {
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


    protected static String formatCdsCode(String uoCode) {
        if (uoCode == null) {
            return null;
        }
        String[] parts = uoCode.split("\\.");
        StringBuilder formattedUoCode = new StringBuilder();
        for (String part : parts) {
            int numZerosToAdd = 3 - part.length();
            for (int i = 0; i < numZerosToAdd; i++) {
                formattedUoCode.append("0");
            }
            formattedUoCode.append(part).append(".");
        }
        return formattedUoCode.deleteCharAt(formattedUoCode.length() - 1).toString();
    }

    protected static List<String> getNoDoubleSigners(List<String> signers) {
        return new ArrayList<>(new LinkedHashSet<>(signers));
    }

    protected static void setTemplateFirme(StartWorflowDto startWorflowDto) {
        int numFirmatari = startWorflowDto.getSigners().size();
        switch (numFirmatari) {
            case 1:
                startWorflowDto.setTemplateName(template1Firma);
                break;
            case 2:
                startWorflowDto.setTemplateName(template2Firme);
                break;
            case 3:
                startWorflowDto.setTemplateName(template3Firme);
                break;
            case 4:
                startWorflowDto.setTemplateName(template4Firme);
                break;
            case 5:
                startWorflowDto.setTemplateName(template5Firme);
                break;
            default:
                break;
        }
    }


}
