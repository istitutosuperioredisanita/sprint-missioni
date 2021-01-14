package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.si.missioni.util.Utility;

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
@Table(name = "DATI_SEDE")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_DATI_SEDE", allocationSize=0)
public class DatiSede extends OggettoBulkXmlTransient implements Serializable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

//    @JsonIgnore
    @Size(min = 0, max = 30)
    @Column(name = "CODICE_SEDE", length = 30, nullable = false)
    private String codiceSede;

    @Column(name = "DATA_INIZIO", nullable = false)
    public LocalDate dataInizio;

    @Column(name = "DATA_FINE", nullable = true)
    public LocalDate dataFine;

    @Size(min = 0, max = 200)
    @Column(name = "RESPONSABILE", length = 200, nullable = true)
    private String responsabile;

    @Size(min = 0, max = 1)
    @Column(name = "RESPONSABILE_SOLO_ITALIA", length = 1, nullable = true)
    private String responsabileSoloItalia;

    @Size(min = 0, max = 30)
    @Column(name = "SEDE_RESP_ESTERO", length = 30, nullable = true)
    private String sedeRespEstero;

    @Size(min = 0, max = 1)
    @Column(name = "DELEGA_SPESA", length = 1, nullable = true)
    private String delegaSpesa;

	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Serializable getId() {
		return id;
	}
	public String getCodice_sede() {
		return codiceSede;
	}
	public void setCodiceSede(String codiceSede) {
		this.codiceSede = codiceSede;
	}
	public LocalDate getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}
	public LocalDate getDataFine() {
		return dataFine;
	}
	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}
	public String getResponsabile() {
		return responsabile;
	}
	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}
	public String getResponsabileSoloItalia() {
		return responsabileSoloItalia;
	}
	public void setResponsabileSoloItalia(String responsabileSoloItalia) {
		this.responsabileSoloItalia = responsabileSoloItalia;
	}
	public String getSedeRespEstero() {
		return sedeRespEstero;
	}
	public void setSedeRespEstero(String sedeRespEstero) {
		this.sedeRespEstero = sedeRespEstero;
	}
	public String getDelegaSpesa() {
		return delegaSpesa;
	}
	public void setDelegaSpesa(String delegaSpesa) {
		this.delegaSpesa = delegaSpesa;
	}
	public boolean isResponsabileEstero(){
		return Utility.nvl(getResponsabileSoloItalia(),"N").equals("N");
	}
	public boolean isDelegaSpesa(){
		return Utility.nvl(getDelegaSpesa(),"N").equals("S");
	}
}
