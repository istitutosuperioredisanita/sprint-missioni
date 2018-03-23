package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

/**
 * A user.
 */
//@Entity
//@Inheritance
public abstract class Missione extends OggettoBulkXmlTransient implements Serializable {

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

	@Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    public String uidInsert;

    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    public String uid;

    @Column(name = "ANNO", length = 4, nullable = false)
    public Integer anno;

    @Column(name = "NUMERO", length = 50, nullable = false)
    public Long numero;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_INSERIMENTO", nullable = false)
    public Date dataInserimento;

    @Size(min = 0, max = 40)
    @Column(name = "COMUNE_RESIDENZA_RICH", length = 40, nullable = false)
    public String comuneResidenzaRich;

    @Size(min = 0, max = 80)
    @Column(name = "INDIRIZZO_RESIDENZA_RICH", length = 80, nullable = false)
    public String indirizzoResidenzaRich;

    @Size(min = 0, max = 100)
    @Column(name = "DOMICILIO_FISCALE_RICH", length = 100, nullable = true)
    public String domicilioFiscaleRich;

    @Size(min = 0, max = 250)
    @Column(name = "DATORE_LAVORO_RICH", length = 250, nullable = true)
    public String datoreLavoroRich;

    @Size(min = 0, max = 50)
    @Column(name = "CONTRATTO_RICH", length = 50, nullable = true)
    public String contrattoRich;

    @Size(min = 0, max = 50)
    @Column(name = "QUALIFICA_RICH", length = 50, nullable = true)
    public String qualificaRich;

    @Column(name = "LIVELLO_RICH", length = 4, nullable = true)
    public Integer livelloRich;

    @Size(min = 0, max = 1000)
    @Column(name = "OGGETTO", length = 1000, nullable = false)
    public String oggetto;

    @Size(min = 0, max = 200)
    @Column(name = "DESTINAZIONE", length = 200, nullable = false)
    public String destinazione;

    @Column(name = "NAZIONE", length = 10, nullable = true)
    public Long nazione;

    @Size(min = 0, max = 3)
    @Column(name = "TIPO_MISSIONE", length = 3, nullable = false)
    public String tipoMissione;

    @Size(min = 0, max = 3)
    @Column(name = "TRATTAMENTO", length = 3, nullable = false)
    public String trattamento;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_INIZIO_MISSIONE", nullable = false)
    public Date dataInizioMissione;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_FINE_MISSIONE", nullable = false)
    public Date dataFineMissione;

    @Size(min = 0, max = 1)
    @Column(name = "VALIDATO", length = 1, nullable = false)
    public String validato;

    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_TAXI", length = 1, nullable = false)
    public String utilizzoTaxi;

    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_AUTO_NOLEGGIO", length = 1, nullable = false)
    public String utilizzoAutoNoleggio;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE", length = 1000, nullable = true)
    public String note;

    @Size(min = 0, max = 28)
    @Column(name = "VOCE", length = 28, nullable = true)
    public String voce;

    @Size(min = 0, max = 28)
    @Column(name = "GAE", length = 28, nullable = true)
    public String gae;

    @Size(min = 0, max = 28)
    @Column(name = "CDR_RICH", length = 28, nullable = true)
    public String cdrRich;

    @Size(min = 0, max = 28)
    @Column(name = "UO_RICH", length = 28, nullable = false)
    public String uoRich;

    @Size(min = 0, max = 28)
    @Column(name = "CDR_SPESA", length = 28, nullable = true)
    public String cdrSpesa;

    @Size(min = 0, max = 28)
    @Column(name = "UO_SPESA", length = 28, nullable = false)
    public String uoSpesa;

    @Size(min = 0, max = 28)
    @Column(name = "CDS_COMPETENZA", length = 28, nullable = true)
    public String cdsCompetenza;

    @Size(min = 0, max = 28)
    @Column(name = "UO_COMPETENZA", length = 28, nullable = false)
    public String uoCompetenza;

    @Size(min = 0, max = 28)
    @Column(name = "CDS_RICH", length = 28, nullable = false)
    public String cdsRich;

