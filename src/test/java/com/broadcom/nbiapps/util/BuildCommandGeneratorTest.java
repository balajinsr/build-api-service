/**
 * 
 */
package com.broadcom.nbiapps.util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Balaji N
 *
 */
@SpringBootTest
public class BuildCommandGeneratorTest {

	private static final Logger logger = LoggerFactory.getLogger(BuildCommandGeneratorTest.class);
	PomContainer srcPomContainer = null;
	PomContainer destPomContainer = null;
	
	PomContainer srcInheritPomContainer = null;
	PomContainer destInheritPomContainer = null;
	
	{
		try {
			// added or removed files from parent pom 
			srcPomContainer = new PomContainer("src-pom.xml");
			destPomContainer = new PomContainer("dest-pom.xml");
			
			// added or removed files from inherited pom 
			srcInheritPomContainer = new PomContainer("src-inherit-pom.xml");
			destInheritPomContainer = new PomContainer("dest-inherit-pom.xml");
			
		} catch (IOException | XmlPullParserException e) {
			logger.error("maven pom file parse failed.");
		}

	}

	@Test
	public void addDependenciesTest() {
		List<Dependency> addedDependencies = destPomContainer.getDependencies().stream().filter(o1 -> srcPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& o2.getVersion().equals(o1.getVersion())
				&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
		logger.info("Added dependencies to dest-pom.xml - "+addedDependencies.toString());
		Assertions.assertTrue(!addedDependencies.isEmpty());
	}

	@Test
	public void removedDependenciesTest() {
		List<Dependency> removedDependencies = srcPomContainer.getDependencies().stream().filter(o1 -> destPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& o2.getVersion().equals(o1.getVersion())
				&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
		logger.info("Removed dependencies from dest-pom.xml - "+removedDependencies.toString());
		Assertions.assertTrue(!removedDependencies.isEmpty());
		
	}
	
	
	@Test
	public void addInheritDependenciesTest() {
		List<Dependency> addedInheritDependencies = destInheritPomContainer.getDependencies().stream().filter(o1 -> srcInheritPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
		logger.info("Inherit pom - Added dependencies to dest-pom.xml - "+addedInheritDependencies.toString());
		Assertions.assertTrue(!addedInheritDependencies.isEmpty());
	}

	@Test
	public void removedInheritDependenciesTest() {
		List<Dependency> removedInheritDependencies = srcInheritPomContainer.getDependencies().stream().filter(o1 -> destInheritPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
		logger.info("Inherit pom - Removed dependencies from dest-pom.xml - "+removedInheritDependencies.toString());
		Assertions.assertTrue(!removedInheritDependencies.isEmpty());
		
	}
}
