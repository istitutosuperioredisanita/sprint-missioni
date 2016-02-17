package it.cnr.si.missioni.domain.custom.persistence;

import it.cnr.jada.bulk.OggettoBulk;

import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
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
