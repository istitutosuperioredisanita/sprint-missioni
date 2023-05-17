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

package it.cnr.si.missioni.util.proxy.json.object.print;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Params {

    @JsonProperty("params")
    private List<Param> params = new ArrayList<Param>();

    @JsonProperty("report")
    private String report;


    @JsonProperty("pgStampa")
    private Long pgStampa;

    /**
     * @return The params
     */
    public List<Param> getParams() {
        return params;
    }

    /**
     * @param params The params
     */
    public void setParams(List<Param> params) {
        this.params = params;
    }

    /**
     * @return The report
     */
    public String getReport() {
        return report;
    }

    /**
     * @param report The report
     */
    public void setReport(String report) {
        this.report = report;
    }

    public Long getPgStampa() {
        return pgStampa;
    }

    public void setPgStampa(Long pgStampa) {
        this.pgStampa = pgStampa;
    }

}
