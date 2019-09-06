/**
 * 
 */
package com.ca.nbiapps.model;

import java.math.BigInteger;

/**
 * @author Balaji N
 *
 */
public class SCRCreateReq {
	private BigInteger siloId;
	private String releaseId;
	public BigInteger getSiloId() {
		return siloId;
	}
	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
}
