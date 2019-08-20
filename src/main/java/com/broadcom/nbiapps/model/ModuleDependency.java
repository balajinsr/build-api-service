/**
 * 
 */
package com.broadcom.nbiapps.model;

import org.apache.maven.model.Dependency;

/**
 * @author Balaji N
 *
 */
public class ModuleDependency extends Dependency {	
	private static final long serialVersionUID = 1L;
	
	private String file_path;
	private String md5_value;
	private String action;

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getMd5_value() {
		return md5_value;
	}

	public void setMd5_value(String md5_value) {
		this.md5_value = md5_value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}	
	
	
}
