/**
 * 
 */
package com.broadcom.nbiapps.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.broadcom.nbiapps.dao.ICustomDAO;
import com.broadcom.nbiapps.dao.SiloNameDAO;
import com.broadcom.nbiapps.entities.SiloName;
import com.broadcom.nbiapps.entities.SiloNameReq;
import com.broadcom.nbiapps.exceptions.BuildValidationException;
import com.broadcom.nbiapps.service.AdminService;

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
		siloName.setSiloId(3L);
		siloName.setSiloNameReq(siloNameReq);
		//siloNameDAO.save(siloName);
		customDAO.merge(siloName);
	}
}
