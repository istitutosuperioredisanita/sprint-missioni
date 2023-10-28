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

package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.si.missioni.util.Costanti;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A user.
 */
@Entity
@Table(name = "MISSIONE_RESPINTA")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_MISSIONE_RESPINTA", allocationSize = 0)
public class MissioneRespinta extends OggettoBulkXmlTransient {

    public final static String OPERAZIONE_MISSIONE_ORDINE = "OR";
    public final static String OPERAZIONE_MISSIONE_RIMBORSO = "RI";
    public final static String OPERAZIONE_MISSIONE_ANNULLAMENTO_ORDINE = "AO";
    public final static String OPERAZIONE_MISSIONE_ANNULLAMENTO_RIMBORSO = "AR";

    public final static String FASE_RESPINGI_UO = Costanti.STATO_RESPINTO_UO_FLUSSO;
    public final static String FASE_RESPINGI_UO_SPESA = Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO;
    public final static String FASE_RESPINGI_RESP_GRUPPO = "RRG";
    public final static String FASE_RESPINGI_AMMINISTRATIVI = "RAM";
    public final static Map<String, String> FASI;

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(FASE_RESPINGI_AMMINISTRATIVI, "Verifica Ammininistrativi");
        aMap.put(FASE_RESPINGI_RESP_GRUPPO, "Responsabile Gruppo");
        aMap.put(FASE_RESPINGI_UO, "Firma");
        aMap.put(FASE_RESPINGI_UO_SPESA, "Firma Uo Spesa");
        FASI = Collections.unmodifiableMap(aMap);
    }

    @Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    public String uidInsert;
    @Column(name = "DATA_INSERIMENTO", nullable = false)
    public Timestamp dataInserimento;

    @Size(min = 0, max = 2)
    @Column(name = "TIPO_OPERAZIONE_MISSIONE", length = 2, nullable = false)
    public String tipoOperazioneMissione;

    @Size(min = 0, max = 3)
    @Column(name = "TIPO_FASE_RESPINGI", length = 3, nullable = false)
    public String tipoFaseRespingi;

    @Size(min = 0, max = 2000)
    @Column(name = "MOTIVO_RESPINGI", length = 2000, nullable = false)
    public String motivoRespingi;

    @Transient
    public String decodeFase;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Column(name = "ID_MISSIONE", nullable = false, length = 20)
    private Long idMissione;

    public MissioneRespinta() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUidInsert() {
        return uidInsert;
    }

    public void setUidInsert(String uidInsert) {
        this.uidInsert = uidInsert;
    }

    public Long getIdMissione() {
        return idMissione;
    }

    public void setIdMissione(Long idMissione) {
        this.idMissione = idMissione;
    }

    public Timestamp getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(Timestamp dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    public String getTipoOperazioneMissione() {
        return tipoOperazioneMissione;
    }

    public void setTipoOperazioneMissione(String tipoOperazioneMissione) {
        this.tipoOperazioneMissione = tipoOperazioneMissione;
    }

    public String getTipoFaseRespingi() {
        return tipoFaseRespingi;
    }

    public void setTipoFaseRespingi(String tipoFaseRespingi) {
        this.tipoFaseRespingi = tipoFaseRespingi;
    }


    public String getTranslateStatoFlusso() {
        if (!StringUtils.isEmpty(getTipoFaseRespingi()))
            return "stato-flusso.".concat(getTipoFaseRespingi());
        return "";
    }


    @Transient
    public String getDecodeFase() {
        if (!StringUtils.isEmpty(getTipoFaseRespingi())) {
            return Costanti.STATO_FLUSSO.get(getTipoFaseRespingi());
        }
        return "";
    }

    public String getMotivoRespingi() {
        return motivoRespingi;
    }

    public void setMotivoRespingi(String motivoRespingi) {
        this.motivoRespingi = motivoRespingi;
    }
}
