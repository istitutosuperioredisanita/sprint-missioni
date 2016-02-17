package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;

public class Cds extends RestServiceBean  implements Serializable {
	private String cd_proprio_unita;
	private String cd_tipo_unita;
	private String ds_unita_organizzativa;
	private String cd_responsabile;
	public String getCd_proprio_unita() {
		return cd_proprio_unita;
	}
	public void setCd_proprio_unita(String cd_proprio_unita) {
		this.cd_proprio_unita = cd_proprio_unita;
	}
	public String getDs_unita_organizzativa() {
		return ds_unita_organizzativa;
	}
	public void setDs_unita_organizzativa(String ds_unita_organizzativa) {
		this.ds_unita_organizzativa = ds_unita_organizzativa;
	}
	public String getCd_tipo_unita() {
		return cd_tipo_unita;
	}
	public void setCd_tipo_unita(String cd_tipo_unita) {
		this.cd_tipo_unita = cd_tipo_unita;
	}
	public String getCd_responsabile() {
		return cd_responsabile;
	}
	public void setCd_responsabile(String cd_responsabile) {
		this.cd_responsabile = cd_responsabile;
	}

}
