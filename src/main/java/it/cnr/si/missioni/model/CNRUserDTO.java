package it.cnr.si.missioni.model;

import it.cnr.si.missioni.service.dto.UserDTO;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by francesco on 02/04/15.
 */
public class CNRUserDTO extends UserDTO {

    public static final boolean ACTIVATED = true;
    private String matricola;
    private String strutturaAppartenenza;
    private String livello;
    private String profilo;

    private String cittaSede;

    public String getStrutturaAppartenenza() {
        return strutturaAppartenenza;
    }

    public void setStrutturaAppartenenza(String strutturaAppartenenza) {
        this.strutturaAppartenenza = strutturaAppartenenza;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }

    public String getProfilo() {
        return profilo;
    }

    public void setProfilo(String profilo) {
        this.profilo = profilo;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getCittaSede() {
        return cittaSede;
    }

    public void setCittaSede(String cittaSede) {
        this.cittaSede = cittaSede;
    }

    private Map<Object, Object> map;

    public Map<Object, Object> getMap() {
		return map;
	}

    public CNRUserDTO(String login, String password, String matricola, String firstName, String lastName, String email, String langKey,
                      List<String> roles, String departmentNumber) {

        super(login, firstName, lastName, email, ACTIVATED, langKey, new HashSet<String>(roles));
        this.matricola = matricola;
        this.map = new Hashtable<Object, Object>();
    }
}
