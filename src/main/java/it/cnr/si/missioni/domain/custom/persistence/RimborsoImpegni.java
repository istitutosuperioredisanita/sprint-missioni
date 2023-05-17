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

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A user.
 */
@Entity
@Table(name = "RIMBORSO_IMPEGNI")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_RIMBORSO_IMPEGNI", allocationSize = 0)
public class RimborsoImpegni extends OggettoBulkXmlTransient implements Serializable {

    @Size(min = 0, max = 30)
    @Column(name = "CD_CDS_OBBLIGAZIONE", length = 30, nullable = false)
    public String cdCdsObbligazione;
    @Column(name = "ESERCIZIO_OBBLIGAZIONE", length = 4, nullable = false)
    public Integer esercizioObbligazione;
    @Column(name = "PG_OBBLIGAZIONE", length = 50, nullable = false)
    public Long pgObbligazione;
    @Column(name = "ESERCIZIO_ORIGINALE_OBBLIGAZIONE", length = 4, nullable = false)
    public Integer esercizioOriginaleObbligazione;
    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    public String stato;
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
    @ManyToOne
    @JoinColumn(name = "ID_RIMBORSO_MISSIONE", nullable = false)
    private RimborsoMissione rimborsoMissione;

    public RimborsoImpegni(Long id) {
        super();
        this.setId(id);
    }

    public RimborsoImpegni() {
        super();
    }

    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public RimborsoMissione getRimborsoMissione() {
        return rimborsoMissione;
    }

    public void setRimborsoMissione(RimborsoMissione rimborsoMissione) {
        this.rimborsoMissione = rimborsoMissione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
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

    @Override
    public String toString() {
        return "RimborsoImpegni{" +
                "id=" + id +
                ", cdCdsObbligazione='" + cdCdsObbligazione + '\'' +
                ", esercizioObbligazione=" + esercizioObbligazione +
                ", pgObbligazione=" + pgObbligazione +
                ", esercizioOriginaleObbligazione=" + esercizioOriginaleObbligazione +
                ", stato='" + stato + '\'' +
                ", voce='" + voce + '\'' +
                ", dsVoce='" + dsVoce + '\'' +
                '}';
    }
}
