package it.cnr.si.missioni.util.proxy.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.cnr.si.missioni.util.proxy.json.object.sigla.Context;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRESTBody extends JSONSIGLABody implements Cloneable, Serializable {
	String data;
	Long nazione;
	Long inquadramento;
	String cdTipoSpesa;
	String cdTipoPasto;
	String divisa;
	String km;
	String importoSpesa;
	
	public JSONRESTBody() {
		super();
	}

	public JSONRESTBody(String data, Long nazione,
			Long inquadramento, String cdTipoSpesa,
			String cdTipoPasto, String divisa,
			String km, String importoSpesa) {
		super();
		this.data = data;
		this.nazione = nazione;
		this.inquadramento = inquadramento;
		this.cdTipoSpesa = cdTipoSpesa;
		this.cdTipoPasto = cdTipoPasto;
		this.divisa = divisa;
		this.km = km;
		this.importoSpesa = importoSpesa;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Long getNazione() {
		return nazione;
	}

	public void setNazione(Long nazione) {
		this.nazione = nazione;
	}

	public Long getInquadramento() {
		return inquadramento;
	}

	public void setInquadramento(Long inquadramento) {
		this.inquadramento = inquadramento;
	}

	public String getCdTipoSpesa() {
		return cdTipoSpesa;
	}

	public void setCdTipoSpesa(String cdTipoSpesa) {
		this.cdTipoSpesa = cdTipoSpesa;
	}

	public String getCdTipoPasto() {
		return cdTipoPasto;
	}

	public void setCdTipoPasto(String cdTipoPasto) {
		this.cdTipoPasto = cdTipoPasto;
	}

	public String getDivisa() {
		return divisa;
	}

	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getImportoSpesa() {
		return importoSpesa;
	}

	public void setImportoSpesa(String importoSpesa) {
		this.importoSpesa = importoSpesa;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
