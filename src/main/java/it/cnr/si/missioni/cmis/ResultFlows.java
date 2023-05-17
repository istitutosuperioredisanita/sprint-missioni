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

import it.cnr.si.missioni.util.Costanti;
import org.springframework.util.StringUtils;

public class ResultFlows {
    private String state;
    private String comment;
    private String taskId;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean isStateReject() {
        return !StringUtils.isEmpty(getState()) && (getState().equals(Costanti.STATO_RESPINTO_SPESA_FROM_CMIS) || getState().equals(Costanti.STATO_RESPINTO_UO_FROM_CMIS) ||
                getState().equals(Costanti.STATO_RESPINTO_UO_RIMBORSO_FROM_CMIS) || getState().equals(Costanti.STATO_RESPINTO_SPESA_RIMBORSO_FROM_CMIS) ||
                getState().equals(Costanti.STATO_RESPINTO_UO_REVOCA_FROM_CMIS) || getState().equals(Costanti.STATO_RESPINTO_SPESA_REVOCA_FROM_CMIS));
    }

    public Boolean isApprovato() {
        return !StringUtils.isEmpty(getState()) && getState().equals(Costanti.STATO_FIRMATO_FROM_CMIS);
    }

    public Boolean isFirmaSpesa() {
        return !StringUtils.isEmpty(getState()) && (getState().equals(Costanti.STATO_FIRMA_SPESA_FROM_CMIS) || getState().equals(Costanti.STATO_FIRMA_SPESA_REVOCA_FROM_CMIS) || getState().equals(Costanti.STATO_FIRMA_SPESA_RIMBORSO_FROM_CMIS));
    }

    public Boolean isAnnullato() {
        return !StringUtils.isEmpty(getState()) && getState().equals(Costanti.STATO_ANNULLATO_FROM_CMIS);
    }
}
