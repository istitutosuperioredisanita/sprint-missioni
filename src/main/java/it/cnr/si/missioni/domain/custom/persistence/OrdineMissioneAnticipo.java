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
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A user.
 */
@Entity
@Table(name = "ORDINE_MISSIONE_ANTICIPO")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_ANTICIPO", allocationSize = 0)
public class OrdineMissioneAnticipo extends OggettoBulkXmlTransient implements Serializable {

    public final static String CMIS_PROPERTY_NAME_DOC_ANTICIPO = "Principale";
    public final static String CMIS_PROPERTY_NAME_TIPODOC_ANTICIPO = "Richiesta Anticipo";

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;

    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    private String uid;

    @Column(name = "IMPORTO", length = 28, nullable = true)
    private BigDecimal importo;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE", length = 3, nullable = true)
    private String note;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_RICHIESTA", nullable = false)
    private Date dataRichiesta;

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

    public Date getDataRichiesta() {
        return dataRichiesta;
    }

    public void setDataRichiesta(Date dataRichiesta) {
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
                '}';
    }
}
