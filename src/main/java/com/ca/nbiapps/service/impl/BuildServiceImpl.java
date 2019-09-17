/**
 * 
 */
package com.ca.nbiapps.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.ca.nbiapps.business.layer.GitOpertions;
import com.ca.nbiapps.business.layer.ModulesValidation;
import com.ca.nbiapps.client.TriggerBuild;
import com.ca.nbiapps.constants.BuildConstants;
import com.ca.nbiapps.constants.OpenPullFileStatusConstants;
import com.ca.nbiapps.dao.BuildAuditDAO;
import com.ca.nbiapps.dao.PullRequestDataDAO;
import com.ca.nbiapps.dao.SiloNameDAO;
import com.ca.nbiapps.entities.BuildAudit;
import com.ca.nbiapps.entities.BuildAuditReq;
import com.ca.nbiapps.entities.PullRequestData;
import com.ca.nbiapps.entities.SiloName;
import com.ca.nbiapps.exceptions.BuildSystemException;
import com.ca.nbiapps.exceptions.BuildValidationException;
import com.ca.nbiapps.model.BuildAuditAddlData;
import com.ca.nbiapps.model.PullRequest;
import com.ca.nbiapps.model.PullRequestEvent;
import com.ca.nbiapps.model.ResponseBuilder;
import com.ca.nbiapps.service.BuildService;
import com.ca.nbiapps.util.CoreUtils;
import com.ca.nbiapps.util.FileProbeUtil;

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
	
	@Value("${spring.allowed.filelist}")
	private String allowedFileList;
	
	@Value("${spring.build.workspace}")
	private String workspace;

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

		MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<String, String>();
		paramsMap.add("SILO_ID", "" + siloId);
		paramsMap.add("PULL_REQUEST_NUMBER", "" + pullReq.getNumber());
		paramsMap.add("TASK_ID", "" + taskId);
		triggerBuild.jenkinsRemoteAPIBuildWithParameters(jobName, paramsMap);
	}
	
	@Override
	public void preBuildProcess(BuildAuditReq buildAuditReq) {
		PullRequestData pullReqData = pullRequestDataDAO.findByPullReqNumberAndSiloId(buildAuditReq.getPullReqNumber(), buildAuditReq.getSiloId());
		String taskId = pullReqData.getPullRequest().getTitle().trim();
		// pullGitChanges(pullReqData); - do it later. right work with shell script.
			
		List<DiffEntry> diffEntry = null;
		try {
			diffEntry = getDiffEntries(pullReqData);
			doBuildValidation(taskId, diffEntry);
		}  catch (IOException | GitAPIException e) {		
			throw new BuildSystemException(e);
		} 
	}

	private List<DiffEntry> getDiffEntries(PullRequestData pullReqData) throws GitAPIException, IOException {
		String taskId = pullReqData.getPullRequest().getTitle().trim();
		SiloName siloName = siloNameDAO.findById(pullReqData.getSiloId()).get();
		String localGitRepo = workspace+File.separator+siloName.getSiloNameReq().getSiloName();
		List<DiffEntry> diffEntry = null;
		try(GitOpertions op = new GitOpertions(localGitRepo)) {
			diffEntry = op.gitDiff("refs/heads/master", "refs/heads/"+taskId);
		} 
		return diffEntry;
	}

	// not used - we will plugin it later.
	private void pullGitChanges(PullRequestData pullReqData) throws IOException, GitAPIException {
		SiloName siloName = siloNameDAO.findById(pullReqData.getSiloId()).get();
		String localGitRepo = workspace+File.separator+siloName.getSiloNameReq().getSiloName();
		String taskBranchName = pullReqData.getTaskId();
		GitOpertions gitOpertions = new GitOpertions(localGitRepo);
		gitOpertions.checkoutDir("master");
		gitOpertions.gitPull("origin", "master");
		gitOpertions.fetch(taskBranchName);
		gitOpertions.checkoutDir(taskBranchName);
	}

	private BuildAudit saveBuildAudit(BuildAuditReq buildAuditReq) {
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

	@Override
	public void updateBuildAudit(BuildAudit buildAuditInput) {
		BuildAuditReq buildAuditReq = buildAuditInput.getBuildAuditReq();
		BuildAudit buildAuditSave = buildAuditDAO.findByPullReqNumberAndBuildNumberAndSiloId(buildAuditReq.getPullReqNumber(), buildAuditReq.getBuildNumber(),
				buildAuditReq.getSiloId());
		buildAuditSave.setStatusCode(buildAuditInput.getStatusCode());
		BuildAuditAddlData BuildAuditAddlDataInput = buildAuditInput.getBuildAuditAddlData();
		if (BuildAuditAddlDataInput.getReason() != null) {
			buildAuditSave.getBuildAuditAddlData().setReason(BuildAuditAddlDataInput.getReason());
		}
	}


	private void validatePullRequest(BuildAuditReq buildAuditReq) {
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

		// TODO: sales force task validation.
	}

	/**
	 * Pre-validation Checks on opening a pull request The following checks
	 * needs to be put in place:-
	 * 
	 * ADD 1. Are the extensions of the files correct 2. Duplicate Files are not
	 * present a) java files should not have the same package.
	 * 
	 * MODIFY 1. Duplicate Files are not present a) java files should not have
	 * the same package.
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws XmlPullParserException 
	 */
	private void doBuildValidation(String taskId, List<DiffEntry> diffEntry) throws IOException, GitAPIException  {
		String basePath = System.getProperty("user.dir");
		List<String> validationDetails = new ArrayList<>();
		try {
			String relativeFilePath = null;
			String operation = null;
			for (DiffEntry entry : diffEntry) {	
				operation = entry.getChangeType().name();
				if (entry.getChangeType().equals(DiffEntry.ChangeType.ADD) || entry.getChangeType().equals(DiffEntry.ChangeType.MODIFY)) {
					relativeFilePath = entry.getNewPath();
				} else if (entry.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
					relativeFilePath = entry.getOldPath(); 
				} 
			}
			
		
				boolean isAdded = operation.equals(OpenPullFileStatusConstants.ADD.getStrAction());
				boolean isModified = operation.equals(OpenPullFileStatusConstants.MODIFY.getStrAction());
				
				FileProbeUtil fileProbe = new FileProbeUtil(basePath);
				if (isAdded) {
					ResponseBuilder areFileExtensionsCorrect = fileProbe.areFileExtensionsCorrect(allowedFileList, relativeFilePath);
					if (!areFileExtensionsCorrect.isResult()) {
						validationDetails.add(areFileExtensionsCorrect.getResultDesc());
					}
				}
				
				if (isAdded || isModified) {
					
						if (relativeFilePath.endsWith("pom.xml")) {
							logger.info("relativeFilePath: " + relativeFilePath);
							ResponseBuilder isPomCorrect = fileProbe.isPomCorrect(relativeFilePath);
							if (!isPomCorrect.isResult()) {
								validationDetails.add(isPomCorrect.getResultDesc());
							}
			
						} else if (relativeFilePath.endsWith("java")) {
							ResponseBuilder isFileUnique = fileProbe.isFileUnique(relativeFilePath.substring(relativeFilePath.lastIndexOf("/") + 1), "java", basePath);
							if (!isFileUnique.isResult()) {
								validationDetails.add(isFileUnique.getResultDesc());
							}
						} else {
							logger.warn("[" + taskId + "] unhandled file operation [ " + operation + " ] ignored for validation");
						}
					
				
			}
			
			ModulesValidation modulesValidator = new ModulesValidation(basePath, taskId, diffEntry);	
			ResponseBuilder responseBuilder = modulesValidator.sonarPropertyFilePresent();
			if (!responseBuilder.isResult()) {
				validationDetails.add(responseBuilder.getResultDesc());
			}
	
			responseBuilder = modulesValidator.sonarPropertyCorrect();
			if (!responseBuilder.isResult()) {
				validationDetails.add(responseBuilder.getResultDesc());
			}
			
			responseBuilder =  modulesValidator.validateDeleteModuleCheck();
			if (!responseBuilder.isResult()) {
				validationDetails.add(responseBuilder.getResultDesc());
			}
			
			responseBuilder =  modulesValidator.validateUnUsedDependenciesCheck();
			if (!responseBuilder.isResult()) {
				validationDetails.add(responseBuilder.getResultDesc());
			}
			//TODO: module version check increment.
		} catch(XmlPullParserException xpe) {
			validationDetails.add(" pom.xml parsing failed - " + xpe.getLocalizedMessage());
			throw new BuildValidationException(validationDetails, xpe);
		}
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
		buildAuditAddlData.setCommitterFullName(""); // TODO:
		return buildAuditAddlData;
	}

	private ResponseBuilder prechecks(String taskIdFromRef, String taskId, boolean fork, boolean isPrivate) {
		logger.debug("isPrivateRep: " + isPrivate);
		if (fork) {
			if (!"master".equals(taskIdFromRef)) {
				return new ResponseBuilder(false, "Developer should open a PULL request from feature [DT or Case] branch.");
			}
		} else if (!fork && isPrivate) {
			if ("master".equals(taskIdFromRef)) {
				return new ResponseBuilder(false, "Developer should not open PULL Request from a master branch.");
			}

			if (!taskId.equals(taskIdFromRef)) {
				return new ResponseBuilder(false,
						"Title of the pull request should be same as feature branch name. PullRequest title passed: [" + taskId + "], It should be [" + taskIdFromRef + "]");
			}
		}
		return new ResponseBuilder(true, "");
	}

	@Override
	public void postBuildProcess(BuildAuditReq buildAuditReq) {
		//generateArtifacts method.
		//uploadArtifacts method.
		//audit to DB.
	}
}
