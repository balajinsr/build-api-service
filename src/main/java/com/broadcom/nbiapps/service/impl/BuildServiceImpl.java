/**
 * 
 */
package com.broadcom.nbiapps.service.impl;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.broadcom.nbiapps.client.TriggerBuild;
import com.broadcom.nbiapps.constants.BuildConstants;
import com.broadcom.nbiapps.dao.BuildAuditDAO;
import com.broadcom.nbiapps.dao.PullRequestDataDAO;
import com.broadcom.nbiapps.dao.SiloNameDAO;
import com.broadcom.nbiapps.entities.BuildAudit;
import com.broadcom.nbiapps.entities.BuildAuditReq;
import com.broadcom.nbiapps.entities.PullRequestData;
import com.broadcom.nbiapps.exceptions.BuildValidationException;
import com.broadcom.nbiapps.model.BuildAuditAddlData;
import com.broadcom.nbiapps.model.PullRequest;
import com.broadcom.nbiapps.model.PullRequestEvent;
import com.broadcom.nbiapps.model.ResponseBuilder;
import com.broadcom.nbiapps.service.BuildService;
import com.broadcom.nbiapps.util.CoreUtils;

/**
 * @author Balaji N
 *
 */
@Service
public class BuildServiceImpl implements BuildService {

	private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);
	
	@Autowired
	TriggerBuild triggerBuild;
	
	@Autowired
	PullRequestDataDAO pullRequestDataDAO;
	
	@Autowired
	SiloNameDAO siloNameDAO;
	
	@Autowired
	BuildAuditDAO buildAuditDAO;
	
	@Override
	public void processPullRequest(String payload) {
		PullRequestEvent pullReq = CoreUtils.convertToObject(PullRequestEvent.class, payload);
		PullRequestData pullRequestData = new PullRequestData();
		String siloName = pullReq.getPull_request().getHead().getRepo().getName();
		String taskId = pullReq.getPull_request().getTitle().trim();
		BigInteger siloId = siloNameDAO.findBySiloName(siloName).getSiloId();
		String jobName = siloName;
		pullRequestData.setSiloId(siloId);
		BeanUtils.copyProperties(pullReq, pullRequestData);
		pullRequestDataDAO.save(pullRequestData);
		
		MultiValueMap<String, String> paramsMap= new LinkedMultiValueMap<String, String>();
		paramsMap.add("SILO_ID", ""+siloId);
		paramsMap.add("PULL_REQUEST_NUMBER", ""+pullReq.getNumber());
		paramsMap.add("TASK_ID", ""+taskId);
		triggerBuild.jenkinsRemoteAPIBuildWithParameters(jobName, paramsMap);
	}
	
	@Override
	public BuildAudit saveBuildAudit(BuildAuditReq buildAuditReq) {
		BuildAudit buildAudit = new BuildAudit();
		buildAudit.setBuildAuditReq(buildAuditReq);
		buildAudit.setStatusCode(BigInteger.valueOf(BuildConstants.BUILD_FAILED.getStatus()));
		PullRequestData pullReqData = pullRequestDataDAO.findByPullReqNumberAndSiloId(buildAuditReq.getPullReqNumber(), buildAuditReq.getSiloId());
		String taskId = pullReqData.getPullRequest().getTitle().trim();
		buildAudit.setTaskId(taskId);
		buildAudit.setParentTaskId(taskId);
		buildAudit.setBuildAuditAddlData(getBuildAuditAdditionalData(buildAuditReq, pullReqData));
		return buildAuditDAO.save(buildAudit);
	}
	
	
	private BuildAuditAddlData getBuildAuditAdditionalData(BuildAuditReq buildAuditReq, PullRequestData pullReqData) {
		BuildAuditAddlData buildAuditAddlData = new BuildAuditAddlData();
		PullRequest pullReq = pullReqData.getPullRequest();
		StringBuilder mergeCommitUrl = new StringBuilder();
		mergeCommitUrl.append(pullReq.getBase().getRepo().getHtml_url()).append("/compare/");
		mergeCommitUrl.append(pullReq.getBase().getSha()).append("...").append(pullReq.getHead().getSha());
		buildAuditAddlData.setMergeCommitUrl(mergeCommitUrl.toString());
		buildAuditAddlData.setCommitterLoginId(pullReq.getHead().getUser().getLogin());
		buildAuditAddlData.setBuildDuration(0);
		buildAuditAddlData.setReason("");
		buildAuditAddlData.setCommitterEmail("");
		buildAuditAddlData.setCommitterFullName(""); //TODO:
		return buildAuditAddlData;
	}

	@Override
	public Object validatePullRequest(BuildAuditReq buildAuditReq) {
		PullRequestData pullReqData = pullRequestDataDAO.findByPullReqNumberAndSiloId(buildAuditReq.getPullReqNumber(), buildAuditReq.getSiloId());
		PullRequest pullReq = pullReqData.getPullRequest();
		String taskIdFromRef = pullReq.getHead().getRef();
		String taskId = pullReq.getTitle().trim();
		boolean fork = pullReq.getHead().getRepo().isFork();
		boolean isPrivateRep = pullReq.getHead().getRepo().isPrivateRep();
		ResponseBuilder responseBuilder = prechecks(taskIdFromRef, taskId, fork, isPrivateRep);
		if (!responseBuilder.isResult()) {
			throw new BuildValidationException(responseBuilder.getResultDesc());
		} 
		return ResponseEntity.ok().build();
	}

	
	private ResponseBuilder prechecks(String taskIdFromRef, String taskId, boolean fork, boolean isPrivate)  {
		logger.debug("isPrivateRep: "+isPrivate);
		if(fork) {
		   if (!"master".equals(taskIdFromRef)) {
			    return new ResponseBuilder(false, "Developer should open a PULL request from feature [DT or Case] branch.");
	       }
		} else if(!fork && isPrivate) {
			if ("master".equals(taskIdFromRef)) {				
				return new ResponseBuilder(false, "Developer should not open PULL Request from a master branch.");
			}
			
			if(!taskId.equals(taskIdFromRef)) {
				return new ResponseBuilder(false, "Title of the pull request should be same as feature branch name. PullRequest title passed: [" + taskId + "], It should be ["+taskIdFromRef+"]");
			}
		} 
	   return new ResponseBuilder(true,"");
	}
}
