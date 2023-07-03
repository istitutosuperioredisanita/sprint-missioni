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

/**
 * A user.
 */
@Entity
@Table(name = "ORDINE_MISSIONE")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_ORDINE_MISSIONE", allocationSize = 0)
public class OrdineMissione extends OggettoBulkXmlTransient implements Serializable {

    public static final String CMIS_PROPERTY_MAIN = "F:missioni:main";
    public static final String CMIS_PROPERTY_NAME_ANNO = "missioni:anno";
    public static final String CMIS_PROPERTY_NAME_NUMERO = "missioni:numero";
    public static final String CMIS_PROPERTY_NAME_ID = "missioni:id";
    public static final String CMIS_PROPERTY_NAME_UID = "missioni:uid";
    public static final String CMIS_PROPERTY_NAME_MODULO = "missioni:modulo";
    public static final String CMIS_PROPERTY_NAME_OGGETTO = "missioni:oggetto";
    public static final String CMIS_PROPERTY_NAME_DESTINAZIONE = "missioni:destinazione";
    public static final String CMIS_PROPERTY_NAME_NOTE = "missioni:note";
    public static final String CMIS_PROPERTY_NAME_NOTE_SEGRETERIA = "missioni:noteSegreteria";
    public static final String CMIS_PROPERTY_NAME_DATA_INIZIO = "missioni:dataInizio";
    public static final String CMIS_PROPERTY_NAME_DATA_FINE = "missioni:dataFine";
    public static final String CMIS_PROPERTY_NAME_DATA_INSERIMENTO = "missioni:dataInserimento";
    public static final String CMIS_PROPERTY_ATTACHMENT_DOCUMENT = "D:missioni_ordine_attachment:document";
    public static final String CMIS_PROPERTY_NAME_DOC_ORDINE = "Principale";
    public static final String CMIS_PROPERTY_NAME_DOC_ALLEGATO = "Allegato";
    public static final String CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO = "Allegati";
    public static final String CMIS_PROPERTY_NAME_TIPODOC_ALLEGATO_ANTICIPO = "Allegati Anticipo";
    public static final String CMIS_PROPERTY_NAME_TIPODOC_ORDINE = "Ordine di Missione";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE = "cnrmissioni:descrizioneOrdine";
    public static final String CMIS_PROPERTY_FLOW_NOTE = "cnrmissioni:note";
    public static final String CMIS_PROPERTY_FLOW_NOTE_SEGRETERIA = "cnrmissioni:noteSegreteria";
    public static final String CMIS_PROPERTY_FLOW_DUE_DATE = "bpm:workflowDueDate";
    public static final String CMIS_PROPERTY_FLOW_PRIORITY = "bpm:workflowPriority";
    public static final String CMIS_PROPERTY_FLOW_VALIDAZIONE_SPESA = "cnrmissioni:validazioneSpesaFlag";
    public static final String CMIS_PROPERTY_FLOW_ANTICIPO = "cnrmissioni:missioneConAnticipoFlag";
    public static final String CMIS_PROPERTY_FLOW_VALIDAZIONE_MODULO = "cnrmissioni:validazioneModuloFlag";
    public static final String CMIS_PROPERTY_FLOW_USERNAME_ORDINE = "cnrmissioni:userNameUtenteOrdineMissione";
    public static final String CMIS_PROPERTY_FLOW_USERNAME_RICHIEDENTE = "cnrmissioni:userNameRichiedente";
    public static final String CMIS_PROPERTY_FLOW_USERNAME_RESPONSABILE_MODULO = "cnrmissioni:userNameResponsabileModulo";
    public static final String CMIS_PROPERTY_FLOW_MISSIONE_GRATUITA = "cnrmissioni:missioneGratuita";
    public static final String CMIS_PROPERTY_FLOW_NOTE_AUTORIZZAZIONI_AGGIUNTIVE = "cnrmissioni:noteAutorizzazioniAggiuntive";
    public static final String CMIS_PROPERTY_FLOW_FONDI = "cnrmissioni:competenzaResiduo";
    public static final String CMIS_PROPERTY_AUTO_PROPRIA_ALTRI_MOTIVI = "cnrmissioni:autoPropriaAltriMotivo";
    public static final String CMIS_PROPERTY_AUTO_PROPRIA_PRIMO_MOTIVO = "cnrmissioni:autoPropriaPrimoMotivo";
    public static final String CMIS_PROPERTY_AUTO_PROPRIA_SECONDO_MOTIVO = "cnrmissioni:autoPropriaSecondoMotivo";
    public static final String CMIS_PROPERTY_AUTO_PROPRIA_TERZO_MOTIVO = "cnrmissioni:autoPropriaTerzoMotivo";
    public static final String CMIS_PROPERTY_FLOW_USERNAME_FIRMA_UO = "cnrmissioni:userNamePrimoFirmatario";
    public static final String CMIS_PROPERTY_FLOW_USERNAME_FIRMA_SPESA = "cnrmissioni:userNameFirmatarioSpesa";
    public static final String CMIS_PROPERTY_FLOW_UO_ORDINE = "cnrmissioni:uoOrdine";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_ORDINE = "cnrmissioni:descrizioneUoOrdine";
    public static final String CMIS_PROPERTY_FLOW_UO_SPESA = "cnrmissioni:uoSpesa";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_SPESA = "cnrmissioni:descrizioneUoSpesa";
    public static final String CMIS_PROPERTY_FLOW_UO_COMPETENZA = "cnrmissioni:uoCompetenza";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_UO_COMPETENZA = "cnrmissioni:descrizioneUoCompetenza";
    public static final String CMIS_PROPERTY_FLOW_DESTINAZIONE = "cnrmissioni:destinazione";
    public static final String CMIS_PROPERTY_FLOW_ESTERA_FLAG = "cnrmissioni:missioneEsteraFlag";
    public static final String CMIS_PROPERTY_FLOW_DATA_INIZIO_MISSIONE = "cnrmissioni:dataInizioMissione";
    public static final String CMIS_PROPERTY_FLOW_DATA_FINE_MISSIONE = "cnrmissioni:dataFineMissione";
    public static final String CMIS_PROPERTY_FLOW_AUTO_PROPRIA = "cnrmissioni:autoPropriaFlag";
    public static final String CMIS_PROPERTY_FLOW_NOLEGGIO = "cnrmissioni:noleggioFlag";
    public static final String CMIS_PROPERTY_FLOW_TAXI = "cnrmissioni:taxiFlag";
    public static final String CMIS_PROPERTY_FLOW_AUTO_SERVIZIO = "cnrmissioni:servizioFlagOk";
    public static final String CMIS_PROPERTY_FLOW_PERSONA_SEGUITO = "cnrmissioni:personaSeguitoFlagOk";
    public static final String CMIS_PROPERTY_FLOW_CAPITOLO = "cnrmissioni:capitolo";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_CAPITOLO = "cnrmissioni:descrizioneCapitolo";
    public static final String CMIS_PROPERTY_FLOW_MODULO = "cnrmissioni:modulo";
    public static final String CMIS_PROPERTY_FLOW_TRATTAMENTO = "cnrmissioni:trattamento";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_MODULO = "cnrmissioni:descrizioneModulo";
    public static final String CMIS_PROPERTY_FLOW_GAE = "cnrmissioni:gae";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_GAE = "cnrmissioni:descrizioneGae";
    public static final String CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_RES = "cnrmissioni:impegnoAnnoResiduo";
    public static final String CMIS_PROPERTY_FLOW_ANNO_IMPEGNO_COMP = "cnrmissioni:impegnoAnnoCompetenza";
    public static final String CMIS_PROPERTY_FLOW_NUMERO_IMPEGNO = "cnrmissioni:impegnoNumeroOk";
    public static final String CMIS_PROPERTY_FLOW_DESCRIZIONE_IMPEGNO = "cnrmissioni:descrizioneImpegno";
    public static final String CMIS_PROPERTY_FLOW_IMPORTO_MISSIONE = "cnrmissioni:importoMissione";
    public static final String CMIS_PROPERTY_FLOW_DISPONIBILITA_IMPEGNO = "cnrmissioni:disponibilita";
    public static final String ORDINE_MISSIONE_ATTACHMENT_QUERY_CMIS = "missioni_ordine_attachment";
    public static final String ATTACHMENT_ALLEGATO_ANTICIPO = ":allegati_anticipo";
    public static final String ATTACHMENT_ALLEGATO_ORDINE_MISSIONE = ":allegati";
    public static final String CMIS_PROPERTY_ORDINE_ATTACHMENT_ELIMINATO = "missioni_ordine_attachment:eliminato";
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
            add(Projections.property("responsabileGruppo")).
            add(Projections.property("uoRich")).
            add(Projections.property("trattamento"));
    @Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    public String uidInsert;
    @Size(min = 0, max = 256)
    @Column(name = "USER_NAME", length = 256, nullable = false)
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
    @Size(min = 0, max = 1000)
    @Column(name = "COMMENTO_FLUSSO", length = 1000, nullable = true)
    public String commentoFlusso;
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
    @Column(name = "DATA_INVIO_RESP_GRUPPO", nullable = true)
    public ZonedDateTime dataInvioRespGruppo;
    @Column(name = "DATA_INVIO_AMMINISTRATIVO", nullable = true)
    public ZonedDateTime dataInvioAmministrativo;
    @Column(name = "DATA_INVIO_FIRMA", nullable = true)
    public ZonedDateTime dataInvioFirma;
    @Size(min = 0, max = 1)
    @Column(name = "BYPASS_RESP_GRUPPO", length = 1, nullable = true)
    public String bypassRespGruppo;
    @Size(min = 0, max = 1)
    @Column(name = "BYPASS_AMMINISTRATIVO", length = 1, nullable = true)
    public String bypassAmministrativo;
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
    @Size(min = 0, max = 1000)
    @Column(name = "PARTENZA_DA_ALTRO", length = 100, nullable = true)
    public String partenzaDaAltro;
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
    @Size(min = 0, max = 1000)
    @Column(name = "NOTE_UTILIZZO_TAXI_NOLEGGIO", length = 1000, nullable = true)
    public String noteUtilizzoTaxiNoleggio;
    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_AUTO_SERVIZIO", length = 1, nullable = false)
    public String utilizzoAutoServizio;
    @Size(min = 0, max = 1)
    @Column(name = "PERSONALE_AL_SEGUITO", length = 1, nullable = false)
    public String personaleAlSeguito;
    @Size(min = 0, max = 50)
    @Column(name = "CUP", length = 50, nullable = true)
    public String cup;
    @Column(name = "MISSIONE_GRATUITA", length = 1, nullable = true)
    public String missioneGratuita;
    @Column(name = "CUG", length = 1, nullable = true)
    public String cug;
    @Column(name = "PRESIDENTE", length = 1, nullable = true)
    public String presidente;
    @Size(min = 0, max = 1000)
    @Column(name = "NOTE_RESPINGI", length = 1000, nullable = true)
    public String noteRespingi;
    @Size(min = 0, max = 1)
    @Column(name = "OBBLIGO_RIENTRO", length = 1, nullable = false)
    private String obbligoRientro;
    @Column(name = "DISTANZA_DALLA_SEDE", length = 5, nullable = true)
    private Long distanzaDallaSede;
    @Size(min = 0, max = 28)
    @Column(name = "MODULO", length = 28, nullable = true)
    private String modulo;
    @Size(min = 0, max = 3)
    @Column(name = "PRIORITA", length = 3, nullable = false)
    private String priorita;
    @Column(name = "IMPORTO_PRESUNTO", length = 28, nullable = true)
    private BigDecimal importoPresunto;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Size(min = 0, max = 6)
    @Column(name = "MATRICOLA", length = 6, nullable = true)
    private String matricola;
    @Size(min = 0, max = 1)
    @Column(name = "PARTENZA_DA", length = 1, nullable = false)
    private String partenzaDa;
    @Size(min = 0, max = 1)
    @Column(name = "FONDI", length = 1, nullable = true)
    private String fondi;
    @Size(min = 0, max = 250)
    @Column(name = "RESPONSABILE_GRUPPO", length = 250, nullable = true)
    private String responsabileGruppo;
    @Transient
    private String daValidazione;
    @Transient
    private String daChron;
    @Transient
    private String utilizzoAutoPropria;
    @Transient
    private DatiIstituto datiIstituto;
    @Transient
    private String richiestaAnticipo;
    @Transient
    private String stringBasePath;
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

