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
import java.io.Serializable;
import java.util.List;

/**
 * A user.
 */

@Entity
@Table(name = "ORDINE_MISSIONE_AUTO_NOLEGGIO")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_AUTO_NOLEGGIO", allocationSize = 0)
public class OrdineMissioneAutoNoleggio extends OggettoBulkXmlTransient implements Serializable {

    public final static String CMIS_PROPERTY_NAME_DOC_AUTO_NOLEGGIO = "Principale";
    public final static String CMIS_PROPERTY_NAME_TIPODOC_AUTO_NOLEGGIO = "Richiesta Auto A Noleggio";

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE", nullable = false)
    private OrdineMissione ordineMissione;

    @Size(min = 0, max = 1)
    @Column(name = "ESIGENZE_SERVIZIO", length = 1, nullable = true)
    public String esigenzeServizio;

    @Size(min = 0, max = 1)
    @Column(name = "MOTIVATA_ECCEZIONALITA", length = 1, nullable = true)
    public String motivataEccezionalita;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE", length = 1000, nullable = true)
    public String note;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;

    @Size(min = 0, max = 100)
    @Column(name = "ID_FLUSSO", length = 100, nullable = true)
    private String idFlusso;

    @Size(min = 0, max = 3)
    @Column(name = "STATO_FLUSSO", length = 3, nullable = false)
    private String statoFlusso;

    @Transient
    private List<SpostamentiAutoNoleggio> listSpostamenti;

    public List<SpostamentiAutoNoleggio> getListSpostamenti() {
        return listSpostamenti;
    }

    public void setListSpostamenti(List<SpostamentiAutoNoleggio> listSpostamenti) {
        this.listSpostamenti = listSpostamenti;
    }

    public Boolean isStatoNonInviatoAlFlusso() {
        return !StringUtils.isEmpty(getStatoFlusso()) && getStatoFlusso().equals(Costanti.STATO_INSERITO);
    }

    @Transient
    public Boolean isRichiestaAutoNoleggioInserito() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_INSERITO);
        }
        return false;
    }

    @Override
    public String toString() {
        return "OrdineMissioneAutoNoleggio{" +
                "id=" + id +
                ", ordineMissione=" + ordineMissione +
                ", esigenzeServizio='" + esigenzeServizio + '\'' +
                ", motivataEccezionalita='" + motivataEccezionalita + '\'' +
                ", note='" + note + '\'' +
                ", stato='" + stato + '\'' +
                ", idFlusso='" + idFlusso + '\'' +
                ", statoFlusso='" + statoFlusso + '\'' +
                '}';
    }


    public String getFileName() {
        return "AutoNoleggioOrdineMissione" + getId() + ".pdf";
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdineMissione getOrdineMissione() {
        return ordineMissione;
    }

    public void setOrdineMissione(OrdineMissione ordineMissione) {
        this.ordineMissione = ordineMissione;
    }


    public String getEsigenzeServizio() {
        return esigenzeServizio;
    }

    public void setEsigenzeServizio(String esigenzeServizio) {
        this.esigenzeServizio = esigenzeServizio;
    }

    public String getMotivataEccezionalita() {
        return motivataEccezionalita;
    }

    public void setMotivataEccezionalita(String motivataEccezionalita) {
        this.motivataEccezionalita = motivataEccezionalita;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
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
}
