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

public class Inquadramento extends RestServiceBean implements Serializable {
    private Object dt_ini_validita;
    private java.lang.String cd_tipo_rapporto;
    private java.lang.String ds_inquadramento;
    private java.lang.Integer pg_rif_inquadramento;
    private java.lang.Integer cd_anag;
    private Object dt_fin_validita;

    public Object getDt_ini_validita() {
        return dt_ini_validita;
    }

    public void setDt_ini_validita(Object dt_ini_validita) {
        this.dt_ini_validita = dt_ini_validita;
    }

    public java.lang.String getDs_inquadramento() {
        return ds_inquadramento;
    }

    public void setDs_inquadramento(java.lang.String ds_inquadramento) {
        this.ds_inquadramento = ds_inquadramento;
    }

    public java.lang.Integer getPg_rif_inquadramento() {
        return pg_rif_inquadramento;
    }

    public void setPg_rif_inquadramento(java.lang.Integer pg_rif_inquadramento) {
        this.pg_rif_inquadramento = pg_rif_inquadramento;
    }

    public java.lang.Integer getCd_anag() {
        return cd_anag;
    }

    public void setCd_anag(java.lang.Integer cd_anag) {
        this.cd_anag = cd_anag;
    }

    public Object getDt_fin_validita() {
        return dt_fin_validita;
    }

    public void setDt_fin_validita(Object dt_fin_validita) {
        this.dt_fin_validita = dt_fin_validita;
    }

    public java.lang.String getCd_tipo_rapporto() {
        return cd_tipo_rapporto;
    }

    public void setCd_tipo_rapporto(java.lang.String cd_tipo_rapporto) {
        this.cd_tipo_rapporto = cd_tipo_rapporto;
    }
}
