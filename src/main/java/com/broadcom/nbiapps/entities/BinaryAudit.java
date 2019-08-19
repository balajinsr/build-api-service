package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the binary_audit database table.
 * 
 */
@Entity
@Table(name="binary_audit")
@NamedQuery(name="BinaryAudit.findAll", query="SELECT b FROM BinaryAudit b")
public class BinaryAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="binary_audit_id")
	private BigInteger binaryAuditId;

	

	@Column(name="build_number")
	private BigInteger buildNumber;

	
	@Column(name="task_id")
	private String taskId;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="artifact_audit_id")
	private ArtifactsAudit artifactsAudit;

	private String action;

	@Column(name="md5_value")
	private String md5Value;

	
	@ManyToOne(cascade = CascadeType.MERGE)
	@Column(name="module_id")
	private ModuleName moduleName;

	@ManyToOne(targetEntity=SiloName.class, cascade = CascadeType.MERGE)
	@JoinColumn(name="silo_id")
	private SiloName siloName;

	public BinaryAudit() {
	}

	public BigInteger getBinaryAuditId() {
		return this.binaryAuditId;
	}

	public void setBinaryAuditId(BigInteger binaryAuditId) {
		this.binaryAuditId = binaryAuditId;
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

	public String getMd5Value() {
		return this.md5Value;
	}

	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public ArtifactsAudit getArtifactsAudit() {
		return this.artifactsAudit;
	}

	public void setArtifactsAudit(ArtifactsAudit artifactsAudit) {
		this.artifactsAudit = artifactsAudit;
	}

	public ModuleName getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(ModuleName moduleName) {
		this.moduleName = moduleName;
	}

	public SiloName getSiloName() {
		return this.siloName;
	}

	public void setSiloName(SiloName siloName) {
		this.siloName = siloName;
	}

}