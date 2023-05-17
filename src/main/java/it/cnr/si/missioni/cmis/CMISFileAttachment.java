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

import java.util.Optional;

public class CMISFileAttachment {
    private Long idMissione;
    private String id;
    private String nomeFile;
    private String nodeRef;
    private String tipo;

    public Long getIdMissione() {
        return idMissione;
    }

    public void setIdMissione(Long idMissione) {
        this.idMissione = idMissione;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Optional.ofNullable(id)
                .filter(s -> s.indexOf(";") != -1)
                .map(s -> s.substring(0, s.indexOf(";")))
                .orElse(id);
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
    }
}
