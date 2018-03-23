package it.cnr.si.missioni.domain.custom.persistence;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A user.
 */
@Entity
@Table(name = "SPOSTAMENTI_AUTO_PROPRIA")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_SPOSTAMENTI", allocationSize=0)
public class SpostamentiAutoPropria extends OggettoBulkXmlTransient implements Serializable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    private String uid;

    @Size(min = 0, max = 256)
    @Column(name = "PERCORSO_DA", length = 256, nullable = false)
    private String percorsoDa;

    @Size(min = 0, max = 256)
    @Column(name = "PERCORSO_A", length = 256, nullable = false)
    private String percorsoA;

    @Column(name = "RIGA", length = 50, nullable = false)
    private Long riga;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;

	@ManyToOne
	@JoinColumn(name="ID_ORDINE_MISSIONE_AUTO_PROPRIA", nullable=false)
	private OrdineMissioneAutoPropria ordineMissioneAutoPropria;

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getPercorsoDa() {
		return percorsoDa;
	}

	public void setPercorsoDa(String percorsoDa) {
		this.percorsoDa = percorsoDa;
	}

	public String getPercorsoA() {
		return percorsoA;
	}

	public void setPercorsoA(String percorsoA) {
		this.percorsoA = percorsoA;
	}

	public Long getRiga() {
		return riga;
	}

	public void setRiga(Long riga) {
		this.riga = riga;
	}

	public OrdineMissioneAutoPropria getOrdineMissioneAutoPropria() {
		return ordineMissioneAutoPropria;
	}

	public void setOrdineMissioneAutoPropria(
			OrdineMissioneAutoPropria ordineMissioneAutoPropria) {
		this.ordineMissioneAutoPropria = ordineMissioneAutoPropria;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

}
