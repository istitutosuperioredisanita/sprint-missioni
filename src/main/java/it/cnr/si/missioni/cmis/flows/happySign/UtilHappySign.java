package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.service.MailService;
import it.cnr.si.spring.storage.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class UtilHappySign {
    @Autowired(required = false)
    private MailService mailService;
    @Value("${spring.mail.messages.firmatariMissioneInProd.oggetto}")
    private String firmatariMissioneInProd;

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";


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

    /**
     * Ricava il content type dall'estensione del file, con fallback su application/octet-stream
     */
    public static String mimeTypeOf(String fileName) {
        int dot = fileName == null ? -1 : fileName.lastIndexOf('.');
        if (dot < 0) {
            return DEFAULT_MIME_TYPE;
        }
        try {
            return MimeTypes.valueOf(fileName.substring(dot + 1).toUpperCase()).mimetype();
        } catch (IllegalArgumentException e) {
            return DEFAULT_MIME_TYPE;
        }
    }
}
