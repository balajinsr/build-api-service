/**
 * 
 */
package com.ca.nbiapps.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * @author Balaji N
 *
 */
@Embeddable
public class SiloNameReq {
	@Column(name="silo_name", nullable=false, length=50)
	private String siloName;
	@Transient
	private BigInteger id;

	public String getSiloName() {
		return siloName;
	}

	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
}
