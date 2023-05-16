package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.jada.criteria.Projection;
import it.cnr.jada.criteria.projections.Projections;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A user.
 */
@Entity
@Table(name = "MISSIONE_RESPINTA")
@SequenceGenerator(name="SEQUENZA", sequenceName="SEQ_MISSIONE_RESPINTA", allocationSize=0)
public class MissioneRespinta extends OggettoBulkXmlTransient {

	public final static String OPERAZIONE_MISSIONE_ORDINE = "OR";
	public final static String OPERAZIONE_MISSIONE_RIMBORSO = "RI";
	public final static String OPERAZIONE_MISSIONE_ANNULLAMENTO_ORDINE = "AO";
	public final static String OPERAZIONE_MISSIONE_ANNULLAMENTO_RIMBORSO = "AR";

	public final static String FASE_RESPINGI_UO = Costanti.STATO_RESPINTO_UO_FLUSSO;
	public final static String FASE_RESPINGI_UO_SPESA = Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO;
	public final static String FASE_RESPINGI_RESP_GRUPPO = "RRG";
	public final static String FASE_RESPINGI_AMMINISTRATIVI = "RAM";

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

	@Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    public String uidInsert;

	@Column(name="ID_MISSIONE", nullable=false, length = 20)
	private Long idMissione;

    @Column(name = "DATA_INSERIMENTO", nullable = false)
    public Timestamp dataInserimento;

    @Size(min = 0, max = 2)
    @Column(name = "TIPO_OPERAZIONE_MISSIONE", length = 2, nullable = false)
    public String tipoOperazioneMissione;

	@Size(min = 0, max = 3)
	@Column(name = "TIPO_FASE_RESPINGI", length = 3, nullable = false)
	public String tipoFaseRespingi;

    @Size(min = 0, max = 2000)
    @Column(name = "MOTIVO_RESPINGI", length = 2000, nullable = false)
    public String motivoRespingi;

	@Transient
	public String decodeFase;

	public final static Map<String, String> FASI;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put(FASE_RESPINGI_AMMINISTRATIVI, "Verifica Ammininistrativi");
		aMap.put(FASE_RESPINGI_RESP_GRUPPO, "Responsabile Gruppo");
		aMap.put(FASE_RESPINGI_UO, "Firma");
		aMap.put(FASE_RESPINGI_UO_SPESA, "Firma Uo Spesa");
		FASI = Collections.unmodifiableMap(aMap);
	}

	public MissioneRespinta(){
		super();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUidInsert() {
		return uidInsert;
	}

	public void setUidInsert(String uidInsert) {
		this.uidInsert = uidInsert;
	}

	public Long getIdMissione() {
		return idMissione;
	}

	public void setIdMissione(Long idMissione) {
		this.idMissione = idMissione;
	}

	public Timestamp getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Timestamp dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	public String getTipoOperazioneMissione() {
		return tipoOperazioneMissione;
	}

	public void setTipoOperazioneMissione(String tipoOperazioneMissione) {
		this.tipoOperazioneMissione = tipoOperazioneMissione;
	}

	public String getTipoFaseRespingi() {
		return tipoFaseRespingi;
	}

	@Transient
	public String getDecodeFase() {
		if (!StringUtils.isEmpty(getTipoFaseRespingi())){
			return Costanti.STATO_FLUSSO.get(getTipoFaseRespingi());
		}
		return "";
	}

	public void setTipoFaseRespingi(String tipoFaseRespingi) {
		this.tipoFaseRespingi = tipoFaseRespingi;
	}

	public String getMotivoRespingi() {
		return motivoRespingi;
	}

	public void setMotivoRespingi(String motivoRespingi) {
		this.motivoRespingi = motivoRespingi;
	}
}
