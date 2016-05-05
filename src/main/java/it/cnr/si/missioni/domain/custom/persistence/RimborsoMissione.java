package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.Cds;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import net.bzdyl.ejb3.criteria.Projection;
import net.bzdyl.ejb3.criteria.projections.Projections;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

/**
 * A user.
 */
@Entity
@Table(name = "RIMBORSO_MISSIONE")
public class RimborsoMissione extends OggettoBulkXmlTransient implements Serializable {

/*	public final static String CMIS_PROPERTY_MAIN = "F:missioni_ordine:main",
			CMIS_PROPERTY_NAME_ANNO = "missioni_ordine:anno",
			CMIS_PROPERTY_NAME_NUMERO = "missioni_ordine:numero",
			CMIS_PROPERTY_NAME_ID = "missioni_ordine:id",
			CMIS_PROPERTY_NAME_UID = "missioni_ordine:uid",
			CMIS_PROPERTY_NAME_MODULO = "missioni_ordine:modulo",
			CMIS_PROPERTY_NAME_OGGETTO = "missioni_ordine:oggetto",
			CMIS_PROPERTY_NAME_DESTINAZIONE = "missioni_ordine:destinazione",
			CMIS_PROPERTY_NAME_NOTE = "missioni_ordine:note",
			CMIS_PROPERTY_NAME_DATA_INIZIO = "missioni_ordine:dataInizio",
			CMIS_PROPERTY_NAME_DATA_FINE = "missioni_ordine:dataFine",
			CMIS_PROPERTY_NAME_DATA_INSERIMENTO = "missioni_ordine:dataInserimento",
			CMIS_PROPERTY_ATTACHMENT_DOCUMENT = "D:missioni_ordine_attachment:document",
			CMIS_PROPERTY_NAME_DOC_ORDINE = "Principale",
			CMIS_PROPERTY_NAME_DOC_ALLEGATO = "Allegato",
			CMIS_PROPERTY_NAME_TIPODOC_ORDINE = "Ordine di Missione",
			CMIS_PROPERTY_FLOW_DESCRIZIONE	= "cnrmissioni:descrizioneOrdine",
			CMIS_PROPERTY_FLOW_NOTE	= "cnrmissioni:note",
			CMIS_PROPERTY_FLOW_DUE_DATE	= "bpm:workflowDueDate",
			CMIS_PROPERTY_FLOW_PRIORITY	= "bpm:workflowPriority",
			CMIS_PROPERTY_FLOW_VALIDAZIONE_SPESA	= "cnrmissioni:validazioneSpesaFlag",
			CMIS_PROPERTY_FLOW_ANTICIPO	= "cnrmissioni:missioneConAnticipoFlag",
			CMIS_PROPERTY_FLOW_VALIDAZIONE_MODULO	= "cnrmissioni:validazioneModuloFlag",
			CMIS_PROPERTY_FLOW_USERNAME_ORDINE = "cnrmissioni:userNameUtenteOrdineMissione",
			CMIS_PROPERTY_FLOW_USERNAME_RICHIEDENTE	= "cnrmissioni:userNameRichiedente",
			CMIS_PROPERTY_FLOW_USERNAME_RESPONSABILE_MODULO	= "cnrmissioni:userNameResponsabileModulo",
			CMIS_PROPERTY_FLOW_USERNAME_FIRMA_UO	= "cnrmissioni:userNamePrimoFirmatario",
			CMIS_PROPERTY_FLOW_USERNAME_FIRMA_SPESA	= "cnrmissioni:userNameFirmatarioSpesa",
			CMIS_PROPERTY_FLOW_UO_ORDINE	= "cnrmissioni:uoOrdine",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_ORDINE	= "cnrmissioni:descrizioneUoOrdine",
			CMIS_PROPERTY_FLOW_UO_SPESA	= "cnrmissioni:uoSpesa",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_SPESA	= "cnrmissioni:descrizioneUoSpesa",
			CMIS_PROPERTY_FLOW_UO_COMPETENZA	= "cnrmissioni:uoCompetenza",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_COMPETENZA	= "cnrmissioni:descrizioneUoCompetenza",
			CMIS_PROPERTY_FLOW_DESTINAZIONE	= "cnrmissioni:destinazione",
			CMIS_PROPERTY_FLOW_ESTERA_FLAG	= "cnrmissioni:missioneEsteraFlag",
			CMIS_PROPERTY_FLOW_DATA_INIZIO_MISSIONE	= "cnrmissioni:dataInizioMissione",
			CMIS_PROPERTY_FLOW_DATA_FINE_MISSIONE	= "cnrmissioni:dataFineMissione",
			CMIS_PROPERTY_FLOW_AUTO_PROPRIA	= "cnrmissioni:autoPropriaFlag",
			CMIS_PROPERTY_FLOW_NOLEGGIO	= "cnrmissioni:noleggioFlag",
			CMIS_PROPERTY_FLOW_TAXI	= "cnrmissioni:taxiFlag",
			CMIS_PROPERTY_FLOW_CAPITOLO	= "cnrmissioni:capitolo",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_CAPITOLO	= "cnrmissioni:descrizioneCapitolo",
			CMIS_PROPERTY_FLOW_MODULO	= "cnrmissioni:modulo",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_MODULO	= "cnrmissioni:descrizioneModulo",
			CMIS_PROPERTY_FLOW_GAE	= "cnrmissioni:gae",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_GAE	= "cnrmissioni:descrizioneGae",
			CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_RES	= "cnrmissioni:impegnoAnnoResiduo",
			CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_COMP	= "cnrmissioni:impegnoAnnoCompetenza",
			CMIS_PROPERTY_FLOW_NUMERO_IMPEGNO	= "cnrmissioni:impegnoNumero",
			CMIS_PROPERTY_FLOW_DESCRIZIONE_IMPEGNO	= "cnrmissioni:descrizioneImpegno",
			CMIS_PROPERTY_FLOW_IMPORTO_MISSIONE	= "cnrmissioni:importoMissione",
			CMIS_PROPERTY_FLOW_DISPONIBILITA_IMPEGNO	= "cnrmissioni:disponibilita";
*/	
	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

