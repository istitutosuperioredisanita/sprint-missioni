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
@Table(name = "SPOSTAMENTI_AUTO_PROPRIA")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_SPOSTAMENTI", allocationSize = 0)
public class SpostamentiAutoPropria extends OggettoBulkXmlTransient implements Serializable {

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;

    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    private String uid;

    @Size(min = 0, max = 256)
    @Column(name = "PERCORSO_DA", length = 256, nullable = false)
    private String percorsoDa;

    @Size(min = 0, max = 256)
    @Column(name = "PERCORSO_A", length = 256, nullable = false)
    private String percorsoA;

    @Column(name = "RIGA", length = 50, nullable = false)
    private Long riga;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;

    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE_AUTO_PROPRIA", nullable = false)
    private OrdineMissioneAutoPropria ordineMissioneAutoPropria;

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

    public String getPercorsoDa() {
        return percorsoDa;
    }

    public void setPercorsoDa(String percorsoDa) {
        this.percorsoDa = percorsoDa;
    }

    public String getPercorsoA() {
        return percorsoA;
    }

    public void setPercorsoA(String percorsoA) {
        this.percorsoA = percorsoA;
    }

    public Long getRiga() {
        return riga;
    }

    public void setRiga(Long riga) {
        this.riga = riga;
    }

    public OrdineMissioneAutoPropria getOrdineMissioneAutoPropria() {
        return ordineMissioneAutoPropria;
    }

    public void setOrdineMissioneAutoPropria(
            OrdineMissioneAutoPropria ordineMissioneAutoPropria) {
        this.ordineMissioneAutoPropria = ordineMissioneAutoPropria;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {
        return "SpostamentiAutoPropria{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", percorsoDa='" + percorsoDa + '\'' +
                ", percorsoA='" + percorsoA + '\'' +
                ", riga=" + riga +
                ", stato='" + stato + '\'' +
                '}';
    }
}
