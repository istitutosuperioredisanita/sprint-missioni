package it.cnr.si.missioni.domain.custom;

import it.cnr.si.missioni.util.Costanti;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlowResult implements Serializable {
    private String processInstanceId;
    private String tipologiaMissione;
    private String idMissione;
    private String stato;
    private String commento;

    public final static String TIPO_FLUSSO_ORDINE = "ordine";
    public final static String TIPO_FLUSSO_RIMBORSO = "rimborso";
    public final static String TIPO_FLUSSO_REVOCA = "revoca";

    public final static String ESITO_FLUSSO_FIRMATO = "FIRMATO";
    public final static String ESITO_FLUSSO_FIRMA_UO = "FIRMA_UO";
    public final static String ESITO_FLUSSO_RESPINTO_UO = "RESPINTO_UO";
    public final static String ESITO_FLUSSO_RESPINTO_UO_SPESA = "RESPINTO_UO_SPESA";


    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return "FlowResult{" +
                "idFlusso='" + processInstanceId + '\'' +
                ", tipoFlusso='" + tipologiaMissione + '\'' +
                ", idMissione='" + idMissione + '\'' +
                ", esito='" + stato + '\'' +
                ", commento='" + commento + '\'' +
                '}';
    }

    public String getTipologiaMissione() {
        return tipologiaMissione;
    }

    public void setTipologiaMissione(String tipologiaMissione) {
        this.tipologiaMissione = tipologiaMissione;
    }

    public String getIdMissione() {
        return idMissione;
    }

    public void setIdMissione(String idMissione) {
        this.idMissione = idMissione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public final static Map<String, String> STATO_FLUSSO_SCRIVANIA_MISSIONI;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(ESITO_FLUSSO_FIRMA_UO, Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
        aMap.put(ESITO_FLUSSO_FIRMATO, Costanti.STATO_APPROVATO_FLUSSO);
        aMap.put(ESITO_FLUSSO_RESPINTO_UO, Costanti.STATO_RESPINTO_UO_FLUSSO);
        aMap.put(ESITO_FLUSSO_RESPINTO_UO_SPESA, Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO);
        STATO_FLUSSO_SCRIVANIA_MISSIONI = Collections.unmodifiableMap(aMap);
    }

}
