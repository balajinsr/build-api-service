/**
 * 
 */
package com.broadcom.nbiapps.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.broadcom.nbiapps.model.FileChanges;
import com.broadcom.nbiapps.model.ModuleData;


/**
 * @author Balaji N
 *
 */
public class BuildCommandGenerator extends ModulesValidator {
	private static final Logger logger = LoggerFactory.getLogger(BuildCommandGenerator.class);
	private Set<ModuleData> impactedModuleForCompile = new HashSet<>();
	
	
	/**
	 * This model is used filter module list for build command.
	 * @param basePath
	 * @param operation
	 * @param fileChangeList
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	BuildCommandGenerator(String basePath, String taskId, List<FileChanges> fileChanges) throws FileNotFoundException, IOException, XmlPullParserException {
		super(basePath, taskId, fileChanges);
		
		List<String> fileChangeList = new ArrayList<String>();
		for(FileChanges fileChange : fileChanges) {
			fileChangeList.addAll(fileChange.getChangeList());
		}
		List<String> listOfMavenFiles = fileChangeList.stream().filter(s -> s.contains("pom.xml")).collect(Collectors.toList());
		
		/**
		 * We are only preparing root pom impacted module list for buildcommand. Its basically loop through complete multlevel project.
		 * 
		 * Ignoring path/pom.xml etc because we already gone through the list in base class.
		 * 
		 * case 1: assume if there was a change in CommonComponent/pom.xml or issue level module pom. 
		 * we already added those module for validation and compile [uniqueAddedOrModifiedModules list]
		 */
		
		if(!listOfMavenFiles.isEmpty()) {
			String rootPomFile = "pom.xml";
			PomContainer modifiedPomContainer = new PomContainer(basePath + File.separator + rootPomFile);
			PomContainer originalPomContainer = new PomContainer(getBaseBranchRootPomContent(rootPomFile));
			
			//It means root pom.
			if(modifiedPomContainer.isParentPom().isResult()) {
				List<Dependency> removedDependencies =  originalPomContainer.getRemovedDependencies(modifiedPomContainer);
				logger.debug("[" + taskId + "] - removed dependecies in root pom is : "+removedDependencies.toString());
				List<Dependency> addedDependencies = originalPomContainer.getAddedDependencies(modifiedPomContainer);
				logger.debug("[" + taskId + "] - added dependecies in root pom is : "+addedDependencies.toString());
				// below method logging for more clarity between two pom's compare.
				modifiedPomContainer.printDependenciesChanges(removedDependencies, addedDependencies);
				
				addImpactedModulesForCompile(modifiedPomContainer, removedDependencies);
				logger.debug("[" + taskId + "] - following dependencies removed or upgraded - [+removedDependencies+], so impacted modules to recompile : ["+impactedModuleForCompile.toString()+"]");
				getUniqueAddedOrModifiedModules().addAll(impactedModuleForCompile);
			} 
		} else {
			logger.info("[" + taskId + "] No pom.xml changes");
		}
	}
	
	/**
	 * @param removedDependencies - 
	 * @return
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException  
	 */
	private void addImpactedModulesForCompile(PomContainer modifiedPomContainer, List<Dependency> removedDependencies) throws FileNotFoundException, IOException, XmlPullParserException {
		if(!removedDependencies.isEmpty()) {
			List<String> childModuleList = modifiedPomContainer.getChildModules();
			for(String moduleName : childModuleList) {
				 PomContainer childPomContainer = new PomContainer(getBasePath()+File.separator+moduleName+File.separator+"pom.xml");
				 boolean dependencyMatched = removedDependencies.stream().anyMatch(l1 -> childPomContainer.getDependencies().stream().anyMatch(l2 -> l2.getGroupId().equals(l1.getGroupId())
							&& l1.getArtifactId().equals(l2.getArtifactId()) && l1.getVersion() == l2.getVersion()));
					 logger.debug("[" + getTaskId() + "] - Changed dependency is matched in ["+moduleName+"] module ");
					 ModuleData moduleData = null;
					 if(dependencyMatched) {
						 moduleData = new ModuleData();
						 moduleData.setModuleName(moduleName);
						 moduleData.setTwoLevelModule(childPomContainer.isTwoLevelModule());
						 impactedModuleForCompile.add(moduleData);
						 if(childPomContainer.isTwoLevelModule()) {
							 PomContainer subMultiParentPomContainer = new PomContainer(getBasePath()+ File.separator + moduleName+File.separator+"pom.xml"); 
							 addImpactedModulesForCompile(subMultiParentPomContainer, removedDependencies);
						 }
					 }
			}
		} else {
			logger.info("No removed or upgrading dependencies in root pom.xml");
		}
	
	}

	private class ModuleNamesSort implements Comparator<ModuleData> {
		@Override
		public int compare(ModuleData o1, ModuleData o2) {
			if (o2.getModuleName().equalsIgnoreCase("CommonComponents")) {
				return 1;
			}
			return -1;
		}
	}
	
	public String getBuildCommandValue() {
		StringBuilder buildCommand = new StringBuilder();
		String command = "mvn clean install -DskipTests=true";
		List<ModuleData> moduleList = new ArrayList<>(getUniqueAddedOrModifiedModules());

		Collections.sort(moduleList, new ModuleNamesSort());
		for (ModuleData moduleData : moduleList) {
			if (buildCommand.length() == 0) {
				buildCommand.append("(");
			} else {
				buildCommand.append(" && (");
			}
			buildCommand.append("cd ").append(moduleData.getModuleName()).append(" && ").append(command);
			buildCommand.append(")");
		}

		if (buildCommand.length() == 0) {
			buildCommand.append("");
		}
		logger.info("Command to build: " + buildCommand.toString());
		return buildCommand.toString();
	}
}
