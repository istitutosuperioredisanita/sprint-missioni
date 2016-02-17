package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;

public class Cdr extends RestServiceBean implements Serializable {
	private String cd_centro_responsabilita;
	private String cd_unita_organizzativa;
	private String ds_cdr;
	private String cd_responsabile;
	public String getCd_responsabile() {
		return cd_responsabile;
	}
	public void setCd_responsabile(String cd_responsabile) {
		this.cd_responsabile = cd_responsabile;
	}
	public String getCd_centro_responsabilita() {
		return cd_centro_responsabilita;
	}
	public void setCd_centro_responsabilita(String cd_centro_responsabilita) {
		this.cd_centro_responsabilita = cd_centro_responsabilita;
	}
	public String getCd_unita_organizzativa() {
		return cd_unita_organizzativa;
	}
	public void setCd_unita_organizzativa(String cd_unita_organizzativa) {
		this.cd_unita_organizzativa = cd_unita_organizzativa;
	}
	public String getDs_cdr() {
		return ds_cdr;
	}
	public void setDs_cdr(String ds_cdr) {
		this.ds_cdr = ds_cdr;
	}

}
