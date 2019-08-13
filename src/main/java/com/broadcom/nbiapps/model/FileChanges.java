/**
 * 
 */
package com.broadcom.nbiapps.model;

import java.util.List;

/**
 * @author Balaji N
 *
 */
public class FileChanges {
	private String operation;
	private List<String> changeList;
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public List<String> getChangeList() {
		return changeList;
	}
	public void setChangeList(List<String> changeList) {
		this.changeList = changeList;
	}
}
