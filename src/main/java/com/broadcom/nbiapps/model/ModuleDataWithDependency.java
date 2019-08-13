/**
 * 
 */
package com.broadcom.nbiapps.model;

import java.util.List;

import org.apache.maven.model.Dependency;

/**
 * @author Balaji N
 *
 */
public class ModuleDataWithDependency extends ModuleData{
	private String moduleName;
	private boolean twoLevelModule;
	private String operation;
	private List<Dependency> dependencyList;
	
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
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<Dependency> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<Dependency> dependencyList) {
		this.dependencyList = dependencyList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
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
		ModuleDataWithDependency other = (ModuleDataWithDependency) obj;
		if (moduleName == null) {
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		
		return true;
	}

}
