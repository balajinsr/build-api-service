/**
 * 
 */
package com.broadcom.nbiapps.model;

/**
 * @author Balaji N
 *
 */
public class FileChanges {
	private String operation;
	private String changeList;
	
	public String getChangeList() {
		return changeList;
	}
	public void setChangeList(String changeList) {
		this.changeList = changeList;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
}
