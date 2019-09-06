package com.ca.nbiapps.model;

import java.util.List;

import com.ca.nbiapps.entities.BuildAuditReq;

public class ListOfBuildFilesReq extends BuildAuditReq {
	
	private String taskId;
	private List<FileChanges> fileChangeList;

	public List<FileChanges> getFileChangeList() {
		return fileChangeList;
	}

	public void setFileChangeList(List<FileChanges> fileChangeList) {
		this.fileChangeList = fileChangeList;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
