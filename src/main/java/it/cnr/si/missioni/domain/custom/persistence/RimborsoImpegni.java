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
@Table(name = "RIMBORSO_IMPEGNI")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_RIMBORSO_IMPEGNI", allocationSize=0)
public class RimborsoImpegni extends OggettoBulkXmlTransient implements Serializable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

    @Size(min = 0, max = 30)
    @Column(name = "CD_CDS_OBBLIGAZIONE", length = 30, nullable = false)
    public String cdCdsObbligazione;

    @ManyToOne
	@JoinColumn(name="ID_RIMBORSO_MISSIONE", nullable=false)
	private RimborsoMissione rimborsoMissione;

    @Column(name = "ESERCIZIO_OBBLIGAZIONE", length = 4, nullable = false)
    public Integer esercizioObbligazione;

    @Column(name = "PG_OBBLIGAZIONE", length = 50, nullable = false)
    public Long pgObbligazione;

    @Column(name = "ESERCIZIO_ORIGINALE_OBBLIGAZIONE", length = 4, nullable = false)
    public Integer esercizioOriginaleObbligazione;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    public String stato;
    
    @Size(min = 0, max = 28)
    @Column(name = "VOCE", length = 28, nullable = true)
    public String voce;

    @Size(min = 0, max = 500)
    @Column(name = "DS_VOCE", length = 500, nullable = true)
    public String dsVoce;

	public RimborsoImpegni(Long id){
		super();
		this.setId(id);
	}

	public RimborsoImpegni(){
		super();
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Serializable getId() {
		return id;
	}

	public String getCdCdsObbligazione() {
		return cdCdsObbligazione;
	}

	public void setCdCdsObbligazione(String cdCdsObbligazione) {
		this.cdCdsObbligazione = cdCdsObbligazione;
	}

	public Integer getEsercizioObbligazione() {
		return esercizioObbligazione;
	}

	public void setEsercizioObbligazione(Integer esercizioObbligazione) {
		this.esercizioObbligazione = esercizioObbligazione;
	}

	public Long getPgObbligazione() {
		return pgObbligazione;
	}

	public void setPgObbligazione(Long pgObbligazione) {
		this.pgObbligazione = pgObbligazione;
	}

	public Integer getEsercizioOriginaleObbligazione() {
		return esercizioOriginaleObbligazione;
	}

	public void setEsercizioOriginaleObbligazione(Integer esercizioOriginaleObbligazione) {
		this.esercizioOriginaleObbligazione = esercizioOriginaleObbligazione;
	}

	public RimborsoMissione getRimborsoMissione() {
		return rimborsoMissione;
	}

	public void setRimborsoMissione(RimborsoMissione rimborsoMissione) {
		this.rimborsoMissione = rimborsoMissione;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getVoce() {
		return voce;
	}

	public void setVoce(String voce) {
		this.voce = voce;
	}

	public String getDsVoce() {
		return dsVoce;
	}

	public void setDsVoce(String dsVoce) {
		this.dsVoce = dsVoce;
	}

	@Override
	public String toString() {
		return "RimborsoImpegni{" +
				"id=" + id +
				", cdCdsObbligazione='" + cdCdsObbligazione + '\'' +
				", esercizioObbligazione=" + esercizioObbligazione +
				", pgObbligazione=" + pgObbligazione +
				", esercizioOriginaleObbligazione=" + esercizioOriginaleObbligazione +
				", stato='" + stato + '\'' +
				", voce='" + voce + '\'' +
				", dsVoce='" + dsVoce + '\'' +
				'}';
	}
}
