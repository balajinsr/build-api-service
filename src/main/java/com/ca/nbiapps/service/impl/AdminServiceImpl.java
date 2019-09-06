/**
 * 
 */
package com.ca.nbiapps.service.impl;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.nbiapps.dao.ICustomDAO;
import com.ca.nbiapps.dao.SiloNameDAO;
import com.ca.nbiapps.entities.SiloName;
import com.ca.nbiapps.entities.SiloNameReq;
import com.ca.nbiapps.exceptions.BuildValidationException;
import com.ca.nbiapps.service.AdminService;

/**
 * @author Balaji N
 *
 */
@Service
public class AdminServiceImpl implements AdminService {
	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	
	@Autowired
	private SiloNameDAO siloNameDAO;
	
	@Autowired
	private ICustomDAO customDAO;
	
	public void hello(SiloNameReq siloNameReq) {
		logger.info("inside service - hello");
		String value=null;
		SiloName siloName = new SiloName();
		siloName.setSiloNameReq(siloNameReq);
		SiloName s = siloNameDAO.save(siloName);
		if(value== null) {
			
			throw new BuildValidationException("Invalid Request");
			
		} 
		
		
	}
	
	
	public void merge(SiloNameReq siloNameReq) {
		logger.info("inside service - hello");
		/*Optional<SiloName> siloName = siloNameDAO.findById(3L);
		if(siloName.isPresent()) {
			SiloName fetchedSILO = siloName.get();
			fetchedSILO.setSiloNameReq(siloNameReq);
			customDAO.merge(fetchedSILO);
		}*/
		
		SiloName siloName = new SiloName();
		siloName.setSiloId(BigInteger.valueOf(3));
		siloName.setSiloNameReq(siloNameReq);
		//siloNameDAO.save(siloName);
		customDAO.merge(siloName);
	}
}
