package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the silo_names database table.
 * 
 */
@Entity
@Table(name="silo_names")
@NamedQuery(name="SiloName.findAll", query="SELECT s FROM SiloName s")
@NamedQuery(name="SiloName.findBySiloName", query="SELECT s FROM SiloName s where s.siloNameReq.siloName=:siloName")
public class SiloName implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="silo_id", nullable=false)
	private BigInteger siloId;

	@Embedded
	SiloNameReq siloNameReq;

	public SiloName() {
	}

	public BigInteger getSiloId() {
		return this.siloId;
	}

	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}

	public SiloNameReq getSiloNameReq() {
		return siloNameReq;
	}

	public void setSiloNameReq(SiloNameReq siloNameReq) {
		this.siloNameReq = siloNameReq;
	}
}