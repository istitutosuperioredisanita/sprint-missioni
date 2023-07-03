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
import it.cnr.si.missioni.util.Utility;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A user.
 */
@Entity
@Table(name = "RIMBORSO_MISSIONE_DETTAGLI")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_RIMBORSO_DETTAGLI", allocationSize = 0)
public class RimborsoMissioneDettagli extends OggettoBulkXmlTransient implements Serializable {

    public final static String CMIS_PROPERTY_ID_DETTAGLIO_RIMBORSO = "missioni_rimborso_dettaglio:id",
            CMIS_PROPERTY_RIGA_DETTAGLIO_RIMBORSO_MISSIONE = "missioni_rimborso_dettaglio:riga",
            CMIS_PROPERTY_CD_TIPO_SPESA_DETTAGLIO_RIMBORSO_MISSIONE = "missioni_rimborso_dettaglio:cdTiSpesa",
            CMIS_PROPERTY_DS_TIPO_SPESA_DETTAGLIO_RIMBORSO_MISSIONE = "missioni_rimborso_dettaglio:dsTiSpesa",
            CMIS_PROPERTY_DATA_SPESA_DETTAGLIO_RIMBORSO_MISSIONE = "missioni_rimborso_dettaglio:dataSpesa",
            CMIS_PROPERTY_MAIN = "F:missioni_rimborso_dettaglio:main";
    @Size(min = 0, max = 30)
    @Column(name = "CD_CDS_OBBLIGAZIONE", length = 30, nullable = true)
    public String cdCdsObbligazione;
    @Column(name = "ESERCIZIO_OBBLIGAZIONE", length = 4, nullable = true)
    public Integer esercizioObbligazione;
    @Column(name = "PG_OBBLIGAZIONE", length = 50, nullable = true)
    public Long pgObbligazione;
    @Column(name = "ESERCIZIO_ORIGINALE_OBBLIGAZIONE", length = 4, nullable = true)
    public Integer esercizioOriginaleObbligazione;
    @Size(min = 0, max = 28)
    @Column(name = "VOCE", length = 28, nullable = true)
    public String voce;
    @Size(min = 0, max = 500)
    @Column(name = "DS_VOCE", length = 500, nullable = true)
    public String dsVoce;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Size(min = 0, max = 256)
    @Column(name = "USER_NAME", length = 256, nullable = false)
    private String uid;
    @Size(min = 0, max = 1000)
    @Column(name = "NOTE", length = 1000, nullable = true)
    private String note;
    @Size(min = 0, max = 100)
    @Column(name = "DS_NO_GIUSTIFICATIVO", length = 100, nullable = true)
    private String dsNoGiustificativo;
    @Size(min = 0, max = 200)
    @Column(name = "LOCALITA_SPOSTAMENTO", length = 200, nullable = true)
    private String localitaSpostamento;
    @Column(name = "RIGA", length = 50, nullable = false)
    private Long riga;
    @Column(name = "DATA_SPESA", nullable = false)
    private LocalDate dataSpesa;
    @ManyToOne
    @JoinColumn(name = "ID_RIMBORSO_MISSIONE", nullable = false)
    private RimborsoMissione rimborsoMissione;
    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;
    @Size(min = 0, max = 1)
    @Column(name = "TI_SPESA_DIARIA", length = 1, nullable = false)
    private String tiSpesaDiaria;
    @Size(min = 0, max = 20)
    @Column(name = "CD_TI_SPESA", length = 20, nullable = true)
    private String cdTiSpesa;
    @Size(min = 0, max = 250)
    @Column(name = "DS_TI_SPESA", length = 250, nullable = true)
    private String dsTiSpesa;
    @Size(min = 0, max = 250)
    @Column(name = "DS_SPESA", length = 250, nullable = true)
    private String dsSpesa;
    @Size(min = 0, max = 20)
    @Column(name = "CD_TI_PASTO", length = 20, nullable = true)
    private String cdTiPasto;
    @Column(name = "KM_PERCORSI", length = 10, nullable = true)
    private Long kmPercorsi;
    @Column(name = "FL_SPESA_ANTICIPATA", length = 1, nullable = true)
    private String flSpesaAnticipata;
    @Size(min = 0, max = 10)
    @Column(name = "CD_DIVISA", length = 10, nullable = true)
    private String cdDivisa;
    @Column(name = "IMPORTO_DIVISA", length = 28, nullable = true)
    private BigDecimal importoDivisa;
    @Column(name = "IMPORTO_EURO", length = 28, nullable = true)
    private BigDecimal importoEuro;
    @Column(name = "CAMBIO", length = 19, nullable = true)
    private BigDecimal cambio;
    @Column(name = "GIUSTIFICATIVO", length = 1, nullable = true)
    private String giustificativo;
    @Size(min = 0, max = 1)
    @Column(name = "TI_CD_TI_SPESA", length = 1, nullable = true)
    private String tiCdTiSpesa;
    @Transient
    private String decodeSpesaAnticipata;

