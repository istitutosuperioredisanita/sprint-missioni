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

package it.cnr.si.missioni.cmis;

public enum CMISMissioniAspect {
    ORDINE_MISSIONE_ASPECT("P:missioni_commons_aspect:ordine_missione"),
    FILE_ELIMINATO("P:missioni_commons_aspect:file_eliminato"),
    RIMBORSO_MISSIONE_ASPECT("P:missioni_commons_aspect:rimborso_missione");


    private final String value;

    CMISMissioniAspect(String value) {
        this.value = value;
    }

    public static CMISMissioniAspect fromValue(String v) {
        for (CMISMissioniAspect c : CMISMissioniAspect.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