    public OrdineMissione(Long id, Integer anno, Long numero, LocalDate dataInserimento, String uid, String stato, String statoFlusso, String idFlusso, String destinazione,
                          String oggetto, ZonedDateTime dataInizioMissione, ZonedDateTime dataFineMissione, String validato, String responsabileGruppo, String uoRich,
                          String trattamento) {
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
        this.setResponsabileGruppo(responsabileGruppo);
        this.setUoRich(uoRich);
        this.setTrattamento(trattamento);
    }

    public OrdineMissione() {
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

    public String getDaChron() {
        return daChron;
    }

    public void setDaChron(String daChron) {
        this.daChron = daChron;
    }

    public String getStringBasePath() {
        return stringBasePath;
    }

    public void setStringBasePath(String stringBasePath) {
        this.stringBasePath = stringBasePath;
    }

    public String getObbligoRientro() {
        return obbligoRientro;
    }

    public void setObbligoRientro(String obbligoRientro) {
        this.obbligoRientro = obbligoRientro;
    }

    public Long getDistanzaDallaSede() {
        return distanzaDallaSede;
    }

    public void setDistanzaDallaSede(Long distanzaDallaSede) {
        this.distanzaDallaSede = distanzaDallaSede;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public BigDecimal getImportoPresunto() {
        return importoPresunto;
    }

    public void setImportoPresunto(BigDecimal importoPresunto) {
        this.importoPresunto = importoPresunto;
    }

    public String getPriorita() {
        return priorita;
    }

    public void setPriorita(String priorita) {
        this.priorita = priorita;
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
        nomeFile = nomeFile.append(Utility.lpad(this.getNumero().toString(), 9, '0'));
        return nomeFile.toString();
    }

    @Transient
    public String decodePriorita() {
        if (!StringUtils.isEmpty(getPriorita())) {
            return Costanti.PRIORITA.get(getPriorita());
        }
        return "";
    }

    @Transient
    public String decodeObbligoRientro() {
        if (!StringUtils.isEmpty(getObbligoRientro())) {
            return Costanti.SI_NO.get(getObbligoRientro());
        }
        return "";
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

    @Override
    public String toString() {
        return "OrdineMissione{" +
                "obbligoRientro='" + obbligoRientro + '\'' +
                ", distanzaDallaSede=" + distanzaDallaSede +
                ", modulo='" + modulo + '\'' +
                ", priorita='" + priorita + '\'' +
                ", importoPresunto=" + importoPresunto +
                ", id=" + id +
                ", uidInsert='" + uidInsert + '\'' +
                ", uid='" + uid + '\'' +
                ", anno=" + anno +
                ", numero=" + numero +
                ", dataInserimento=" + dataInserimento +
                ", comuneResidenzaRich='" + comuneResidenzaRich + '\'' +
                ", indirizzoResidenzaRich='" + indirizzoResidenzaRich + '\'' +
                ", matricola='" + matricola + '\'' +
                ", domicilioFiscaleRich='" + domicilioFiscaleRich + '\'' +
                ", datoreLavoroRich='" + datoreLavoroRich + '\'' +
                ", contrattoRich='" + contrattoRich + '\'' +
                ", qualificaRich='" + qualificaRich + '\'' +
                ", livelloRich='" + livelloRich + '\'' +
                ", oggetto='" + oggetto + '\'' +
                ", commentoFlusso='" + commentoFlusso + '\'' +
                ", destinazione='" + destinazione + '\'' +
                ", nazione=" + nazione +
                ", tipoMissione='" + tipoMissione + '\'' +
                ", trattamento='" + trattamento + '\'' +
                ", dataInizioMissione=" + dataInizioMissione +
                ", dataFineMissione=" + dataFineMissione +
                ", dataInvioRespGruppo=" + dataInvioRespGruppo +
                ", dataInvioAmministrativo=" + dataInvioAmministrativo +
                ", dataInvioFirma=" + dataInvioFirma +
                ", bypassRespGruppo='" + bypassRespGruppo + '\'' +
                ", bypassAmministrativo='" + bypassAmministrativo + '\'' +
                ", validato='" + validato + '\'' +
                ", utilizzoTaxi='" + utilizzoTaxi + '\'' +
                ", utilizzoAutoNoleggio='" + utilizzoAutoNoleggio + '\'' +
                ", note='" + note + '\'' +
                ", noteSegreteria='" + noteSegreteria + '\'' +
                ", partenzaDaAltro='" + partenzaDaAltro + '\'' +
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
                ", noteUtilizzoTaxiNoleggio='" + noteUtilizzoTaxiNoleggio + '\'' +
                ", partenzaDa='" + partenzaDa + '\'' +
                ", fondi='" + fondi + '\'' +
                ", responsabileGruppo='" + responsabileGruppo + '\'' +
                ", utilizzoAutoServizio='" + utilizzoAutoServizio + '\'' +
                ", personaleAlSeguito='" + personaleAlSeguito + '\'' +
                ", cup='" + cup + '\'' +
                ", missioneGratuita='" + missioneGratuita + '\'' +
                ", cug='" + cug + '\'' +
                ", presidente='" + presidente + '\'' +
                ", noteRespingi='" + noteRespingi + '\'' +
                '}';
    }

    @Transient
    public String getDecodeStatoFlusso() {
        if (!StringUtils.isEmpty(getStateFlows())) {
            return Costanti.STATO_FLUSSO_FROM_CMIS.get(getStateFlows());
        } else {
            if (!StringUtils.isEmpty(getStatoFlusso())) {
                return Costanti.STATO_FLUSSO.get(getStatoFlusso());
            }
        }
        return "";
    }

    @Transient
    public String getDecodeFondi() {
        if (!StringUtils.isEmpty(getFondi())) {
            return Costanti.FONDI.get(getFondi());
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
        return "OrdineMissione" + getId() + ".pdf";
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
    public String getDecodeTrattamentoShort() {
        if (!StringUtils.isEmpty(getTrattamento())) {
            return Costanti.TRATTAMENTO_SHORT.get(getTrattamento());
        }
        return "";
    }

    @Transient
    public Boolean isMissioneEstera() {
        return getTipoMissione() != null && getTipoMissione().equals(Costanti.MISSIONE_ESTERA);
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

    @Transient
    public String decodeUtilizzoTaxi() {
        if (!StringUtils.isEmpty(getUtilizzoTaxi())) {
            return Costanti.SI_NO.get(getUtilizzoTaxi());
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
    public String decodeUtilizzoAutoNoleggio() {
        if (!StringUtils.isEmpty(getUtilizzoAutoNoleggio())) {
            return Costanti.SI_NO.get(getUtilizzoAutoNoleggio());
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

    @Transient
    public String decodeMissioneGratuita() {
        if (!StringUtils.isEmpty(getMissioneGratuita())) {
            return Costanti.SI_NO.get(getMissioneGratuita());
        }
        return "";
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
    public Boolean isMissioneInviataResponsabile() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_INVIATO_RESPONSABILE);
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

    public Boolean isFondiCompetenza() {
        return getFondi() != null && getFondi().equals(Costanti.FONDI_DI_COMPETENZA);
    }

    public Boolean isFondiResiduo() {
        return getFondi() != null && getFondi().equals(Costanti.FONDI_DI_RESIDUO);
    }

    public Boolean isMissioneDipendente() {
        return !StringUtils.isEmpty(getMatricola()) && !getMatricola().equals("0");
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
    public String decodePartenzaDa() {
        if (!StringUtils.isEmpty(getPartenzaDa())) {
            if (getPartenzaDa().equals("A")) {
                return getPartenzaDaAltro();
            }
            return Costanti.PARTENZA_DA.get(getPartenzaDa());
        }
        return "";
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getResponsabileGruppo() {
        return responsabileGruppo;
    }

    public void setResponsabileGruppo(String responsabileGruppo) {
        this.responsabileGruppo = responsabileGruppo;
    }

    public String getFondi() {
        return fondi;
    }

    public void setFondi(String fondi) {
        this.fondi = fondi;
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

    @Transient
    public Boolean isTrattamentoAlternativoMissione() {
        return getTrattamento() != null && getTrattamento().equals(Costanti.TAM);
    }

    public String getNoteSegreteria() {
        return noteSegreteria;
    }

    public void setNoteSegreteria(String noteSegreteria) {
        this.noteSegreteria = noteSegreteria;
    }

    public String getPartenzaDaAltro() {
        return partenzaDaAltro;
    }

    public void setPartenzaDaAltro(String partenzaDaAltro) {
        this.partenzaDaAltro = partenzaDaAltro;
    }

    public String getMissioneGratuita() {
        return missioneGratuita;
    }

    public void setMissioneGratuita(String missioneGratuita) {
        this.missioneGratuita = missioneGratuita;
    }

    public Boolean isMissioneGratuita() {
        return Utility.nvl(getMissioneGratuita()).equals("S");
    }

    public String getNoteRespingi() {
        return noteRespingi;
    }

    public void setNoteRespingi(String noteRespingi) {
        this.noteRespingi = noteRespingi;
    }

    public ZonedDateTime getDataInvioRespGruppo() {
        return dataInvioRespGruppo;
    }

    public void setDataInvioRespGruppo(ZonedDateTime dataInvioRespGruppo) {
        this.dataInvioRespGruppo = dataInvioRespGruppo;
    }

    public ZonedDateTime getDataInvioAmministrativo() {
        return dataInvioAmministrativo;
    }

    public void setDataInvioAmministrativo(ZonedDateTime dataInvioAmministrativo) {
        this.dataInvioAmministrativo = dataInvioAmministrativo;
    }

    public ZonedDateTime getDataInvioFirma() {
        return dataInvioFirma;
    }

    public void setDataInvioFirma(ZonedDateTime dataInvioFirma) {
        this.dataInvioFirma = dataInvioFirma;
    }

    public String getBypassRespGruppo() {
        return bypassRespGruppo;
    }

    public void setBypassRespGruppo(String bypassRespGruppo) {
        this.bypassRespGruppo = bypassRespGruppo;
    }

    public String getBypassAmministrativo() {
        return bypassAmministrativo;
    }

    public void setBypassAmministrativo(String bypassAmministrativo) {
        this.bypassAmministrativo = bypassAmministrativo;
    }

    public String getCug() {
        return cug;
    }

    public void setCug(String cug) {
        this.cug = cug;
    }

    public String getPresidente() {
        return presidente;
    }

    public void setPresidente(String presidente) {
        this.presidente = presidente;
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
    public Boolean isOrdineMissioneVecchiaScrivania() {
        return getIdFlusso() != null && getIdFlusso().startsWith("activiti");
    }

}
