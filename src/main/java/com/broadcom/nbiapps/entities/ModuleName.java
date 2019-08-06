package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the module_names database table.
 * 
 */
@Entity
@Table(name="module_names")
@NamedQuery(name="ModuleName.findAll", query="SELECT m FROM ModuleName m")
public class ModuleName implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="module_id")
	private Long moduleId;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="module_name")
	private String moduleName;


	public ModuleName() {
	}

	public Long getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/*public List<BinaryAudit> getBinaryAudits() {
		return this.binaryAudits;
	}

	public void setBinaryAudits(List<BinaryAudit> binaryAudits) {
		this.binaryAudits = binaryAudits;
	}

	public BinaryAudit addBinaryAudit(BinaryAudit binaryAudit) {
		getBinaryAudits().add(binaryAudit);
		binaryAudit.setModuleName(this);

		return binaryAudit;
	}

	public BinaryAudit removeBinaryAudit(BinaryAudit binaryAudit) {
		getBinaryAudits().remove(binaryAudit);
		binaryAudit.setModuleName(null);

		return binaryAudit;
	}

	public List<ModuleAudit> getModuleAudits() {
		return this.moduleAudits;
	}

	public void setModuleAudits(List<ModuleAudit> moduleAudits) {
		this.moduleAudits = moduleAudits;
	}

	public ModuleAudit addModuleAudit(ModuleAudit moduleAudit) {
		getModuleAudits().add(moduleAudit);
		moduleAudit.setModuleName(this);

		return moduleAudit;
	}

	public ModuleAudit removeModuleAudit(ModuleAudit moduleAudit) {
		getModuleAudits().remove(moduleAudit);
		moduleAudit.setModuleName(null);

		return moduleAudit;
	}*/

}