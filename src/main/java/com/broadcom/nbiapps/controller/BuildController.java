/**
 * 
 */
package com.broadcom.nbiapps.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.broadcom.nbiapps.entities.BuildAudit;
import com.broadcom.nbiapps.entities.SiloNameReq;
import com.broadcom.nbiapps.model.ListOfBuildFilesReq;
import com.broadcom.nbiapps.service.BuildService;

/**
 * @author Balaji N
 *
 */
@RestController
@RequestMapping(path = "/build", produces = MediaType.APPLICATION_JSON_VALUE)
public class BuildController {

	private static final Logger logger = LoggerFactory.getLogger(BuildController.class);
	
	
	@Autowired
	private BuildService buildService;
	
	@PostMapping(path = "/processPullRequest", consumes = "application/json")
	public ResponseEntity<Object> pullRequestHookListener(HttpServletRequest request, @RequestHeader HttpHeaders httpHeaders) {
		//TODO:
		
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping(path = "/saveBuildAudit", consumes = "application/json")
	public ResponseEntity<Object> addBuildAudit(HttpServletRequest request, @RequestBody BuildAudit buildAuditReq)  {
		//TODO:
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping(path = "/preBuildValidation", consumes = "application/json")
	public ResponseEntity<Object> preBuildValidtion(HttpServletRequest request, @RequestBody ListOfBuildFilesReq listOfFilesReq)  {
		//TODO:
		return ResponseEntity.ok().body(null);
	}
	
	
	@PostMapping(path = "/getCommandForBuild", consumes = "application/json")
	public Object getCommandForBuild(HttpServletRequest request, @RequestBody ListOfBuildFilesReq listOfFilesReq)  {
		//TODO:
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping(path = "/processBuildArtifacts", consumes = "application/json")
	public Object processBuildArtifacts(HttpServletRequest request, @RequestBody ListOfBuildFilesReq listOfFilesReq)  {
		//TODO:
		return ResponseEntity.ok().body(null);
	}
	
	
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/hello")
	@ResponseBody
	public Object hello(@RequestBody SiloNameReq siloName) {
		logger.info("Hello - API start");
		return siloName;
	}
}
