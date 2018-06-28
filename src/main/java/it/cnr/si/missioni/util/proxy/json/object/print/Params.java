package it.cnr.si.missioni.util.proxy.json.object.print;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Params {

	@JsonProperty("params")
	private List<Param> params = new ArrayList<Param>();
	
	@JsonProperty("report")
	private String report;
	

	@JsonProperty("pgStampa")
	private Long pgStampa;

	/**
	*
	* @return
	* The params
	*/
	public List<Param> getParams() {
	return params;
	}

	/**
	*
	* @param params
	* The params
	*/
	public void setParams(List<Param> params) {
	this.params = params;
	}

	/**
	*
	* @return
	* The report
	*/
	public String getReport() {
	return report;
	}

	/**
	*
	* @param report
	* The report
	*/
	public void setReport(String report) {
	this.report = report;
	}

	public Long getPgStampa() {
		return pgStampa;
	}

	public void setPgStampa(Long pgStampa) {
		this.pgStampa = pgStampa;
	}

}
