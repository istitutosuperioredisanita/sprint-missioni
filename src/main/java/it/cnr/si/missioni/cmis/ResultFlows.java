package it.cnr.si.missioni.cmis;

import it.cnr.si.missioni.util.Costanti;

import org.springframework.util.StringUtils;

public class ResultFlows {
	private String state;
	private String comment;
	private String taskId;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Boolean isStateReject(){
		if (!StringUtils.isEmpty(getState()) && (getState().equals(Costanti.STATO_RESPINTO_SPESA_FROM_CMIS) || getState().equals(Costanti.STATO_RESPINTO_UO_FROM_CMIS)|| 
				getState().equals(Costanti.STATO_RESPINTO_UO_RIMBORSO_FROM_CMIS)|| getState().equals(Costanti.STATO_RESPINTO_SPESA_RIMBORSO_FROM_CMIS))){
			return true;
		}
		return false;
	}

	public Boolean isApprovato(){
		if (!StringUtils.isEmpty(getState()) && getState().equals(Costanti.STATO_FIRMATO_FROM_CMIS)){
			return true;
		}
		return false;
	}

	public Boolean isFirmaSpesa(){
		if (!StringUtils.isEmpty(getState()) && (getState().equals(Costanti.STATO_FIRMA_SPESA_FROM_CMIS) || getState().equals(Costanti.STATO_FIRMA_SPESA_RIMBORSO_FROM_CMIS))){
			return true;
		}
		return false;
	}

	public Boolean isAnnullato(){
		if (!StringUtils.isEmpty(getState()) && getState().equals(Costanti.STATO_ANNULLATO_FROM_CMIS)){
			return true;
		}
		return false;
	}
}
