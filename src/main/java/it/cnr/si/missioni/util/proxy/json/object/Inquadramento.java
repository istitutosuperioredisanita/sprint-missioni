package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;

public class Inquadramento extends RestServiceBean  implements Serializable {
	private Serializable dt_ini_validita;
	private java.lang.String ds_inquadramento;
	private java.lang.Integer pg_rif_inquadramento;
	private java.lang.Integer cd_anag;
	private Serializable dt_fin_validita;
	public Serializable getDt_ini_validita() {
		return dt_ini_validita;
	}
	public void setDt_ini_validita(Serializable dt_ini_validita) {
		this.dt_ini_validita = dt_ini_validita;
	}
	public java.lang.String getDs_inquadramento() {
		return ds_inquadramento;
	}
	public void setDs_inquadramento(java.lang.String ds_inquadramento) {
		this.ds_inquadramento = ds_inquadramento;
	}
	public java.lang.Integer getPg_rif_inquadramento() {
		return pg_rif_inquadramento;
	}
	public void setPg_rif_inquadramento(java.lang.Integer pg_rif_inquadramento) {
		this.pg_rif_inquadramento = pg_rif_inquadramento;
	}
	public java.lang.Integer getCd_anag() {
		return cd_anag;
	}
	public void setCd_anag(java.lang.Integer cd_anag) {
		this.cd_anag = cd_anag;
	}
	public Serializable getDt_fin_validita() {
		return dt_fin_validita;
	}
	public void setDt_fin_validita(Serializable dt_fin_validita) {
		this.dt_fin_validita = dt_fin_validita;
	}
}
