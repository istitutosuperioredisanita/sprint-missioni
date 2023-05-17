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

package it.cnr.si.missioni.domain.custom.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.cnr.jada.bulk.OggettoBulk;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OggettoBulkXmlTransient extends OggettoBulk {
    private static final long serialVersionUID = 1L;

    @Override
    @XmlTransient
    public String getUtcr() {
        return super.getUtcr();
    }

    @Override
    @XmlTransient
    public String getUtuv() {
        return super.getUtuv();
    }

    @Override
    @XmlTransient
    public Long getPg_ver_rec() {
        return super.getPg_ver_rec();
    }

    @Override
    @XmlTransient
    public Date getDacr() {
        return super.getDacr();
    }

    @Override
    @XmlTransient
    public Date getDuva() {
        return super.getDuva();
    }

    @Override
    @XmlTransient
    public String getUser() {
        return super.getUser();
    }

    @Override
    @XmlTransient
    public boolean isNew() {
        return super.isNew();
    }

    @Override
    @XmlTransient
    public boolean isNotNew() {
        return super.isNotNew();
    }

    @Override
    @XmlTransient
    public boolean isToBeCreated() {
        return super.isToBeCreated();
    }

    @Override
    @XmlTransient
    public boolean isToBeUpdated() {
        return super.isToBeUpdated();
    }

    @Override
    @XmlTransient
    public boolean isToBeDeleted() {
        return super.isToBeDeleted();
    }

    @Override
    @XmlTransient
    public boolean isToBeChecked() {
        return super.isToBeChecked();
    }

    @Override
    @XmlTransient
    public int getCrudStatus() {
        return super.getCrudStatus();
    }
}
