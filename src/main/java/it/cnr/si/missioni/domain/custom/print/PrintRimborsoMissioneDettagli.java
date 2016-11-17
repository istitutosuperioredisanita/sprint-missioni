package it.cnr.si.missioni.domain.custom.print;

public class PrintRimborsoMissioneDettagli {
	private String data;
	private String dsSpesa;
	private String dsTipoSpesa;
	private String importo;
	private String kmPercorsi;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getDsSpesa() {
		return dsSpesa;
	}
	public void setDsSpesa(String dsSpesa) {
		this.dsSpesa = dsSpesa;
	}
	public String getDsTipoSpesa() {
		return dsTipoSpesa;
	}
	public void setDsTipoSpesa(String dsTipoSpesa) {
		this.dsTipoSpesa = dsTipoSpesa;
	}
	public String getImporto() {
		return importo;
	}
	public void setImporto(String importo) {
		this.importo = importo;
	}
	public String getKmPercorsi() {
		return kmPercorsi;
	}
	public void setKmPercorsi(String kmPercorsi) {
		this.kmPercorsi = kmPercorsi;
	}
}
