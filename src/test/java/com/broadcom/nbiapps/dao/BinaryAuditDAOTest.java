/**
 * 
 */
package com.broadcom.nbiapps.dao;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.broadcom.nbiapps.entities.BinaryAudit;

/**
 * @author Balaji N
 *
 */
@SpringBootTest
public class BinaryAuditDAOTest {

	
	@Autowired
	ModuleNameDAO moduleNameDAO;
	
	@Autowired
	BinaryAuditDAO binaryAuditDAO;
	
	@Autowired
	SiloNameDAO siloNameDAO;
	
	@Autowired
	ArtifactAuditDAO artifactAuditDAO;
	
	@Test
	public void binaryAuditSaveTest() {
		/*SiloNameReq siloNameReq = new SiloNameReq();
		siloNameReq.setSiloName("Balaji");
		
		SiloName siloName = new SiloName();
		siloName.setSiloNameReq(siloNameReq);
		SiloName newSiloName = siloNameDAO.save(siloName);
		
		ModuleName moduleName = new ModuleName();
		moduleName.setModuleName("CAPONE");
		moduleName.setIsDeleted("N");
		System.out.println("old::"+moduleName);
		ModuleName newModuleName = moduleNameDAO.save(moduleName);
		System.out.println("new::"+newModuleName);*/
		
	/*	ArtifactsAudit artifactAudit = new ArtifactsAudit();
		artifactAudit.setGroupId("com.ca");
		artifactAudit.setArtifactId("nbiservice-samplemodule");
		artifactAudit.setVersion("1.0.0");
		ArtifactsAudit newArtifactAudit = artifactAuditDAO.save(artifactAudit);*/
		
		
		
		BinaryAudit binaryAudit = new BinaryAudit();
		binaryAudit.setAction("A");
		binaryAudit.setBuildNumber(BigInteger.valueOf(1));
		binaryAudit.setMd5Value("xyz");
		binaryAudit.setSiloName(siloNameDAO.findById(BigInteger.valueOf(4)).get());
		binaryAudit.setArtifactsAudit(artifactAuditDAO.findById(BigInteger.valueOf(1)).get());
		binaryAudit.setModuleName(moduleNameDAO.findById(BigInteger.valueOf(1)).get());
		binaryAudit.setTaskId("DT-23423");
		
		binaryAuditDAO.save(binaryAudit);
		
	}
}
