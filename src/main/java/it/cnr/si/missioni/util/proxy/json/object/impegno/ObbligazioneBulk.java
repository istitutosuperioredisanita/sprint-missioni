package it.cnr.si.missioni.util.proxy.json.object.impegno;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"cd_cds",
	"cd_unita_organizzativa",
	"cd_cds_origine",
	"cd_uo_origine",
	"cd_centro_responsabilita",
	"cd_linea_attivita",
	"ds_linea_attivita",
	"cd_elemento_voce",
	"dt_registrazione",
	"data_pagamento",
	"data_docamm",
	"esercizio_originale",
	"pg_obbligazione",
	"esercizio_ori_ori_riporto",
	"pg_obbligazione_ori_riporto",
	"esercizio",
	"esercizio_ori_riporto",
	"pg_obbligazione_scadenzario",
	"ds_scadenza",
	"im_scadenza",
	"im_voce",
	"tipo_doc_amm",
	"esercizio_docamm",
	"pg_doc_amm",
	"pg_mandato",
	"cd_terzo",
	"denominazione_sede",
	"esercizio_contratto",
	"pg_contratto"
})
public class ObbligazioneBulk {

	@JsonProperty("cd_cds")
	private String cdCds;
	@JsonProperty("cd_unita_organizzativa")
	private String cdUnitaOrganizzativa;
	@JsonProperty("cd_cds_origine")
	private String cdCdsOrigine;
	@JsonProperty("cd_uo_origine")
	private String cdUoOrigine;
	@JsonProperty("ti_appartenenza")
	private String tiAppartenenza;
	@JsonProperty("ti_gestione")
	private String tiGestione;

//--daFare get e set e da continuare.	
	@JsonProperty("ds_obbligazione")
	private String dsObbligazione;
	@JsonProperty("note_obbligazione")
	private String noteObbligazione;
	
	@JsonProperty("cd_centro_responsabilita")
	private String cdCentroResponsabilita;
	@JsonProperty("cd_linea_attivita")
	private String cdLineaAttivita;
	@JsonProperty("ds_linea_attivita")
	private String dsLineaAttivita;
	@JsonProperty("cd_elemento_voce")
	private String cdElementoVoce;
	@JsonProperty("dt_registrazione")
	private String dtRegistrazione;
	@JsonProperty("data_pagamento")
	private Object dataPagamento;
	@JsonProperty("data_docamm")
	private Object dataDocamm;
	@JsonProperty("esercizio_originale")
	private Integer esercizioOriginale;
	@JsonProperty("pg_obbligazione")
	private Integer pgObbligazione;
	@JsonProperty("esercizio_ori_ori_riporto")
	private Object esercizioOriOriRiporto;
	@JsonProperty("pg_obbligazione_ori_riporto")
	private Object pgObbligazioneOriRiporto;
	@JsonProperty("esercizio")
	private Integer esercizio;
	@JsonProperty("esercizio_ori_riporto")
	private Object esercizioOriRiporto;
	@JsonProperty("pg_obbligazione_scadenzario")
	private Integer pgObbligazioneScadenzario;
	@JsonProperty("ds_scadenza")
	private String dsScadenza;
	@JsonProperty("im_scadenza")
	private Integer imScadenza;
	@JsonProperty("im_voce")
	private Integer imVoce;
	@JsonProperty("tipo_doc_amm")
	private Object tipoDocAmm;
	@JsonProperty("esercizio_docamm")
	private Object esercizioDocamm;
	@JsonProperty("pg_doc_amm")
	private Object pgDocAmm;
	@JsonProperty("pg_mandato")
	private Object pgMandato;
	@JsonProperty("cd_terzo")
	private Object cdTerzo;
	@JsonProperty("denominazione_sede")
	private Object denominazioneSede;
	@JsonProperty("esercizio_contratto")
	private Object esercizioContratto;
	@JsonProperty("pg_contratto")
	private Object pgContratto;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("cd_cds")
	public String getCdCds() {
		return cdCds;
	}

	@JsonProperty("cd_cds")
	public void setCdCds(String cd_cds) {
		this.cdCds = cd_cds;
	}

	@JsonProperty("cd_cds_origine")
	public String getCdCdsOrigine() {
		return cdCdsOrigine;
	}

	@JsonProperty("cd_cds_origine")
	public void setCdCdsOrigine(String cd_cds_origine) {
		this.cdCdsOrigine = cd_cds_origine;
	}

