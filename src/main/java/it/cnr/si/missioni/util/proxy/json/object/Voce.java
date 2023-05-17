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

public class Voce extends RestServiceBean implements Serializable {
    private String cd_elemento_voce;
    private Integer esercizio;
    private String ds_elemento_voce;
    private Boolean fl_solo_residuo;

    public String getCd_elemento_voce() {
        return cd_elemento_voce;
    }

    public void setCd_elemento_voce(String cd_elemento_voce) {
        this.cd_elemento_voce = cd_elemento_voce;
    }

    public String getDs_elemento_voce() {
        return ds_elemento_voce;
    }

    public void setDs_elemento_voce(String ds_elemento_voce) {
        this.ds_elemento_voce = ds_elemento_voce;
    }

    public Integer getEsercizio() {
        return esercizio;
    }

    public void setEsercizio(Integer esercizio) {
        this.esercizio = esercizio;
    }

    public Boolean getFl_solo_residuo() {
        return fl_solo_residuo;
    }

    public void setFl_solo_residuo(Boolean fl_solo_residuo) {
        this.fl_solo_residuo = fl_solo_residuo;
    }
}