    @Column(name = "ID_RIMBORSO_IMPEGNI", length = 20)
    private Long idRimborsoImpegni;

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

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public Long getRiga() {
        return riga;
    }

    public void setRiga(Long riga) {
        this.riga = riga;
    }

    public LocalDate getDataSpesa() {
        return dataSpesa;
    }

    public void setDataSpesa(LocalDate dataSpesa) {
        this.dataSpesa = dataSpesa;
    }

    public RimborsoMissione getRimborsoMissione() {
        return rimborsoMissione;
    }

    public void setRimborsoMissione(RimborsoMissione rimborsoMissione) {
        this.rimborsoMissione = rimborsoMissione;
    }

    public String getTiSpesaDiaria() {
        return tiSpesaDiaria;
    }

    public void setTiSpesaDiaria(String tiSpesaDiaria) {
        this.tiSpesaDiaria = tiSpesaDiaria;
    }

    public String getCdTiSpesa() {
        return cdTiSpesa;
    }

    public void setCdTiSpesa(String cdTiSpesa) {
        this.cdTiSpesa = cdTiSpesa;
    }

    public String getDsTiSpesa() {
        return dsTiSpesa;
    }

    public void setDsTiSpesa(String dsTiSpesa) {
        this.dsTiSpesa = dsTiSpesa;
    }

    public String getDsSpesa() {
        return dsSpesa;
    }

    public void setDsSpesa(String dsSpesa) {
        this.dsSpesa = dsSpesa;
    }

    public String getCdTiPasto() {
        return cdTiPasto;
    }

    public void setCdTiPasto(String cdTiPasto) {
        this.cdTiPasto = cdTiPasto;
    }

    public Long getKmPercorsi() {
        return kmPercorsi;
    }

    public void setKmPercorsi(Long kmPercorsi) {
        this.kmPercorsi = kmPercorsi;
    }

    public String getFlSpesaAnticipata() {
        return flSpesaAnticipata;
    }

    public void setFlSpesaAnticipata(String flSpesaAnticipata) {
        this.flSpesaAnticipata = flSpesaAnticipata;
    }

    public String getCdDivisa() {
        return cdDivisa;
    }

    public void setCdDivisa(String cdDivisa) {
        this.cdDivisa = cdDivisa;
    }

    public BigDecimal getImportoDivisa() {
        return importoDivisa;
    }

    public void setImportoDivisa(BigDecimal importoDivisa) {
        this.importoDivisa = importoDivisa;
    }

    public BigDecimal getImportoEuro() {
        return importoEuro;
    }

