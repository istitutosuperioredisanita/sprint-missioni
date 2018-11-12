package it.cnr.si.missioni.cmis;

public enum CMISRimborsoMissioneAspect {
	RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI("P:missioni_rimborso_attachment:allegati"),
	RIMBORSO_MISSIONE_ATTACHMENT_ALLEGATI_ANNULLAMENTO("P:missioni_rimborso_attachment:allegati_annullamento"),
	RIMBORSO_MISSIONE_ATTACHMENT_SCONTRINI("P:missioni_rimborso_attachment:scontrini"),
	RIMBORSO_MISSIONE_ATTACHMENT_RIMBORSO("P:missioni_rimborso_attachment:rimborso");
	
	
	private final String value;

	private CMISRimborsoMissioneAspect(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static CMISRimborsoMissioneAspect fromValue(String v) {
		for (CMISRimborsoMissioneAspect c : CMISRimborsoMissioneAspect.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}	

}