	@JsonProperty("cd_uo_origine")
	public String getCdUoOrigine() {
		return cdUoOrigine;
	}

	@JsonProperty("cd_uo_origine")
	public void setCdUoOrigine(String cd_uo_origine) {
		this.cdUoOrigine = cd_uo_origine;
	}

	@JsonProperty("cd_unita_organizzativa")
	public String getCdUnitaOrganizzativa() {
		return cdUnitaOrganizzativa;
	}

	@JsonProperty("cd_unita_organizzativa")
	public void setCdUnitaOrganizzativa(String cd_unita_organizzativa) {
		this.cdUnitaOrganizzativa = cd_unita_organizzativa;
	}

	@JsonProperty("ti_appartenenza")
	public String getTiAppartenenza() {
		return tiAppartenenza;
	}

	@JsonProperty("ti_appartenenza")
	public void setTiAppartenenza(String ti_appartenenza) {
		this.tiAppartenenza = ti_appartenenza;
	}

	@JsonProperty("ti_gestione")
	public String getTiGestione() {
		return tiGestione;
	}

	@JsonProperty("ti_gestione")
	public void setTiGestione(String ti_gestione) {
		this.tiGestione = ti_gestione;
	}

	@JsonProperty("cd_centro_responsabilita")
	public String getCdCentroResponsabilita() {
		return cdCentroResponsabilita;
	}

	@JsonProperty("cd_centro_responsabilita")
	public void setCdCentroResponsabilita(String cdCentroResponsabilita) {
		this.cdCentroResponsabilita = cdCentroResponsabilita;
	}

	@JsonProperty("cd_linea_attivita")
	public String getCdLineaAttivita() {
		return cdLineaAttivita;
	}

	@JsonProperty("cd_linea_attivita")
	public void setCdLineaAttivita(String cdLineaAttivita) {
		this.cdLineaAttivita = cdLineaAttivita;
	}

	@JsonProperty("ds_linea_attivita")
	public String getDsLineaAttivita() {
		return dsLineaAttivita;
	}

	@JsonProperty("ds_linea_attivita")
	public void setDsLineaAttivita(String dsLineaAttivita) {
		this.dsLineaAttivita = dsLineaAttivita;
	}

	@JsonProperty("cd_elemento_voce")
	public String getCdElementoVoce() {
		return cdElementoVoce;
	}

	@JsonProperty("cd_elemento_voce")
	public void setCdElementoVoce(String cdElementoVoce) {
		this.cdElementoVoce = cdElementoVoce;
	}

	@JsonProperty("dt_registrazione")
	public String getDtRegistrazione() {
		return dtRegistrazione;
	}

	@JsonProperty("dt_registrazione")
	public void setDtRegistrazione(String dtRegistrazione) {
		this.dtRegistrazione = dtRegistrazione;
	}

	@JsonProperty("data_pagamento")
	public Object getDataPagamento() {
		return dataPagamento;
	}

