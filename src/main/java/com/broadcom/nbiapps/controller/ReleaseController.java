/**
 * 
 */
package com.broadcom.nbiapps.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.broadcom.nbiapps.model.ReleaseReq;
import com.broadcom.nbiapps.model.SCRCreateReq;

/**
 * @author Balaji N
 *
 */
@RestController
@RequestMapping(path = "/release", consumes=MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ReleaseController {

	@PostMapping(path = "/releasePackageArtifacts")
	public ResponseEntity<Object> processReleaseArtifacts(HttpServletRequest request, @RequestBody ReleaseReq releaseReq)  {
		//TODO:
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping(path = "/createSCR")
	public  ResponseEntity<Object> createSCR(HttpServletRequest request, @RequestBody SCRCreateReq scrCreateReq)  {
		//TODO:
		return ResponseEntity.ok().body(null);
	}
}
