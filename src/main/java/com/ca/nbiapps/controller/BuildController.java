/**
 * 
 */
package com.ca.nbiapps.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ca.nbiapps.entities.BuildAudit;
import com.ca.nbiapps.entities.BuildAuditReq;
import com.ca.nbiapps.entities.SiloNameReq;
import com.ca.nbiapps.service.BuildService;
import com.ca.nbiapps.util.CoreUtils;


/**
 * @author Balaji N
 *
 */
@RestController
@RequestMapping(path = "/build", consumes=MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class BuildController {
	private static final Logger logger = LoggerFactory.getLogger(BuildController.class);
	
	@Autowired
	private BuildService buildService;
	
	@PostMapping(path = "/processPullRequest")
	public ResponseEntity<Object> pullRequestHookListener(HttpServletRequest request, @RequestHeader HttpHeaders httpHeaders) throws IOException {
		StringBuilder sb = new StringBuilder();
		String body;
		while ((body = request.getReader().readLine()) != null) {
			sb.append(body);
		}

		logger.info("Event Type: " + httpHeaders.get("X-GitHub-Event"));
		List<String> eventList = httpHeaders.get("X-GitHub-Event");
		logger.debug("[Payload]processPullRequest : " + CoreUtils.prettyJSONPrinter(sb.toString()));
		
		if (eventList != null && !eventList.isEmpty()) {
			String event = eventList.get(0);
			if ("ping".equalsIgnoreCase(event)) {
				logger.info("Received ping request, so responded back with success");
				return ResponseEntity.ok().build();
			} else if ("pull_request".equalsIgnoreCase(event)) {
				buildService.processPullRequest(sb.toString());
				return ResponseEntity.accepted().build();
			} else {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		}
	}
	
	@PostMapping(path = "/preBuildProcess")
	public ResponseEntity<Object> preBuildProcess(@RequestBody BuildAuditReq buildAuditReq)  {
		buildService.preBuildProcess(buildAuditReq);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping(path = "/generateBuildCommand")
	public Object generateBuildCommand(@RequestBody BuildAuditReq buildAuditReq)  {
		Map<String,String> buildCommand = buildService.generateBuildCommand(buildAuditReq);
		return ResponseEntity.ok().body(buildCommand);
	}
	
	
	@PostMapping(path = "/postBuildProcess")
	public Object postBuildProcess(HttpServletRequest request, @RequestBody BuildAuditReq buildAuditReq)  {
		Map<String,String> response = buildService.postBuildProcess(buildAuditReq);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping(path = "/updateBuildAudit")
	public ResponseEntity<Object> updateBuildAudit(HttpServletRequest request, @RequestBody BuildAudit buildAudit)  {
		buildService.updateBuildAudit(buildAudit);
		logger.info("BuildAuditData Updated to DB: "+buildAudit.toString());
		return ResponseEntity.ok().build();
	}
	
	
	
	@PostMapping(path = "/releaseBuildArtifacts")
	public Object processBuildArtifacts(HttpServletRequest request, @RequestBody BuildAudit buildAudit)  {
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
