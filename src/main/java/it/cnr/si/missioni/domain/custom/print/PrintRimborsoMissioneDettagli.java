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

package it.cnr.si.missioni.domain.custom.print;

public class PrintRimborsoMissioneDettagli {
    private String data;
    private String dsSpesa;
    private String importo;
    private String kmPercorsi;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDsSpesa() {
        return dsSpesa;
    }

    public void setDsSpesa(String dsSpesa) {
        this.dsSpesa = dsSpesa;
    }

    public String getImporto() {
        return importo;
    }

    public void setImporto(String importo) {
        this.importo = importo;
    }

    public String getKmPercorsi() {
        return kmPercorsi;
    }

    public void setKmPercorsi(String kmPercorsi) {
        this.kmPercorsi = kmPercorsi;
    }
}
