/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.domain.custom;

import it.cnr.si.missioni.domain.custom.persistence.MissioneRespinta;
import it.cnr.si.missioni.util.Costanti;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlowResult implements Serializable {
    public final static String TIPO_FLUSSO_ORDINE = "ordine";
    public final static String TIPO_FLUSSO_RIMBORSO = "rimborso";
    public final static String TIPO_FLUSSO_REVOCA = "revoca";
    public final static String ESITO_FLUSSO_FIRMATO = "FIRMATO";
    public final static String ESITO_FLUSSO_FIRMA_UO = "FIRMATO_UO";
    public final static String ESITO_FLUSSO_RESPINTO_UO = "RESPINTO_UO";
    public final static String ESITO_FLUSSO_RESPINTO_UO_SPESA = "RESPINTO_UO_SPESA";
    public final static Map<String, String> STATO_FLUSSO_SCRIVANIA_MISSIONI;
    public final static Map<String, String> TIPO_FLUSSO_MISSIONE;

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(ESITO_FLUSSO_FIRMA_UO, Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
        aMap.put(ESITO_FLUSSO_FIRMATO, Costanti.STATO_APPROVATO_FLUSSO);
        aMap.put(ESITO_FLUSSO_RESPINTO_UO, Costanti.STATO_RESPINTO_UO_FLUSSO);
        aMap.put(ESITO_FLUSSO_RESPINTO_UO_SPESA, Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO);
        STATO_FLUSSO_SCRIVANIA_MISSIONI = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(TIPO_FLUSSO_ORDINE, MissioneRespinta.OPERAZIONE_MISSIONE_ORDINE);
        aMap.put(TIPO_FLUSSO_REVOCA, MissioneRespinta.OPERAZIONE_MISSIONE_ANNULLAMENTO_ORDINE);
        aMap.put(TIPO_FLUSSO_RIMBORSO, MissioneRespinta.OPERAZIONE_MISSIONE_RIMBORSO);
        TIPO_FLUSSO_MISSIONE = Collections.unmodifiableMap(aMap);
    }

    private String processInstanceId;
    private String tipologiaMissione;
    private String idMissione;
    private String stato;
    private String commento;
    private String user;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
