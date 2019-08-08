package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import com.broadcom.nbiapps.model.PullRequest;


/**
 * The persistent class for the pull_request_data database table.
 * 
 */
@Entity
@Table(name="pull_request_data")
@NamedQuery(name="PullRequestData.findAll", query="SELECT p FROM PullRequestData p")
public class PullRequestData implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="pull_req_number", unique=true, nullable=false)
	@XmlElement(name="number")
	private BigInteger pullReqNumber;

	@Column(nullable=false, length=30)
	private String action;

	@Column(name="create_date_time", nullable=false)
	private Timestamp createDateTime;

	@Lob
	@Column(name="payload", nullable=false)
	@Convert(converter = PullRequestConverter.class)
	private PullRequest pullRequest;
	
	@Column(name="silo_id", nullable=false)
	private BigInteger siloId;

	public PullRequestData() {
	}
	
	public BigInteger getPullReqNumber() {
		return this.pullReqNumber;
	}

	public void setPullReqNumber(BigInteger pullReqNumber) {
		this.pullReqNumber = pullReqNumber;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Timestamp getCreateDateTime() {
		return this.createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
	}

	public PullRequest getPullRequest() {
		return pullRequest;
	}

	public void setPullRequest(PullRequest pullRequest) {
		this.pullRequest = pullRequest;
	}


	public BigInteger getSiloId() {
		return this.siloId;
	}

	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}

	@Override
	public String toString() {
		return "PullRequestData [pullReqNumber=" + pullReqNumber + ", action=" + action + ", createDateTime=" + createDateTime + ", pullRequest=" + pullRequest + ", siloId="
				+ siloId + "]";
	}
}