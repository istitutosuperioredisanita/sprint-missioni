package it.cnr.si.missioni.util.proxy.json.object;

import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.cnr.si.model.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"codice_sede",
	"codice_uo",
	"livello_profilo",
	"matricola",
	"nome",
	"sesso",
	"data_nascita",
	"comune_nascita",
	"provincia_nascita",
	"nazione_nascita",
	"fl_cittadino_italiano",
	"indirizzo_residenza",
	"num_civico_residenza",
	"cap_residenza",
	"comune_residenza",
	"provincia_residenza",
	"nazione_residenza",
	"indirizzo_comunicazioni",
	"num_civico_comunicazioni",
	"cap_comunicazioni",
	"comune_comunicazioni",
	"provincia_comunicazioni",
	"nazione_comunicazioni",
	"telefono_comunicazioni",
	"email_comunicazioni",
	"profilo",
	"struttura_appartenenza",
	"uoForUsersSpecial",
	"allUoForUsersSpecial",
	"cdTerzoSigla",
	"inquadramenti"
})
public class Account extends UserInfoDto implements Serializable {

	@JsonProperty("uoForUsersSpecial")
	private List<UoForUsersSpecial> uoForUsersSpecial;
	@JsonProperty("allUoForUsersSpecial")
	private String allUoForUsersSpecial;
	@JsonProperty("cdTerzoSigla")
	private String cdTerzoSigla;
	@JsonProperty("inquadramenti")
	private List<Inquadramento> inquadramenti;
	@JsonProperty("roles")
	private List<String> internalRoles;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Account() {
		super();
	}

	public Account(it.cnr.si.service.dto.anagrafica.UserInfoDto userInfoDto) {
		setData_cessazione(userInfoDto.getData_cessazione());
		setArea_scientifica(userInfoDto.getArea_scientifica());
		setCap_comunicazioni(userInfoDto.getCap_comunicazioni());
		setCap_residenza(userInfoDto.getCap_residenza());
		setCitta_sede(userInfoDto.getCitta_sede());
		setCodice_fiscale(userInfoDto.getCodice_fiscale());
		setCodice_sede(userInfoDto.getCodice_sede());
		setCodice_uo(userInfoDto.getCodice_uo());
		setCognome(userInfoDto.getCognome());
		setNome(userInfoDto.getNome());
		setComune_comunicazioni(userInfoDto.getComune_comunicazioni());
		setComune_nascita(userInfoDto.getComune_nascita());
		setComune_residenza(userInfoDto.getComune_residenza());
		setData_nascita(userInfoDto.getData_nascita());
		setEmail_comunicazioni(userInfoDto.getEmail_comunicazioni());
		setFl_cittadino_italiano(userInfoDto.getFl_cittadino_italiano());
		setSettore_scientifico_tecnologico(userInfoDto.getSettore_scientifico_tecnologico());
		setTelefono_comunicazioni(userInfoDto.getTelefono_comunicazioni());
		setIndirizzo_comunicazioni(userInfoDto.getIndirizzo_comunicazioni());
		setIndirizzo_residenza(userInfoDto.getIndirizzo_residenza());
		setLivello_profilo(userInfoDto.getLivello_profilo());
		setMatricola(userInfoDto.getMatricola());
		setNazione_comunicazioni(userInfoDto.getNazione_comunicazioni());
		setNazione_nascita(userInfoDto.getNazione_nascita());
		setNazione_residenza(userInfoDto.getNazione_residenza());
		setNum_civico_comunicazioni(userInfoDto.getNum_civico_comunicazioni());
		setNum_civico_residenza(userInfoDto.getNum_civico_residenza());
		setProfilo(userInfoDto.getProfilo());
		setProvincia_comunicazioni(userInfoDto.getProvincia_comunicazioni());
		setProvincia_nascita(userInfoDto.getProvincia_nascita());
		setProvincia_residenza(userInfoDto.getProvincia_residenza());
		setScadenza_account(userInfoDto.getScadenza_account());
		setSesso(userInfoDto.getSesso());
		setSigla_sede(userInfoDto.getSigla_sede());
		setStruttura_appartenenza(userInfoDto.getStruttura_appartenenza());
		setUid(userInfoDto.getUid());

	}

