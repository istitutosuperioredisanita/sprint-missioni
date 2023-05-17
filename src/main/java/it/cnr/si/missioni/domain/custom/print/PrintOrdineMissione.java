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


public class PrintOrdineMissione extends PrintMissione {

    public String missioneGratuita;
    public String richiestaAutoPropria;
    public String richiestaAnticipo;
    public String partenzaDaAltro;
    public String tipo;
    public String motivoAnnullamento;
    private String obbligoRientro;
    private String partenzaDa;
    private String distanzaDallaSede;
    private String note;
    private String priorita;
    private String importoPresunto;

    public String getObbligoRientro() {
        return obbligoRientro;
    }

    public void setObbligoRientro(String obbligoRientro) {
        this.obbligoRientro = obbligoRientro;
    }

    public String getPartenzaDa() {
        return partenzaDa;
    }

    public void setPartenzaDa(String partenzaDa) {
        this.partenzaDa = partenzaDa;
    }

    public String getDistanzaDallaSede() {
        return distanzaDallaSede;
    }

    public void setDistanzaDallaSede(String distanzaDallaSede) {
        this.distanzaDallaSede = distanzaDallaSede;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPriorita() {
        return priorita;
    }

    public void setPriorita(String priorita) {
        this.priorita = priorita;
    }

    public String getImportoPresunto() {
        return importoPresunto;
    }

    public void setImportoPresunto(String importoPresunto) {
        this.importoPresunto = importoPresunto;
    }

    public String getPartenzaDaAltro() {
        return partenzaDaAltro;
    }

    public void setPartenzaDaAltro(String partenzaDaAltro) {
        this.partenzaDaAltro = partenzaDaAltro;
    }

    public String getMissioneGratuita() {
        return missioneGratuita;
    }

    public void setMissioneGratuita(String missioneGratuita) {
        this.missioneGratuita = missioneGratuita;
    }

    public String getRichiestaAutoPropria() {
        return richiestaAutoPropria;
    }

    public void setRichiestaAutoPropria(String richiestaAutoPropria) {
        this.richiestaAutoPropria = richiestaAutoPropria;
    }

    public String getRichiestaAnticipo() {
        return richiestaAnticipo;
    }

    public void setRichiestaAnticipo(String richiestaAnticipo) {
        this.richiestaAnticipo = richiestaAnticipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivoAnnullamento() {
        return motivoAnnullamento;
    }

    public void setMotivoAnnullamento(String motivoAnnullamento) {
        this.motivoAnnullamento = motivoAnnullamento;
    }

}