    @Size(min = 0, max = 28)
    @Column(name = "CDS_SPESA", length = 28, nullable = false)
    public String cdsSpesa;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    public String stato;

    @Column(name = "PG_PROGETTO", length = 20, nullable = true)
    public Long pgProgetto;

    @Size(min = 0, max = 30)
    @Column(name = "CD_CDS_OBBLIGAZIONE", length = 30, nullable = true)
    public String cdCdsObbligazione;

    @Column(name = "ESERCIZIO_OBBLIGAZIONE", length = 4, nullable = true)
    public Integer esercizioObbligazione;

    @Column(name = "PG_OBBLIGAZIONE", length = 50, nullable = true)
    public Long pgObbligazione;

    @Column(name = "ESERCIZIO_ORIGINALE_OBBLIGAZIONE", length = 4, nullable = true)
    public Integer esercizioOriginaleObbligazione;

    @Size(min = 0, max = 100)
    @Column(name = "ID_FLUSSO", length = 100, nullable = true)
    public String idFlusso;

    @Size(min = 0, max = 3)
    @Column(name = "STATO_FLUSSO", length = 3, nullable = false)
    public String statoFlusso;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE_UTILIZZO_TAXI_NOLEGGIO", length = 1000, nullable = true)
    public String noteUtilizzoTaxiNoleggio;

    @Size(min = 0, max = 1)
    @Column(name = "PARTENZA_DA", length = 1, nullable = false)
    private String partenzaDa;