    @Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    private String uidInsert;

    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    private String uid;

    @Column(name = "ANNO", length = 4, nullable = false)
    private Integer anno;

    @Column(name = "NUMERO", length = 50, nullable = false)
    private Long numero;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_INSERIMENTO", nullable = false)
    private Date dataInserimento;

    @Size(min = 0, max = 40)
    @Column(name = "COMUNE_RESIDENZA_RICH", length = 40, nullable = false)
    private String comuneResidenzaRich;

    @Size(min = 0, max = 80)
    @Column(name = "INDIRIZZO_RESIDENZA_RICH", length = 80, nullable = false)
    private String indirizzoResidenzaRich;

    @Size(min = 0, max = 100)
    @Column(name = "DOMICILIO_FISCALE_RICH", length = 100, nullable = true)
    private String domicilioFiscaleRich;

    @Size(min = 0, max = 250)
    @Column(name = "DATORE_LAVORO_RICH", length = 250, nullable = true)
    private String datoreLavoroRich;

    @Size(min = 0, max = 50)
    @Column(name = "CONTRATTO_RICH", length = 50, nullable = true)
    private String contrattoRich;

    @Size(min = 0, max = 50)
    @Column(name = "QUALIFICA_RICH", length = 50, nullable = true)
    private String qualificaRich;

    @Column(name = "LIVELLO_RICH", length = 4, nullable = true)
    private Integer livelloRich;

    @Size(min = 0, max = 1000)
    @Column(name = "OGGETTO", length = 1000, nullable = false)
    private String oggetto;

    @Size(min = 0, max = 200)
    @Column(name = "DESTINAZIONE", length = 200, nullable = false)
    private String destinazione;

    @Column(name = "NAZIONE", length = 10, nullable = true)
    private Long nazione;

    @Size(min = 0, max = 3)
    @Column(name = "TIPO_MISSIONE", length = 3, nullable = false)
    private String tipoMissione;

    @Size(min = 0, max = 3)
    @Column(name = "TRATTAMENTO", length = 3, nullable = false)
    private String trattamento;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_INIZIO_MISSIONE", nullable = false)
    private Date dataInizioMissione;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_FINE_MISSIONE", nullable = false)
    private Date dataFineMissione;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_INIZIO_ESTERO", nullable = true)
    private Date dataInizioEstero;

    @Type(type = "java.util.Date")
    @Column(name = "DATA_FINE_ESTERO", nullable = true)
    private Date dataFineEstero;

    @Size(min = 0, max = 1)
    @Column(name = "VALIDATO", length = 1, nullable = false)
    private String validato;

    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_TAXI", length = 1, nullable = false)
    private String utilizzoTaxi;

    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_AUTO_NOLEGGIO", length = 1, nullable = false)
    private String utilizzoAutoNoleggio;

