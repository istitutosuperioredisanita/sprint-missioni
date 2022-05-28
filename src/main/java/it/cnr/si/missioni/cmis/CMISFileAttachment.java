package it.cnr.si.missioni.cmis;

import java.util.Optional;

public class CMISFileAttachment {
	private Long idMissione;
	private String id;
	private String nomeFile;
	private String nodeRef;
	private String tipo;
	public Long getIdMissione() {
		return idMissione;
	}
	public void setIdMissione(Long idMissione) {
		this.idMissione = idMissione;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = Optional.ofNullable(id)
				.filter(s -> s.indexOf(";") != -1)
				.map(s -> s.substring(0, s.indexOf(";")))
				.orElse(id);
	}
	public String getNomeFile() {
		return nomeFile;
	}
	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNodeRef() {
		return nodeRef;
	}
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}
}
