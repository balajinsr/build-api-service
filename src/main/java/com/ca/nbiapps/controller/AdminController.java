/**
 * 
 */
package com.ca.nbiapps.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ca.nbiapps.entities.SiloNameReq;
import com.ca.nbiapps.service.AdminService;

/**
 * @author Balaji N
 *
 */
@RestController
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	@Autowired
	private AdminService adminService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/helloadmin")
	@ResponseBody
	public ResponseEntity<Object> hello(@RequestBody SiloNameReq siloNameReq) {
		logger.info("Hello Admin- API start");
		//adminService.hello(siloNameReq);
		System.out.print("Hello:::"+siloNameReq.getId());
		return ResponseEntity.ok().body(siloNameReq);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/hellomerge")
	
	public ResponseEntity<Object> merge(@RequestBody SiloNameReq siloNameReq) {
		logger.info("Hello Admin- API start");
		
		adminService.merge(siloNameReq);
		return ResponseEntity.ok().body(siloNameReq);
	}
}