    @Column(name = "CD_TERZO_SIGLA", length = 8, nullable = false)
    private String cdTerzoSigla;

    @Size(min = 0, max = 5)
    @Column(name = "MODPAG", length = 5, nullable = false)
    private String modpag;

    @Size(min = 0, max = 34)
    @Column(name = "IBAN", length = 34, nullable = true)
    private String iban;

    @Size(min = 0, max = 1)
    @Column(name = "ANTICIPO_RICEVUTO", length = 1, nullable = false)
    private String anticipoRicevuto;

    @Column(name = "ANTICIPO_ANNO_MANDATO", length = 4, nullable = true)
    private Integer anticipoAnnoMandato;

    @Column(name = "ANTICIPO_NUMERO_MANDATO", length = 50, nullable = true)
    private Long anticipoNumeroMandato;

    @Column(name = "ANTICIPO_IMPORTO", length = 28, nullable = true)
    private BigDecimal anticipoImporto;

    @Size(min = 0, max = 250)
    @Column(name = "ALTRE_SPESE_ANT_DESCRIZIONE", length = 250, nullable = true)
    private String altreSpeseAntDescrizione;

    @Column(name = "ALTRE_SPESE_ANT_IMPORTO", length = 28, nullable = true)
    private BigDecimal altreSpeseAntImporto;

    @Size(min = 0, max = 1)
    @Column(name = "SPESE_TERZI_RICEVUTE", length = 1, nullable = false)
    private String speseTerziRicevute;

    @Column(name = "SPESE_TERZI_IMPORTO", length = 28, nullable = true)
    private BigDecimal speseTerziImporto;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE", length = 1000, nullable = true)
    private String note;

    @Size(min = 0, max = 28)
    @Column(name = "VOCE", length = 28, nullable = true)
    private String voce;

    @Size(min = 0, max = 28)
    @Column(name = "GAE", length = 28, nullable = true)
    private String gae;

    @Size(min = 0, max = 28)
    @Column(name = "CDR_RICH", length = 28, nullable = true)
    private String cdrRich;

    @Size(min = 0, max = 28)
    @Column(name = "UO_RICH", length = 28, nullable = false)
    private String uoRich;

    @Size(min = 0, max = 28)
    @Column(name = "CDR_SPESA", length = 28, nullable = true)
    private String cdrSpesa;

    @Size(min = 0, max = 28)
    @Column(name = "UO_SPESA", length = 28, nullable = false)
    private String uoSpesa;

    @Size(min = 0, max = 28)
    @Column(name = "CDS_COMPETENZA", length = 28, nullable = true)
    private String cdsCompetenza;

    @Size(min = 0, max = 28)
    @Column(name = "UO_COMPETENZA", length = 28, nullable = false)
    private String uoCompetenza;

    @Size(min = 0, max = 28)
    @Column(name = "CDS_RICH", length = 28, nullable = false)
    private String cdsRich;

    @Size(min = 0, max = 28)
    @Column(name = "CDS_SPESA", length = 28, nullable = false)
    private String cdsSpesa;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;

    @Column(name = "PG_PROGETTO", length = 20, nullable = true)
    private Long pgProgetto;

    @Size(min = 0, max = 30)
    @Column(name = "CD_CDS_OBBLIGAZIONE", length = 30, nullable = true)
    private String cdCdsObbligazione;

    @Column(name = "ESERCIZIO_OBBLIGAZIONE", length = 4, nullable = true)
    private Integer esercizioObbligazione;

    @Column(name = "PG_OBBLIGAZIONE", length = 50, nullable = true)
    private Long pgObbligazione;

    @Column(name = "ESERCIZIO_ORIGINALE_OBBLIGAZIONE", length = 4, nullable = true)
    private Integer esercizioOriginaleObbligazione;

    @Size(min = 0, max = 100)
    @Column(name = "ID_FLUSSO", length = 100, nullable = true)
    private String idFlusso;

    @Size(min = 0, max = 3)
    @Column(name = "STATO_FLUSSO", length = 3, nullable = false)
    private String statoFlusso;

    @Size(min = 0, max = 1000)
    @Column(name = "NOTE_UTILIZZO_TAXI_NOLEGGIO", length = 1000, nullable = true)
    private String noteUtilizzoTaxiNoleggio;

	@ManyToOne
	@JoinColumn(name="ID_ORDINE_MISSIONE", nullable=true)
	private OrdineMissione ordineMissione;

