/*
 * Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.domain.custom.persistence;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cnr.si.missioni.config.BaseEntity;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import org.springframework.util.StringUtils;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A user.
 */
@Entity
@Table(name = "ORDINE_MISSIONE_DETTAGLI")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_ORDINE_DETTAGLI", allocationSize = 0)
public class OrdineMissioneDettagli extends BaseEntity {

    public final static String CMIS_PROPERTY_ID_DETTAGLIO_ORDINE = "missioni_ordine_dettaglio:id",
            CMIS_PROPERTY_RIGA_DETTAGLIO_ORDINE_MISSIONE = "missioni_ordine_dettaglio:riga",
            CMIS_PROPERTY_CD_TIPO_SPESA_DETTAGLIO_ORDINE_MISSIONE = "missioni_ordine_dettaglio:cdTiSpesa",
            CMIS_PROPERTY_DS_TIPO_SPESA_DETTAGLIO_ORDINE_MISSIONE = "missioni_ordine_dettaglio:dsTiSpesa",
            CMIS_PROPERTY_MAIN = "F:missioni_ordine_dettaglio:main";
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Size(min = 0, max = 256)
    @Column(name = "USER_NAME", length = 256, nullable = false)
    private String uid;
    @Column(name = "RIGA", length = 50, nullable = false)
    private Long riga;

    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE", nullable = false)
    @JsonBackReference // Indica che questo è il lato "indietro" della relazione. Jackson ignorerà questo riferimento durante la serializzazione per prevenire la ricorsione.
    private OrdineMissione ordineMissione;
    
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
    @Size(min = 0, max = 10)
    @Column(name = "CD_DIVISA", length = 10, nullable = true)
    private String cdDivisa;
    @Column(name = "IMPORTO_DIVISA", length = 28, nullable = true)
    private BigDecimal importoDivisa;
    @Column(name = "IMPORTO_EURO", length = 28, nullable = true)
    private BigDecimal importoEuro;
    @Column(name = "CAMBIO", length = 19, nullable = true)
    private BigDecimal cambio;
    @Size(min = 0, max = 1)
    @Column(name = "TI_CD_TI_SPESA", length = 1, nullable = true)
    private String tiCdTiSpesa;

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

    public OrdineMissione getOrdineMissione() {
        return ordineMissione;
    }

    public void setOrdineMissione(OrdineMissione ordineMissione) {
        this.ordineMissione = ordineMissione;
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

    public String getTiCdTiSpesa() {
        return tiCdTiSpesa;
    }

    public void setTiCdTiSpesa(String tiCdTiSpesa) {
        this.tiCdTiSpesa = tiCdTiSpesa;
    }


    public Boolean isModificaSoloDatiFinanziari(OrdineMissioneDettagli other) {
        if (cdTiSpesa == null) {
            if (other.cdTiSpesa != null)
                return false;
        } else if (!cdTiSpesa.equals(other.cdTiSpesa))
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
        if (importoEuro == null) {
            if (other.importoEuro != null)
                return false;
        } else if (importoEuro.compareTo(other.importoEuro) != 0)
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
        return "OrdineMissioneDettagli{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", riga=" + riga +
                ", stato='" + stato + '\'' +
                ", tiSpesaDiaria='" + tiSpesaDiaria + '\'' +
                ", cdTiSpesa='" + cdTiSpesa + '\'' +
                ", dsTiSpesa='" + dsTiSpesa + '\'' +
                ", dsSpesa='" + dsSpesa + '\'' +
                ", cdDivisa='" + cdDivisa + '\'' +
                ", importoDivisa=" + importoDivisa +
                ", importoEuro=" + importoEuro +
                ", cambio=" + cambio +
                ", tiCdTiSpesa='" + tiCdTiSpesa + '\'' +
                "} ";
    }
}