	public Account(it.cnr.si.model.UserInfoDto userInfoDto) {
		setData_cessazione(userInfoDto.getData_cessazione());
		setArea_scientifica(userInfoDto.getArea_scientifica());
		setCap_comunicazioni(userInfoDto.getCap_comunicazioni());
		setCap_residenza(userInfoDto.getCap_residenza());
		setCitta_sede(userInfoDto.getCitta_sede());
		setCodice_fiscale(userInfoDto.getCodice_fiscale());
		setCodice_sede(userInfoDto.getCodice_sede());
		setCodice_uo(userInfoDto.getCodice_uo());
		setCognome(userInfoDto.getCognome());
		setNome(userInfoDto.getNome());
		setComune_comunicazioni(userInfoDto.getComune_comunicazioni());
		setComune_nascita(userInfoDto.getComune_nascita());
		setComune_residenza(userInfoDto.getComune_residenza());
		setData_nascita(userInfoDto.getData_nascita());
		setEmail_comunicazioni(userInfoDto.getEmail_comunicazioni());
		setFl_cittadino_italiano(userInfoDto.getFl_cittadino_italiano());
		setSettore_scientifico_tecnologico(userInfoDto.getSettore_scientifico_tecnologico());
		setTelefono_comunicazioni(userInfoDto.getTelefono_comunicazioni());
		setIndirizzo_comunicazioni(userInfoDto.getIndirizzo_comunicazioni());
		setIndirizzo_residenza(userInfoDto.getIndirizzo_residenza());
		setLivello_profilo(userInfoDto.getLivello_profilo());
		setMatricola(userInfoDto.getMatricola());
		setNazione_comunicazioni(userInfoDto.getNazione_comunicazioni());
		setNazione_nascita(userInfoDto.getNazione_nascita());
		setNazione_residenza(userInfoDto.getNazione_residenza());
		setNum_civico_comunicazioni(userInfoDto.getNum_civico_comunicazioni());
		setNum_civico_residenza(userInfoDto.getNum_civico_residenza());
		setProfilo(userInfoDto.getProfilo());
		setProvincia_comunicazioni(userInfoDto.getProvincia_comunicazioni());
		setProvincia_nascita(userInfoDto.getProvincia_nascita());
		setProvincia_residenza(userInfoDto.getProvincia_residenza());
		setScadenza_account(userInfoDto.getScadenza_account());
		setSesso(userInfoDto.getSesso());
		setSigla_sede(userInfoDto.getSigla_sede());
		setStruttura_appartenenza(userInfoDto.getStruttura_appartenenza());
		setUid(userInfoDto.getUid());

	}

	public Account(SimpleUtenteWebDto persona) {
		setData_cessazione(DateUtils.getDefaultDateAsString(persona.getPersona().getDataCessazione()));
		setCodice_fiscale(persona.getPersona().getCodiceFiscale());
		setCognome(persona.getPersona().getCognome());
		setNome(persona.getPersona().getNome());
		setComune_residenza("");
		setMatricola(persona.getPersona().getMatricola());
		setUid(persona.getUsername());
		setEmail_comunicazioni(persona.getEmail());
	}

	/**
	 *
	 * @return
	 * The uoForUsersSpecial
	 */
	@JsonProperty("uoForUsersSpecial")
	public List<UoForUsersSpecial> getUoForUsersSpecials() {
		return uoForUsersSpecial;
	}

	/**
	 *
	 * @param uoForUsersSpecial
	 * The uoForUsersSpecial
	 */
	@JsonProperty("uoForUsersSpecial")
	public void setUoForUsersSpecial(List<UoForUsersSpecial> uoForUsersSpecial) {
		this.uoForUsersSpecial = uoForUsersSpecial;
	}

	/**
	 *
	 * @return
	 * The allUoForUsersSpecial
	 */
	@JsonProperty("allUoForUsersSpecial")
	public String getAllUoForUsersSpecial() {
		return allUoForUsersSpecial;
	}

	/**
	 *
	 * @param allUoForUsersSpecial
	 * The allUoForUsersSpecial
	 */
	@JsonProperty("allUoForUsersSpecial")
	public void setAllUoForUsersSpecial(String allUoForUsersSpecial) {
		this.allUoForUsersSpecial = allUoForUsersSpecial;
	}

	/**
	 *
	 * @return
	 * The cdTerzoSigla
	 */
	@JsonProperty("cdTerzoSigla")
	public String getCdTerzoSigla() {
		return cdTerzoSigla;
	}

	/**
	 *
	 * @param cdTerzoSigla
	 * The cdTerzoSigla
	 */
	@JsonProperty("cdTerzoSigla")
	public void setCdTerzoSigla(String cdTerzoSigla) {
		this.cdTerzoSigla = cdTerzoSigla;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	/**
	 *
	 * @return
	 * The inquadramenti
	 */
	@JsonProperty("inquadramenti")
	public List<Inquadramento> getInquadramenti() {
		return inquadramenti;
	}

	/**
	 *
	 * @param inquadramenti
	 * The inquadramenti
	 */
	@JsonProperty("inquadramenti")
	public void setInquadramenti(List<Inquadramento> inquadramenti) {
		this.inquadramenti = inquadramenti;
	}

	public void setInternalRoles(List<String> internalRoles) {
		this.internalRoles = internalRoles;
	}

	public List<String> getInternalRoles() {
		return internalRoles;
	}
}

