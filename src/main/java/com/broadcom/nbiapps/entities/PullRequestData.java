package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigInteger;


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
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pull_req_number", unique=true, nullable=false)
	private BigInteger pullReqNumber;

	@Column(nullable=false, length=30)
	private String action;

	@Column(name="create_date_time", nullable=false)
	private Timestamp createDateTime;

	@Lob
	@Column(nullable=false)
	private String payload;

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

	public String getPayload() {
		return this.payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public BigInteger getSiloId() {
		return this.siloId;
	}

	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}

}