package it.cnr.si.missioni.cmis;

public enum CMISOrdineMissioneAspect {
	ORDINE_MISSIONE_ATTACHMENT_ALLEGATI("P:missioni_ordine_attachment:allegati"),
	ORDINE_MISSIONE_ATTACHMENT_ALLEGATI_ANTICIPO("P:missioni_ordine_attachment:allegati_anticipo"),
	ORDINE_MISSIONE_ATTACHMENT_USO_AUTO_PROPRIA("P:missioni_ordine_attachment:uso_auto_propria"),
	ORDINE_MISSIONE_ATTACHMENT_RICHIESTA_ANTICIPO("P:missioni_ordine_attachment:richiesta_anticipo"),
	ORDINE_MISSIONE_ATTACHMENT_DOCUMENT("D:missioni_ordine_attachment:document"),
	ORDINE_MISSIONE_ATTACHMENT_ORDINE("P:missioni_ordine_attachment:ordine");
	
	
	private final String value;

	private CMISOrdineMissioneAspect(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static CMISOrdineMissioneAspect fromValue(String v) {
		for (CMISOrdineMissioneAspect c : CMISOrdineMissioneAspect.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}	

}
