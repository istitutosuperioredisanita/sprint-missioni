package it.cnr.si.missioni.domain.custom.persistence;


import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A user.
 */
@Entity
@Table(name = "PARAMETRI")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_PARAMETRI", allocationSize=0)
public class Parametri extends OggettoBulkXmlTransient implements Serializable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

    @Size(min = 0, max = 200)
    @Column(name = "RESPONSABILE_CUG", length = 200, nullable = true)
    private String responsabileCug;

	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Serializable getId() {
		return id;
	}
	public String getResponsabileCug() {
		return responsabileCug;
	}
	public void setResponsabileCug(String responsabileCug) {
		this.responsabileCug = responsabileCug;
	}
}
