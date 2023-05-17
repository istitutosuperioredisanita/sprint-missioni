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

package it.cnr.si.missioni.util.proxy.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRESTBody extends JSONSIGLABody implements Cloneable, Serializable {
    String data;
    Long nazione;
    Long inquadramento;
    String cdTipoSpesa;
    String cdTipoPasto;
    String divisa;
    String km;
    String importoSpesa;

    public JSONRESTBody() {
        super();
    }

    public JSONRESTBody(String data, Long nazione,
                        Long inquadramento, String cdTipoSpesa,
                        String cdTipoPasto, String divisa,
                        String km, String importoSpesa) {
        super();
        this.data = data;
        this.nazione = nazione;
        this.inquadramento = inquadramento;
        this.cdTipoSpesa = cdTipoSpesa;
        this.cdTipoPasto = cdTipoPasto;
        this.divisa = divisa;
        this.km = km;
        this.importoSpesa = importoSpesa;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getNazione() {
        return nazione;
    }

    public void setNazione(Long nazione) {
        this.nazione = nazione;
    }

    public Long getInquadramento() {
        return inquadramento;
    }

    public void setInquadramento(Long inquadramento) {
        this.inquadramento = inquadramento;
    }

    public String getCdTipoSpesa() {
        return cdTipoSpesa;
    }

    public void setCdTipoSpesa(String cdTipoSpesa) {
        this.cdTipoSpesa = cdTipoSpesa;
    }

    public String getCdTipoPasto() {
        return cdTipoPasto;
    }

    public void setCdTipoPasto(String cdTipoPasto) {
        this.cdTipoPasto = cdTipoPasto;
    }

    public String getDivisa() {
        return divisa;
    }

    public void setDivisa(String divisa) {
        this.divisa = divisa;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getImportoSpesa() {
        return importoSpesa;
    }

    public void setImportoSpesa(String importoSpesa) {
        this.importoSpesa = importoSpesa;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
