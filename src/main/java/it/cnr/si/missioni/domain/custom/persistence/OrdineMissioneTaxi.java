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
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A user.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDINE_MISSIONE_TAXI")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_TAXI", allocationSize = 0)
public class OrdineMissioneTaxi extends OggettoBulkXmlTransient implements Serializable {

    public final static String CMIS_PROPERTY_NAME_DOC_TAXI = "Principale";
    public final static String CMIS_PROPERTY_NAME_TIPODOC_TAXI = "Richiesta Taxi";

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE", nullable = false)
    private OrdineMissione ordineMissione;

    @Size(min = 0, max = 1)
    @Column(name = "MANCANZA_ASS_MEZZI", length = 1, nullable = true)
    public String mancanzaAssMezzi;

    @Size(min = 0, max = 1)
    @Column(name = "MANCANZA_MEZZI", length = 1, nullable = true)
    public String mancanzaMezzi;

    @Size(min = 0, max = 1)
    @Column(name = "TRASPORTO_MATERIALI", length = 1, nullable = true)
    public String trasportoMateriali;

    @Size(min = 0, max = 1)
    @Column(name = "MOTIVI_HANDICAP", length = 1, nullable = true)
    public String motiviHandicap;

    @Size(min = 0, max = 1000)
    @Column(name = "UTILIZZO_ALTRI_MOTIVI", length = 1000, nullable = true)
    public String utilizzoAltriMotivi;

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
    private List<SpostamentiTaxi> listSpostamenti;


    public Boolean isStatoNonInviatoAlFlusso() {
        return !StringUtils.isEmpty(getStatoFlusso()) && getStatoFlusso().equals(Costanti.STATO_INSERITO);
    }

    @Transient
    public Boolean isRichiestaTaxiInserito() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_INSERITO);
        }
        return false;
    }

    @Override
    public String toString() {
        return "OrdineMissioneAutoPropria{" +
                "id=" + id +
                ", ordineMissione=" + ordineMissione +
                ", mancanzaAssMezzi='" + mancanzaAssMezzi + '\'' +
                ", mancanzaMezzi='" + mancanzaMezzi + '\'' +
                ", trasportoMateriali='" + trasportoMateriali + '\'' +
                ", motiviHandicap='" + motiviHandicap + '\'' +
                ", utilizzoAltriMotivi='" + utilizzoAltriMotivi + '\'' +
                ", stato='" + stato + '\'' +
                ", idFlusso='" + idFlusso + '\'' +
                ", statoFlusso='" + statoFlusso + '\'' +
                '}';
    }


    public String getFileName() {
        return "TaxiOrdineMissione" + getId() + ".pdf";
    }

}
