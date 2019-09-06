package com.ca.nbiapps.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


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
	@Column(name="module_id", unique=true, nullable=false)
	private Long moduleId;

	@Column(name="is_deleted", nullable=false, length=1)
	private String isDeleted;

	@Column(name="module_name", nullable=false, length=50)
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
}