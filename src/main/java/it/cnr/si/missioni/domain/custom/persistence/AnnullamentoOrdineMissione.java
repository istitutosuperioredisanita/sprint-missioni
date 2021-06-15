package it.cnr.si.missioni.domain.custom.persistence;


import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.util.StringUtils;

import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import net.bzdyl.ejb3.criteria.Projection;
import net.bzdyl.ejb3.criteria.projections.Projections;

/**
 * A user.
 */
@Entity
@Table(name = "ANNULLAMENTO_ORDINE_MISSIONE")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_ANNULLAMENTO", allocationSize=0)
public class AnnullamentoOrdineMissione extends OggettoBulkXmlTransient {

	public final static String CMIS_PROPERTY_NAME_DOC_ANNULLAMENTO = "Principale";
	public final static String CMIS_PROPERTY_NAME_TIPODOC_ANNULLAMENTO = "Annullamento Ordine di Missione";

	@ManyToOne
	@JoinColumn(name="ID_ORDINE_MISSIONE", nullable=true)
	private OrdineMissione ordineMissione;

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

    @Column(name = "DATA_INSERIMENTO", nullable = false)
    public LocalDate dataInserimento;

    @Size(min = 0, max = 40)
    @Column(name = "COMUNE_RESIDENZA_RICH", length = 40, nullable = true)
    public String comuneResidenzaRich;

    @Size(min = 0, max = 6)
    @Column(name = "MATRICOLA", length = 6, nullable = true)
    private String matricola;

    @Size(min = 0, max = 80)
    @Column(name = "INDIRIZZO_RESIDENZA_RICH", length = 80, nullable = true)
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
    public String livelloRich;

    @Size(min = 0, max = 1000)
    @Column(name = "MOTIVO_ANNULLAMENTO", length = 1000, nullable = false)
    public String motivoAnnullamento;

    @Size(min = 0, max = 1)
    @Column(name = "VALIDATO", length = 1, nullable = false)
    public String validato;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    public String stato;

    @Size(min = 0, max = 100)
    @Column(name = "ID_FLUSSO", length = 100, nullable = true)
    public String idFlusso;

    @Size(min = 0, max = 3)
    @Column(name = "STATO_FLUSSO", length = 3, nullable = false)
    public String statoFlusso;

	@Size(min = 0, max = 1)
	@Column(name = "CONSENTI_RIMBORSO", length = 1, nullable = true)
	public String consentiRimborso;

	@Size(min = 0, max = 2000)
	@Column(name = "COMMENTO_FLUSSO", length = 1000, nullable = true)
	public String commentoFlusso;

	public String getCommentoFlusso() {
		return commentoFlusso;
	}

	public void setCommentoFlusso(String commentoFlusso) {
		this.commentoFlusso = commentoFlusso;
	}

	@Transient
    private String daValidazione;
	
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
	
	public AnnullamentoOrdineMissione(Long id, Integer anno, Long numero, LocalDate dataInserimento, String uid, String stato, String statoFlusso, String idFlusso, 
			String motivoAnnullamento, String validato){
		super();
		this.setId(id);
		this.setAnno(anno);
		this.setNumero(numero);
		this.setDataInserimento(dataInserimento);
		this.setUid(uid);
		this.setStatoFlusso(statoFlusso);
		this.setIdFlusso(idFlusso);
		this.setStato(stato);
		this.setMotivoAnnullamento(motivoAnnullamento);
		this.setValidato(validato);
	}

	public AnnullamentoOrdineMissione(){
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
			add(Projections.property("motivo")).
			add(Projections.property("validato"));

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
	public String constructCMISNomeFile() {
		StringBuffer nomeFile = new StringBuffer();
		nomeFile = nomeFile.append(Utility.lpad(this.getNumero().toString(),9,'0'));
		return nomeFile.toString();
	}

	@Transient
	public String getDecodeStatoFlusso() {
		if (!StringUtils.isEmpty(getStateFlows())){
			return Costanti.STATO_FLUSSO_RIMBORSO_FROM_CMIS.get(getStateFlows());
		} else {
			if (!StringUtils.isEmpty(getStatoFlusso())){
				return Costanti.STATO_FLUSSO.get(getStatoFlusso());
			}
		}
		return "";
	}
	
	@Transient
    public String getFileName() {
		return "AnnullamentoOrdineMissione"+getId()+".pdf";
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

	public LocalDate getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(LocalDate dataInserimento) {
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

	public String getLivelloRich() {
		return livelloRich;
	}

	public void setLivelloRich(String livelloRich) {
		this.livelloRich = livelloRich;
	}

	public String getStato() {
		return stato;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	@Transient
	public String getDecodeStato() {
		if (!StringUtils.isEmpty(getStato())){
			return Costanti.STATO.get(getStato());
		}
		return "";
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
		if (!StringUtils.isEmpty(getStatoFlusso()) && (getStatoFlusso().equals(Costanti.STATO_INVIATO_FLUSSO) || getStatoFlusso().equals(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO))){
			return true;
		}
		return false;
	}

	public Boolean isStatoRespintoFlusso(){
		if (!StringUtils.isEmpty(getStatoFlusso()) && (getStatoFlusso().equals(Costanti.STATO_RESPINTO_UO_FLUSSO) || getStatoFlusso().equals(Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO))){
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

	public String getValidato() {
		return validato;
	}

	public void setValidato(String validato) {
		this.validato = validato;
	}


	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public Boolean isMissioneDipendente(){
		if (StringUtils.isEmpty(getMatricola()) || getMatricola().equals("0")){
			return false;
		}
		return true;
	}

	public String getMotivoAnnullamento() {
		return motivoAnnullamento;
	}

	public void setMotivoAnnullamento(String motivoAnnullamento) {
		this.motivoAnnullamento = motivoAnnullamento;
	}

	public String getConsentiRimborso() {
		return consentiRimborso;
	}

	public void setConsentiRimborso(String consentiRimborso) {
		this.consentiRimborso = consentiRimborso;
	}
	public boolean isConsentitoRimborso(){
		return Utility.nvl(getConsentiRimborso(),"N").equals("S");
	}

	@Override
	public String toString() {
		return "AnnullamentoOrdineMissione{" +
				"id=" + id +
				", uidInsert='" + uidInsert + '\'' +
				", uid='" + uid + '\'' +
				", anno=" + anno +
				", numero=" + numero +
				", dataInserimento=" + dataInserimento +
				", comuneResidenzaRich='" + comuneResidenzaRich + '\'' +
				", matricola='" + matricola + '\'' +
				", indirizzoResidenzaRich='" + indirizzoResidenzaRich + '\'' +
				", domicilioFiscaleRich='" + domicilioFiscaleRich + '\'' +
				", datoreLavoroRich='" + datoreLavoroRich + '\'' +
				", contrattoRich='" + contrattoRich + '\'' +
				", qualificaRich='" + qualificaRich + '\'' +
				", livelloRich='" + livelloRich + '\'' +
				", motivoAnnullamento='" + motivoAnnullamento + '\'' +
				", validato='" + validato + '\'' +
				", stato='" + stato + '\'' +
				", idFlusso='" + idFlusso + '\'' +
				", statoFlusso='" + statoFlusso + '\'' +
				", consentiRimborso='" + consentiRimborso + '\'' +
				'}';
	}
}
