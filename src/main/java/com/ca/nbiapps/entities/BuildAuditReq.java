/**
 * 
 */
package com.ca.nbiapps.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Balaji N
 *
 */
@Embeddable
public class BuildAuditReq {
	@Column(name="silo_id",  unique=true, nullable=false)
	private BigInteger siloId;
	
	@Column(name="build_number" , nullable=false)
	private BigInteger buildNumber;
	
	@Column(name="pull_req_number", nullable=false)
	private BigInteger pullReqNumber;
	
	public BigInteger getSiloId() {
		return siloId;
	}

	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}

	public BigInteger getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(BigInteger buildNumber) {
		this.buildNumber = buildNumber;
	}

	public BigInteger getPullReqNumber() {
		return pullReqNumber;
	}

	public void setPullReqNumber(BigInteger pullReqNumber) {
		this.pullReqNumber = pullReqNumber;
	}

	@Override
	public String toString() {
		return "BuildAuditReq [siloId=" + siloId + ", buildNumber=" + buildNumber + ", pullReqNumber=" + pullReqNumber + "]";
	}
}
