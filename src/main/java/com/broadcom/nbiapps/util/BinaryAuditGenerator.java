/**
 * 
 */
package com.broadcom.nbiapps.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.broadcom.nbiapps.entities.DataAudit;
import com.broadcom.nbiapps.model.FileChanges;
import com.broadcom.nbiapps.model.ModuleData;


/**
 * @author Balaji N
 *
 */
public class BinaryAuditGenerator extends ModulesValidator {
	private static final Logger logger = LoggerFactory.getLogger(ModulesValidator.class);
	private String basePath;
	//private Map<ModuleData, List<Dependency>> impactedModules = new HashMap<>();
	

	/**
	 * This model is used filter module list for build command.
	 * @param basePath
	 * @param operation
	 * @param fileChangeList
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	BinaryAuditGenerator(String basePath, String taskId, List<FileChanges> fileChanges) throws FileNotFoundException, IOException, XmlPullParserException {
		super(basePath,taskId, fileChanges);
		
		List<String> fileChangeList = new ArrayList<String>();
		for(FileChanges fileChange : fileChanges) {
			fileChangeList.addAll(fileChange.getChangeList());
		}
		
		List<String> listOfMavenFiles = fileChangeList
				  .stream()
				  .filter(s -> s.contains("pom.xml"))
				  .collect(Collectors.toList());
		
		if(!listOfMavenFiles.isEmpty()) {
			String rootPomFile = "pom.xml";
			PomContainer featureBranchPomContainer = new PomContainer(basePath + File.separator + rootPomFile);
			PomContainer masterBranchPomContainer = new PomContainer(getBaseBranchRootPomContent(rootPomFile));
			
			//It means root pom.
			if(featureBranchPomContainer.isParentPom().isResult()) {
				addImpactedModulesForCompile(featureBranchPomContainer, masterBranchPomContainer.getRemovedDependencies(featureBranchPomContainer));
			} 
		}
		
		//TODO:: for commandTOCompile its required only below code. **** below code is required for binary files to audit.. keep this for time.
		
		// better to do complete loop through for parent pom if present
		/*String rootPomFile = "pom.xml";
		if(listOfMavenFiles.contains(rootPomFile)) {
			PomContainer featureBranchPomContainer = new PomContainer(basePath + File.separator + rootPomFile);
			PomContainer masterBranchPomContainer = new PomContainer(getBaseBranchRootPomContent(rootPomFile));
			
			//It means root pom.
			if(featureBranchPomContainer.isParentPom().isResult()) {
				addImpactedModulesForCompile(featureBranchPomContainer, masterBranchPomContainer.removedDependencies(featureBranchPomContainer));
			} 
			listOfMavenFiles.remove(rootPomFile);
		}*/
		
		
		//TODO: below loop not required. we will see.
		/*for(String relativeFilePath : listOfMavenFiles) {
			PomContainer featureBranchPomContainer = new PomContainer(basePath + File.separator + relativeFilePath);
			PomContainer masterBranchPomContainer = new PomContainer(getBaseBranchRootPomContent(relativeFilePath));
			
			// it means commoncomponent pom
			addImpactedModulesForCompile(featureBranchPomContainer, masterBranchPomContainer.removedDependencies(featureBranchPomContainer));
				
			
		} */
		
	}
	
	/**
	 * @param removedDependencies - 
	 * @return
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void addImpactedModulesForCompile(PomContainer featureBranchPomContainer, List<Dependency> removedDependencies) throws FileNotFoundException, IOException, XmlPullParserException {
		if(removedDependencies.isEmpty()) {
			return;
		}
		
		List<String> childModuleList = featureBranchPomContainer.getChildModules();
		for(String moduleName : childModuleList) {
			 PomContainer childPomContainer = new PomContainer(basePath+File.separator+moduleName+File.separator+"pom.xml");
			 boolean dependencyMatched = removedDependencies.stream().anyMatch(l1 -> childPomContainer.getDependencies().stream().anyMatch(l2 -> l2.getGroupId().equals(l1.getGroupId())
						&& l1.getArtifactId().equals(l2.getArtifactId()) && l1.getVersion() == l2.getVersion()));
				 
				 ModuleData moduleData = null;
				 if(dependencyMatched) {
					 moduleData = new ModuleData();
					 moduleData.setModuleName(moduleName);
					 moduleData.setTwoLevelModule(childPomContainer.isTwoLevelModule());
					 getUniqueAddedOrModifiedModules().add(moduleData);
					 
					 if(childPomContainer.isTwoLevelModule()) {
						 PomContainer subMultiParentPomContainer = new PomContainer(basePath + File.separator + moduleName+File.separator+"pom.xml"); 
						 addImpactedModulesForCompile(subMultiParentPomContainer, removedDependencies);
					 }
				 }
		}
	
	}


	public byte [] getBaseBranchRootPomContent(String pomFileRelativePath) throws IOException  {
		if(isValidLocalRepository()) {
			try (Git git = Git.open(new File(basePath))) {
				 ObjectId masterTreeId = git.getRepository().resolve("refs/heads/master^{tree}" );
				 try (TreeWalk treeWalk = TreeWalk.forPath( git.getRepository(), pomFileRelativePath, masterTreeId )) {
					 	ObjectId blobId = treeWalk.getObjectId( 0 );
					    ObjectLoader objectLoader = loadObject(git, blobId );
					    return objectLoader.getBytes(); 
				 }
			 }
		} else {
			throw new IOException("Not a valid git local repository - ["+basePath+"]");
		}
	}
	
	private ObjectLoader loadObject(Git git, ObjectId objectId) throws IOException {
	    try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
	    	return objectReader.open( objectId );
	    }
	  }
	
	private boolean isValidLocalRepository() {
		boolean result;
		try (FileRepository objDir = new FileRepository(new File(basePath))) {
			result = objDir.getObjectDatabase().exists();
		} catch (IOException e) {
			result = false;
		}
		return result;
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
	

	public List<DataAudit> getAllFileChangeList(String operation, String fileChangeList) {
		List<DataAudit> dataAuditList = new ArrayList<>();
		if (fileChangeList != null && !"".equals(fileChangeList)) {
			List<String> changeList = Arrays.asList(fileChangeList.split("\\|"));
			DataAudit dataAudit;
			for (String relativeFilePath : changeList) {
				if (relativeFilePath.toLowerCase().contains("readme.md")) {
					logger.debug("Ignoring read me file.");
					continue;
				}

				dataAudit = new DataAudit();
				dataAudit.setAction(operation);
				dataAudit.setRelativeFilePath(relativeFilePath);
				dataAuditList.add(dataAudit);
			}
		}
		return dataAuditList;
	}
}
