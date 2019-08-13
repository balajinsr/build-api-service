/**
 * 
 */
package com.broadcom.nbiapps.model;

/**
 * @author Balaji N
 *
 */
public class ModuleData {
	private String moduleName;
	private boolean twoLevelModule;
	
	
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
}
