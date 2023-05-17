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

package it.cnr.si.missioni.domain.custom;

import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;

public class DatiFlusso {
    private String usernamePrimoFirmatario;
    private String usernameFirmatarioSpesa;
    private String uoCompetenzaPerFlusso;
    private UnitaOrganizzativa uoSpesa;
    private UnitaOrganizzativa uoRich;
    private String uoSpesaPerFlusso;
    private String uoRichPerFlusso;

    public String getUoCompetenzaPerFlusso() {
        return uoCompetenzaPerFlusso;
    }

    public void setUoCompetenzaPerFlusso(String uoCompetenzaPerFlusso) {
        this.uoCompetenzaPerFlusso = uoCompetenzaPerFlusso;
    }

    public String getUoSpesaPerFlusso() {
        return uoSpesaPerFlusso;
    }

    public void setUoSpesaPerFlusso(String uoSpesaPerFlusso) {
        this.uoSpesaPerFlusso = uoSpesaPerFlusso;
    }

    public String getUoRichPerFlusso() {
        return uoRichPerFlusso;
    }

    public void setUoRichPerFlusso(String uoRichPerFlusso) {
        this.uoRichPerFlusso = uoRichPerFlusso;
    }

    public UnitaOrganizzativa getUoSpesa() {
        return uoSpesa;
    }

    public void setUoSpesa(UnitaOrganizzativa uoSpesa) {
        this.uoSpesa = uoSpesa;
    }

    public UnitaOrganizzativa getUoRich() {
        return uoRich;
    }

    public void setUoRich(UnitaOrganizzativa uoRich) {
        this.uoRich = uoRich;
    }

    public String getUsernamePrimoFirmatario() {
        return usernamePrimoFirmatario;
    }

    public void setUsernamePrimoFirmatario(String usernamePrimoFirmatario) {
        this.usernamePrimoFirmatario = usernamePrimoFirmatario;
    }

    public String getUsernameFirmatarioSpesa() {
        return usernameFirmatarioSpesa;
    }

    public void setUsernameFirmatarioSpesa(String usernameFirmatarioSpesa) {
        this.usernameFirmatarioSpesa = usernameFirmatarioSpesa;
    }

}
