package it.cnr.si.missioni.domain.custom.print;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"anno",
	"numero",
	"dataInserimento",
	"cognomeRich",
	"nomeRich",
	"matricolaRich",
	"codiceFiscaleRich",
	"luogoDiNascitaRich",
	"dataDiNascitaRich",
	"comuneResidenzaRich",
	"indirizzoResidenzaRich",
	"domicilioFiscaleRich",
	"datoreLavoroRich",
	"qualificaRich",
	"livelloRich",
	"oggetto",
	"dataInizioMissione",
	"dataFineMissione",
	"importoPresunto",
	"dataAnticipo",
	"importoAnticipo",
	"note",
	"targa",
	"cartaCircolazione",
	"polizzaAssicurativa",
	"marca",
	"modello",
	"numeroPatente",
	"dataRilascioPatente",
	"dataScadenzaPatente",
	"entePatente",
	"stato"
})


public class PrintOrdineMissioneAnticipo {

	@JsonProperty("anno")
	private Integer anno;
	@JsonProperty("numero")
	private Long numero;
	@JsonProperty("dataInserimento")
	private String dataInserimento;
	@JsonProperty("cognomeRich")
	private String cognomeRich;
	@JsonProperty("nomeRich")
	private String nomeRich;
	@JsonProperty("matricolaRich")
	private String matricolaRich;
	@JsonProperty("codiceFiscaleRich")
	private String codiceFiscaleRich;
	@JsonProperty("luogoDiNascitaRich")
	private String luogoDiNascitaRich;
	@JsonProperty("dataDiNascitaRich")
	private String dataDiNascitaRich;
	@JsonProperty("comuneResidenzaRich")
	private String comuneResidenzaRich;
	@JsonProperty("indirizzoResidenzaRich")
	private String indirizzoResidenzaRich;
	@JsonProperty("domicilioFiscaleRich")
	private String domicilioFiscaleRich;
	@JsonProperty("datoreLavoroRich")
	private String datoreLavoroRich;
	@JsonProperty("qualificaRich")
	private String qualificaRich;
	@JsonProperty("livelloRich")
	private String livelloRich;
	@JsonProperty("oggetto")
	private String oggetto;
	@JsonProperty("dataInizioMissione")
	private String dataInizioMissione;
	@JsonProperty("dataFineMissione")
	private String dataFineMissione;
	@JsonProperty("importoPresunto")
	private String importoPresunto;
	@JsonProperty("dataAnticipo")
	private String dataAnticipo;
	@JsonProperty("importoAnticipo")
	private String importoAnticipo;
	@JsonProperty("note")
	private String note;
	@JsonProperty("stato")
	private String stato;
	@JsonProperty("targa")
	private String targa;
	@JsonProperty("cartaCircolazione")
	private String cartaCircolazione;
	@JsonProperty("polizzaAssicurativa")
	private String polizzaAssicurativa;
	@JsonProperty("marca")
	private String marca;
	@JsonProperty("modello")
	private String modello;
	@JsonProperty("numeroPatente")
	private String numeroPatente;
	@JsonProperty("dataRilascioPatente")
	private String dataRilascioPatente;
	@JsonProperty("dataScadenzaPatente")
	private String dataScadenzaPatente;
	@JsonProperty("entePatente")
	private String entePatente;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The anno
	 */
	@JsonProperty("anno")
	public Integer getAnno() {
		return anno;
	}

	/**
	 *
	 * @param anno
	 * The anno
	 */
	@JsonProperty("anno")
	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	/**
	 *
	 * @return
	 * The numero
	 */
	@JsonProperty("numero")
	public Long getNumero() {
		return numero;
	}

	/**
	 *
	 * @param numero
	 * The numero
	 */
	@JsonProperty("numero")
	public void setNumero(Long numero) {
		this.numero = numero;
	}

	/**
	 *
	 * @return
	 * The dataInserimento
	 */
	@JsonProperty("dataInserimento")
	public String getDataInserimento() {
		return dataInserimento;
	}

