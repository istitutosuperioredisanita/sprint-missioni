package it.cnr.si.missioni.cmis;

public class CMISOrdineMissione extends CMISMissione {
	private String anticipo;
	private String validazioneModulo;
	private String modulo;
	private String descrizioneModulo;
	public String getAnticipo() {
		return anticipo;
	}
	public void setAnticipo(String anticipo) {
		this.anticipo = anticipo;
	}
	public String getValidazioneModulo() {
		return validazioneModulo;
	}
	public void setValidazioneModulo(String validazioneModulo) {
		this.validazioneModulo = validazioneModulo;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public String getDescrizioneModulo() {
		return descrizioneModulo;
	}
	public void setDescrizioneModulo(String descrizioneModulo) {
		this.descrizioneModulo = descrizioneModulo;
	}

}
