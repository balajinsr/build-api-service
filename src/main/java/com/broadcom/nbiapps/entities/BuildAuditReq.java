/**
 * 
 */
package com.broadcom.nbiapps.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Balaji N
 *
 */
@Embeddable
public class BuildAuditReq {
	@Column(name="silo_id")
	private BigInteger siloId;
	
	@Column(name="build_number")
	private BigInteger buildNumber;
	
	
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
	
	
}
