package com.broadcom.nbiapps.entities;

import java.io.Serializable;
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
@Table(name = "artifacts_audit")
@NamedQuery(name = "ArtifactsAudit.findAll", query = "SELECT a FROM ArtifactsAudit a")
public class ArtifactsAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "artifact_audit_id")
	private Long artifactAuditId;

	@Column(name = "artifact_id")
	private String artifactId;

	private String classifier;

	@Column(name = "group_id")
	private String groupId;

	private String scope;

	private String type;

	private String version;

	public ArtifactsAudit() {
	}

	public Long getArtifactAuditId() {
		return this.artifactAuditId;
	}

	public void setArtifactAuditId(Long artifactAuditId) {
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