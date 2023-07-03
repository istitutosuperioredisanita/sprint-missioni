/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.jada.criteria.Projection;
import it.cnr.jada.criteria.projections.Projections;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.Cds;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * A user.
 */
@Entity
@Table(name = "RIMBORSO_MISSIONE")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_RIMBORSO_MISSIONE", allocationSize = 0)
public class RimborsoMissione extends OggettoBulkXmlTransient {
    public static final String CMIS_PROPERTY_NAME_DOC_RIMBORSO = "Principale";
    public static final String CMIS_PROPERTY_NAME_DOC_ALLEGATO = "Allegati";
    public static final String CMIS_PROPERTY_VALUE_TIPODOC_RIMBORSO = "Rimborso Missione";
    public static final String CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO = "Allegati al Rimborso Missione";
    public static final String CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO_ANNULLAMENTO = "Allegati all'Annullamento del Rimborso Missione";
    public static final String CMIS_PROPERTY_NAME_TIPODOC_SCONTRINO = "Giustificativo";
    public static final String CMIS_PROPERTY_NAME_ID_ORDINE_MISSIONE = "missioni:ordine_id";
    public static final String CMIS_PROPERTY_NAME_TOT_RIMBORSO_MISSIONE = "missioni:totRimborsoMissione";
    public static final String CMIS_PROPERTY_FLOW_TOTALE_RIMBORSO_MISSIONE = "cnrmissioni:totaleRimborsoMissione";
    public static final String CMIS_PROPERTY_FLOW_ID_FLOW_ORDINE = "cnrmissioni:wfOrdineDaRimborso";
    public static final String CMIS_PROPERTY_FLOW_ANTICIPO_RICEVUTO = "cnrmissioni:anticipoRicevuto";
    public static final String CMIS_PROPERTY_FLOW_ANNO_MANDATO = "cnrmissioni:annoMandato";
    public static final String CMIS_PROPERTY_FLOW_NUMERO_MANDATO = "cnrmissioni:numeroMandatoOk";
    public static final String CMIS_PROPERTY_FLOW_IMPORTO_MANDATO = "cnrmissioni:importoMandato";
    public static final String RIMBORSO_MISSIONE_ATTACHMENT_QUERY_CMIS = "missioni_rimborso_attachment";
    public static final String CMIS_PROPERTY_ATTACHMENT_DOCUMENT = "D:missioni_rimborso_attachment:document";
    public static final String CMIS_PROPERTY_NAME_DATA_INIZIO_MISSIONE_ESTERO = "missioni:dataInizioMissioneEstero";
    public static final String CMIS_PROPERTY_NAME_DATA_FINE_MISSIONE_ESTERO = "missioni:dataFineMissioneEstero";
    public static final String CMIS_PROPERTY_FLOW_DIFFERENZE_ORDINE_RIMBORSO = "cnrmissioni:differenzeOrdineRimborso";
    public static final String CMIS_PROPERTY_RIMBORSO_ATTACHMENT_ELIMINATO = "missioni_rimborso_attachment:eliminato";
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
            add(Projections.property("validato")).
            add(Projections.property("uoRich")).
            add(Projections.property("trattamento")).
            add(Projections.property("validaAmm"));
    @Column(name = "PG_BANCA", length = 4, nullable = true)
    public Integer pgBanca;
    @Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    public String uidInsert;
    @Size(min = 0, max = 256)
    @Column(name = "USER_NAME", length = 256, nullable = false)
    public String uid;
    @Size(min = 0, max = 1)
    @Column(name = "AUTO_PROPRIA", length = 1, nullable = true)
    public String autoPropria;
    @Size(min = 0, max = 1)
    @Column(name = "VALIDA_AMM", length = 1, nullable = true)
    public String validaAmm;
    @Column(name = "CUG", length = 1, nullable = true)
    public String cug;
    @Column(name = "PRESIDENTE", length = 1, nullable = true)
    public String presidente;
    @Column(name = "ANNO", length = 4, nullable = false)
    public Integer anno;
    @Column(name = "NUMERO", length = 50, nullable = false)
    public Long numero;
    @Column(name = "DATA_INSERIMENTO", nullable = false)
    public LocalDate dataInserimento;
    @Size(min = 0, max = 40)
    @Column(name = "COMUNE_RESIDENZA_RICH", length = 40, nullable = true)
    public String comuneResidenzaRich;
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
    @Column(name = "DATA_INIZIO_MISSIONE", nullable = false)
    public ZonedDateTime dataInizioMissione;
    @Column(name = "DATA_FINE_MISSIONE", nullable = false)
    public ZonedDateTime dataFineMissione;
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
    @Size(min = 0, max = 1000)
    @Column(name = "NOTE_SEGRETERIA", length = 1000, nullable = true)
    public String noteSegreteria;
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
    @Column(name = "UO_COMPETENZA", length = 28, nullable = true)
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
    @Size(min = 0, max = 3)
    @Column(name = "STATO_INVIO_SIGLA", length = 3, nullable = true)
    public String statoInvioSigla;
    @Size(min = 0, max = 1000)
    @Column(name = "NOTE_UTILIZZO_TAXI_NOLEGGIO", length = 1000, nullable = true)
    public String noteUtilizzoTaxiNoleggio;
    @Column(name = "PG_MISSIONE_SIGLA", length = 10, nullable = true)
    public Long pgMissioneSigla;
    @Column(name = "ESERCIZIO_SIGLA", length = 4, nullable = true)
    public Integer esercizioSigla;
    @Size(min = 0, max = 30)
    @Column(name = "CD_CDS_SIGLA", length = 30, nullable = true)
    public String cdCdsSigla;
    @Size(min = 0, max = 30)
    @Column(name = "CD_UO_SIGLA", length = 30, nullable = true)
    public String cdUoSigla;
    @Size(min = 0, max = 10)
    @Column(name = "CD_TIPO_RAPPORTO", length = 10, nullable = true)
    public String cdTipoRapporto;
    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_AUTO_SERVIZIO", length = 1, nullable = false)
    public String utilizzoAutoServizio;
    @Size(min = 0, max = 1)
    @Column(name = "PERSONALE_AL_SEGUITO", length = 1, nullable = false)
    public String personaleAlSeguito;
    @Size(min = 0, max = 50)
    @Column(name = "CUP", length = 50, nullable = true)
    public String cup;
    @Size(min = 0, max = 2000)
    @Column(name = "NOTE_RESPINGI", length = 2000, nullable = true)
    public String noteRespingi;
    @Column(name = "ANNO_INIZIALE", length = 4, nullable = true)
    public Integer annoIniziale;
    @Column(name = "NUMERO_INIZIALE", length = 50, nullable = true)
    public Long numeroIniziale;
    @Size(min = 0, max = 2000)
    @Column(name = "COMMENTO_FLUSSO", length = 1000, nullable = true)
    public String commentoFlusso;
    @Column(name = "DATA_INIZIO_ESTERO", nullable = true)
    private ZonedDateTime dataInizioEstero;
    @Column(name = "DATA_FINE_ESTERO", nullable = true)
    private ZonedDateTime dataFineEstero;
    @Column(name = "CD_TERZO_SIGLA", length = 8, nullable = false)
    private Long cdTerzoSigla;
    @Size(min = 0, max = 5)
    @Column(name = "MODPAG", length = 5, nullable = false)
    private String modpag;
    @Size(min = 0, max = 1)
    @Column(name = "TIPO_PAGAMENTO", length = 1, nullable = true)
    private String tipoPagamento;
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
    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE", nullable = true)
    private OrdineMissione ordineMissione;
    @Column(name = "INQUADRAMENTO", length = 10, nullable = true)
    private Long inquadramento;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Size(min = 0, max = 6)
    @Column(name = "MATRICOLA", length = 6, nullable = true)
    private String matricola;
    @Size(min = 0, max = 1)
    @Column(name = "RIMBORSO_0", length = 1, nullable = true)
    private String rimborso0;
    @Size(min = 0, max = 20)
    @Column(name = "UO_CONTR_AMM", length = 20, nullable = true)
    private String uoContrAmm;
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
    private String decodeStatoInvioSigla;
    @Transient
    private String stateFlows;
    @Transient
    private String commentFlows;
    @Transient
    private String statoFlussoRitornoHome;
    @Transient
    private List<RimborsoMissioneDettagli> rimborsoMissioneDettagli;
    @Transient
    private BigDecimal totaleRimborsoSenzaAnticipi;
    @Transient
    private BigDecimal totaleRimborsoComplessivo;
    @Transient
    private String stringBasePath;

