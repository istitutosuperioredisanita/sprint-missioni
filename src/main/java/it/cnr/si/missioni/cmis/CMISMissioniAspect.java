package it.cnr.si.missioni.cmis;

public enum CMISMissioniAspect {
	ORDINE_MISSIONE_ASPECT("P:missioni_commons_aspect:ordine_missione"),
	RIMBORSO_MISSIONE_ASPECT("P:missioni_commons_aspect:rimborso_missione");
	
	
	private final String value;

	private CMISMissioniAspect(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static CMISMissioniAspect fromValue(String v) {
		for (CMISMissioniAspect c : CMISMissioniAspect.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}	

}
