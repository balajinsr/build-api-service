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
 * The persistent class for the data_audit database table.
 * 
 */
@Entity
@Table(name="data_audit")
@NamedQuery(name="DataAudit.findAll", query="SELECT d FROM DataAudit d")
public class DataAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="data_audit_id" , unique=true, nullable=false)
	private BigInteger dataAuditId;

	@Column(nullable=false, length=1)
	private String action;

	@Column(name="build_number", nullable=false)
	private BigInteger buildNumber;

	@Column(name="file_path", nullable=false, length=500)
	private String filePath;

	@Column(name="md5_value", length=50)
	private String md5Value;

	@Column(name="silo_id", nullable=false)
	private BigInteger siloId;

	public DataAudit() {
	}

	public BigInteger getDataAuditId() {
		return this.dataAuditId;
	}

	public void setDataAuditId(BigInteger dataAuditId) {
		this.dataAuditId = dataAuditId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public BigInteger getBuildNumber() {
		return this.buildNumber;
	}

	public void setBuildNumber(BigInteger buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMd5Value() {
		return this.md5Value;
	}

	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}

	public BigInteger getSiloId() {
		return this.siloId;
	}

	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}

}