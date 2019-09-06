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

import com.ca.nbiapps.model.ModuleData;


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

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="silo_id")
	private SiloName siloName;
	
	@Column(name="task_id")
	private String taskId;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="module_id")	
	private ModuleName moduleName;

	@Lob
	@Column(name="module_data")
	@Convert(converter = ModuleDataConverter.class)
	private ModuleData moduleData;
	
	@Lob
	@Column(name="common_data")
	@Convert(converter = CommonDataConverter.class)
	private CommonData commonData;

	public BinaryAudit() {
	}

	public BigInteger getBinaryAuditId() {
		return this.binaryAuditId;
	}

	public void setBinaryAuditId(BigInteger binaryAuditId) {
		this.binaryAuditId = binaryAuditId;
	}

	public BigInteger getBuildNumber() {
		return this.buildNumber;
	}

	public void setBuildNumber(BigInteger buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public ModuleData getModuleData() {
		return moduleData;
	}

	public void setModuleData(ModuleData moduleData) {
		this.moduleData = moduleData;
	}

	public CommonData getCommonData() {
		return commonData;
	}

	public void setCommonData(CommonData commonData) {
		this.commonData = commonData;
	}

}