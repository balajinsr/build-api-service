package com.broadcom.nbiapps.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

import com.broadcom.nbiapps.model.FileChanges;
import com.broadcom.nbiapps.model.ModuleData;
import com.broadcom.nbiapps.model.ResponseBuilder;

/**
 * This validator class used to pre validate the multilevel maven projects and sonar properties file validations.
 * @author Balaji N
 *
 */

public class ModulesValidator {
	private static final Logger logger = LoggerFactory.getLogger(ModulesValidator.class);
	
	public static final String ADDED = "A";
	public static final String MODIFIED = "M";
	public static final String DELETED="D";
	
	private String basePath;
	private Map<String, ModuleData> uniqueAddedOrModifiedModulesMap = new HashMap<>();
	private Set<ModuleData> uniqueDeletedModulesList = new HashSet<>();
	private Set<String> srcChangedModules = new HashSet<>();
	
	
	
	//validate: it may be removed in child pom or added in root pom but not used. we need to make sure clean in root pom while validating.
	private List<Dependency> unUsedDependenciesInRootPom = new ArrayList<>();
	
	private boolean isRootPomAdded;
	private boolean isRootPomModified;
	private List<Dependency> removedDependenciesFromRootPom = new ArrayList<>();
	private List<Dependency> addedDependenciesInRootPom = new ArrayList<>();
	
	private String siloName;
	private String taskId;

