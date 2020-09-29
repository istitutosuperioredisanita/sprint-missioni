package it.cnr.si.missioni.domain.custom;

import it.cnr.si.missioni.util.Costanti;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlowResult implements Serializable {
    private String idFlusso;
    private String tipoFlusso;
    private String idMissione;
    private String esito;
    private String commento;

    public final static String TIPO_FLUSSO_ORDINE = "missioni-ordine";
    public final static String TIPO_FLUSSO_RIMBORSO = "missioni-rimborso";
    public final static String TIPO_FLUSSO_REVOCA = "missioni-revoca";

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

    public String getIdFlusso() {
        return idFlusso;
    }

    public void setIdFlusso(String idFlusso) {
        this.idFlusso = idFlusso;
    }

    @Override
    public String toString() {
        return "FlowResult{" +
                "idFlusso='" + idFlusso + '\'' +
                ", tipoFlusso='" + tipoFlusso + '\'' +
                ", idMissione='" + idMissione + '\'' +
                ", esito='" + esito + '\'' +
                ", commento='" + commento + '\'' +
                '}';
    }

    public String getTipoFlusso() {
        return tipoFlusso;
    }

    public void setTipoFlusso(String tipoFlusso) {
        this.tipoFlusso = tipoFlusso;
    }

    public String getIdMissione() {
        return idMissione;
    }

    public void setIdMissione(String idMissione) {
        this.idMissione = idMissione;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
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
