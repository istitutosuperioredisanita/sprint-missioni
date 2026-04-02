package it.cnr.si.missioni.model;

import java.io.Serializable;

public class IndirizzoWebDto implements Serializable {

  private String via;
  private String civico;
  private String localita;
  private String comune;
  private String provincia;
  private String siglaProvincia;
  private String nazione;
  private String cap;
  private Integer geoId;
  private String comuneCodiceCnr;
  private Integer geoCapId;
  private Integer geoComuneId;

  public String getVia() {
    return via;
  }

  public void setVia(String via) {
    this.via = via;
  }

  public String getCivico() {
    return civico;
  }

  public void setCivico(String civico) {
    this.civico = civico;
  }

  public String getLocalita() {
    return localita;
  }

  public void setLocalita(String localita) {
    this.localita = localita;
  }

  public String getComune() {
    return comune;
  }

  public void setComune(String comune) {
    this.comune = comune;
  }

  public String getProvincia() {
    return provincia;
  }

  public void setProvincia(String provincia) {
    this.provincia = provincia;
  }

  public String getSiglaProvincia() {
    return siglaProvincia;
  }

  public void setSiglaProvincia(String siglaProvincia) {
    this.siglaProvincia = siglaProvincia;
  }

  public String getNazione() {
    return nazione;
  }

  public void setNazione(String nazione) {
    this.nazione = nazione;
  }

  public String getCap() {
    return cap;
  }

  public void setCap(String cap) {
    this.cap = cap;
  }

  public Integer getGeoId() {
    return geoId;
  }

  public void setGeoId(Integer geoId) {
    this.geoId = geoId;
  }

  public String getComuneCodiceCnr() {
    return comuneCodiceCnr;
  }

  public void setComuneCodiceCnr(String comuneCodiceCnr) {
    this.comuneCodiceCnr = comuneCodiceCnr;
  }

  public Integer getGeoCapId() {
    return geoCapId;
  }

  public void setGeoCapId(Integer geoCapId) {
    this.geoCapId = geoCapId;
  }

  public Integer getGeoComuneId() {
    return geoComuneId;
  }

  public void setGeoComuneId(Integer geoComuneId) {
    this.geoComuneId = geoComuneId;
  }
}
