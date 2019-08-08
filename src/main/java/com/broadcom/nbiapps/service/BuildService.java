/**
 * 
 */
package com.broadcom.nbiapps.service;

import com.broadcom.nbiapps.entities.BuildAudit;
import com.broadcom.nbiapps.entities.BuildAuditReq;

/**
 * @author Balaji N
 *
 */
public interface BuildService {
	void processPullRequest(String payload);
	Object validatePullRequest(BuildAuditReq buildAuditReq);
	BuildAudit saveBuildAudit(BuildAuditReq buildAuditReq);
}
