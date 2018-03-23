package it.cnr.si.missioni.domain.custom.persistence;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * A user.
 */
@Entity
@Table(name = "DATI_PATENTE")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_DATI_PATENTE", allocationSize=0)
public class DatiPatente extends OggettoBulkXmlTransient implements Serializable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

//    @JsonIgnore
    @Size(min = 0, max = 50)
    @Column(name = "NUMERO", length = 50, nullable = false)
    private String numero;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_RILASCIO", nullable = false)
    private Date dataRilascio;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_SCADENZA", nullable = false)
    private Date dataScadenza;

    @Size(min = 0, max = 100)
    @Column(name = "ENTE", length = 100, nullable = false)
    private String ente;

    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    private String uid;

	public void setId(Long id) {
		this.id = id;
	}

	//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		AutoPropria other = (AutoPropria) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//
	@Override
	public Serializable getId() {
		return id;
	}

    public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getDataRilascio() {
		return dataRilascio;
	}

	public void setDataRilascio(Date dataRilascio) {
		this.dataRilascio = dataRilascio;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getEnte() {
		return ente;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}
}
