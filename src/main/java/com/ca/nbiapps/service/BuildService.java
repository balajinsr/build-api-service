/**
 * 
 */
package com.ca.nbiapps.service;

import com.ca.nbiapps.entities.BuildAudit;
import com.ca.nbiapps.entities.BuildAuditReq;
import com.ca.nbiapps.model.ListOfBuildFilesReq;

/**
 * @author Balaji N
 *
 */
public interface BuildService {
	void processPullRequest(String payload);
	BuildAudit saveBuildAudit(BuildAuditReq buildAuditReq);
	void validatePullRequest(BuildAuditReq buildAuditReq);
	void updateBuildAudit(BuildAudit buildAudit);
	void preBuildValidtion(ListOfBuildFilesReq listOfFilesReq);
}