	@JsonProperty("data_pagamento")
	public void setDataPagamento(Object dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	@JsonProperty("data_docamm")
	public Object getDataDocamm() {
		return dataDocamm;
	}

	@JsonProperty("data_docamm")
	public void setDataDocamm(Object dataDocamm) {
		this.dataDocamm = dataDocamm;
	}

	@JsonProperty("esercizio_originale")
	public Integer getEsercizioOriginale() {
		return esercizioOriginale;
	}

	@JsonProperty("esercizio_originale")
	public void setEsercizioOriginale(Integer esercizioOriginale) {
		this.esercizioOriginale = esercizioOriginale;
	}

	@JsonProperty("pg_obbligazione")
	public Integer getPgObbligazione() {
		return pgObbligazione;
	}

	@JsonProperty("pg_obbligazione")
	public void setPgObbligazione(Integer pgObbligazione) {
		this.pgObbligazione = pgObbligazione;
	}

	@JsonProperty("esercizio_ori_ori_riporto")
	public Object getEsercizioOriOriRiporto() {
		return esercizioOriOriRiporto;
	}

	@JsonProperty("esercizio_ori_ori_riporto")
	public void setEsercizioOriOriRiporto(Object esercizioOriOriRiporto) {
		this.esercizioOriOriRiporto = esercizioOriOriRiporto;
	}

	@JsonProperty("pg_obbligazione_ori_riporto")
	public Object getPgObbligazioneOriRiporto() {
		return pgObbligazioneOriRiporto;
	}

	@JsonProperty("pg_obbligazione_ori_riporto")
	public void setPgObbligazioneOriRiporto(Object pgObbligazioneOriRiporto) {
		this.pgObbligazioneOriRiporto = pgObbligazioneOriRiporto;
	}

	@JsonProperty("esercizio")
	public Integer getEsercizio() {
		return esercizio;
	}

	@JsonProperty("esercizio")
	public void setEsercizio(Integer esercizio) {
		this.esercizio = esercizio;
	}

	@JsonProperty("esercizio_ori_riporto")
	public Object getEsercizioOriRiporto() {
		return esercizioOriRiporto;
	}

	@JsonProperty("esercizio_ori_riporto")
	public void setEsercizioOriRiporto(Object esercizioOriRiporto) {
		this.esercizioOriRiporto = esercizioOriRiporto;
	}

	@JsonProperty("pg_obbligazione_scadenzario")
	public Integer getPgObbligazioneScadenzario() {
		return pgObbligazioneScadenzario;
	}

	@JsonProperty("pg_obbligazione_scadenzario")
	public void setPgObbligazioneScadenzario(Integer pgObbligazioneScadenzario) {
		this.pgObbligazioneScadenzario = pgObbligazioneScadenzario;
	}

	@JsonProperty("ds_scadenza")
	public String getDsScadenza() {
		return dsScadenza;
	}

	@JsonProperty("ds_scadenza")
	public void setDsScadenza(String dsScadenza) {
		this.dsScadenza = dsScadenza;
	}

	@JsonProperty("im_scadenza")
	public Integer getImScadenza() {
		return imScadenza;
	}

	@JsonProperty("im_scadenza")
	public void setImScadenza(Integer imScadenza) {
		this.imScadenza = imScadenza;
	}

	@JsonProperty("im_voce")
	public Integer getImVoce() {
		return imVoce;
	}

	@JsonProperty("im_voce")
	public void setImVoce(Integer imVoce) {
		this.imVoce = imVoce;
	}

	@JsonProperty("tipo_doc_amm")
	public Object getTipoDocAmm() {
		return tipoDocAmm;
	}

	@JsonProperty("tipo_doc_amm")
	public void setTipoDocAmm(Object tipoDocAmm) {
		this.tipoDocAmm = tipoDocAmm;
	}

	@JsonProperty("esercizio_docamm")
	public Object getEsercizioDocamm() {
		return esercizioDocamm;
	}

	@JsonProperty("esercizio_docamm")
	public void setEsercizioDocamm(Object esercizioDocamm) {
		this.esercizioDocamm = esercizioDocamm;
	}

	@JsonProperty("pg_doc_amm")
	public Object getPgDocAmm() {
		return pgDocAmm;
	}

	@JsonProperty("pg_doc_amm")
	public void setPgDocAmm(Object pgDocAmm) {
		this.pgDocAmm = pgDocAmm;
	}

	@JsonProperty("pg_mandato")
	public Object getPgMandato() {
		return pgMandato;
	}

	@JsonProperty("pg_mandato")
	public void setPgMandato(Object pgMandato) {
		this.pgMandato = pgMandato;
	}

	@JsonProperty("cd_terzo")
	public Object getCdTerzo() {
		return cdTerzo;
	}

	@JsonProperty("cd_terzo")
	public void setCdTerzo(Object cdTerzo) {
		this.cdTerzo = cdTerzo;
	}

	@JsonProperty("denominazione_sede")
	public Object getDenominazioneSede() {
		return denominazioneSede;
	}

	@JsonProperty("denominazione_sede")
	public void setDenominazioneSede(Object denominazioneSede) {
		this.denominazioneSede = denominazioneSede;
	}

	@JsonProperty("esercizio_contratto")
	public Object getEsercizioContratto() {
		return esercizioContratto;
	}

	@JsonProperty("esercizio_contratto")
	public void setEsercizioContratto(Object esercizioContratto) {
		this.esercizioContratto = esercizioContratto;
	}

	@JsonProperty("pg_contratto")
	public Object getPgContratto() {
		return pgContratto;
	}

	@JsonProperty("pg_contratto")
	public void setPgContratto(Object pgContratto) {
		this.pgContratto = pgContratto;
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
