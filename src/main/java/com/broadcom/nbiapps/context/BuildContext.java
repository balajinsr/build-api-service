/**
 * 
 */
package com.broadcom.nbiapps.context;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.broadcom.nbiapps.entities.BuildAudit;
import com.broadcom.nbiapps.model.ListOfBuildFilesReq;

/**
 * @author Balaji N
 *
 */
@Component
@Scope(value="prototype")
public class BuildContext {
	private BuildAudit buildAudit;
	private List<ListOfBuildFilesReq> listOfBuildFiles;

	public BuildAudit getBuildAudit() {
		return buildAudit;
	}

	public void setBuildAudit(BuildAudit buildAudit) {
		this.buildAudit = buildAudit;
	}

	public List<ListOfBuildFilesReq> getListOfBuildFiles() {
		return listOfBuildFiles;
	}

	public void setListOfBuildFiles(List<ListOfBuildFilesReq> listOfBuildFiles) {
		this.listOfBuildFiles = listOfBuildFiles;
	}
}