    public void setImportoEuro(BigDecimal importoEuro) {
        this.importoEuro = importoEuro;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    @Transient
    public String constructCMISNomeFile() {
        StringBuffer nomeFile = new StringBuffer();
        nomeFile = nomeFile.append(Utility.lpad(this.getRiga().toString(), 4, '0'));
        return nomeFile.toString();
    }

    @Transient
    public String getDecodeSpesaAnticipata() {
        if (!StringUtils.isEmpty(getFlSpesaAnticipata())) {
            return Costanti.SI_NO.get(getFlSpesaAnticipata());
        } else {
            return Costanti.SI_NO.get("N");
        }
    }

    @Transient
    public Boolean isSpesaAnticipata() {
        return Utility.nvl(getFlSpesaAnticipata(), "N").equals("S");
    }

    public String getGiustificativo() {
        return giustificativo;
    }

    public void setGiustificativo(String giustificativo) {
        this.giustificativo = giustificativo;
    }

    @Transient
    public Boolean isGiustificativoObbligatorio() {
        if (!StringUtils.isEmpty(getGiustificativo()) && getGiustificativo().equals("N")) {
            return false;
        } else {
            return StringUtils.isEmpty(getTiCdTiSpesa()) || !getTiCdTiSpesa().equals("R");
        }
    }

    @Transient
    public Boolean isDettaglioPasto() {
        return !StringUtils.isEmpty(getCdTiPasto());
    }

    @Transient
    public Boolean isDettaglioIndennitaKm() {
        return !StringUtils.isEmpty(getKmPercorsi());
    }

    public String getDsNoGiustificativo() {
        return dsNoGiustificativo;
    }

    public void setDsNoGiustificativo(String dsNoGiustificativo) {
        this.dsNoGiustificativo = dsNoGiustificativo;
    }

    public String getLocalitaSpostamento() {
        return localitaSpostamento;
    }

    public void setLocalitaSpostamento(String localitaSpostamento) {
        this.localitaSpostamento = localitaSpostamento;
    }

    public String getTiCdTiSpesa() {
        return tiCdTiSpesa;
    }

    public void setTiCdTiSpesa(String tiCdTiSpesa) {
        this.tiCdTiSpesa = tiCdTiSpesa;
    }

    public String getCdCdsObbligazione() {
        return cdCdsObbligazione;
    }

    public void setCdCdsObbligazione(String cdCdsObbligazione) {
        this.cdCdsObbligazione = cdCdsObbligazione;
    }

    public Integer getEsercizioObbligazione() {
        return esercizioObbligazione;
    }

    public void setEsercizioObbligazione(Integer esercizioObbligazione) {
        this.esercizioObbligazione = esercizioObbligazione;
    }

    public Long getPgObbligazione() {
        return pgObbligazione;
    }

    public void setPgObbligazione(Long pgObbligazione) {
        this.pgObbligazione = pgObbligazione;
    }

    public Integer getEsercizioOriginaleObbligazione() {
        return esercizioOriginaleObbligazione;
    }

    public void setEsercizioOriginaleObbligazione(Integer esercizioOriginaleObbligazione) {
        this.esercizioOriginaleObbligazione = esercizioOriginaleObbligazione;
    }

    public String getVoce() {
        return voce;
    }

    public void setVoce(String voce) {
        this.voce = voce;
    }

    public String getDsVoce() {
        return dsVoce;
    }

    public void setDsVoce(String dsVoce) {
        this.dsVoce = dsVoce;
    }

    public Long getIdRimborsoImpegni() {
        return idRimborsoImpegni;
    }

    public void setIdRimborsoImpegni(Long idRimborsoImpegni) {
        this.idRimborsoImpegni = idRimborsoImpegni;
    }

    public Boolean isModificaSoloDatiFinanziari(RimborsoMissioneDettagli other) {
        if (cdTiPasto == null) {
            if (other.cdTiPasto != null)
                return false;
        } else if (!cdTiPasto.equals(other.cdTiPasto))
            return false;
        if (cdTiSpesa == null) {
            if (other.cdTiSpesa != null)
                return false;
        } else if (!cdTiSpesa.equals(other.cdTiSpesa))
            return false;
        if (dataSpesa == null) {
            if (other.dataSpesa != null)
                return false;
        } else if (!dataSpesa.equals(other.dataSpesa))
            return false;
        if (dsSpesa == null) {
            if (other.dsSpesa != null)
                return false;
        } else if (!dsSpesa.equals(other.dsSpesa))
            return false;
        if (dsTiSpesa == null) {
            if (other.dsTiSpesa != null)
                return false;
        } else if (!dsTiSpesa.equals(other.dsTiSpesa))
            return false;
        if (flSpesaAnticipata == null) {
            if (other.flSpesaAnticipata != null)
                return false;
        } else if (!flSpesaAnticipata.equals(other.flSpesaAnticipata))
            return false;
        if (importoEuro == null) {
            if (other.importoEuro != null)
                return false;
        } else if (importoEuro.compareTo(other.importoEuro) != 0)
            return false;
        if (kmPercorsi == null) {
            if (other.kmPercorsi != null)
                return false;
        } else if (!kmPercorsi.equals(other.kmPercorsi))
            return false;
        if (localitaSpostamento == null) {
            if (other.localitaSpostamento != null)
                return false;
        } else if (!localitaSpostamento.equals(other.localitaSpostamento))
            return false;
        if (note == null) {
            if (other.note != null)
                return false;
        } else if (!note.equals(other.note))
            return false;
        if (tiCdTiSpesa == null) {
            if (other.tiCdTiSpesa != null)
                return false;
        } else if (!tiCdTiSpesa.equals(other.tiCdTiSpesa))
            return false;
        if (tiSpesaDiaria == null) {
            return other.tiSpesaDiaria == null;
        } else return tiSpesaDiaria.equals(other.tiSpesaDiaria);
    }

    @Override
    public String toString() {
        return "RimborsoMissioneDettagli{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", note='" + note + '\'' +
                ", dsNoGiustificativo='" + dsNoGiustificativo + '\'' +
                ", localitaSpostamento='" + localitaSpostamento + '\'' +
                ", riga=" + riga +
                ", dataSpesa=" + dataSpesa +
                ", stato='" + stato + '\'' +
                ", tiSpesaDiaria='" + tiSpesaDiaria + '\'' +
                ", cdTiSpesa='" + cdTiSpesa + '\'' +
                ", dsTiSpesa='" + dsTiSpesa + '\'' +
                ", dsSpesa='" + dsSpesa + '\'' +
                ", cdTiPasto='" + cdTiPasto + '\'' +
                ", kmPercorsi=" + kmPercorsi +
                ", flSpesaAnticipata='" + flSpesaAnticipata + '\'' +
                ", cdDivisa='" + cdDivisa + '\'' +
                ", importoDivisa=" + importoDivisa +
                ", importoEuro=" + importoEuro +
                ", cambio=" + cambio +
                ", giustificativo='" + giustificativo + '\'' +
                ", tiCdTiSpesa='" + tiCdTiSpesa + '\'' +
                "} ";
    }
}
