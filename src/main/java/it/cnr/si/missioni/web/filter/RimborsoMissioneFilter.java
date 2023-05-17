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

package it.cnr.si.missioni.web.filter;

public class RimborsoMissioneFilter extends MissioneFilter {
    private Integer annoOrdine;
    private Long daNumeroOrdine;
    private Long aNumeroOrdine;
    private String statoInvioSigla;
    private String recuperoTotali;
    private Long idOrdineMissione;

    public Integer getAnnoOrdine() {
        return annoOrdine;
    }

    public void setAnnoOrdine(Integer annoOrdine) {
        this.annoOrdine = annoOrdine;
    }

    public Long getDaNumeroOrdine() {
        return daNumeroOrdine;
    }

    public void setDaNumeroOrdine(Long daNumeroOrdine) {
        this.daNumeroOrdine = daNumeroOrdine;
    }

    public Long getaNumeroOrdine() {
        return aNumeroOrdine;
    }

    public void setaNumeroOrdine(Long aNumeroOrdine) {
        this.aNumeroOrdine = aNumeroOrdine;
    }

    public String getStatoInvioSigla() {
        return statoInvioSigla;
    }

    public void setStatoInvioSigla(String statoInvioSigla) {
        this.statoInvioSigla = statoInvioSigla;
    }

    public Long getIdOrdineMissione() {
        return idOrdineMissione;
    }

    public void setIdOrdineMissione(Long idOrdineMissione) {
        this.idOrdineMissione = idOrdineMissione;
    }

    public String getRecuperoTotali() {
        return recuperoTotali;
    }

    public void setRecuperoTotali(String recuperoTotali) {
        this.recuperoTotali = recuperoTotali;
    }

    @Override
    public String toString() {
        return "RimborsoMissioneFilter{" +
                "annoOrdine=" + annoOrdine +
                ", daNumeroOrdine=" + daNumeroOrdine +
                ", aNumeroOrdine=" + aNumeroOrdine +
                ", statoInvioSigla='" + statoInvioSigla + '\'' +
                ", recuperoTotali='" + recuperoTotali + '\'' +
                ", idOrdineMissione=" + idOrdineMissione +
                "} " + super.toString();
    }
}