	@Transient
    private String daValidazione;
	
	@Transient
    private String utilizzoAutoPropria;
	
	@Transient
    private DatiIstituto datiIstituto;
	
	@Transient
    private String richiestaAnticipo;
	
	@Transient
    private Cds objCdsSpesa;
	
	@Transient
    private Cdr objCdrSpesa;
	
	@Transient
    private UnitaOrganizzativa objUoSpesa;
	
	@Transient
    private String decodeStato;
	
	@Transient
    private String decodeStatoFlusso;
	
	@Transient
	private String stateFlows;
	
	@Transient
    private String commentFlows;
	
	@Transient
    private String statoFlussoRitornoHome;
	
	public void setId(Long id) {
		this.id = id;
	}

	public RimborsoMissione(Long id, Integer anno, Long numero, Date dataInserimento, String uid, String stato, String statoFlusso, String idFlusso, String destinazione, 
			String oggetto, Date dataInizioMissione, Date dataFineMissione, String validato){
		super();
		this.setId(id);
		this.setAnno(anno);
		this.setNumero(numero);
		this.setDataInserimento(dataInserimento);
		this.setUid(uid);
		this.setStatoFlusso(statoFlusso);
		this.setIdFlusso(idFlusso);
		this.setStato(stato);
		this.setDestinazione(destinazione);
		this.setOggetto(oggetto);
		this.setDataInizioMissione(dataInizioMissione);
		this.setDataFineMissione(dataFineMissione);
		this.setValidato(validato);
	}

	public RimborsoMissione(){
		super();
	}

	public static final Projection PROJECTIONLIST_ELENCO_MISSIONI = Projections.projectionList().
			add(Projections.property("id")).
			add(Projections.property("anno")).
			add(Projections.property("numero")).
			add(Projections.property("dataInserimento")).
			add(Projections.property("uid")).
			add(Projections.property("stato")).
			add(Projections.property("statoFlusso")).
			add(Projections.property("idFlusso")).
			add(Projections.property("destinazione")).
			add(Projections.property("oggetto")).
			add(Projections.property("dataInizioMissione")).
			add(Projections.property("dataFineMissione")).
			add(Projections.property("validato"));

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
		if (!StringUtils.isEmpty(stato)){
			this.decodeStato = Costanti.STATO.get(stato);
		}
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

