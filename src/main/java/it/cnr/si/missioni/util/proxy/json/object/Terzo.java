package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;

public class Terzo extends RestServiceBean  implements Serializable {
	private Integer cd_terzo;
	private Integer cd_anag;
	private String denominazione_sede;
	private String codice_fiscale_anagrafico;
	private String partita_iva_anagrafico;
	private String dt_fine_rapporto;
	private String descrizioneAnagrafica;
	private String italianoEstero;
	public Integer getCd_terzo() {
		return cd_terzo;
	}
	public void setCd_terzo(Integer cd_terzo) {
		this.cd_terzo = cd_terzo;
	}
	public Integer getCd_anag() {
		return cd_anag;
	}
	public void setCd_anag(Integer cd_anag) {
		this.cd_anag = cd_anag;
	}
	public String getDenominazione_sede() {
		return denominazione_sede;
	}
	public void setDenominazione_sede(String denominazione_sede) {
		this.denominazione_sede = denominazione_sede;
	}
	public String getCodice_fiscale_anagrafico() {
		return codice_fiscale_anagrafico;
	}
	public void setCodice_fiscale_anagrafico(String codice_fiscale_anagrafico) {
		this.codice_fiscale_anagrafico = codice_fiscale_anagrafico;
	}
	public String getPartita_iva_anagrafico() {
		return partita_iva_anagrafico;
	}
	public void setPartita_iva_anagrafico(String partita_iva_anagrafico) {
		this.partita_iva_anagrafico = partita_iva_anagrafico;
	}
	public String getDt_fine_rapporto() {
		return dt_fine_rapporto;
	}
	public void setDt_fine_rapporto(String dt_fine_rapporto) {
		this.dt_fine_rapporto = dt_fine_rapporto;
	}
	public String getDescrizioneAnagrafica() {
		return descrizioneAnagrafica;
	}
	public void setDescrizioneAnagrafica(String descrizioneAnagrafica) {
		this.descrizioneAnagrafica = descrizioneAnagrafica;
	}
	public String getItalianoEstero() {
		return italianoEstero;
	}
	public void setItalianoEstero(String italianoEstero) {
		this.italianoEstero = italianoEstero;
	}
}
