/**
 * 
 */
package com.broadcom.nbiapps.model;

import java.util.List;

/**
 * @author Balaji N
 *
 */

public class ModuleData {
	private String moduleName;
	private String moduleAction;
	
	
	// module artifacts as list - both added and deleted
    private List<ModuleDependency> moduleArtifacts;
    private List<ModuleDependency> moduleDependencies;
    
	
	// module pom action with added or modified
	private boolean twoLevelModule;
	private String pomAction;
	private boolean isSourceChanged;
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public boolean isTwoLevelModule() {
		return twoLevelModule;
	}

	public void setTwoLevelModule(boolean twoLevelModule) {
		this.twoLevelModule = twoLevelModule;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleData other = (ModuleData) obj;
		if (moduleName == null) {
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "ModuleData [moduleName=" + moduleName + ", twoLevelModule=" + twoLevelModule + "]";
	}

	public String getModuleAction() {
		return moduleAction;
	}

	public void setModuleAction(String moduleAction) {
		this.moduleAction = moduleAction;
	}

	public List<ModuleDependency> getModuleArtifacts() {
		return moduleArtifacts;
	}

	public void setModuleArtifacts(List<ModuleDependency> moduleArtifacts) {
		this.moduleArtifacts = moduleArtifacts;
	}

	public String getPomAction() {
		return pomAction;
	}

	public void setPomAction(String pomAction) {
		this.pomAction = pomAction;
	}

	public boolean isSourceChanged() {
		return isSourceChanged;
	}

	public void setSourceChanged(boolean isSourceChanged) {
		this.isSourceChanged = isSourceChanged;
	}

	public List<ModuleDependency> getModuleDependencies() {
		return moduleDependencies;
	}

	public void setModuleDependencies(List<ModuleDependency> moduleDependencies) {
		this.moduleDependencies = moduleDependencies;
	}
}
