/**
 * 
 */
package com.ca.nbiapps.service;

import com.ca.nbiapps.entities.BuildAudit;
import com.ca.nbiapps.entities.BuildAuditReq;

/**
 * @author Balaji N
 *
 */
public interface BuildService {
	void processPullRequest(String payload);
	void updateBuildAudit(BuildAudit buildAudit);
	void preBuildProcess(BuildAuditReq buildAuditReq);
	void postBuildProcess(BuildAuditReq buildAuditReq);
}
