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
public class RimborsoMissione extends Missione {
	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

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
    private Long cdTerzoSigla;

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
    @Column(name = "NOTE_DIFFERENZE_ORDINE", length = 1000, nullable = true)
    private String noteDifferenzeOrdine;

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

	public void setStato(String stato) {
		this.stato = stato;
		if (!StringUtils.isEmpty(stato)){
			this.decodeStato = Costanti.STATO.get(stato);
		}
	}

	@XmlTransient
	public static Projection getProjectionForElencoMissioni() {
		return PROJECTIONLIST_ELENCO_MISSIONI;
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

	public String getCommentFlows() {
		return commentFlows;
	}

	public void setCommentFlows(String commentFlows) {
		this.commentFlows = commentFlows;
	}

	public String getStateFlows() {
		return stateFlows;
	}

	public void setStateFlows(String stateFlows) {
		this.stateFlows = stateFlows;
	}
	public String getStatoFlussoRitornoHome() {
		return statoFlussoRitornoHome;
	}

	public void setStatoFlussoRitornoHome(
			String statoFlussoRitornoHome) {
		this.statoFlussoRitornoHome = statoFlussoRitornoHome;
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

	public Long getCdTerzoSigla() {
		return cdTerzoSigla;
	}

	public void setCdTerzoSigla(Long cdTerzoSigla) {
		this.cdTerzoSigla = cdTerzoSigla;
	}

	public String getNoteDifferenzeOrdine() {
		return noteDifferenzeOrdine;
	}

	public void setNoteDifferenzeOrdine(String noteDifferenzeOrdine) {
		this.noteDifferenzeOrdine = noteDifferenzeOrdine;
	}
}
