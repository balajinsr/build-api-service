package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the cycle_names database table.
 * 
 */
@Entity
@Table(name="cycle_names")
@NamedQuery(name="CycleName.findAll", query="SELECT c FROM CycleName c")
public class CycleName implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cycle_id")
	private Long cycleId;

	@Column(name="cycle_name")
	private String cycleName;

	public CycleName() {
	}

	public Long getCycleId() {
		return this.cycleId;
	}

	public void setCycleId(Long cycleId) {
		this.cycleId = cycleId;
	}

	public String getCycleName() {
		return this.cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}

}