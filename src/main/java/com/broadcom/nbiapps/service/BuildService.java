/**
 * 
 */
package com.broadcom.nbiapps.service;

import com.broadcom.nbiapps.entities.BuildAudit;
import com.broadcom.nbiapps.entities.BuildAuditReq;
import com.broadcom.nbiapps.model.ListOfBuildFilesReq;

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
