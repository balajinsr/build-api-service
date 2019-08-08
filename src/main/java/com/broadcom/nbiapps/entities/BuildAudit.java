package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.broadcom.nbiapps.model.BuildAuditAddlData;


/**
 * The persistent class for the build_audit database table.
 * 
 */
@Entity
@Table(name="build_audit")
@NamedQuery(name="BuildAudit.findAll", query="SELECT b FROM BuildAudit b")
@NamedQuery(name="BuildAudit.findByPullReqNumberAndBuildNumberAndSiloId", query="SELECT b FROM BuildAudit b WHERE b.buildAuditReq.pullReqNumber=:pullReqNumber"
		+ " and b.buildAuditReq.buildNumber=:buildNumber and b.buildAuditReq.siloId=:siloId")

public class BuildAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="build_req_id")
	private Long buildReqId;
	
	@Embedded
	BuildAuditReq buildAuditReq;

	@Column(name="task_id", nullable=false, length=50)
	private String taskId;
	
	@Column(name="parent_task_id", length=20)
	private String parentTaskId;
	
	@Column(name="create_date_time", nullable=false)
	private Timestamp createDateTime;

	@Column(name="last_modified_date_time", nullable=false)
	private Timestamp lastModifiedDateTime;
	
	@Column(name="status_code", nullable=false)
	private BigInteger statusCode;
	
	@Lob
	@Column(name="build_additional_data")
	@Convert(converter = BuildAuditAddlDataConverter.class)
	private BuildAuditAddlData buildAuditAddlData;

	public BuildAudit() {
	}

	public Long getBuildReqId() {
		return this.buildReqId;
	}

	public void setBuildReqId(Long buildReqId) {
		this.buildReqId = buildReqId;
	}
	
	public Timestamp getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Timestamp getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	public void setLastModifiedDateTime(Timestamp lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}

	public BuildAuditAddlData getBuildAuditAddlData() {
		return buildAuditAddlData;
	}

	public void setBuildAuditAddlData(BuildAuditAddlData buildAuditAddlData) {
		this.buildAuditAddlData = buildAuditAddlData;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getParentTaskId() {
		return this.parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public BigInteger getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(BigInteger statusCode) {
		this.statusCode = statusCode;
	}

	public BuildAuditReq getBuildAuditReq() {
		return buildAuditReq;
	}

	public void setBuildAuditReq(BuildAuditReq buildAuditReq) {
		this.buildAuditReq = buildAuditReq;
	}

	@Override
	public String toString() {
		return "BuildAudit [buildReqId=" + buildReqId + ", buildAuditReq=" + buildAuditReq + ", taskId=" + taskId + ", parentTaskId=" + parentTaskId + ", statusCode=" + statusCode
				+ ", buildAuditAddlData=" + buildAuditAddlData + "]";
	}
}