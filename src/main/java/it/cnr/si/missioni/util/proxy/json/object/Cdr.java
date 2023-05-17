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

package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;

public class Cdr extends RestServiceBean implements Serializable {
    private String cd_centro_responsabilita;
    private String cd_unita_organizzativa;
    private String ds_cdr;
    private String cd_responsabile;

    public String getCd_responsabile() {
        return cd_responsabile;
    }

    public void setCd_responsabile(String cd_responsabile) {
        this.cd_responsabile = cd_responsabile;
    }

    public String getCd_centro_responsabilita() {
        return cd_centro_responsabilita;
    }

    public void setCd_centro_responsabilita(String cd_centro_responsabilita) {
        this.cd_centro_responsabilita = cd_centro_responsabilita;
    }

    public String getCd_unita_organizzativa() {
        return cd_unita_organizzativa;
    }

    public void setCd_unita_organizzativa(String cd_unita_organizzativa) {
        this.cd_unita_organizzativa = cd_unita_organizzativa;
    }

    public String getDs_cdr() {
        return ds_cdr;
    }

    public void setDs_cdr(String ds_cdr) {
        this.ds_cdr = ds_cdr;
    }

}
