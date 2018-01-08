package it.cnr.si.missioni.amq.domain;

public enum TypeMissione {
	ORDINE("Ordine"),
	ANNULLAMENTO("Annullamento"),
	RIMBORSO("Rimborso");
	
	private final String value;

	private TypeMissione(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static TypeMissione fromValue(String v) {
		for (TypeMissione c : TypeMissione.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}	
}
