/**
 * 
 */
package com.broadcom.nbiapps.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Balaji N
 *
 */
public class BuildArtifacts {
	private Set<ModuleDependency> commonArtifacts = new HashSet<>();
	private List<ModuleData> moduleData = new ArrayList<>();
	
	public Set<ModuleDependency> getCommonArtifacts() {
		return commonArtifacts;
	}
	public void setCommonArtifacts(Set<ModuleDependency> commonArtifacts) {
		this.commonArtifacts = commonArtifacts;
	}
	public List<ModuleData> getModuleData() {
		return moduleData;
	}
	public void setModuleData(List<ModuleData> moduleData) {
		this.moduleData = moduleData;
	}
}
