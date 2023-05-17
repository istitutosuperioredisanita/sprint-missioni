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


import it.cnr.si.missioni.util.Utility;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A user.
 */
@Entity
@Table(name = "DATI_SEDE")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_DATI_SEDE", allocationSize = 0)
public class DatiSede extends OggettoBulkXmlTransient implements Serializable {

    @Column(name = "DATA_INIZIO", nullable = false)
    public LocalDate dataInizio;
    @Column(name = "DATA_FINE", nullable = true)
    public LocalDate dataFine;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    //    @JsonIgnore
    @Size(min = 0, max = 30)
    @Column(name = "CODICE_SEDE", length = 30, nullable = false)
    private String codiceSede;
    @Size(min = 0, max = 200)
    @Column(name = "RESPONSABILE", length = 200, nullable = true)
    private String responsabile;

    @Size(min = 0, max = 1)
    @Column(name = "RESPONSABILE_SOLO_ITALIA", length = 1, nullable = true)
    private String responsabileSoloItalia;

    @Size(min = 0, max = 30)
    @Column(name = "SEDE_RESP_ESTERO", length = 30, nullable = true)
    private String sedeRespEstero;

    @Size(min = 0, max = 1)
    @Column(name = "DELEGA_SPESA", length = 1, nullable = true)
    private String delegaSpesa;

    @Size(min = 0, max = 1)
    @Column(name = "TIPO_MAIL_DOPO_ORDINE", length = 1, nullable = true)
    private String tipoMailDopoOrdine;

    @Size(min = 0, max = 200)
    @Column(name = "MAIL_DOPO_ORDINE", length = 200, nullable = true)
    private String mailDopoOrdine;

    public String getTipoMailDopoOrdine() {
        return tipoMailDopoOrdine;
    }

    public void setTipoMailDopoOrdine(String tipoMailDopoOrdine) {
        this.tipoMailDopoOrdine = tipoMailDopoOrdine;
    }

    public String getMailDopoOrdine() {
        return mailDopoOrdine;
    }

    public void setMailDopoOrdine(String mailDopoOrdine) {
        this.mailDopoOrdine = mailDopoOrdine;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodice_sede() {
        return codiceSede;
    }

    public void setCodiceSede(String codiceSede) {
        this.codiceSede = codiceSede;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String responsabile) {
        this.responsabile = responsabile;
    }

    public String getResponsabileSoloItalia() {
        return responsabileSoloItalia;
    }

    public void setResponsabileSoloItalia(String responsabileSoloItalia) {
        this.responsabileSoloItalia = responsabileSoloItalia;
    }

    public String getSedeRespEstero() {
        return sedeRespEstero;
    }

    public void setSedeRespEstero(String sedeRespEstero) {
        this.sedeRespEstero = sedeRespEstero;
    }

    public String getDelegaSpesa() {
        return delegaSpesa;
    }

    public boolean isResponsabileEstero() {
        return Utility.nvl(getResponsabileSoloItalia(), "N").equals("N");
    }

    public boolean isDelegaSpesa() {
        return Utility.nvl(getDelegaSpesa(), "N").equals("S");
    }

    public void setDelegaSpesa(String delegaSpesa) {
        this.delegaSpesa = delegaSpesa;
    }
}
