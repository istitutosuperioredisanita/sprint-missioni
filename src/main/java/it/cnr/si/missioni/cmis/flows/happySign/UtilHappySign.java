package it.cnr.si.missioni.cmis.flows.happySign;

public class UtilHappySign {

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
}
