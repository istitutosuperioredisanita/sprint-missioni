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

package it.cnr.si.missioni.util.proxy.json.object.sigla;

import it.cnr.si.missioni.util.DateUtils;

import java.io.Serializable;

public class Context implements Serializable {
    private int esercizio = DateUtils.getCurrentYear();
    private String cd_unita_organizzativa;
    private String cd_cds;
    private String cd_cdr;

    public String getCd_unita_organizzativa() {
        return cd_unita_organizzativa;
    }

    public void setCd_unita_organizzativa(String cd_unita_organizzativa) {
        this.cd_unita_organizzativa = cd_unita_organizzativa;
    }

    public String getCd_cds() {
        return cd_cds;
    }

    public void setCd_cds(String cd_cds) {
        this.cd_cds = cd_cds;
    }

    public String getCd_cdr() {
        return cd_cdr;
    }

    public void setCd_cdr(String cd_cdr) {
        this.cd_cdr = cd_cdr;
    }

    public int getEsercizio() {
        return esercizio;
    }

    public void setEsercizio(int esercizio) {
        this.esercizio = esercizio;
    }
}
