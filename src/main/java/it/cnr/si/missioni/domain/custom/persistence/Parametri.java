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
@Table(name = "PARAMETRI")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_PARAMETRI", allocationSize = 0)
public class Parametri extends OggettoBulkXmlTransient implements Serializable {

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;

    @Size(min = 0, max = 200)
    @Column(name = "RESPONSABILE_CUG", length = 200, nullable = true)
    private String responsabileCug;

    @Size(min = 0, max = 200)
    @Column(name = "PRESIDENTE", length = 200, nullable = true)
    private String presidente;

    @Size(min = 0, max = 200)
    @Column(name = "DIPENDENTE_CDA", length = 200, nullable = true)
    private String dipendenteCda;

    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResponsabileCug() {
        return responsabileCug;
    }

    public void setResponsabileCug(String responsabileCug) {
        this.responsabileCug = responsabileCug;
    }

    public String getPresidente() {
        return presidente;
    }

    public void setPresidente(String presidente) {
        this.presidente = presidente;
    }

    public String getDipendenteCda() {
        return dipendenteCda;
    }

    public void setDipendenteCda(String dipendenteCda) {
        this.dipendenteCda = dipendenteCda;
    }
}