    public RimborsoMissione(Long id, Integer anno, Long numero, LocalDate dataInserimento, String uid, String stato, String statoFlusso, String idFlusso, String destinazione,
                            String oggetto, ZonedDateTime dataInizioMissione, ZonedDateTime dataFineMissione, String validato, String uoRich, String trattamento, String validaAmm) {
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
        this.setUoRich(uoRich);
        this.setTrattamento(trattamento);
        this.setValidaAmm(validaAmm);
    }

    public RimborsoMissione() {
        super();
    }

    @XmlTransient
    public static Projection getProjectionForElencoMissioni() {
        return PROJECTIONLIST_ELENCO_MISSIONI;
    }

    public String getCommentoFlusso() {
        return commentoFlusso;
    }

    public void setCommentoFlusso(String commentoFlusso) {
        this.commentoFlusso = commentoFlusso;
    }

    public String getStringBasePath() {
        return stringBasePath;
    }

    public void setStringBasePath(String stringBasePath) {
        this.stringBasePath = stringBasePath;
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
        nomeFile = nomeFile.append(Utility.lpad(this.getNumeroIniziale().toString(), 9, '0'));
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
        if (!StringUtils.isEmpty(getStateFlows())) {
            return Costanti.STATO_FLUSSO_RIMBORSO_FROM_CMIS.get(getStateFlows());
        } else {
            if (!StringUtils.isEmpty(getStatoFlusso())) {
                return Costanti.STATO_FLUSSO.get(getStatoFlusso());
            }
        }
        return "";
    }

