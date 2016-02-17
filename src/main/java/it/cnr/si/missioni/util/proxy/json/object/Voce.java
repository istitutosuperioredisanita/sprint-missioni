package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;

public class Voce extends RestServiceBean  implements Serializable {
	private String cd_elemento_voce;
	private Integer esercizio;
	private String ds_elemento_voce;
	private Boolean fl_solo_residuo;
	public String getCd_elemento_voce() {
		return cd_elemento_voce;
	}
	public void setCd_elemento_voce(String cd_elemento_voce) {
		this.cd_elemento_voce = cd_elemento_voce;
	}
	public String getDs_elemento_voce() {
		return ds_elemento_voce;
	}
	public void setDs_elemento_voce(String ds_elemento_voce) {
		this.ds_elemento_voce = ds_elemento_voce;
	}
	public Integer getEsercizio() {
		return esercizio;
	}
	public void setEsercizio(Integer esercizio) {
		this.esercizio = esercizio;
	}
	public Boolean getFl_solo_residuo() {
		return fl_solo_residuo;
	}
	public void setFl_solo_residuo(Boolean fl_solo_residuo) {
		this.fl_solo_residuo = fl_solo_residuo;
	}
}
