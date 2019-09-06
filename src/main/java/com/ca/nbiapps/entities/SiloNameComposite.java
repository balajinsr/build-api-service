package com.ca.nbiapps.entities;

public class SiloNameComposite {
	private String siloName;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;	
		result = prime * result + ((siloName == null) ? 0 : siloName.hashCode());
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
		SiloNameComposite other = (SiloNameComposite) obj;
		
		if (siloName == null) {
			if (other.siloName != null)
				return false;
		} else if (!siloName.equals(other.siloName))
			return false;
		
		return true;
	}
	
}