package it.cnr.si.missioni.domain.custom;

import java.io.Serializable;

import it.cnr.si.missioni.util.proxy.json.JSONBody;

public class ExternalProblem extends JSONBody implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int PRIORITY = 2;
    private String firstName;
    private String familyName;
    private String email;
    private String login;

    // problem
	private Long idSegnalazione;
    private String titolo;
    private String descrizione;
    private Integer categoria;
    private String categoriaDescrizione;
    
    // note
    private String nota;
    private Integer stato;
    
	public static final String ALLEGATO = "allegato";

    // allegato bas64
    private String allegato;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getIdSegnalazione() {
		return idSegnalazione;
	}

	public void setIdSegnalazione(Long idSegnalazione) {
		this.idSegnalazione = idSegnalazione;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getCategoria() {
		return categoria;
	}

	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}

	public String getCategoriaDescrizione() {
		return categoriaDescrizione;
	}

	public void setCategoriaDescrizione(String categoriaDescrizione) {
		this.categoriaDescrizione = categoriaDescrizione;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	public String getAllegato() {
		return allegato;
	}

	public void setAllegato(String allegato) {
		this.allegato = allegato;
	}

}
