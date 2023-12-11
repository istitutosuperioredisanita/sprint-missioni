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

public enum CMISOrdineMissioneAspect {
    ORDINE_MISSIONE_ATTACHMENT_ALLEGATI("P:missioni_ordine_attachment:allegati"),
    ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_ANTICIPO("P:missioni_ordine_attachment:allegati_anticipo"),
    ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_TAXI("P:missioni_ordine_attachment:allegati_taxi"),
    ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA("P:missioni_ordine_attachment:uso_auto_propria"),
    ORDINE_MISSIONE_ATTACHMENT_USO_TAXI("P:missioni_ordine_attachment:uso_taxi"),
    ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO("P:missioni_ordine_attachment:richiesta_anticipo"),
    ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_TAXI("P:missioni_ordine_attachment:richiesta_taxi"),
    ORDINE_MISSIONE_ATTACHMENT_DOCUMENT("D:missioni_ordine_attachment:document"),
    ORDINE_MISSIONE_ATTACHMENT_ORDINE("P:missioni_ordine_attachment:ordine"),
    ORDINE_MISSIONE_ATTACHMENT_ANNULLAMENTO_ORDINE("P:missioni_ordine_attachment:annullamento_ordine");


    private final String value;

    CMISOrdineMissioneAspect(String value) {
        this.value = value;
    }

    public static CMISOrdineMissioneAspect fromValue(String v) {
        for (CMISOrdineMissioneAspect c : CMISOrdineMissioneAspect.values()) {
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