	/**
	 *
	 * @param dataInserimento
	 * The dataInserimento
	 */
	@JsonProperty("dataInserimento")
	public void setDataInserimento(String dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	/**
	 *
	 * @return
	 * The cognomeRich
	 */
	@JsonProperty("cognomeRich")
	public String getCognomeRich() {
		return cognomeRich;
	}

	/**
	 *
	 * @param cognomeRich
	 * The cognomeRich
	 */
	@JsonProperty("cognomeRich")
	public void setCognomeRich(String cognomeRich) {
		this.cognomeRich = cognomeRich;
	}

	/**
	 *
	 * @return
	 * The nomeRich
	 */
	@JsonProperty("nomeRich")
	public String getNomeRich() {
		return nomeRich;
	}

	/**
	 *
	 * @param nomeRich
	 * The nomeRich
	 */
	@JsonProperty("nomeRich")
	public void setNomeRich(String nomeRich) {
		this.nomeRich = nomeRich;
	}

	/**
	 *
	 * @return
	 * The matricolaRich
	 */
	@JsonProperty("matricolaRich")
	public String getMatricolaRich() {
		return matricolaRich;
	}

	/**
	 *
	 * @param matricolaRich
	 * The matricolaRich
	 */
	@JsonProperty("matricolaRich")
	public void setMatricolaRich(String matricolaRich) {
		this.matricolaRich = matricolaRich;
	}

	/**
	 *
	 * @return
	 * The codiceFiscaleRich
	 */
	@JsonProperty("codiceFiscaleRich")
	public String getCodiceFiscaleRich() {
		return codiceFiscaleRich;
	}

	/**
	 *
	 * @param codiceFiscaleRich
	 * The codiceFiscaleRich
	 */
	@JsonProperty("codiceFiscaleRich")
	public void setCodiceFiscaleRich(String codiceFiscaleRich) {
		this.codiceFiscaleRich = codiceFiscaleRich;
	}

	/**
	 *
	 * @return
	 * The luogoDiNascitaRich
	 */
	@JsonProperty("luogoDiNascitaRich")
	public String getLuogoDiNascitaRich() {
		return luogoDiNascitaRich;
	}

	/**
	 *
	 * @param luogoDiNascitaRich
	 * The luogoDiNascitaRich
	 */
	@JsonProperty("luogoDiNascitaRich")
	public void setLuogoDiNascitaRich(String luogoDiNascitaRich) {
		this.luogoDiNascitaRich = luogoDiNascitaRich;
	}

	/**
	 *
	 * @return
	 * The dataDiNascitaRich
	 */
	@JsonProperty("dataDiNascitaRich")
	public String getDataDiNascitaRich() {
		return dataDiNascitaRich;
	}

	/**
	 *
	 * @param dataDiNascitaRich
	 * The dataDiNascitaRich
	 */
	@JsonProperty("dataDiNascitaRich")
	public void setDataDiNascitaRich(String dataDiNascitaRich) {
		this.dataDiNascitaRich = dataDiNascitaRich;
	}

	/**
	 *
	 * @return
	 * The comuneResidenzaRich
	 */
	@JsonProperty("comuneResidenzaRich")
	public String getComuneResidenzaRich() {
		return comuneResidenzaRich;
	}

	/**
	 *
	 * @param comuneResidenzaRich
	 * The comuneResidenzaRich
	 */
	@JsonProperty("comuneResidenzaRich")
	public void setComuneResidenzaRich(String comuneResidenzaRich) {
		this.comuneResidenzaRich = comuneResidenzaRich;
	}

	/**
	 *
	 * @return
	 * The indirizzoResidenzaRich
	 */
	@JsonProperty("indirizzoResidenzaRich")
	public String getIndirizzoResidenzaRich() {
		return indirizzoResidenzaRich;
	}

	/**
	 *
	 * @param indirizzoResidenzaRich
	 * The indirizzoResidenzaRich
	 */
	@JsonProperty("indirizzoResidenzaRich")
	public void setIndirizzoResidenzaRich(String indirizzoResidenzaRich) {
		this.indirizzoResidenzaRich = indirizzoResidenzaRich;
	}

	/**
	 *
	 * @return
	 * The domicilioFiscaleRich
	 */
	@JsonProperty("domicilioFiscaleRich")
	public String getDomicilioFiscaleRich() {
		return domicilioFiscaleRich;
	}

	/**
	 *
	 * @param domicilioFiscaleRich
	 * The domicilioFiscaleRich
	 */
	@JsonProperty("domicilioFiscaleRich")
	public void setDomicilioFiscaleRich(String domicilioFiscaleRich) {
		this.domicilioFiscaleRich = domicilioFiscaleRich;
	}

	/**
	 *
	 * @return
	 * The datoreLavoroRich
	 */
	@JsonProperty("datoreLavoroRich")
	public String getDatoreLavoroRich() {
		return datoreLavoroRich;
	}

	/**
	 *
	 * @param datoreLavoroRich
	 * The datoreLavoroRich
	 */
	@JsonProperty("datoreLavoroRich")
	public void setDatoreLavoroRich(String datoreLavoroRich) {
		this.datoreLavoroRich = datoreLavoroRich;
	}

	/**
	 *
	 * @return
	 * The qualificaRich
	 */
	@JsonProperty("qualificaRich")
	public String getQualificaRich() {
		return qualificaRich;
	}

	/**
	 *
	 * @param qualificaRich
	 * The qualificaRich
	 */
	@JsonProperty("qualificaRich")
	public void setQualificaRich(String qualificaRich) {
		this.qualificaRich = qualificaRich;
	}

	/**
	 *
	 * @return
	 * The livelloRich
	 */
	@JsonProperty("livelloRich")
	public String getLivelloRich() {
		return livelloRich;
	}

	/**
	 *
	 * @param livelloRich
	 * The livelloRich
	 */
	@JsonProperty("livelloRich")
	public void setLivelloRich(String livelloRich) {
		this.livelloRich = livelloRich;
	}

	/**
	 *
	 * @return
	 * The oggetto
	 */
	@JsonProperty("oggetto")
	public String getOggetto() {
		return oggetto;
	}

	/**
	 *
	 * @param oggetto
	 * The oggetto
	 */
	@JsonProperty("oggetto")
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	/**
	 *
	 * @return
	 * The dataInizioMissione
	 */
	@JsonProperty("dataInizioMissione")
	public String getDataInizioMissione() {
		return dataInizioMissione;
	}

	/**
	 *
	 * @param dataInizioMissione
	 * The dataInizioMissione
	 */
	@JsonProperty("dataInizioMissione")
	public void setDataInizioMissione(String dataInizioMissione) {
		this.dataInizioMissione = dataInizioMissione;
	}

	/**
	 *
	 * @return
	 * The dataFineMissione
	 */
	@JsonProperty("dataFineMissione")
	public String getDataFineMissione() {
		return dataFineMissione;
	}

	/**
	 *
	 * @param dataFineMissione
	 * The dataFineMissione
	 */
	@JsonProperty("dataFineMissione")
	public void setDataFineMissione(String dataFineMissione) {
		this.dataFineMissione = dataFineMissione;
	}

	/**
	 *
	 * @return
	 * The importoPresunto
	 */
	@JsonProperty("importoPresunto")
	public String getImportoPresunto() {
		return importoPresunto;
	}

	/**
	 *
	 * @param importoPresunto
	 * The importoPresunto
	 */
	@JsonProperty("importoPresunto")
	public void setImportoPresunto(String importoPresunto) {
		this.importoPresunto = importoPresunto;
	}

	/**
	 *
	 * @return
	 * The dataAnticipo
	 */
	@JsonProperty("dataAnticipo")
	public String getDataAnticipo() {
		return dataAnticipo;
	}

	/**
	 *
	 * @param dataAnticipo
	 * The dataAnticipo
	 */
	@JsonProperty("dataAnticipo")
	public void setDataAnticipo(String dataAnticipo) {
		this.dataAnticipo = dataAnticipo;
	}

	/**
	 *
	 * @return
	 * The importoAnticipo
	 */
	@JsonProperty("importoAnticipo")
	public String getImportoAnticipo() {
		return importoAnticipo;
	}

	/**
	 *
	 * @param importoAnticipo
	 * The importoAnticipo
	 */
	@JsonProperty("importoAnticipo")
	public void setImportoAnticipo(String importoAnticipo) {
		this.importoAnticipo = importoAnticipo;
	}

	/**
	 *
	 * @return
	 * The note
	 */
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	/**
	 *
	 * @param note
	 * The note
	 */
	@JsonProperty("note")
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 *
	 * @return
	 * The stato
	 */
	@JsonProperty("stato")
	public String getStato() {
		return stato;
	}

	/**
	 *
	 * @param stato
	 * The stato
	 */
	@JsonProperty("stato")
	public void setStato(String stato) {
		this.stato = stato;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
