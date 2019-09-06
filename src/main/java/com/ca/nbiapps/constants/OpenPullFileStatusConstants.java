package com.ca.nbiapps.constants;

public enum OpenPullFileStatusConstants {
 
	ADD ("A","addition of a file"),
	COPY ("C","copy of a file into a new one"),
	DELETE ("D","deletion of a file"),
	MODIFY ("M","modification of the contents or mode of a file"),
	RENAME ("R","renaming of a file"),
	TYPECHANGE ("T","change in the type of the file"),
	UNMERGED ("U","file is unmerged. You must complete the merge before it can be committed"),
	UNKNOWN ("X","unknown file");
	
	private OpenPullFileStatusConstants(String strAction, String strDesc) {
		this.strAction = strAction;
		this.strDesc = strDesc;
	}
	private String strAction;
	private String strDesc;
	
	public String getStrAction() {
		return strAction;
	}
	public void setStrAction(String strAction) {
		this.strAction = strAction;
	}
	public String getStrDesc() {
		return strDesc;
	}
	public void setStrDesc(String strDesc) {
		this.strDesc = strDesc;
	}
	
}
