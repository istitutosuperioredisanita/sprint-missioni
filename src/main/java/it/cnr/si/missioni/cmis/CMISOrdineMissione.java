package it.cnr.si.missioni.cmis;

public class CMISOrdineMissione extends CMISMissione {
	private Long idMissioneOrdine;
	private Long idMissioneRevoca;
	private String usernameFirmatarioAggiunto;
	private String usernameFirmatarioSpesaAggiunto;

	public Long getIdMissioneRevoca() {
		return idMissioneRevoca;
	}

	public void setIdMissioneRevoca(Long idMissioneRevoca) {
		this.idMissioneRevoca = idMissioneRevoca;
	}

	public Long getIdMissioneOrdine() {
		return idMissioneOrdine;
	}

	public void setIdMissioneOrdine(Long idMissioneOrdine) {
		this.idMissioneOrdine = idMissioneOrdine;
	}

	private String usernameResponsabileGruppo;
	private String fondi;
	private String altriMotiviAutoPropria;
	private String primoMotivoAutoPropria;
	private String secondoMotivoAutoPropria;
	private String terzoMotivoAutoPropria;
	private String anticipo;
	private String validazioneModulo;
	private String modulo;
	private String descrizioneModulo;
	private String missioneGratuita;
	public final static String PRIMO_MOTIVO_UTILIZZO_AUTO_PROPRIA = "Richiesta auto propria per lo svolgimento di funzioni istituzionali relativi a compiti ispettivi, di verifica e di controllo";
	public final static String SECONDO_MOTIVO_UTILIZZO_AUTO_PROPRIA = "Richiesta auto propria per Attività caratterizzata da emergenza, urgenza, indifferibilità";
	public final static String TERZO_MOTIVO_UTILIZZO_AUTO_PROPRIA = "Richiesta auto propria per Attività che richiede necessariamente il trasporto di materiale, o attrezzature ingombranti, pesanti, o fragili in dotazione";
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
	public String getUsernameResponsabileGruppo() {
		return usernameResponsabileGruppo;
	}
	public void setUsernameResponsabileGruppo(String usernameResponsabileGruppo) {
		this.usernameResponsabileGruppo = usernameResponsabileGruppo;
	}
	public String getFondi() {
		return fondi;
	}
	public void setFondi(String fondi) {
		this.fondi = fondi;
	}
	public String getPrimoMotivoAutoPropria() {
		return primoMotivoAutoPropria;
	}
	public void setPrimoMotivoAutoPropria(String primoMotivoAutoPropria) {
		this.primoMotivoAutoPropria = primoMotivoAutoPropria;
	}
	public String getSecondoMotivoAutoPropria() {
		return secondoMotivoAutoPropria;
	}
	public void setSecondoMotivoAutoPropria(String secondoMotivoAutoPropria) {
		this.secondoMotivoAutoPropria = secondoMotivoAutoPropria;
	}
	public String getTerzoMotivoAutoPropria() {
		return terzoMotivoAutoPropria;
	}
	public void setTerzoMotivoAutoPropria(String terzoMotivoAutoPropria) {
		this.terzoMotivoAutoPropria = terzoMotivoAutoPropria;
	}
	public String getMissioneGratuita() {
		return missioneGratuita;
	}
	public void setMissioneGratuita(String missioneGratuita) {
		this.missioneGratuita = missioneGratuita;
	}
	public String getAltriMotiviAutoPropria() {
		return altriMotiviAutoPropria;
	}
	public void setAltriMotiviAutoPropria(String altriMotiviAutoPropria) {
		this.altriMotiviAutoPropria = altriMotiviAutoPropria;
	}
	public String getUsernameFirmatarioAggiunto() {
		return usernameFirmatarioAggiunto;
	}
	public void setUsernameFirmatarioAggiunto(String usernameFirmatarioAggiunto) {
		this.usernameFirmatarioAggiunto = usernameFirmatarioAggiunto;
	}
	public String getUsernameFirmatarioSpesaAggiunto() {
		return usernameFirmatarioSpesaAggiunto;
	}
	public void setUsernameFirmatarioSpesaAggiunto(String usernameFirmatarioSpesaAggiunto) {
		this.usernameFirmatarioSpesaAggiunto = usernameFirmatarioSpesaAggiunto;
	}
}
