package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the module_audit database table.
 * 
 */
@Entity
@Table(name="module_audit")
@NamedQuery(name="ModuleAudit.findAll", query="SELECT m FROM ModuleAudit m")
public class ModuleAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="module_audit_id")
	private Long moduleAuditId;

	@Column(name="artifact_action")
	private String artifactAction;

	//bi-directional many-to-one association to ArtifactsAudit
	@ManyToOne
	@JoinColumn(name="artifact_audit_id")
	private ArtifactsAudit artifactsAudit;

	//bi-directional many-to-one association to ModuleName
	@ManyToOne
	@JoinColumn(name="module_id")
	private ModuleName moduleName;

	//bi-directional many-to-one association to SiloName
	@ManyToOne
	@JoinColumn(name="silo_id")
	private SiloName siloName;

	public ModuleAudit() {
	}

	public Long getModuleAuditId() {
		return this.moduleAuditId;
	}

	public void setModuleAuditId(Long moduleAuditId) {
		this.moduleAuditId = moduleAuditId;
	}

	public String getArtifactAction() {
		return this.artifactAction;
	}

	public void setArtifactAction(String artifactAction) {
		this.artifactAction = artifactAction;
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