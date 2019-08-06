package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


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
	@Column(name="data_audit_id")
	private Long dataAuditId;

	private String action;

	@Column(name="build_number")
	private BigInteger buildNumber;

	@Column(name="file_path")
	private String filePath;

	@Column(name="md5_value")
	private String md5Value;

	@Column(name="silo_id")
	private BigInteger siloId;

	public DataAudit() {
	}

	public Long getDataAuditId() {
		return this.dataAuditId;
	}

	public void setDataAuditId(Long dataAuditId) {
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