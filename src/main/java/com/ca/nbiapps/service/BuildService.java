/**
 * 
 */
package com.ca.nbiapps.service;

import java.util.Map;

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
	
	Map<String,String> generateBuildCommand(BuildAuditReq buildAuditReq);
	Map<String,String> postBuildProcess(BuildAuditReq buildAuditReq);
}
