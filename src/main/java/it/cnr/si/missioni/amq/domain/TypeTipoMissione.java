package it.cnr.si.missioni.amq.domain;

public enum TypeTipoMissione {
	ITALIA("I"),
	ESTERA("E");
	
	private final String value;

	private TypeTipoMissione(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static TypeTipoMissione fromValue(String v) {
		for (TypeTipoMissione c : TypeTipoMissione.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}	
}
