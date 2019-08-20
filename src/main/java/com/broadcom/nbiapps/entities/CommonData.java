package com.broadcom.nbiapps.entities;

import java.util.List;

import com.broadcom.nbiapps.model.ModuleDependency;
/**
 * @author Balaji N
 *
 */
public class CommonData {
	private List<ModuleDependency> commonArtifacts;

	public List<ModuleDependency> getCommonArtifacts() {
		return commonArtifacts;
	}

	public void setCommonArtifacts(List<ModuleDependency> commonArtifacts) {
		this.commonArtifacts = commonArtifacts;
	}
}
