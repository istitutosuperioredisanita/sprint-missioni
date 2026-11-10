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


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.cnr.si.missioni.config.BaseEntity;
import it.cnr.si.missioni.util.Costanti;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * A user.
 */
@Entity @JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "ORDINE_MISSIONE_ANTICIPO")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_ANTICIPO", allocationSize = 0)
public class OrdineMissioneAnticipo extends BaseEntity {

    public final static String CMIS_PROPERTY_NAME_DOC_ANTICIPO = "Principale";
    public final static String CMIS_PROPERTY_NAME_TIPODOC_ANTICIPO = "Richiesta Anticipo";

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;

    @Size(min = 0, max = 256)
    @Column(name = "USER_NAME", length = 256, nullable = false)
    private String uid;

    @Column(name = "IMPORTO", length = 28, nullable = true)
    private BigDecimal importo;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE", length = 3, nullable = true)
    private String note;

    @Column(name = "DATA_RICHIESTA", nullable = false)
    private LocalDateTime dataRichiesta;

    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE", nullable = false)
    private OrdineMissione ordineMissione;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;

    @Size(min = 0, max = 100)
    @Column(name = "ID_FLUSSO", length = 100, nullable = true)
    private String idFlusso;

    @Size(min = 0, max = 3)
    @Column(name = "STATO_FLUSSO", length = 3, nullable = false)
    private String statoFlusso;

    // Dati del mandato: compilati dai validatori sull'ordine firmato e riportati nel rimborso
    @Size(min = 0, max = 5)
    @Column(name = "MODPAG", length = 5, nullable = true)
    private String modpag;

    @Size(min = 0, max = 1)
    @Column(name = "TIPO_PAGAMENTO", length = 1, nullable = true)
    private String tipoPagamento;

    @Column(name = "PG_BANCA", length = 4, nullable = true)
    private Integer pgBanca;

    @Size(min = 0, max = 34)
    @Column(name = "IBAN", length = 34, nullable = true)
    private String iban;

    @Size(min = 0, max = 1)
    @Column(name = "ANTICIPO_RICEVUTO", length = 1, nullable = true)
    private String anticipoRicevuto;

    @Column(name = "ANTICIPO_ANNO_MANDATO", length = 4, nullable = true)
    private Integer anticipoAnnoMandato;

    @Column(name = "ANTICIPO_NUMERO_MANDATO", length = 50, nullable = true)
    private Long anticipoNumeroMandato;

    @Column(name = "ANTICIPO_IMPORTO", length = 28, nullable = true)
    private BigDecimal anticipoImporto;

    // null: da compilare, INS: salvato in bozza, DEF: salvato in definitivo
    @Size(min = 0, max = 3)
    @Column(name = "STATO_MANDATO", length = 3, nullable = true)
    private String statoMandato;

    @Size(min = 0, max = 256)
    @Column(name = "UID_MANDATO", length = 256, nullable = true)
    private String uidMandato;

    @Column(name = "DATA_MANDATO", nullable = true)
    private LocalDateTime dataMandato;

    //	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		AutoPropria other = (AutoPropria) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//
    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public OrdineMissione getOrdineMissione() {
        return ordineMissione;
    }

    public void setOrdineMissione(OrdineMissione ordineMissione) {
        this.ordineMissione = ordineMissione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getDataRichiesta() {
        return dataRichiesta;
    }

    public void setDataRichiesta(LocalDateTime dataRichiesta) {
        this.dataRichiesta = dataRichiesta;
    }

    @Transient
    public String getDecodeStato() {
        if (!StringUtils.isEmpty(getStato())) {
            return Costanti.STATO.get(getStato());
        }
        return "";
    }

    @Transient
    public Boolean isAnticipoConfermato() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_CONFERMATO);
        }
        return false;
    }

    @Transient
    public Boolean isAnticipoInserito() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_INSERITO);
        }
        return false;
    }

    @Transient
    public String getFileName() {
        return "AnticipoOrdineMissione" + getId() + ".pdf";
    }


    public String getIdFlusso() {
        return idFlusso;
    }

    public void setIdFlusso(String idFlusso) {
        this.idFlusso = idFlusso;
    }

    public String getStatoFlusso() {
        return statoFlusso;
    }

    public void setStatoFlusso(String statoFlusso) {
        this.statoFlusso = statoFlusso;
    }

    public Boolean isStatoNonInviatoAlFlusso() {
        return !StringUtils.isEmpty(getStatoFlusso()) && getStatoFlusso().equals(Costanti.STATO_INSERITO);
    }

    public String getModpag() { return modpag; }
    public void setModpag(String modpag) { this.modpag = modpag; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public Integer getPgBanca() { return pgBanca; }
    public void setPgBanca(Integer pgBanca) { this.pgBanca = pgBanca; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getAnticipoRicevuto() { return anticipoRicevuto; }
    public void setAnticipoRicevuto(String anticipoRicevuto) { this.anticipoRicevuto = anticipoRicevuto; }

    public Integer getAnticipoAnnoMandato() { return anticipoAnnoMandato; }
    public void setAnticipoAnnoMandato(Integer anticipoAnnoMandato) { this.anticipoAnnoMandato = anticipoAnnoMandato; }

    public Long getAnticipoNumeroMandato() { return anticipoNumeroMandato; }
    public void setAnticipoNumeroMandato(Long anticipoNumeroMandato) { this.anticipoNumeroMandato = anticipoNumeroMandato; }

    public BigDecimal getAnticipoImporto() { return anticipoImporto; }
    public void setAnticipoImporto(BigDecimal anticipoImporto) { this.anticipoImporto = anticipoImporto; }

    public String getStatoMandato() { return statoMandato; }
    public void setStatoMandato(String statoMandato) { this.statoMandato = statoMandato; }

    public String getUidMandato() { return uidMandato; }
    public void setUidMandato(String uidMandato) { this.uidMandato = uidMandato; }

    public LocalDateTime getDataMandato() { return dataMandato; }
    public void setDataMandato(LocalDateTime dataMandato) { this.dataMandato = dataMandato; }

    @Transient
    public Boolean isMandatoDefinitivo() {
        return Costanti.STATO_DEFINITIVO.equals(getStatoMandato());
    }

    // anticipo effettuato: mandato salvato in definitivo con anticipo ricevuto
    @Transient
    public Boolean isAnticipoEffettuato() {
        return isMandatoDefinitivo() && "S".equals(getAnticipoRicevuto());
    }

    @Override
    public String toString() {
        return "OrdineMissioneAnticipo{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", importo=" + importo +
                ", note='" + note + '\'' +
                ", dataRichiesta=" + dataRichiesta +
                ", stato='" + stato + '\'' +
                ", idFlusso='" + idFlusso + '\'' +
                ", statoFlusso='" + statoFlusso + '\'' +
                ", statoMandato='" + statoMandato + '\'' +
                '}';
    }
}
