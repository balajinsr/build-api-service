/**
 * 
 */
package com.broadcom.nbiapps.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Balaji N
 *
 */
@Embeddable
public class SiloNameReq {
	@Column(name="silo_name", nullable=false, length=50)
	private String siloName;

	public String getSiloName() {
		return siloName;
	}

	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}
}
