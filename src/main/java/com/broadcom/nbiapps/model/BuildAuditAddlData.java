/**
 * 
 */
package com.broadcom.nbiapps.model;

/**
 * @author Balaji N
 *
 */
public class BuildAuditAddlData {
	private String committerLoginId;
	private String committerFullName;
	private String committerEmail;
	private String reason;
	private long buildDuration;
	private String mergeCommitUrl;
	
	public String getCommitterLoginId() {
		return committerLoginId;
	}
	public void setCommitterLoginId(String committerLoginId) {
		this.committerLoginId = committerLoginId;
	}
	public String getCommitterFullName() {
		return committerFullName;
	}
	public void setCommitterFullName(String committerFullName) {
		this.committerFullName = committerFullName;
	}
	public String getCommitterEmail() {
		return committerEmail;
	}
	public void setCommitterEmail(String committerEmail) {
		this.committerEmail = committerEmail;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public long getBuildDuration() {
		return buildDuration;
	}
	public void setBuildDuration(long buildDuration) {
		this.buildDuration = buildDuration;
	}
	public String getMergeCommitUrl() {
		return mergeCommitUrl;
	}
	public void setMergeCommitUrl(String mergeCommitUrl) {
		this.mergeCommitUrl = mergeCommitUrl;
	}
	@Override
	public String toString() {
		return "BuildAuditAddlData [committerLoginId=" + committerLoginId + ", committerFullName=" + committerFullName + ", committerEmail=" + committerEmail + ", reason=" + reason
				+ ", buildDuration=" + buildDuration + ", mergeCommitUrl=" + mergeCommitUrl + "]";
	}
}