	@XmlTransient
	public static Projection getProjectionForElencoMissioni() {
		return PROJECTIONLIST_ELENCO_MISSIONI;
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
	public Cds getObjCdsSpesa() {
		return objCdsSpesa;
	}

	@Transient
	public void setObjCdsSpesa(Cds objCdsSpesa) {
		this.objCdsSpesa = objCdsSpesa;
	}

	public Cdr getObjCdrSpesa() {
		return objCdrSpesa;
	}

	public void setObjCdrSpesa(Cdr objCdrSpesa) {
		this.objCdrSpesa = objCdrSpesa;
	}

	public UnitaOrganizzativa getObjUoSpesa() {
		return objUoSpesa;
	}

	public void setObjUoSpesa(UnitaOrganizzativa objUoSpesa) {
		this.objUoSpesa = objUoSpesa;
	}

	@Transient
	public String constructCMISNomeFile() {
		StringBuffer nomeFile = new StringBuffer();
		nomeFile = nomeFile.append(Utility.lpad(this.getNumero().toString(),9,'0'));
		return nomeFile.toString();
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

	public String getUtilizzoAutoPropria() {
		return utilizzoAutoPropria;
	}

	public void setUtilizzoAutoPropria(String utilizzoAutoPropria) {
		this.utilizzoAutoPropria = utilizzoAutoPropria;
	}

	public String getRichiestaAnticipo() {
		return richiestaAnticipo;
	}

	public void setRichiestaAnticipo(String richiestaAnticipo) {
		this.richiestaAnticipo = richiestaAnticipo;
	}

	@Transient
	public String getDecodeStato() {
		if (!StringUtils.isEmpty(getStato())){
			return Costanti.STATO.get(getStato());
		}
		return "";
	}
	
	@Transient
	public String getDecodeStatoFlusso() {
		if (!StringUtils.isEmpty(getStateFlows())){
			return Costanti.STATO_FLUSSO_FROM_CMIS.get(getStateFlows());
		} else {
			if (!StringUtils.isEmpty(getStatoFlusso())){
				return Costanti.STATO_FLUSSO.get(getStatoFlusso());
			}
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

	public DatiIstituto getDatiIstituto() {
		return datiIstituto;
	}

	public void setDatiIstituto(DatiIstituto datiIstituto) {
		this.datiIstituto = datiIstituto;
	}

	@Transient
    public String getFileName() {
		return "OrdineMissione"+getId()+".pdf";
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

	public String getCommentFlows() {
		return commentFlows;
	}

	public void setCommentFlows(String commentFlows) {
		this.commentFlows = commentFlows;
	}

	public String getUidInsert() {
		return uidInsert;
	}

	public void setUidInsert(String uidInsert) {
		this.uidInsert = uidInsert;
	}

	public String getStateFlows() {
		return stateFlows;
	}

	public void setStateFlows(String stateFlows) {
		this.stateFlows = stateFlows;
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

	public String getStatoFlussoRitornoHome() {
		return statoFlussoRitornoHome;
	}

	public void setStatoFlussoRitornoHome(
			String statoFlussoRitornoHome) {
		this.statoFlussoRitornoHome = statoFlussoRitornoHome;
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

	public String getDaValidazione() {
		return daValidazione;
	}

	public void setDaValidazione(String daValidazione) {
		this.daValidazione = daValidazione;
	}

	public Date getDataInizioEstero() {
		return dataInizioEstero;
	}

	public void setDataInizioEstero(Date dataInizioEstero) {
		this.dataInizioEstero = dataInizioEstero;
	}

	public Date getDataFineEstero() {
		return dataFineEstero;
	}

	public void setDataFineEstero(Date dataFineEstero) {
		this.dataFineEstero = dataFineEstero;
	}

	public String getModpag() {
		return modpag;
	}

	public void setModpag(String modpag) {
		this.modpag = modpag;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getAnticipoRicevuto() {
		return anticipoRicevuto;
	}

	public void setAnticipoRicevuto(String anticipoRicevuto) {
		this.anticipoRicevuto = anticipoRicevuto;
	}

	public Integer getAnticipoAnnoMandato() {
		return anticipoAnnoMandato;
	}

	public void setAnticipoAnnoMandato(Integer anticipoAnnoMandato) {
		this.anticipoAnnoMandato = anticipoAnnoMandato;
	}

	public Long getAnticipoNumeroMandato() {
		return anticipoNumeroMandato;
	}

	public void setAnticipoNumeroMandato(Long anticipoNumeroMandato) {
		this.anticipoNumeroMandato = anticipoNumeroMandato;
	}

	public BigDecimal getAnticipoImporto() {
		return anticipoImporto;
	}

	public void setAnticipoImporto(BigDecimal anticipoImporto) {
		this.anticipoImporto = anticipoImporto;
	}

	public String getAltreSpeseAntDescrizione() {
		return altreSpeseAntDescrizione;
	}

	public void setAltreSpeseAntDescrizione(String altreSpeseAntDescrizione) {
		this.altreSpeseAntDescrizione = altreSpeseAntDescrizione;
	}

	public BigDecimal getAltreSpeseAntImporto() {
		return altreSpeseAntImporto;
	}

	public void setAltreSpeseAntImporto(BigDecimal altreSpeseAntImporto) {
		this.altreSpeseAntImporto = altreSpeseAntImporto;
	}

	public String getSpeseTerziRicevute() {
		return speseTerziRicevute;
	}

	public void setSpeseTerziRicevute(String speseTerziRicevute) {
		this.speseTerziRicevute = speseTerziRicevute;
	}

	public BigDecimal getSpeseTerziImporto() {
		return speseTerziImporto;
	}

	public void setSpeseTerziImporto(BigDecimal speseTerziImporto) {
		this.speseTerziImporto = speseTerziImporto;
	}

	public OrdineMissione getOrdineMissione() {
		return ordineMissione;
	}

	public void setOrdineMissione(OrdineMissione ordineMissione) {
		this.ordineMissione = ordineMissione;
	}

	public void setDecodeStato(String decodeStato) {
		this.decodeStato = decodeStato;
	}

	public void setDecodeStatoFlusso(String decodeStatoFlusso) {
		this.decodeStatoFlusso = decodeStatoFlusso;
	}

	public String getCdTerzoSigla() {
		return cdTerzoSigla;
	}

	public void setCdTerzoSigla(String cdTerzoSigla) {
		this.cdTerzoSigla = cdTerzoSigla;
	}
}
