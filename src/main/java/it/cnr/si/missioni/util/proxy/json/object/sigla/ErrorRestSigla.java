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

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorRestSigla {
    private String error;

    @JsonProperty("ERROR")
    public String getError() {
        return error;
    }

    /**
     * @param codiceSede The codice_sede
     */
    @JsonProperty("ERROR")
    public void setError(String error) {
        this.error = error;
    }

}
