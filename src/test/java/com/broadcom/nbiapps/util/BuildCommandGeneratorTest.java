/**
 * 
 */
package com.broadcom.nbiapps.util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.BeforeTransaction;

/**
 * @author Balaji N
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BuildCommandGeneratorTest {

	private static final Logger logger = LoggerFactory.getLogger(BuildCommandGeneratorTest.class);
	PomContainer srcPomContainer = null;
	PomContainer destPomContainer = null;
	
	PomContainer srcInheritPomContainer = null;
	PomContainer destInheritPomContainer = null;
	
	
	public void beforeTest() {
		try {
			// added or removed files from parent pom 
			srcPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/build-api-service/src/test/java/com/broadcom/nbiapps/util/src-pom.xml");
			destPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/build-api-service/src/test/java/com/broadcom/nbiapps/util/dest-pom.xml");
			
			// added or removed files from inherited pom 
			srcInheritPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/build-api-service/src/test/java/com/broadcom/nbiapps/util/src-pom.xmlsrc-inherit-pom.xml");
			destInheritPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/build-api-service/src/test/java/com/broadcom/nbiapps/util/src-pom.xmldest-inherit-pom.xml");
			
		} catch (IOException | XmlPullParserException e) {
		
			logger.error("maven pom file parse failed.");
		}

	}
	
	
	@Test
	public void addedDependenciesTest() {
		beforeTest();
		if(destPomContainer.getDependencies() != null && srcPomContainer.getDependencies() != null) {
			List<Dependency> addedDependencies = destPomContainer.getDependencies().stream().filter(o1 -> srcPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& o2.getVersion().equals(o1.getVersion())
					&& (o2.getScope() == o1.getScope() && o2.getScope()==null || o1.getScope()== null || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
			logger.info("Added dependencies to dest-pom.xml - "+addedDependencies.toString());
			Assertions.assertTrue(!addedDependencies.isEmpty());
		} else {
			
		}
	}

	@Test
	public void removedDependenciesTest() {
		beforeTest();
		if (destPomContainer.getDependencies() != null && srcPomContainer.getDependencies() != null) {
			List<Dependency> removedDependencies = srcPomContainer.getDependencies().stream()
					.filter(o1 -> destPomContainer.getDependencies().stream()
							.noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
									&& o2.getArtifactId().equals(o1.getArtifactId())
									&& o2.getVersion().equals(o1.getVersion())
									&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope()))))
					.collect(Collectors.toList());
			logger.info("Removed dependencies from dest-pom.xml - " + removedDependencies.toString());
			Assertions.assertTrue(!removedDependencies.isEmpty());
		} else {

		}
		
	}
	
	
	@Test
	public void addedInheritDependenciesTest() {
		beforeTest();
		if (destInheritPomContainer.getDependencies() != null && srcInheritPomContainer.getDependencies() != null) {
			List<Dependency> addedInheritDependencies = destInheritPomContainer.getDependencies().stream().filter(o1 -> srcInheritPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
			logger.info("Inherit pom - Added dependencies to dest-pom.xml - "+addedInheritDependencies.toString());
			Assertions.assertTrue(!addedInheritDependencies.isEmpty());
		}
	}

	@Test
	public void removedInheritDependenciesTest() {
		if (destInheritPomContainer.getDependencies() != null && srcInheritPomContainer.getDependencies() != null) {
			List<Dependency> removedInheritDependencies = srcInheritPomContainer.getDependencies().stream().filter(o1 -> destInheritPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
			logger.info("Inherit pom - Removed dependencies from dest-pom.xml - "+removedInheritDependencies.toString());
			Assertions.assertTrue(!removedInheritDependencies.isEmpty());
		}
	}
	
	@Test
	public void removedModulesTest() {
		beforeTest();
		List<String> removedModule = srcPomContainer.getChildModules().stream().filter(o1 -> destPomContainer.getChildModules().stream().noneMatch(o2 -> o2.equals(o1))
				).collect(Collectors.toList());
		logger.info("Root pom - removed modules in dest-pom.xml - "+removedModule.toString());
		Assertions.assertTrue(!removedModule.isEmpty());
		//TODO: proper assert
	}
	
	@Test
	public void addedModulesTest() {
		
		
		List<String> addedModule = destPomContainer.getChildModules().stream().filter(o1 -> srcPomContainer.getChildModules().stream().noneMatch(o2 -> o2.equals(o1))
				).collect(Collectors.toList());
		logger.info("added modules in dest-pom.xml - "+addedModule.toString());
		Assertions.assertTrue(!addedModule.isEmpty());
		//TODO: proper assert
	}
	
	
}
