package it.cnr.si.missioni.domain.custom.persistence;


import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.springframework.util.StringUtils;

/**
 * A user.
 */
@Entity
@Table(name = "DATI_ISTITUTO")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_DATI_ISTITUTO", allocationSize=0)
public class DatiIstituto extends OggettoBulkXmlTransient implements Serializable, Cloneable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

//    @JsonIgnore
    @Size(min = 0, max = 20)
    @Column(name = "ISTITUTO", length = 20, nullable = false)
    private String istituto;

    @Column(name = "ANNO", length = 4, nullable = false)
    private Integer anno;

    @Column(name = "PROGRESSIVO_ORDINE", length = 20, nullable = true)
    private Long progressivoOrdine;

    @Column(name = "PROGRESSIVO_RIMBORSO", length = 20, nullable = true)
    private Long progressivoRimborso;

    @Size(min = 0, max = 250)
    @Column(name = "DESCR_ISTITUTO", length = 250, nullable = false)
    private String descrIstituto;

    @Size(min = 0, max = 1)
    @Column(name = "GESTIONE_RESP_MODULO", length = 1, nullable = false)
    private String gestioneRespModulo;

    @Size(min = 0, max = 200)
    @Column(name = "MAIL_NOTIFICHE", length = 200, nullable = true)
    private String mailNotifiche;

    @Size(min = 0, max = 200)
    @Column(name = "MAIL_NOTIFICHE_RIMBORSO", length = 200, nullable = true)
    private String mailNotificheRimborso;

    @Size(min = 0, max = 200)
    @Column(name = "MAIL_DOPO_ORDINE", length = 200, nullable = true)
    private String mailDopoOrdine;

    @Size(min = 0, max = 200)
    @Column(name = "MAIL_DOPO_RIMBORSO", length = 200, nullable = true)
    private String mailDopoRimborso;

    @Size(min = 0, max = 200)
    @Column(name = "RESPONSABILE", length = 200, nullable = true)
    private String responsabile;

    @Size(min = 0, max = 1)
    @Column(name = "RESPONSABILE_SOLO_ITALIA", length = 1, nullable = true)
    private String responsabileSoloItalia;

    @Column(name = "DATA_BLOCCO_RIMBORSI_TAM", nullable = true)
    public LocalDate dataBloccoRimborsiTam;

    @Column(name = "DATA_BLOCCO_RIMBORSI", nullable = true)
    public LocalDate dataBloccoRimborsi;

    @Size(min = 0, max = 1)
    @Column(name = "TIPO_MAIL_DOPO_ORDINE", length = 1, nullable = true)
    private String tipoMailDopoOrdine;

    @Size(min = 0, max = 1)
    @Column(name = "TIPO_MAIL_DOPO_RIMBORSO", length = 1, nullable = true)
    private String tipoMailDopoRimborso;

    @Size(min = 0, max = 1)
    @Column(name = "OBBLIGO_ALLEGATI_VALIDAZIONE", length = 1, nullable = true)
    private String obbligoAllegatiValidazione;

    @Size(min = 0, max = 20)
    @Column(name = "UO_RESP_ESTERO", length = 20, nullable = true)
    private String uoRespEstero;

    @Column(name = "MINUTI_MINIMI_RESP", length = 20, nullable = true)
    private Long minutiMinimiResp;

    @Column(name = "MINUTI_PRIMA_INIZIO_RESP", length = 20, nullable = true)
    private Long minutiPrimaInizioResp;

    @Column(name = "MINUTI_PASSATI_RESP", length = 20, nullable = true)
    private Long minutiPassatiResp;

    @Column(name = "MINUTI_MINIMI_AMM", length = 20, nullable = true)
    private Long minutiMinimiAmm;

    @Column(name = "MINUTI_PRIMA_INIZIO_AMM", length = 20, nullable = true)
    private Long minutiPrimaInizioAmm;

    @Column(name = "MINUTI_PASSATI_AMM", length = 20, nullable = true)
    private Long minutiPassatiAmm;

    @Column(name = "CREA_IMPEGNO_AUT", length = 1, nullable = true)
    private String creaImpegnoAut;

    @Column(name = "TERZO_IMP_DEFAULT", length = 8, nullable = true)
    private Long terzoImpDefault;

    @Column(name = "GAE_IMP_DEFAULT", length = 28, nullable = true)
    public String gaeImpDefault;

    @Column(name = "PROGETTO_IMP_DEFAULT", length = 20, nullable = true)
    public Long progettoImpDefault;

    @Size(min = 0, max = 20)
    @Column(name = "UO_RESP_RESPONSABILI", length = 20, nullable = true)
    private String uoRespResponsabili;

    @Column(name = "SALTA_FIRMA_UOS_UO_CDS", length = 1, nullable = true)
    public String saltaFirmaUosUoCds;

    @Column(name = "UO_FIRMA_AGGIUNTA", length = 20, nullable = true)
    public String uoFirmaAggiunta;

    @Column(name = "PROGRESSIVO_ANNULLAMENTO", length = 20, nullable = true)
    private Long progressivoAnnullamento;

    @Column(name = "PROGR_ANNULL_RIMBORSO", length = 20, nullable = true)
    private Long progrAnnullRimborso;

	public void setId(Long id) {
		this.id = id;
	}

	//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		AutoPropria other = (AutoPropria) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//
	@Override
	public Serializable getId() {
		return id;
	}

	public String getIstituto() {
		return istituto;
	}

	public void setIstituto(String istituto) {
		this.istituto = istituto;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public Long getProgressivoOrdine() {
		return progressivoOrdine;
	}

	public void setProgressivoOrdine(Long progressivoOrdine) {
		this.progressivoOrdine = progressivoOrdine;
	}

	public Long getProgressivoRimborso() {
		return progressivoRimborso;
	}

	public void setProgressivoRimborso(Long progressivoRimborso) {
		this.progressivoRimborso = progressivoRimborso;
	}

	public String getDescrIstituto() {
		return descrIstituto;
	}

	public void setDescrIstituto(String descrIstituto) {
		this.descrIstituto = descrIstituto;
	}

	public String getGestioneRespModulo() {
		return gestioneRespModulo;
	}

	public void setGestioneRespModulo(String gestioneRespModulo) {
		this.gestioneRespModulo = gestioneRespModulo;
	}

	@Transient
    public Boolean isAttivaGestioneResponsabileModulo() {
		if (!StringUtils.isEmpty(getGestioneRespModulo())){
        	if (getGestioneRespModulo().equals("S")){
        		return true;
        	} 
    	}
    	return false;
    }

	public String getMailNotifiche() {
		return mailNotifiche;
	}

	public void setMailNotifiche(String mailNotifiche) {
		this.mailNotifiche = mailNotifiche;
	}

	public String getResponsabile() {
		return responsabile;
	}

	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}

	public String getResponsabileSoloItalia() {
		return responsabileSoloItalia;
	}

	public void setResponsabileSoloItalia(String responsabileSoloItalia) {
		this.responsabileSoloItalia = responsabileSoloItalia;
	}

	public String getMailNotificheRimborso() {
		return mailNotificheRimborso;
	}

	public void setMailNotificheRimborso(String mailNotificheRimborso) {
		this.mailNotificheRimborso = mailNotificheRimborso;
	}

	public LocalDate getDataBloccoRimborsiTam() {
		return dataBloccoRimborsiTam;
	}
	public void setDataBloccoRimborsiTam(LocalDate dataBloccoRimborsiTam) {
		this.dataBloccoRimborsiTam = dataBloccoRimborsiTam;
	}
	public LocalDate getDataBloccoRimborsi() {
		return dataBloccoRimborsi;
	}
	public void setDataBloccoRimborsi(LocalDate dataBloccoRimborsi) {
		this.dataBloccoRimborsi = dataBloccoRimborsi;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {

	    return super.clone();
	}

	public String getMailDopoOrdine() {
		return mailDopoOrdine;
	}

	public void setMailDopoOrdine(String mailDopoOrdine) {
		this.mailDopoOrdine = mailDopoOrdine;
	}

	public String getMailDopoRimborso() {
		return mailDopoRimborso;
	}

	public void setMailDopoRimborso(String mailDopoRimborso) {
		this.mailDopoRimborso = mailDopoRimborso;
	}

	public String getTipoMailDopoOrdine() {
		return tipoMailDopoOrdine;
	}

	public void setTipoMailDopoOrdine(String tipoMailDopoOrdine) {
		this.tipoMailDopoOrdine = tipoMailDopoOrdine;
	}

	public String getTipoMailDopoRimborso() {
		return tipoMailDopoRimborso;
	}

	public void setTipoMailDopoRimborso(String tipoMailDopoRimborso) {
		this.tipoMailDopoRimborso = tipoMailDopoRimborso;
	}

	public String getObbligoAllegatiValidazione() {
		return obbligoAllegatiValidazione;
	}

	public void setObbligoAllegatiValidazione(String obbligoAllegatiValidazione) {
		this.obbligoAllegatiValidazione = obbligoAllegatiValidazione;
	}

	public String getUoRespEstero() {
		return uoRespEstero;
	}

	public void setUoRespEstero(String uoRespEstero) {
		this.uoRespEstero = uoRespEstero;
	}

	public Long getMinutiMinimiResp() {
		return minutiMinimiResp;
	}

	public void setMinutiMinimiResp(Long minutiMinimiResp) {
		this.minutiMinimiResp = minutiMinimiResp;
	}

	public Long getMinutiPrimaInizioResp() {
		return minutiPrimaInizioResp;
	}

	public void setMinutiPrimaInizioResp(Long minutiPrimaInizioResp) {
		this.minutiPrimaInizioResp = minutiPrimaInizioResp;
	}

	public Long getMinutiPassatiResp() {
		return minutiPassatiResp;
	}

	public void setMinutiPassatiResp(Long minutiPassatiResp) {
		this.minutiPassatiResp = minutiPassatiResp;
	}

	public Long getMinutiMinimiAmm() {
		return minutiMinimiAmm;
	}

	public void setMinutiMinimiAmm(Long minutiMinimiAmm) {
		this.minutiMinimiAmm = minutiMinimiAmm;
	}

	public Long getMinutiPrimaInizioAmm() {
		return minutiPrimaInizioAmm;
	}

	public void setMinutiPrimaInizioAmm(Long minutiPrimaInizioAmm) {
		this.minutiPrimaInizioAmm = minutiPrimaInizioAmm;
	}

	public Long getMinutiPassatiAmm() {
		return minutiPassatiAmm;
	}

	public void setMinutiPassatiAmm(Long minutiPassatiAmm) {
		this.minutiPassatiAmm = minutiPassatiAmm;
	}

	public String getCreaImpegnoAut() {
		return creaImpegnoAut;
	}

	public void setCreaImpegnoAut(String creaImpegnoAut) {
		this.creaImpegnoAut = creaImpegnoAut;
	}

	public Long getTerzoImpDefault() {
		return terzoImpDefault;
	}

	public void setTerzoImpDefault(Long terzoImpDefault) {
		this.terzoImpDefault = terzoImpDefault;
	}

	public String getGaeImpDefault() {
		return gaeImpDefault;
	}

	public void setGaeImpDefault(String gaeImpDefault) {
		this.gaeImpDefault = gaeImpDefault;
	}

	public Long getProgettoImpDefault() {
		return progettoImpDefault;
	}

	public void setProgettoImpDefault(Long progettoImpDefault) {
		this.progettoImpDefault = progettoImpDefault;
	}

	public String getUoRespResponsabili() {
		return uoRespResponsabili;
	}

	public void setUoRespResponsabili(String uoRespResponsabili) {
		this.uoRespResponsabili = uoRespResponsabili;
	}

	public String getSaltaFirmaUosUoCds() {
		return saltaFirmaUosUoCds;
	}

	public void setSaltaFirmaUosUoCds(String saltaFirmaUosUoCds) {
		this.saltaFirmaUosUoCds = saltaFirmaUosUoCds;
	}

	public String getUoFirmaAggiunta() {
		return uoFirmaAggiunta;
	}

	public void setUoFirmaAggiunta(String uoFirmaAggiunta) {
		this.uoFirmaAggiunta = uoFirmaAggiunta;
	}

	public Long getProgrAnnullRimborso() {
		return progrAnnullRimborso;
	}

	public void setProgrAnnullRimborso(Long progrAnnullRimborso) {
		this.progrAnnullRimborso = progrAnnullRimborso;
	}

	public Long getProgressivoAnnullamento() {
		return progressivoAnnullamento;
	}

	public void setProgressivoAnnullamento(Long progressivoAnnullamento) {
		this.progressivoAnnullamento = progressivoAnnullamento;
	}

}
