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


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A user.
 */
@Entity
@Table(name = "AUTO_PROPRIA")
@SequenceGenerator(name = "SEQUENZA", sequenceName = "SEQ_AUTO_PROPRIA", allocationSize = 0)
public class AutoPropria extends OggettoBulkXmlTransient implements Serializable {

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENZA")
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "AutoPropria{" +
                "id=" + id +
                ", targa='" + targa + '\'' +
                ", cartaCircolazione='" + cartaCircolazione + '\'' +
                ", polizzaAssicurativa='" + polizzaAssicurativa + '\'' +
                ", uid='" + uid + '\'' +
                ", marca='" + marca + '\'' +
                ", modello='" + modello + '\'' +
                '}';
    }
}
