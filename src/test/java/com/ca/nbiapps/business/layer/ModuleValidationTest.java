package com.ca.nbiapps.business.layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.ca.nbiapps.model.ModuleData;

/**
 * 
 * @author Balaji
 *
 */

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ModuleValidationTest {

	private static final Logger logger = LoggerFactory.getLogger(ModuleValidationTest.class);

	private List<DiffEntry> getAddFiles() {
		List<DiffEntry> diffEntries = new ArrayList<>();

		DiffEntry diffEntry = DiffEntry;
		List<String> addedFiles = new ArrayList<>();
		addedFiles.add("CAPONE/pom.xml");
		addedFiles.add("CAPONE/src/main/java/com/ca/nbiapps/MainClass.java");
		addedFiles.add("pom.xml");

		FileChanges fileChanges = new FileChangesS();
		fileChanges.setOperation("A");
		fileChanges.setChangeList(addedFiles);

		fileChangeList.add(fileChanges);
		return fileChangeList;
	}

	private List<FileChangesS> getModifiedFiles() {
		List<FileChangesS> fileChangeList = new ArrayList<>();

		List<String> files = new ArrayList<>();
		files.add("CAPONE/pom.xml");
		files.add("CAPONE/src/main/java/com/ca/nbiapps/MainClass.java");

		FileChangesS fileChanges = new FileChanges();
		fileChanges.setOperation("M");
		fileChanges.setChangeList(files);

		fileChangeList.add(fileChanges);
		return fileChangeList;
	}

	private List<FileChangesS> getAddedRoomPomFile() {
		List<FileChangesS> fileChangeList = new ArrayList<>();

		List<String> addedFiles = new ArrayList<>();

		addedFiles.add("pom.xml");

		FileChangesS fileChanges = new FileChangesS();
		fileChanges.setOperation("A");
		fileChanges.setChangeList(addedFiles);

		fileChangeList.add(fileChanges);
		return fileChangeList;
	}

	private List<FileChanges> getAddedModuleFile() {
		List<FileChangesS> fileChangeList = new ArrayList<>();

		List<String> addedFiles = new ArrayList<>();
		addedFiles.add("CAPONE/pom.xml");
		addedFiles.add("CAPONE/src/main/java/com/ca/nbiapps/MainClass.java");

		FileChanges fileChanges = new FileChangesS();
		fileChanges.setOperation("A");
		fileChanges.setChangeList(addedFiles);

		return fileChangeList;
	}

	@Test
	public void fileAddedTest() throws GitAPIException {
		String basePath = System.getenv("GIT_REPO_LOCATION");
		logger.info("BasePath: " + basePath);

		try {
			ModulesValidation modulesValidation = new ModulesValidation(basePath, "DT-34593483", getAddFiles());
			boolean pomAdded = modulesValidation.isRootPomAdded();
			Set<String> addedDep = modulesValidation.getSrcChangedModules();
			ModuleData moduleData = modulesValidation.getUniqueAddedOrModifiedModulesMap().get("CAPONE");
			boolean filesAssertValue = moduleData.isSourceChanged() && !moduleData.isTwoLevelModule();
			Assertions.assertTrue(pomAdded && !addedDep.isEmpty() && filesAssertValue);
		} catch (IOException | XmlPullParserException e) {
			logger.error("Error: " + e, e);
		}
	}

	@Test
	public void fileModifiedTest() throws GitAPIException {
		String basePath = System.getenv("GIT_REPO_LOCATION");
		logger.info("BasePath: " + basePath);

		try {
			ModulesValidation modulesValidation = new ModulesValidation(basePath, "DT-34593483", getModifiedFiles());
			Set<String> changedModule = modulesValidation.getSrcChangedModules();
			ModuleData moduleData = modulesValidation.getUniqueAddedOrModifiedModulesMap().get("CAPONE");
			boolean filesAssertValue = moduleData.isSourceChanged() && !moduleData.isTwoLevelModule()
					&& "M".equals(moduleData.getModuleAction());
			Assertions.assertTrue(!changedModule.isEmpty() && filesAssertValue);
		} catch (IOException | XmlPullParserException e) {
			logger.error("Error: " + e, e);
		}
	}

	@Test
	public void fileAddedRootPomTest()  throws GitAPIException {
		String basePath = System.getenv("GIT_REPO_LOCATION");
		logger.info("BasePath: " + basePath);

		try {
			ModulesValidation modulesValidation = new ModulesValidation(basePath, "DT-34593483", getAddedRoomPomFile());
			boolean pomAdded = modulesValidation.isRootPomAdded();
			List<Dependency> depnDependencies = modulesValidation.getAddedDependenciesInRootPom();
			logger.debug("depnDependencies::" + depnDependencies.toString());
			Assertions.assertTrue(pomAdded && !depnDependencies.isEmpty());
		} catch (IOException | XmlPullParserException e) {
			logger.error("Error: " + e, e);
		}
	}

	@Test
	public void fileAddedModuleTest() throws GitAPIException {
		String basePath = System.getenv("GIT_REPO_LOCATION");
		logger.info("BasePath: " + basePath);

		try {
			ModulesValidation modulesValidation = new ModulesValidation(basePath, "DT-34593483", getAddedModuleFile());
			Set<String> addedDep = modulesValidation.getSrcChangedModules();
			ModuleData moduleData = modulesValidation.getUniqueAddedOrModifiedModulesMap().get("CAPONE");
			boolean filesAssertValue = moduleData.isSourceChanged() && !moduleData.isTwoLevelModule();
			Assertions.assertTrue(!addedDep.isEmpty() && filesAssertValue);
		} catch (IOException | XmlPullParserException e) {
			logger.error("Error: " + e, e);
		}
	}
}
