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
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A user.
 */
@Entity
@Table(name = "ANNULLAMENTO_RIMBORSO_MISSIONE")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_ANNULLAMENTO_RIMBORSO", allocationSize = 0)
public class AnnullamentoRimborsoMissione extends OggettoBulkXmlTransient {

    public final static String CMIS_PROPERTY_NAME_DOC_ANNULLAMENTO = "Principale";
    public final static String CMIS_PROPERTY_NAME_TIPODOC_ANNULLAMENTO = "Annullamento Rimborso Missione";
    public static final Projection PROJECTIONLIST_ELENCO_MISSIONI = Projections.projectionList().
            add(Projections.property("id")).
            add(Projections.property("anno")).
            add(Projections.property("numero")).
            add(Projections.property("dataInserimento")).
            add(Projections.property("uid")).
            add(Projections.property("stato")).
            add(Projections.property("motivo"));
    @Size(min = 0, max = 256)
    @Column(name = "UID_INSERT", length = 256, nullable = false)
    public String uidInsert;
    @Size(min = 0, max = 256)
    @Column(name = "UID", length = 256, nullable = false)
    public String uid;
    @Size(min = 0, max = 16)
    @Column(name = "CODICE_FISCALE", length = 16, nullable = false)
    public String codiceFiscale;
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
    @Column(name = "MOTIVO_ANNULLAMENTO", length = 1000, nullable = false)
    public String motivoAnnullamento;
    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    public String stato;
    @ManyToOne
    @JoinColumn(name = "ID_RIMBORSO_MISSIONE", nullable = true)
    private RimborsoMissione rimborsoMissione;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Size(min = 0, max = 6)
    @Column(name = "MATRICOLA", length = 6, nullable = true)
    private String matricola;
    @Transient
    private String decodeStato;
    @Transient
    private String isUoDaValidare;

    public AnnullamentoRimborsoMissione(Long id, Integer anno, Long numero, LocalDate dataInserimento, String uid, String stato, String motivoAnnullamento) {
        super();
        this.setId(id);
        this.setAnno(anno);
        this.setNumero(numero);
        this.setDataInserimento(dataInserimento);
        this.setUid(uid);
        this.setStato(stato);
        this.setMotivoAnnullamento(motivoAnnullamento);
    }

    public AnnullamentoRimborsoMissione() {
        super();
    }

    @XmlTransient
    public static Projection getProjectionForElencoMissioni() {
        return PROJECTIONLIST_ELENCO_MISSIONI;
    }

    public String getIsUoDaValidare() {
        return isUoDaValidare;
    }

    public void setIsUoDaValidare(String isUoDaValidare) {
        this.isUoDaValidare = isUoDaValidare;
    }

    @Transient
    public String constructCMISNomeFile() {
        StringBuffer nomeFile = new StringBuffer();
        nomeFile = nomeFile.append(Utility.lpad(this.getNumero().toString(), 9, '0'));
        return nomeFile.toString();
    }

    @Transient
    public String getFileName() {
        return "AnnullamentoRimborsoMissione" + getId() + ".pdf";
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

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
        if (!StringUtils.isEmpty(stato)) {
            this.decodeStato = Costanti.STATO.get(stato);
        }
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
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

    public String getUidInsert() {
        return uidInsert;
    }

    public void setUidInsert(String uidInsert) {
        this.uidInsert = uidInsert;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public Boolean isMissioneDipendente() {
        return !StringUtils.isEmpty(getMatricola()) && !getMatricola().equals("0");
    }

    public String getMotivoAnnullamento() {
        return motivoAnnullamento;
    }

    public void setMotivoAnnullamento(String motivoAnnullamento) {
        this.motivoAnnullamento = motivoAnnullamento;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public RimborsoMissione getRimborsoMissione() {
        return rimborsoMissione;
    }

    public void setRimborsoMissione(RimborsoMissione rimborsoMissione) {
        this.rimborsoMissione = rimborsoMissione;
    }

    @Override
    public String toString() {
        return "AnnullamentoRimborsoMissione{" +
                "id=" + id +
                ", uidInsert='" + uidInsert + '\'' +
                ", uid='" + uid + '\'' +
                ", codiceFiscale='" + codiceFiscale + '\'' +
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
                ", stato='" + stato + '\'' +
                '}';
    }
}