	/**
	 * This model is used filter module list for validate.
	 * 
	 * fileChangeList - Included all git added/modified/deleted files with operation, without ignored files. 
	 * 
	 * @param basePath
	 * @param operation
	 * @param fileChangeList
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ModulesValidator(String basePath, String taskId, List<FileChanges> fileChangeList) throws FileNotFoundException, IOException, XmlPullParserException {
		this.basePath = basePath;
		this.setTaskId(taskId);
		for(FileChanges fileChanges : fileChangeList) {
			processTheFiles(fileChanges.getChangeList(), fileChanges.getOperation());
		}
	}
	
	protected void processTheFiles(List<String> relativeFilePaths, String operation) throws FileNotFoundException, IOException, XmlPullParserException {
		this.siloName = basePath.substring(basePath.lastIndexOf("/") + 1);
		
		
		for (String relativeFilePath : relativeFilePaths) {
			int index = relativeFilePath.indexOf("/");
			
			if (index == -1) {
				/**
				 * Parent pom changed, Added/modified in <modules> list but forgot to commit the module changes into git. 
				 * Covering the validation 
				 * 
				 */
				List<String> changedModules = null;
				 if(relativeFilePath.equals("pom.xml")) {
					
					 logger.info("prepare module list for validation: " + relativeFilePath);
					 PomContainer modifiedPomContainer = new PomContainer(basePath+File.separator+"pom.xml"); 
					 if(MODIFIED.equals(operation)) {
						 PomContainer originalPomContainer = new PomContainer(getBaseBranchRootPomContent("pom.xml")); 
						 changedModules = originalPomContainer.getRemovedModules(modifiedPomContainer);
						 changedModules.addAll(originalPomContainer.getAddedModules(modifiedPomContainer));
						 
						 if(modifiedPomContainer.isParentPom().isResult()) {
							 List<Dependency> removedDependencies =  originalPomContainer.getRemovedDependencies(modifiedPomContainer);
								removedDependenciesFromRootPom.addAll(removedDependencies);
								logger.debug("[" + taskId + "] - removed dependecies in root pom is : "+removedDependencies.toString());
								List<Dependency> addedDependencies = originalPomContainer.getAddedDependencies(modifiedPomContainer);
								addedDependenciesInRootPom.addAll(addedDependencies);
								
								logger.debug("[" + taskId + "] - added dependecies in root pom is : "+addedDependencies.toString());
								// below method logging for more clarity between two pom's compare.
								modifiedPomContainer.printDependenciesChanges(removedDependencies, addedDependencies);
								populateUnUsedDependencyList(modifiedPomContainer, addedDependencies);
						 }
						 isRootPomModified = true;
					 } else if(ADDED.equals(operation)) {
						 isRootPomAdded = true;
						 changedModules = modifiedPomContainer.getChildModules();
						 List<Dependency> addedDependencies = modifiedPomContainer.getDependencies();
						 addedDependenciesInRootPom.addAll(addedDependencies);
						 populateUnUsedDependencyList(modifiedPomContainer, addedDependencies);
					 }
					 
					 for(String moduleDirName : changedModules) {
						 String moduleAction = getModuleAction(relativeFilePath, moduleDirName, operation);
						 populateModuleNames(moduleDirName, false, operation, moduleAction);
					 }
					 
				 } else {
					 logger.info("Ignore the file for module validate: " + relativeFilePath);
				 }
				continue;
			} 
			String moduleDir = relativeFilePath.substring(0, index);
			if(!"SILO".equals(moduleDir)) {
				boolean isSourceCodeChange = relativeFilePath.contains(moduleDir+"/src/main/java");
				String isPomFileAction = relativeFilePath.contains(moduleDir+"/pom.xml")?operation:null;
				String moduleAction = getModuleAction(relativeFilePath, moduleDir, operation);
				populateModuleNames(moduleDir, isSourceCodeChange, isPomFileAction, moduleAction);
			}
		}
	}
	
	public String getModuleAction(String relativeFilePath, String moduleDir, String operation) {
		// to set module action. it will be added/modified/deleted
		String moduleAction = "M";
		if("A".equals(operation)) {
			boolean isModulePomFilePresent = relativeFilePath.contains(moduleDir+File.separator+"pom.xml");
			moduleAction = isModulePomFilePresent ? "A":moduleAction;
		} 
		return moduleAction;
	}

	/**
	 * ModuleDir value be like "CommonComponent/subModule1" if it is two level
	 * maven project.
	 * 
	 * @param moduleDir
	 * @param relativeFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void populateModuleNames(String moduleDirName, boolean isSourceCodeChanged, String pomFileAction, String moduleAction) throws FileNotFoundException, IOException, XmlPullParserException {
		File file = new File(basePath + "/" + moduleDirName);
		ModuleData moduleData = null;
		if (file.exists() ) {			
			if(uniqueAddedOrModifiedModulesMap.containsKey(moduleDirName)) {
				if(isSourceCodeChanged) {
					uniqueAddedOrModifiedModulesMap.get(moduleDirName).setSourceChanged(isSourceCodeChanged);
				}
				
				if(pomFileAction != null) {
					uniqueAddedOrModifiedModulesMap.get(moduleDirName).setPomAction(pomFileAction);
				}
				
				if(moduleAction != null && ADDED.equals(moduleAction)) {
					uniqueAddedOrModifiedModulesMap.get(moduleDirName).setModuleAction(moduleAction);
				}
			} else {
				//to find module is two level or one level
				PomContainer childPomContainer = new PomContainer(basePath + File.separator + moduleDirName + "/pom.xml");
				if (childPomContainer.isTwoLevelModule()) {
					List<String> subChildModuleNames = childPomContainer.getChildModules();
					for (String subChildModuleName : subChildModuleNames) {
						String subChildPomPath = moduleDirName + File.separator + subChildModuleName;
						populateModuleNames(subChildPomPath, isSourceCodeChanged, pomFileAction, moduleAction);
					}
				} else {
					moduleData = new ModuleData();
					moduleData.setModuleName(moduleDirName);
					moduleData.setTwoLevelModule(childPomContainer.isTwoLevelModule());
					moduleData.setModuleAction(moduleAction);
					moduleData.setPomAction(pomFileAction);
					moduleData.setSourceChanged(isSourceCodeChanged);
					uniqueAddedOrModifiedModulesMap.put(moduleDirName, moduleData);
				}
			}
			
			if(isSourceCodeChanged) {
				srcChangedModules.add(moduleDirName);
			}
		} else {
			moduleData = new ModuleData();
			moduleData.setModuleName(moduleDirName);
			moduleData.setModuleAction("D");
			moduleData.setTwoLevelModule(moduleDirName.contains("/"));
			uniqueDeletedModulesList.add(moduleData);
		}

	}
	
	/**
	 * @param addedDependencies - 
	 * @return
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException  
	 */
	private void populateUnUsedDependencyList(PomContainer modifiedPomContainer, List<Dependency> addedDependencies) throws FileNotFoundException, IOException, XmlPullParserException {
		if(addedDependencies != null && !addedDependencies.isEmpty()) {
			List<String> childModuleList = modifiedPomContainer.getChildModules();
			
			for(String moduleName : childModuleList) {
				 PomContainer childPomContainer = new PomContainer(getBasePath()+File.separator+moduleName+File.separator+"pom.xml");
				 List<Dependency> dependencyList  = childPomContainer.getDependencies();
				 for(Dependency dep : dependencyList) {
					 // removing from addedDependencies if exist in through the child modules. 
					 addedDependencies.removeIf(s -> s.getArtifactId().equals(dep.getArtifactId()) && s.getGroupId().equals(dep.getArtifactId()));
				 }
			}
			
			// means - unused dependencies there in root pom.
			if(!addedDependencies.isEmpty()) {
				unUsedDependenciesInRootPom.addAll(addedDependencies);
			}
		} else {
			logger.info("No removed or upgrading dependencies in root pom.xml");
		}
	
	}

	public ResponseBuilder sonarPropertyFilePresent() {
		File file = new File(basePath + File.separator + "/sonar-project.properties");
		if (!file.exists()) {
			return sendResult(false, "sonar-project.properties file should be required under silo base directory - [/" + siloName + "]");
		}
		return sendResult(true);
	}

	public ResponseBuilder sonarPropertyCorrect() throws FileNotFoundException, IOException, XmlPullParserException {
		if (!uniqueAddedOrModifiedModulesMap.isEmpty()) {
			Properties sonarProp = getSonarProperties();
			String sonarModulesStr = (String) sonarProp.get("sonar.modules");
			List<String> sonarModules = Arrays.asList(Arrays.stream(sonarModulesStr.split(",")).map(String::trim).toArray(String[]::new));
			if (!sonarModules.contains("SILO")) {
				return sendResult(false, "ModuleName - [SILO] should be added in sonar.modules list with comma seperated under " + siloName + "/sonar-project.properties file.");
			}

			PomContainer pomContainer;
			Iterator<String> it = uniqueAddedOrModifiedModulesMap.keySet().iterator();
			
			while(it.hasNext()) {
				String key = it.next();
				ModuleData moduleData = uniqueAddedOrModifiedModulesMap.get(key);
				String moduleName = moduleData.getModuleName();
				pomContainer = new PomContainer(basePath + "/" + moduleName + "/pom.xml");

				if (pomContainer.isTwoLevelModule()) {
					if (!sonarModules.contains(moduleName)) {
						return sendResult(false,
								"ModuleName [" + moduleName + "] should be added in sonar.modules list with comma seperated under " + siloName + "/sonar-project.properties file.");
					} else {
						String sonarChildModules = (String) sonarProp.get(moduleName + ".sonar.modules");
						List<String> childModuleNames = pomContainer.getChildModules();
						for (String childModuleName : childModuleNames) {
							if (sonarChildModules == null) {
								return sendResult(false, "Add properties -  [" + moduleName + ".sonar.modules=" + childModuleName + "] and [" + moduleName
										+ ".sonar.sources=src]  under " + siloName + "/sonar-project.properties file.");
							}

							if (!sonarChildModules.contains(childModuleName)) {
								return sendResult(false, "ChildModuleName [" + childModuleName + "] should be added in " + moduleName
										+ ".sonar.modules list with comma seperated under " + siloName + "/sonar-project.properties file.");
							}
						}
					}
				} else {
					if (!sonarModules.contains(moduleName)) {
						return sendResult(false,
								"ModuleName [" + moduleName + "] should be added in sonar.modules list with comma seperated under " + siloName + "/sonar-project.properties file.");
					}
				}
			}
		}
		return sendResult(true);
	}

	public ResponseBuilder validateDeleteModuleCheck() throws FileNotFoundException, IOException, XmlPullParserException {
		if (!uniqueDeletedModulesList.isEmpty()) {
			for (ModuleData moduleData : uniqueDeletedModulesList) {
				String deletedModuleName = moduleData.getModuleName();
				if (!moduleData.isTwoLevelModule()) {
					PomContainer pomContainer = new PomContainer(basePath + "/pom.xml");
					return deletedModulePresentInPom(moduleData.getModuleName(), pomContainer);
				} else {
					return deletedModulePresentInSonarPropertyFile(moduleData.isTwoLevelModule(), deletedModuleName);
				}
			}
		}
		return sendResult(true);
	}
	
	public ResponseBuilder validateUnUsedDependenciesCheck() throws FileNotFoundException, IOException, XmlPullParserException {
		if (!unUsedDependenciesInRootPom.isEmpty()) {
			return sendResult(false, "The dependencies should be ["+unUsedDependenciesInRootPom.toString()+"] removed from [" + basePath + "/pom.xml] under <dependencyManagment> section.");
		}
		return sendResult(true);
	}

	private ResponseBuilder deletedModulePresentInPom(String deletedModuleName, PomContainer pomContainer) {
		List<String> childModules = pomContainer.getChildModules();
		if (childModules != null && !childModules.isEmpty() && childModules.contains(deletedModuleName)) {
			return sendResult(false, "Module [" + deletedModuleName + "] should be removed from [" + basePath + "/pom.xml] under <modules> section.");
		}
		return sendResult(true);
	}

	private ResponseBuilder deletedModulePresentInSonarPropertyFile(boolean isTwoLevelModule, String deletedModuleName) throws IOException {
		Properties sonarProp = getSonarProperties();
		String sonarModulesStr = (String) sonarProp.get("sonar.modules");
		List<String> sonarModules = Arrays.asList(Arrays.stream(sonarModulesStr.split(",")).map(String::trim).toArray(String[]::new));

		if (isTwoLevelModule && deletedModuleName.contains("/")) {
			String parentModuleName = deletedModuleName.substring(0, deletedModuleName.indexOf("/"));
			String childModuleName = deletedModuleName.substring(deletedModuleName.indexOf("/") + 1);
			String sonarChildModules = (String) sonarProp.get(parentModuleName + ".sonar.modules");
			if (sonarChildModules.contains(childModuleName)) {
				return sendResult(false, "ChildModuleName [" + childModuleName + "] should not contain in " + parentModuleName + ".sonar.modules list with comma seperated under "
						+ siloName + "/sonar-project.properties file.");
			}
		} else {
			if (sonarModules.contains(deletedModuleName)) {
				return sendResult(false, "ModuleName [" + deletedModuleName + "] should be removed [" + deletedModuleName + ".sonar.modules , " + deletedModuleName
						+ ".sonar.sources] properties from " + siloName + "/sonar-project.properties file.");
			}

			if (sonarModules.contains(deletedModuleName)) {
				return sendResult(false,
						"ModuleName [" + deletedModuleName + "] is not available. it should be removed from [sonar.modules] list under " + siloName + "/sonar-project.properties file. OR  Module ["+deletedModuleName+"] should be pushed to git repo");
			}
		}
		return sendResult(true);
	}

	private Properties getSonarProperties() throws IOException {
		File sonarPropFile = new File(basePath + File.separator + "sonar-project.properties");
		Properties sonarProp = new Properties();
		try (InputStream inputStream = new FileInputStream(sonarPropFile)) {
			sonarProp.load(inputStream);
		}
		return sonarProp;
	}
	
	
	public byte [] getBaseBranchRootPomContent(String pomFileRelativePath) throws IOException  {
		if(isValidLocalRepository()) {
			try (Git git = Git.open(new File(getBasePath()))) {
				 ObjectId masterTreeId = git.getRepository().resolve("refs/heads/master^{tree}" );
				 try (TreeWalk treeWalk = TreeWalk.forPath( git.getRepository(), pomFileRelativePath, masterTreeId)) {
					 	ObjectId blobId = treeWalk.getObjectId(0);
					    ObjectLoader objectLoader = loadObject(git, blobId );
					    return objectLoader.getBytes(); 
				 }
			 }
		} else {
			throw new IOException("Not a valid git local repository - ["+getBasePath()+"]");
		}
	}
	
	private ObjectLoader loadObject(Git git, ObjectId objectId) throws IOException {
	    try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
	    	return objectReader.open( objectId );
	    }
	  }
	
	private boolean isValidLocalRepository() {
		boolean result;
		try (FileRepository objDir = new FileRepository(new File(getBasePath()))) {
			result = objDir.getObjectDatabase().exists();
		} catch (IOException e) {
			result = false;
		}
		return result;
	}
	
	

	private static ResponseBuilder sendResult(boolean result) {
		return sendResult(result, "");
	}

	private static ResponseBuilder sendResult(boolean result, String resultDesc) {
		return new ResponseBuilder(result, resultDesc);
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	
	public Map<String,ModuleData> getUniqueAddedOrModifiedModulesMap() {
		return uniqueAddedOrModifiedModulesMap;
	}

	public Set<ModuleData> getUniqueDeletedModulesList() {
		return uniqueDeletedModulesList;
	}

	public void setUniqueDeletedModulesList(Set<ModuleData> uniqueDeletedModulesList) {
		this.uniqueDeletedModulesList = uniqueDeletedModulesList;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getSiloName() {
		return siloName;
	}

	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}

	public List<Dependency> getAddedDependenciesInRootPom() {
		return addedDependenciesInRootPom;
	}

	public void setAddedDependenciesInRootPom(List<Dependency> addedDependenciesInRootPom) {
		this.addedDependenciesInRootPom = addedDependenciesInRootPom;
	}

	public List<Dependency> getRemovedDependenciesFromRootPom() {
		return removedDependenciesFromRootPom;
	}

	public void setRemovedDependenciesFromRootPom(List<Dependency> removedDependenciesFromRootPom) {
		this.removedDependenciesFromRootPom = removedDependenciesFromRootPom;
	}

	public boolean isRootPomAdded() {
		return isRootPomAdded;
	}

	public boolean isRootPomModified() {
		return isRootPomModified;
	}
}
