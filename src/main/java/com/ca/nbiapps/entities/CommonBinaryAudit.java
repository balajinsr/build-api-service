package com.ca.nbiapps.entities;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the binary_audit database table.
 * 
 */
@Entity
@Table(name="common_binary_audit")
@NamedQuery(name="CommonBinaryAudit.findAll", query="SELECT b FROM CommonBinaryAudit b")
public class CommonBinaryAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="common_binary_audit_id")
	private BigInteger commonBinaryAuditId;

	@Column(name="build_number")
	private BigInteger buildNumber;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="silo_id")
	private SiloName siloName;
	
	@Column(name="task_id")
	private String taskId;

	@Lob
	@Column(name="common_data")
	@Convert(converter = CommonDataConverter.class)
	private CommonData commonData;

	public CommonBinaryAudit() {
	}

	public BigInteger getCommonBinaryAuditId() {
		return commonBinaryAuditId;
	}

	public void setCommonBinaryAuditId(BigInteger commonBinaryAuditId) {
		this.commonBinaryAuditId = commonBinaryAuditId;
	}

	public BigInteger getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(BigInteger buildNumber) {
		this.buildNumber = buildNumber;
	}

	public SiloName getSiloName() {
		return siloName;
	}

	public void setSiloName(SiloName siloName) {
		this.siloName = siloName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public CommonData getCommonData() {
		return commonData;
	}

	public void setCommonData(CommonData commonData) {
		this.commonData = commonData;
	}
}