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


import it.cnr.si.missioni.util.Costanti;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A user.
 */
@Entity
@Table(name = "ORDINE_MISSIONE_AUTO_PROPRIA")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_ORDINE_AUTO_PROPRIA", allocationSize = 0)
public class OrdineMissioneAutoPropria extends OggettoBulkXmlTransient implements Serializable {

    public final static String CMIS_PROPERTY_NAME_DOC_AUTO_PROPRIA = "Allegato";
    public final static String CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA = "Richiesta Auto Propria";
    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_MOTIVI_ISPETTIVI", length = 1, nullable = true)
    public String utilizzoMotiviIspettivi;
    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_MOTIVI_URGENZA", length = 1, nullable = true)
    public String utilizzoMotiviUrgenza;
    @Size(min = 0, max = 1)
    @Column(name = "UTILIZZO_MOTIVI_TRASPORTO", length = 1, nullable = true)
    public String utilizzoMotiviTrasporto;
    @Size(min = 0, max = 1000)
    @Column(name = "UTILIZZO_ALTRI_MOTIVI", length = 1000, nullable = true)
    public String utilizzoAltriMotivi;
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
    private Long id;
    @Size(min = 0, max = 50)
    @Column(name = "TARGA", length = 50, nullable = false)
    private String targa;
    @Size(min = 0, max = 50)
    @Column(name = "CARTA_CIRCOLAZIONE", length = 50, nullable = false)
    private String cartaCircolazione;
    @Size(min = 0, max = 100)
    @Column(name = "POLIZZA_ASSICURATIVA", length = 100, nullable = false)
    private String polizzaAssicurativa;
    @Size(min = 0, max = 256)
    @Column(name = "USER_NAME", length = 256, nullable = false)
    private String uid;
    @Size(min = 0, max = 50)
    @Column(name = "MARCA", length = 50, nullable = false)
    private String marca;
    @Size(min = 0, max = 100)
    @Column(name = "MODELLO", length = 100, nullable = false)
    private String modello;
    @Size(min = 0, max = 50)
    @Column(name = "NUMERO_PATENTE", length = 50, nullable = false)
    private String numeroPatente;
    @Type(type = "java.util.Date")
    @Column(name = "DATA_RILASCIO_PATENTE", nullable = false)
    private Date dataRilascioPatente;
    @Type(type = "java.util.Date")
    @Column(name = "DATA_SCADENZA_PATENTE", nullable = false)
    private Date dataScadenzaPatente;
    @Size(min = 0, max = 100)
    @Column(name = "ENTE_PATENTE", length = 100, nullable = false)
    private String entePatente;
    @ManyToOne
    @JoinColumn(name = "ID_ORDINE_MISSIONE", nullable = false)
    private OrdineMissione ordineMissione;
    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;
    @Transient
    private List<SpostamentiAutoPropria> listSpostamenti;

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public String getCartaCircolazione() {
        return cartaCircolazione;
    }

    public void setCartaCircolazione(String cartaCircolazione) {
        this.cartaCircolazione = cartaCircolazione;
    }

    public String getPolizzaAssicurativa() {
        return polizzaAssicurativa;
    }

    public void setPolizzaAssicurativa(String polizzaAssicurativa) {
        this.polizzaAssicurativa = polizzaAssicurativa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
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

    public String getNumeroPatente() {
        return numeroPatente;
    }

    public void setNumeroPatente(String numeroPatente) {
        this.numeroPatente = numeroPatente;
    }

    public Date getDataRilascioPatente() {
        return dataRilascioPatente;
    }

    public void setDataRilascioPatente(Date dataRilascioPatente) {
        this.dataRilascioPatente = dataRilascioPatente;
    }

    public Date getDataScadenzaPatente() {
        return dataScadenzaPatente;
    }

    public void setDataScadenzaPatente(Date dataScadenzaPatente) {
        this.dataScadenzaPatente = dataScadenzaPatente;
    }

    public String getEntePatente() {
        return entePatente;
    }

    public void setEntePatente(String entePatente) {
        this.entePatente = entePatente;
    }

    public OrdineMissione getOrdineMissione() {
        return ordineMissione;
    }

    public void setOrdineMissione(OrdineMissione ordineMissione) {
        this.ordineMissione = ordineMissione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public List<SpostamentiAutoPropria> getListSpostamenti() {
        return listSpostamenti;
    }

    public void setListSpostamenti(List<SpostamentiAutoPropria> listSpostamenti) {
        this.listSpostamenti = listSpostamenti;
    }

    @Transient
    public Boolean isRichiestaAutoPropriaInserita() {
        if (!StringUtils.isEmpty(getStato())) {
            return getStato().equals(Costanti.STATO_INSERITO);
        }
        return false;
    }

    @Transient
    public String getFileName() {
        return "AutoPropriaOrdineMissione" + getId() + ".pdf";
    }

    public String getUtilizzoMotiviIspettivi() {
        return utilizzoMotiviIspettivi;
    }

    public void setUtilizzoMotiviIspettivi(String utilizzoMotiviIspettivi) {
        this.utilizzoMotiviIspettivi = utilizzoMotiviIspettivi;
    }

    public String getUtilizzoMotiviUrgenza() {
        return utilizzoMotiviUrgenza;
    }

    public void setUtilizzoMotiviUrgenza(String utilizzoMotiviUrgenza) {
        this.utilizzoMotiviUrgenza = utilizzoMotiviUrgenza;
    }

    public String getUtilizzoMotiviTrasporto() {
        return utilizzoMotiviTrasporto;
    }

    public void setUtilizzoMotiviTrasporto(String utilizzoMotiviTrasporto) {
        this.utilizzoMotiviTrasporto = utilizzoMotiviTrasporto;
    }

    public String getUtilizzoAltriMotivi() {
        return utilizzoAltriMotivi;
    }

    public void setUtilizzoAltriMotivi(String utilizzoAltriMotivi) {
        this.utilizzoAltriMotivi = utilizzoAltriMotivi;
    }

    @Override
    public String toString() {
        return "OrdineMissioneAutoPropria{" +
                "id=" + id +
                ", targa='" + targa + '\'' +
                ", cartaCircolazione='" + cartaCircolazione + '\'' +
                ", polizzaAssicurativa='" + polizzaAssicurativa + '\'' +
                ", uid='" + uid + '\'' +
                ", marca='" + marca + '\'' +
                ", modello='" + modello + '\'' +
                ", numeroPatente='" + numeroPatente + '\'' +
                ", dataRilascioPatente=" + dataRilascioPatente +
                ", dataScadenzaPatente=" + dataScadenzaPatente +
                ", entePatente='" + entePatente + '\'' +
                ", stato='" + stato + '\'' +
                ", utilizzoMotiviIspettivi='" + utilizzoMotiviIspettivi + '\'' +
                ", utilizzoMotiviUrgenza='" + utilizzoMotiviUrgenza + '\'' +
                ", utilizzoMotiviTrasporto='" + utilizzoMotiviTrasporto + '\'' +
                ", utilizzoAltriMotivi='" + utilizzoAltriMotivi + '\'' +
                '}';
    }
}