	public Missione(){
		super();
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Serializable getId() {
		return id;
	}

    public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Date getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Date dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	public String getComuneResidenzaRich() {
		return comuneResidenzaRich;
	}

	public void setComuneResidenzaRich(String comuneResidenzaRich) {
		this.comuneResidenzaRich = comuneResidenzaRich;
	}

	public String getIndirizzoResidenzaRich() {
		return indirizzoResidenzaRich;
	}

	public void setIndirizzoResidenzaRich(String indirizzoResidenzaRich) {
		this.indirizzoResidenzaRich = indirizzoResidenzaRich;
	}

	public String getDomicilioFiscaleRich() {
		return domicilioFiscaleRich;
	}

	public void setDomicilioFiscaleRich(String domicilioFiscaleRich) {
		this.domicilioFiscaleRich = domicilioFiscaleRich;
	}

	public String getDatoreLavoroRich() {
		return datoreLavoroRich;
	}

	public void setDatoreLavoroRich(String datoreLavoroRich) {
		this.datoreLavoroRich = datoreLavoroRich;
	}

	public String getContrattoRich() {
		return contrattoRich;
	}

	public void setContrattoRich(String contrattoRich) {
		this.contrattoRich = contrattoRich;
	}

	public String getQualificaRich() {
		return qualificaRich;
	}

	public void setQualificaRich(String qualificaRich) {
		this.qualificaRich = qualificaRich;
	}

	public Integer getLivelloRich() {
		return livelloRich;
	}

	public void setLivelloRich(Integer livelloRich) {
		this.livelloRich = livelloRich;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTrattamento() {
		return trattamento;
	}

	public void setTrattamento(String trattamento) {
		this.trattamento = trattamento;
	}

	public Date getDataInizioMissione() {
		return dataInizioMissione;
	}

	public void setDataInizioMissione(Date dataInizioMissione) {
		this.dataInizioMissione = dataInizioMissione;
	}

	public Date getDataFineMissione() {
		return dataFineMissione;
	}

	public void setDataFineMissione(Date dataFineMissione) {
		this.dataFineMissione = dataFineMissione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getVoce() {
		return voce;
	}

	public void setVoce(String voce) {
		this.voce = voce;
	}

	public String getGae() {
		return gae;
	}

	public void setGae(String gae) {
		this.gae = gae;
	}

	public String getCdrRich() {
		return cdrRich;
	}

	public void setCdrRich(String cdrRich) {
		this.cdrRich = cdrRich;
	}

	public String getUoRich() {
		return uoRich;
	}

	public void setUoRich(String uoRich) {
		this.uoRich = uoRich;
	}

	public String getCdrSpesa() {
		return cdrSpesa;
	}

	public void setCdrSpesa(String cdrSpesa) {
		this.cdrSpesa = cdrSpesa;
	}

	public String getUoSpesa() {
		return uoSpesa;
	}

	public void setUoSpesa(String uoSpesa) {
		this.uoSpesa = uoSpesa;
	}

	public String getCdsRich() {
		return cdsRich;
	}

	public void setCdsRich(String cdsRich) {
		this.cdsRich = cdsRich;
	}

	public String getCdsSpesa() {
		return cdsSpesa;
	}

	public void setCdsSpesa(String cdsSpesa) {
		this.cdsSpesa = cdsSpesa;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getDestinazione() {
		return destinazione;
	}

	public void setDestinazione(String destinazione) {
		this.destinazione = destinazione;
	}

	public String getTipoMissione() {
		return tipoMissione;
	}

	public void setTipoMissione(String tipoMissione) {
		this.tipoMissione = tipoMissione;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public Long getNazione() {
		return nazione;
	}

	public void setNazione(Long nazione) {
		this.nazione = nazione;
	}

	public String getCdCdsObbligazione() {
		return cdCdsObbligazione;
	}

	public void setCdCdsObbligazione(String cdCdsObbligazione) {
		this.cdCdsObbligazione = cdCdsObbligazione;
	}

	public Integer getEsercizioObbligazione() {
		return esercizioObbligazione;
	}

	public void setEsercizioObbligazione(Integer esercizioObbligazione) {
		this.esercizioObbligazione = esercizioObbligazione;
	}

	public Long getPgObbligazione() {
		return pgObbligazione;
	}

	public void setPgObbligazione(Long pgObbligazione) {
		this.pgObbligazione = pgObbligazione;
	}

	public Integer getEsercizioOriginaleObbligazione() {
		return esercizioOriginaleObbligazione;
	}

	public void setEsercizioOriginaleObbligazione(
			Integer esercizioOriginaleObbligazione) {
		this.esercizioOriginaleObbligazione = esercizioOriginaleObbligazione;
	}

	public Long getPgProgetto() {
		return pgProgetto;
	}

	public void setPgProgetto(Long pgProgetto) {
		this.pgProgetto = pgProgetto;
	}

	@Transient
	public String decodeTipoMissione(){
		if (!StringUtils.isEmpty(getTipoMissione())){
			return Costanti.TIPO_MISSIONE.get(getTipoMissione());
		}
		return "";
	}
	
	@Transient
	public String decodeTrattamento(){
		if (!StringUtils.isEmpty(getTrattamento())){
			return Costanti.TRATTAMENTO.get(getTrattamento());
		}
		return "";
	}
	
	@Transient
	public Boolean isMissioneEstera() {
		if (getTipoMissione() != null && getTipoMissione().equals(Costanti.MISSIONE_ESTERA)){
			return true;
		}
		return false;
	}

	public String getUtilizzoTaxi() {
		return utilizzoTaxi;
	}

	public void setUtilizzoTaxi(String utilizzoTaxi) {
		this.utilizzoTaxi = utilizzoTaxi;
	}

	public String getUtilizzoAutoNoleggio() {
		return utilizzoAutoNoleggio;
	}

	public void setUtilizzoAutoNoleggio(String utilizzoAutoNoleggio) {
		this.utilizzoAutoNoleggio = utilizzoAutoNoleggio;
	}

	@Transient
	public String getDecodeStato() {
		if (!StringUtils.isEmpty(getStato())){
			return Costanti.STATO.get(getStato());
		}
		return "";
	}
	
	@Transient
	public String decodeUtilizzoTaxi(){
		if (!StringUtils.isEmpty(getUtilizzoTaxi())){
			return Costanti.SI_NO.get(getUtilizzoTaxi());
		}
		return "";
	}
	
	@Transient
	public String decodeUtilizzoAutoNoleggio(){
		if (!StringUtils.isEmpty(getUtilizzoAutoNoleggio())){
			return Costanti.SI_NO.get(getUtilizzoAutoNoleggio());
		}
		return "";
	}
	
    
    @Transient
    public Boolean isMissioneConGiorniDivervi() {
    	if (getDataFineMissione() != null && getDataInizioMissione() != null){
            Date dataInizioSenzaOre = Utility.getDateWithoutHours(getDataInizioMissione());
            Date dataFineSenzaOre = Utility.getDateWithoutHours(getDataFineMissione());
        	if (dataFineSenzaOre.after(dataInizioSenzaOre)){
        		return true;
        	} 
    	}
    	return false;
    }

	public String getNoteUtilizzoTaxiNoleggio() {
		return noteUtilizzoTaxiNoleggio;
	}

	public void setNoteUtilizzoTaxiNoleggio(String noteUtilizzoTaxiNoleggio) {
		this.noteUtilizzoTaxiNoleggio = noteUtilizzoTaxiNoleggio;
	} 

	@Transient
    public Boolean isMissioneConfermata() {
		if (!StringUtils.isEmpty(getStato())){
        	if (getStato().equals(Costanti.STATO_CONFERMATO)){
        		return true;
        	} 
    	}
    	return false;
    }

	@Transient
    public Boolean isMissioneDefinitiva() {
		if (!StringUtils.isEmpty(getStato())){
        	if (getStato().equals(Costanti.STATO_DEFINITIVO)){
        		return true;
        	} 
    	}
    	return false;
    }

	@Transient
    public Boolean isMissioneInserita() {
		if (!StringUtils.isEmpty(getStato())){
        	if (getStato().equals(Costanti.STATO_INSERITO)){
        		return true;
        	} 
    	}
    	return false;
    }

	@Transient
    public Boolean isMissioneDaValidare() {
		if (!StringUtils.isEmpty(getValidato())){
        	if (getValidato().equals("N")){
        		return true;
        	} 
    	}
    	return false;
    }

	public String getIdFlusso() {
		return idFlusso;
	}

	public void setIdFlusso(String idFlusso) {
		this.idFlusso = idFlusso;
	}

	public String getStatoFlusso() {
		return statoFlusso;
	}

	public void setStatoFlusso(String statoFlusso) {
		this.statoFlusso = statoFlusso;
	}

	public String getUidInsert() {
		return uidInsert;
	}

	public void setUidInsert(String uidInsert) {
		this.uidInsert = uidInsert;
	}

	public Boolean isStatoInviatoAlFlusso(){
		if (!StringUtils.isEmpty(getStatoFlusso()) && getStatoFlusso().equals(Costanti.STATO_INVIATO_FLUSSO)){
			return true;
		}
		return false;
	}

	public Boolean isStatoNonInviatoAlFlusso(){
		if (!StringUtils.isEmpty(getStatoFlusso()) && getStatoFlusso().equals(Costanti.STATO_INSERITO)){
			return true;
		}
		return false;
	}

	public Boolean isStatoFlussoApprovato(){
		if (getStatoFlusso() != null && getStatoFlusso().equals(Costanti.STATO_APPROVATO_FLUSSO)){
			return true;
		}
		return false;
	}

	public String getCdsCompetenza() {
		return cdsCompetenza;
	}

	public void setCdsCompetenza(String cdsCompetenza) {
		this.cdsCompetenza = cdsCompetenza;
	}

	public String getUoCompetenza() {
		return uoCompetenza;
	}

	public void setUoCompetenza(String uoCompetenza) {
		this.uoCompetenza = uoCompetenza;
	}

	public String getValidato() {
		return validato;
	}

	public void setValidato(String validato) {
		this.validato = validato;
	}

	public String getPartenzaDa() {
		return partenzaDa;
	}

	public void setPartenzaDa(String partenzaDa) {
		this.partenzaDa = partenzaDa;
	}

	@Transient
	public String decodePartenzaDa(){
		if (!StringUtils.isEmpty(getPartenzaDa())){
			return Costanti.PARTENZA_DA.get(getPartenzaDa());
		}
		return "";
	}
	
}