    public void setDecodeStatoFlusso(String decodeStatoFlusso) {
        this.decodeStatoFlusso = decodeStatoFlusso;
    }

    @Transient
    public String getDecodeTrattamentoShort() {
        if (!StringUtils.isEmpty(getTrattamento())) {
            return Costanti.TRATTAMENTO_SHORT.get(getTrattamento());
        }
        return "";
    }

    @Transient
    public String getDecodeStatoInvioSigla() {
        if (!StringUtils.isEmpty(getStatoInvioSigla())) {
            return Costanti.STATO_INVIO_SIGLA.get(getStatoInvioSigla());
        } else {
            return Costanti.STATO_INVIO_SIGLA.get("");
        }
    }

    @Transient
    public String getIdSigla() {
        if (!StringUtils.isEmpty(getPgMissioneSigla())) {
            return getCdUoSigla() + "-" + getEsercizioSigla() + "-" + getPgMissioneSigla();
        } else {
            return null;
        }
    }

    public DatiIstituto getDatiIstituto() {
        return datiIstituto;
    }

    public void setDatiIstituto(DatiIstituto datiIstituto) {
        this.datiIstituto = datiIstituto;
    }

    @Transient
    public String getFileName() {
        return "RimborsoMissione" + getId() + ".pdf";
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

    public ZonedDateTime getDataInizioEstero() {
        return dataInizioEstero;
    }

    public void setDataInizioEstero(ZonedDateTime dataInizioEstero) {
        this.dataInizioEstero = dataInizioEstero;
    }

    public ZonedDateTime getDataFineEstero() {
        return dataFineEstero;
    }

    public void setDataFineEstero(ZonedDateTime dataFineEstero) {
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

    public Long getCdTerzoSigla() {
        return cdTerzoSigla;
    }

    public void setCdTerzoSigla(Long cdTerzoSigla) {
        this.cdTerzoSigla = cdTerzoSigla;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ZonedDateTime getDataInizioMissione() {
        return dataInizioMissione;
    }

    public void setDataInizioMissione(ZonedDateTime dataInizioMissione) {
        this.dataInizioMissione = dataInizioMissione;
    }

    public ZonedDateTime getDataFineMissione() {
        return dataFineMissione;
    }

    public void setDataFineMissione(ZonedDateTime dataFineMissione) {
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
        if (!StringUtils.isEmpty(stato)) {
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
    public String decodeTipoMissione() {
        if (!StringUtils.isEmpty(getTipoMissione())) {
            return Costanti.TIPO_MISSIONE.get(getTipoMissione());
        }
        return "";
    }

    @Transient
    public String decodeTrattamento() {
        if (!StringUtils.isEmpty(getTrattamento())) {
            return Costanti.TRATTAMENTO.get(getTrattamento());
        }
        return "";
    }

    @Transient
    public Boolean isMissioneEstera() {
        return getTipoMissione() != null && getTipoMissione().equals(Costanti.MISSIONE_ESTERA);
    }

    @Transient
    public Boolean isTrattamentoAlternativoMissione() {
        return getTrattamento() != null && getTrattamento().equals(Costanti.TAM);
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
        if (!StringUtils.isEmpty(getStato())) {
            return Costanti.STATO.get(getStato());
        }
        return "";
    }

    public void setDecodeStato(String decodeStato) {
        this.decodeStato = decodeStato;
    }

    @Transient
    public String decodeUtilizzoTaxi() {
        if (!StringUtils.isEmpty(getUtilizzoTaxi())) {
            return Costanti.SI_NO.get(getUtilizzoTaxi());
        }
        return "";
    }

    @Transient
    public String decodeUtilizzoAutoNoleggio() {
        if (!StringUtils.isEmpty(getUtilizzoAutoNoleggio())) {
            return Costanti.SI_NO.get(getUtilizzoAutoNoleggio());
        }
        return "";
    }


    @Transient
    public String decodeUtilizzoAutoServizio() {
        if (!StringUtils.isEmpty(getUtilizzoAutoServizio())) {
            return Costanti.SI_NO.get(getUtilizzoAutoServizio());
        }
        return "";
    }

    @Transient
    public String decodePersonaleAlSeguito() {
        if (!StringUtils.isEmpty(getPersonaleAlSeguito())) {
            return Costanti.SI_NO.get(getPersonaleAlSeguito());
        }
        return "";
    }

    @Transient
    public Boolean isMissioneConGiorniDivervi() {
        if (getDataFineMissione() != null && getDataInizioMissione() != null) {
            ZonedDateTime dataInizioSenzaOre = Utility.getDateWithoutHours(getDataInizioMissione());
            ZonedDateTime dataFineSenzaOre = Utility.getDateWithoutHours(getDataFineMissione());
            return dataFineSenzaOre.isAfter(dataInizioSenzaOre);
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
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_CONFERMATO);
        }
        return false;
    }

    @Transient
    public Boolean isMissioneDefinitiva() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_DEFINITIVO);
        }
        return false;
    }

    @Transient
    public Boolean isMissioneInserita() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_INSERITO);
        }
        return false;
    }

    @Transient
    public Boolean isMissioneDaValidare() {
        if (!StringUtils.isEmpty(getValidato())) {
            return getValidato().equals("N");
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

    public Boolean isStatoInviatoAlFlusso() {
        return !StringUtils.isEmpty(getStatoFlusso()) && (getStatoFlusso().equals(Costanti.STATO_INVIATO_FLUSSO) || getStatoFlusso().equals(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO));
    }

    public Boolean isStatoRespintoFlusso() {
        return !StringUtils.isEmpty(getStatoFlusso()) && (getStatoFlusso().equals(Costanti.STATO_RESPINTO_UO_FLUSSO) || getStatoFlusso().equals(Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO));
    }

    public Boolean isStatoNonInviatoAlFlusso() {
        return !StringUtils.isEmpty(getStatoFlusso()) && getStatoFlusso().equals(Costanti.STATO_INSERITO);
    }


    public Boolean isStatoFlussoApprovato() {
        return getStatoFlusso() != null && getStatoFlusso().equals(Costanti.STATO_APPROVATO_FLUSSO);
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

    public Long getInquadramento() {
        return inquadramento;
    }

    public void setInquadramento(Long inquadramento) {
        this.inquadramento = inquadramento;
    }

    @Transient
    public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli() {
        return rimborsoMissioneDettagli;
    }

    @Transient
    public void setRimborsoMissioneDettagli(List<RimborsoMissioneDettagli> rimborsoMissioneDettagli) {
        this.rimborsoMissioneDettagli = rimborsoMissioneDettagli;
    }

    public Integer getPgBanca() {
        return pgBanca;
    }

    public void setPgBanca(Integer pgBanca) {
        this.pgBanca = pgBanca;
    }

    @Transient
    public BigDecimal getTotaleRimborso() {
        BigDecimal totRimborso = BigDecimal.ZERO;
        if (getRimborsoMissioneDettagli() != null && !getRimborsoMissioneDettagli().isEmpty()) {
            for (Iterator<RimborsoMissioneDettagli> iterator = getRimborsoMissioneDettagli().iterator(); iterator.hasNext(); ) {
                RimborsoMissioneDettagli dettagli = iterator.next();
                totRimborso = totRimborso.add(dettagli.getImportoEuro());
            }
        }
        return Utility.nvl(totRimborso);
    }

    @Transient
    public BigDecimal getTotaleRimborsoSenzaSpeseAnticipate() {
        BigDecimal totRimborso = BigDecimal.ZERO;
        if (getRimborsoMissioneDettagli() != null && !getRimborsoMissioneDettagli().isEmpty()) {
            for (Iterator<RimborsoMissioneDettagli> iterator = getRimborsoMissioneDettagli().iterator(); iterator.hasNext(); ) {
                RimborsoMissioneDettagli dettagli = iterator.next();
                if (!dettagli.isSpesaAnticipata()) {
                    totRimborso = totRimborso.add(dettagli.getImportoEuro());
                }
            }
        }
        return Utility.nvl(totRimborso);
    }

    @Transient
    public BigDecimal getTotaleSpeseAnticipate() {
        BigDecimal totRimborso = BigDecimal.ZERO;
        if (getRimborsoMissioneDettagli() != null && !getRimborsoMissioneDettagli().isEmpty()) {
            for (Iterator<RimborsoMissioneDettagli> iterator = getRimborsoMissioneDettagli().iterator(); iterator.hasNext(); ) {
                RimborsoMissioneDettagli dettagli = iterator.next();
                if (dettagli.isSpesaAnticipata()) {
                    totRimborso = totRimborso.add(dettagli.getImportoEuro());
                }
            }
        }
        return Utility.nvl(totRimborso);
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public Long getPgMissioneSigla() {
        return pgMissioneSigla;
    }

    public void setPgMissioneSigla(Long pgMissioneSigla) {
        this.pgMissioneSigla = pgMissioneSigla;
    }

    public Integer getEsercizioSigla() {
        return esercizioSigla;
    }

    public void setEsercizioSigla(Integer esercizioSigla) {
        this.esercizioSigla = esercizioSigla;
    }

    public String getCdCdsSigla() {
        return cdCdsSigla;
    }

    public void setCdCdsSigla(String cdCdsSigla) {
        this.cdCdsSigla = cdCdsSigla;
    }

    public String getCdUoSigla() {
        return cdUoSigla;
    }

    public void setCdUoSigla(String cdUoSigla) {
        this.cdUoSigla = cdUoSigla;
    }

    public String getStatoInvioSigla() {
        return statoInvioSigla;
    }

    public void setStatoInvioSigla(String statoInvioSigla) {
        this.statoInvioSigla = statoInvioSigla;
    }

    public Boolean isMissioneDipendente() {
        return StringUtils.hasLength(getMatricola()) && !getMatricola().equals("0");
    }

    public String getCdTipoRapporto() {
        return cdTipoRapporto;
    }

    public void setCdTipoRapporto(String cdTipoRapporto) {
        this.cdTipoRapporto = cdTipoRapporto;
    }

    public String getUtilizzoAutoServizio() {
        return utilizzoAutoServizio;
    }

    public void setUtilizzoAutoServizio(String utilizzoAutoServizio) {
        this.utilizzoAutoServizio = utilizzoAutoServizio;
    }

    public String getPersonaleAlSeguito() {
        return personaleAlSeguito;
    }

    public void setPersonaleAlSeguito(String personaleAlSeguito) {
        this.personaleAlSeguito = personaleAlSeguito;
    }

    public String getCup() {
        return cup;
    }

    public void setCup(String cup) {
        this.cup = cup;
    }

    public String getNoteSegreteria() {
        return noteSegreteria;
    }

    public void setNoteSegreteria(String noteSegreteria) {
        this.noteSegreteria = noteSegreteria;
    }

    public String getNoteRespingi() {
        return noteRespingi;
    }

    public void setNoteRespingi(String noteRespingi) {
        this.noteRespingi = noteRespingi;
    }

    public BigDecimal getTotaleRimborsoSenzaAnticipi() {
        return totaleRimborsoSenzaAnticipi;
    }

    public void setTotaleRimborsoSenzaAnticipi(BigDecimal totaleRimborsoSenzaAnticipi) {
        this.totaleRimborsoSenzaAnticipi = totaleRimborsoSenzaAnticipi;
    }

    public BigDecimal getTotaleRimborsoComplessivo() {
        return totaleRimborsoComplessivo;
    }

    public void setTotaleRimborsoComplessivo(BigDecimal totaleRimborsoComplessivo) {
        this.totaleRimborsoComplessivo = totaleRimborsoComplessivo;
    }

    public String getRimborso0() {
        return rimborso0;
    }

    public void setRimborso0(String rimborso0) {
        this.rimborso0 = rimborso0;
    }

    public String getCug() {
        return cug;
    }

    public void setCug(String cug) {
        this.cug = cug;
    }

    public Integer getAnnoIniziale() {
        return Optional.ofNullable(annoIniziale).orElse(getAnno());
    }

    public void setAnnoIniziale(Integer annoIniziale) {
        this.annoIniziale = annoIniziale;
    }

    public Long getNumeroIniziale() {
        return Optional.ofNullable(numeroIniziale).orElse(getNumero());
    }

    public void setNumeroIniziale(Long numeroIniziale) {
        this.numeroIniziale = numeroIniziale;
    }

    public String getPresidente() {
        return presidente;
    }

    public void setPresidente(String presidente) {
        this.presidente = presidente;
    }

    public String getValidaAmm() {
        return validaAmm;
    }

    public void setValidaAmm(String validaAmm) {
        this.validaAmm = validaAmm;
    }

    public Boolean isAllaValidazioneAmministrativa() {
        return Utility.nvl(getValidaAmm(), "S").equals("N");
    }

    public Boolean isValidazioneAmministrativaNonValorizzata() {
        return getValidaAmm() == null;
    }

    public Boolean isPassataValidazioneAmministrativa() {
        return Utility.nvl(getValidaAmm(), "N").equals("S");
    }

    public String getUoContrAmm() {
        return uoContrAmm;
    }

    public void setUoContrAmm(String uoContrAmm) {
        this.uoContrAmm = uoContrAmm;
    }

    public Boolean isAssociato() {
        return getCdTipoRapporto() != null && getInquadramento() != null && (getCdTipoRapporto().equals(Costanti.TIPO_RAPPORTO_ASS) ||
                getCdTipoRapporto().equals(Costanti.TIPO_RAPPORTO_EXDIP) ||
                getCdTipoRapporto().equals(Costanti.TIPO_RAPPORTO_OCCA) ||
                getCdTipoRapporto().equals(Costanti.TIPO_RAPPORTO_PROF)) && getInquadramento().compareTo(Costanti.INQUADRAMENTO_ASSEGNISTA) != 0;
    }

    @Transient
    public Boolean isMissioneCug() {
        return Utility.nvl(getCug(), "N").equals("S");
    }

    @Transient
    public Boolean isMissionePresidente() {
        return Utility.nvl(getPresidente(), "N").equals("S");
    }

    @Transient
    public Boolean isMissioneDaComunicareSigla() {
        return Utility.nvl(getStatoInvioSigla(), "N").equals(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE);
    }

    @Override
    public String toString() {
        return "RimborsoMissione{" +
                "dataInizioEstero=" + dataInizioEstero +
                ", dataFineEstero=" + dataFineEstero +
                ", cdTerzoSigla=" + cdTerzoSigla +
                ", modpag='" + modpag + '\'' +
                ", tipoPagamento='" + tipoPagamento + '\'' +
                ", iban='" + iban + '\'' +
                ", anticipoRicevuto='" + anticipoRicevuto + '\'' +
                ", anticipoAnnoMandato=" + anticipoAnnoMandato +
                ", anticipoNumeroMandato=" + anticipoNumeroMandato +
                ", anticipoImporto=" + anticipoImporto +
                ", altreSpeseAntDescrizione='" + altreSpeseAntDescrizione + '\'' +
                ", altreSpeseAntImporto=" + altreSpeseAntImporto +
                ", speseTerziRicevute='" + speseTerziRicevute + '\'' +
                ", speseTerziImporto=" + speseTerziImporto +
                ", pgBanca=" + pgBanca +
                ", inquadramento=" + inquadramento +
                ", id=" + id +
                ", uidInsert='" + uidInsert + '\'' +
                ", uid='" + uid + '\'' +
                ", autoPropria='" + autoPropria + '\'' +
                ", validaAmm='" + validaAmm + '\'' +
                ", cug='" + cug + '\'' +
                ", presidente='" + presidente + '\'' +
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
                ", oggetto='" + oggetto + '\'' +
                ", destinazione='" + destinazione + '\'' +
                ", nazione=" + nazione +
                ", tipoMissione='" + tipoMissione + '\'' +
                ", trattamento='" + trattamento + '\'' +
                ", dataInizioMissione=" + dataInizioMissione +
                ", dataFineMissione=" + dataFineMissione +
                ", validato='" + validato + '\'' +
                ", utilizzoTaxi='" + utilizzoTaxi + '\'' +
                ", utilizzoAutoNoleggio='" + utilizzoAutoNoleggio + '\'' +
                ", note='" + note + '\'' +
                ", noteSegreteria='" + noteSegreteria + '\'' +
                ", voce='" + voce + '\'' +
                ", gae='" + gae + '\'' +
                ", cdrRich='" + cdrRich + '\'' +
                ", uoRich='" + uoRich + '\'' +
                ", cdrSpesa='" + cdrSpesa + '\'' +
                ", uoSpesa='" + uoSpesa + '\'' +
                ", cdsCompetenza='" + cdsCompetenza + '\'' +
                ", uoCompetenza='" + uoCompetenza + '\'' +
                ", cdsRich='" + cdsRich + '\'' +
                ", cdsSpesa='" + cdsSpesa + '\'' +
                ", stato='" + stato + '\'' +
                ", pgProgetto=" + pgProgetto +
                ", cdCdsObbligazione='" + cdCdsObbligazione + '\'' +
                ", esercizioObbligazione=" + esercizioObbligazione +
                ", pgObbligazione=" + pgObbligazione +
                ", esercizioOriginaleObbligazione=" + esercizioOriginaleObbligazione +
                ", idFlusso='" + idFlusso + '\'' +
                ", statoFlusso='" + statoFlusso + '\'' +
                ", statoInvioSigla='" + statoInvioSigla + '\'' +
                ", noteUtilizzoTaxiNoleggio='" + noteUtilizzoTaxiNoleggio + '\'' +
                ", pgMissioneSigla=" + pgMissioneSigla +
                ", esercizioSigla=" + esercizioSigla +
                ", cdCdsSigla='" + cdCdsSigla + '\'' +
                ", cdUoSigla='" + cdUoSigla + '\'' +
                ", cdTipoRapporto='" + cdTipoRapporto + '\'' +
                ", utilizzoAutoServizio='" + utilizzoAutoServizio + '\'' +
                ", personaleAlSeguito='" + personaleAlSeguito + '\'' +
                ", cup='" + cup + '\'' +
                ", noteRespingi='" + noteRespingi + '\'' +
                ", rimborso0='" + rimborso0 + '\'' +
                ", annoIniziale=" + annoIniziale +
                ", numeroIniziale=" + numeroIniziale +
                ", uoContrAmm='" + uoContrAmm + '\'' +
                '}';
    }
}
