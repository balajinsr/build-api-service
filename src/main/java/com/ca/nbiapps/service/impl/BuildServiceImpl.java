/**
 * 
 */
package com.ca.nbiapps.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.ca.nbiapps.business.layer.ModulesValidation;
import com.ca.nbiapps.business.layer.PomContainer;
import com.ca.nbiapps.client.TriggerBuild;
import com.ca.nbiapps.constants.BuildConstants;
import com.ca.nbiapps.constants.OpenPullFileStatusConstants;
import com.ca.nbiapps.dao.BuildAuditDAO;
import com.ca.nbiapps.dao.PullRequestDataDAO;
import com.ca.nbiapps.dao.SiloNameDAO;
import com.ca.nbiapps.entities.BuildAudit;
import com.ca.nbiapps.entities.BuildAuditReq;
import com.ca.nbiapps.entities.PullRequestData;
import com.ca.nbiapps.exceptions.BuildValidationException;
import com.ca.nbiapps.model.BuildAuditAddlData;
import com.ca.nbiapps.model.FileChanges;
import com.ca.nbiapps.model.ListOfBuildFilesReq;
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

	@Override
	public void validatePullRequest(BuildAuditReq buildAuditReq) {
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
	 * @throws XmlPullParserException 
	 */
	@Override
	public void preBuildValidtion(ListOfBuildFilesReq listOfFilesReq)  {
		String taskId = listOfFilesReq.getTaskId();
		String basePath = System.getProperty("user.dir");
		List<String> validationDetails = new ArrayList<>();
		try {
			for (FileChanges fileChange : listOfFilesReq.getFileChangeList()) {
				String operation = fileChange.getOperation();
				List<String> relativeFilePaths = fileChange.getChangeList();
				boolean isAdded = operation.equals(OpenPullFileStatusConstants.ADD.getStrAction());
				boolean isModified = operation.equals(OpenPullFileStatusConstants.MODIFY.getStrAction());
				logger.info("[" + taskId + "] - Operation: [" + operation + "] Pre file validation Check to be invoked on Set " + fileChange.getChangeList().toString());
				
				FileProbeUtil fileProbe = new FileProbeUtil(basePath);
				if (isAdded) {
					ResponseBuilder areFileExtensionsCorrect = fileProbe.areFileExtensionsCorrect(allowedFileList, relativeFilePaths);
					if (!areFileExtensionsCorrect.isResult()) {
						validationDetails.add(areFileExtensionsCorrect.getResultDesc());
					}
				}
				
				if (isAdded || isModified) {
					for (String relativeFilePath : relativeFilePaths) {
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
				}
			}
			
			ModulesValidation modulesValidator = new ModulesValidation(basePath, taskId, listOfFilesReq.getFileChangeList());	
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
		} catch (IOException ioe) {
			validationDetails.add("File Not found error : " + ioe.getLocalizedMessage());
			validationDetails.add("IOException : "+ioe);
			throw new BuildValidationException(validationDetails, ioe);
		} catch (XmlPullParserException xpe) {
			validationDetails.add(" pom.xml parsing failed - " + xpe.getLocalizedMessage());
			throw new BuildValidationException(validationDetails, xpe);
		} catch (GitAPIException ge) {
			validationDetails.add(" Git operation failed. - " + ge.getLocalizedMessage());
			throw new BuildValidationException(validationDetails, ge);
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


	@Deprecated //remove this method after catch handle in main validate method -- THIS NEW METHOD
	private String getModuleName(String basePath, String relativePath, List<String> validationDetails) {
		String moduleDir = relativePath.substring(0, relativePath.indexOf("/"));
		try {
			PomContainer pomContainer = new PomContainer(basePath + "/" + moduleDir + "/pom.xml");
			if (pomContainer.isTwoLevelModule()) {
				int indexOfTwoLevelModuleIndex = StringUtils.ordinalIndexOf(relativePath, "/", 1);
				String childModuleDirName = relativePath.substring(indexOfTwoLevelModuleIndex + 1, StringUtils.ordinalIndexOf(relativePath, "/", 2));
				return moduleDir + "/" + childModuleDirName;
			}
		} catch (IOException ioe) {
			validationDetails.add("File not found - [" + moduleDir + "/pom.xml]");
			throw new BuildValidationException(validationDetails, ioe);
		} catch (XmlPullParserException xpe) {
			validationDetails.add("[" + moduleDir + "/pom.xml] parsing failed - " + xpe.getLocalizedMessage());
			throw new BuildValidationException(validationDetails, xpe);
		}
		return moduleDir;
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
}
