package it.cnr.si.missioni.domain.custom.persistence;


import it.cnr.si.missioni.util.Costanti;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

/**
 * A user.
 */
@Entity
@Table(name = "ORDINE_MISSIONE_AUTO_PROPRIA")
public class OrdineMissioneAutoPropria extends OggettoBulkXmlTransient implements Serializable {

	public final static String CMIS_PROPERTY_NAME_DOC_AUTO_PROPRIA = "Allegato";
	public final static String CMIS_PROPERTY_NAME_TIPODOC_AUTO_PROPRIA = "Richiesta Auto Propria";

	@Id
	@Column(name="ID", unique=true, nullable=false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENZA")
	private Long id;

//    @JsonIgnore
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
    @Column(name = "UID", length = 256, nullable = false)
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
	@JoinColumn(name="ID_ORDINE_MISSIONE", nullable=false)
	private OrdineMissione ordineMissione;

    @Size(min = 0, max = 3)
    @Column(name = "STATO", length = 3, nullable = false)
    private String stato;

	@Transient
    List<SpostamentiAutoPropria> listSpostamenti;
    
//    @JsonIgnore
//    @ManyToMany
//    @JoinTable(
//            name = "T_USER_AUTHORITY",
//            joinColumns = {@JoinColumn(name = "login", referencedColumnName = "login")},
//            inverseJoinColumns = {@JoinColumn(name = "name", referencedColumnName = "name")})
//    private Set<Authority> authorities = new HashSet<>();
//
//    @JsonIgnore
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
//    private Set<PersistentToken> persistentTokens = new HashSet<>();
//

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

	public void setId(Long id) {
		this.id = id;
	}

	//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		AutoPropria other = (AutoPropria) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//
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
		if (!StringUtils.isEmpty(getStato())){
        	if (getStato().equals(Costanti.STATO_INSERITO)){
        		return true;
        	} 
    	}
    	return false;
    }

	@Transient
    public String getFileName() {
		return "AutoPropriaOrdineMissione"+getId()+".pdf";
	}
	
}
