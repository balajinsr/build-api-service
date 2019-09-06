/**
 * 
 */
package com.ca.nbiapps.model;

import org.apache.maven.model.Dependency;

/**
 * @author Balaji N
 *
 */
public class ModuleDependency extends Dependency {	
	private static final long serialVersionUID = 1L;
	private String filePath;
	private String md5Value;
	private String action;
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getMd5Value() {
		return md5Value;
	}
	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
