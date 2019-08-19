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
 * The persistent class for the artifacts_audit database table.
 * 
 */
@Entity
@Table(name="artifacts_audit")
@NamedQuery(name="ArtifactsAudit.findAll", query="SELECT a FROM ArtifactsAudit a")
public class ArtifactsAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="artifact_audit_id", unique=true, nullable=false)
	private BigInteger artifactAuditId;

	@Column(name="artifact_id", nullable=false, length=100)
	private String artifactId;

	@Column(length=20)
	private String classifier;

	@Column(name="group_id", nullable=false, length=100)
	private String groupId;

	@Column(length=20)
	private String scope;

	@Column(nullable=false, length=20)
	private String type="jar";

	@Column(nullable=false, length=20)
	private String version;

	public ArtifactsAudit() {
	}

	public BigInteger getArtifactAuditId() {
		return this.artifactAuditId;
	}

	public void setArtifactAuditId(BigInteger artifactAuditId) {
		this.artifactAuditId = artifactAuditId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getClassifier() {
		return this.classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}