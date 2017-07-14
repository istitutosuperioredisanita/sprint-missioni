package it.cnr.si.missioni.cmis;

import java.util.Date;

public class CMISFileAttachmentComplete extends CMISFileAttachment{
	private String version;
	private Date date;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
