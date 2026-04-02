package it.cnr.si.missioni.model;

import java.io.Serializable;

public class SimpleUtenteWebDto implements Serializable {

    private Integer id;

    private String username;

    private String email;

    private SimplePersonaWebDto persona;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SimplePersonaWebDto getPersona() {
        return persona;
    }

    public void setPersona(SimplePersonaWebDto persona) {
        this.persona = persona;
    }
}
