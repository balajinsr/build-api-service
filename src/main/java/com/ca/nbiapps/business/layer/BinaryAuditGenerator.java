/**
 * 
 */
package com.ca.nbiapps.business.layer;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.nbiapps.entities.BinaryAudit;
import com.ca.nbiapps.entities.ModuleName;
import com.ca.nbiapps.entities.SiloName;
import com.ca.nbiapps.entities.SiloNameReq;
import com.ca.nbiapps.model.ModuleData;
import com.ca.nbiapps.model.ModuleDependency;


/**
 * @author Balaji N
 *
 */
public class BinaryAuditGenerator extends ModulesValidation {
	private static final Logger logger = LoggerFactory.getLogger(BinaryAuditGenerator.class);
	private static final String baseCommonJarPath = "common/v1.0";
	private Set<ModuleDependency> commonJars = new LinkedHashSet<>();
	private List<BinaryAudit> binaryAudits = new ArrayList<>();
	private BigInteger buildNumber; 
	private SiloName siloName = new SiloName();
	private GitOpertions gitOpertions;
	/**
	 * This model is used filter files for audit.
	 * @param basePath
	 * @param operation
	 * @param fileChangeList
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws GitAPIException 
	 */
	BinaryAuditGenerator(String basePath, BigInteger buildNumber, String taskId, List<DiffEntry> diffEntries) throws FileNotFoundException, IOException, XmlPullParserException, GitAPIException {		
		super(basePath,taskId, diffEntries);
		this.setBuildNumber(buildNumber);
		createSiloName();
		gitOpertions = new GitOpertions(getBasePath());
		populateBinaryAuditInfo();
	}
	
	private void populateBinaryAuditInfo() throws FileNotFoundException, IOException, XmlPullParserException {
		//TODO: populate buildArtifactos for audit.
		List<Dependency> addedDependencies = getAddedDependenciesInRootPom();
		List<Dependency> removedDependencies = getRemovedDependenciesFromRootPom();
		if(removedDependencies != null && !removedDependencies.isEmpty()) {
			ModuleDependency moduleDependency = null;
			for(Dependency dependency : removedDependencies) {
				if("provided".equals(dependency.getScope())) {
					logger.info("Ignoring to audit the dependency - "+ dependency.getGroupId()+":"+dependency.getArtifactId()+":"+dependency.getVersion()+": "+dependency.getScope());
					continue;
				}
				
				moduleDependency = new ModuleDependency();
				moduleDependency.setFilePath(baseCommonJarPath+File.separator+dependency.getArtifactId()+"-"+dependency.getVersion()+".jar");
				moduleDependency.setAction("D");
				commonJars.add(moduleDependency);
			}
		}
		
		Iterator<String> moduleNameKeys = getUniqueAddedOrModifiedModulesMap().keySet().iterator();
		// changes on module level
		BinaryAudit binaryAudit = null;
		ModuleName moduleName = null;
		PomContainer modulePomContainer = null;
		List<Dependency> filterModuleDepList = null;
		ModuleDependency moduleDependency = null;
		while(moduleNameKeys.hasNext()) {
			
			ModuleData moduleData = getUniqueAddedOrModifiedModulesMap().get(moduleNameKeys.next());
			String moduleNameDir = moduleData.getModuleName();
			binaryAudit = new BinaryAudit();
			binaryAudit.setBuildNumber(buildNumber);
			binaryAudit.setTaskId(getTaskId());
			binaryAudit.setSiloName(siloName);
			
			// moduleID set
			moduleName = new ModuleName();
			moduleName.setModuleName(moduleData.getModuleName());
			moduleName.setIsDeleted(DELETED.equals(moduleData.getModuleAction())?"Y":"N");
			binaryAudit.setModuleName(moduleName);
			
			
			//fill common jars. with md5 value
			if(ADDED.equals(moduleData.getModuleAction()) || MODIFIED.equals(moduleData.getModuleAction())) {	
				modulePomContainer = new PomContainer(getBasePath()+File.separator+moduleName+File.separator+"pom.xml");
				if(ADDED.equals(moduleData.getPomAction()))  {
					filterModuleDepList = modulePomContainer.getDependencies().stream().filter(s -> s.getVersion() != null).collect(toList());
					
					for(Dependency dependency : filterModuleDepList) {
						 moduleDependency = new ModuleDependency();
						 moduleDependency.setAction(ADDED);
						 String jarFile = dependency.getArtifactId()+"-"+dependency.getVersion()+".jar";
						 moduleDependency.setFilePath(moduleNameDir+File.separator+jarFile);
						 moduleDependency.setMd5Value(getMd5Value(moduleNameDir+File.separator+"target/dependency-jars/"+jarFile));
						 moduleData.getModuleDependencies().add(moduleDependency);
					}
					
					 // adding module jar to moduleArtifacts List.
					 moduleDependency = new ModuleDependency();
					 moduleDependency.setAction(ADDED);
					 String moduleJarFile = modulePomContainer.getArtifactId()+"-"+modulePomContainer.getVersion()+".jar";
					 moduleDependency.setFilePath(moduleNameDir+File.separator+moduleJarFile);
					 moduleDependency.setMd5Value(getMd5Value(moduleNameDir+File.separator+"target/"+moduleJarFile));
					 moduleData.getModuleArtifacts().add(moduleDependency);
				} else if(MODIFIED.equals(moduleData.getPomAction())) {
					//TODO: module pom changed.. compare and fill the change list.
					PomContainer originalPomContainer = new PomContainer(gitOpertions.getMasterBranchRootPomContent(moduleName+File.separator+"pom.xml"));
					List<Dependency> addedModuleDependencies = originalPomContainer.getAddedDependencies(modulePomContainer);
					List<Dependency> removedModuleDependencies = originalPomContainer.getRemovedDependencies(modulePomContainer);
					
					if(removedModuleDependencies != null && !removedModuleDependencies.isEmpty()) {
						for(Dependency dependency : removedModuleDependencies) {
							moduleDependency = new ModuleDependency();
							moduleDependency.setFilePath(moduleNameDir+File.separator+dependency.getArtifactId()+"-"+dependency.getVersion()+".jar");
							moduleDependency.setAction(DELETED);
							moduleData.getModuleDependencies().add(moduleDependency);
						}
					}
					
					if(addedModuleDependencies != null && !addedModuleDependencies.isEmpty()) {
						for(Dependency dependency : addedModuleDependencies) {
							moduleDependency = new ModuleDependency();
							moduleDependency.setFilePath(moduleNameDir+File.separator+dependency.getArtifactId()+"-"+dependency.getVersion()+".jar");
							moduleDependency.setAction(ADDED);
							moduleData.getModuleDependencies().add(moduleDependency);
						}
					}
					
					// module jar deleted or added
					if(originalPomContainer.getVersion() != modulePomContainer.getVersion() || originalPomContainer.getArtifactId() != modulePomContainer.getArtifactId()) {
						moduleDependency = new ModuleDependency();
						 moduleDependency.setAction(DELETED);
						 String moduleJarFile = originalPomContainer.getArtifactId()+"-"+originalPomContainer.getVersion()+".jar";
						 moduleDependency.setFilePath(moduleNameDir+File.separator+moduleJarFile);
						 moduleDependency.setMd5Value("");
						 moduleData.getModuleArtifacts().add(moduleDependency);
						 
						 moduleDependency = new ModuleDependency();
						 moduleDependency.setAction(ADDED);
						 moduleJarFile = modulePomContainer.getArtifactId()+"-"+modulePomContainer.getVersion()+".jar";
						 moduleDependency.setFilePath(moduleNameDir+File.separator+moduleJarFile);
						 moduleDependency.setMd5Value(getMd5Value(moduleNameDir+File.separator+"target/"+moduleJarFile));
						 moduleData.getModuleArtifacts().add(moduleDependency);
						 
					}
				}
			} 
				 
			// impacted modules because of root pom changed. loop through the impacted module for prepare dependency jars list{added or deleted} with md5 value		 
			if(addedDependencies != null && !addedDependencies.isEmpty()) {		
				 List<Dependency> dependencyList  = modulePomContainer.getDependencies();	
				 for(Dependency dep : dependencyList) {	
					 boolean matched = addedDependencies.stream().anyMatch(s -> s.getArtifactId().equals(dep.getArtifactId()) 
							 && s.getGroupId().equals(dep.getArtifactId()) 
							 && (s.getVersion() != null && dep.getVersion() == null) || (s.getVersion() == dep.getVersion()));
					 if(matched) {
						 moduleDependency = new ModuleDependency();
						 moduleDependency.setAction(ADDED);
						 String jarFile = dep.getArtifactId()+"-"+dep.getVersion()+".jar";
						 moduleDependency.setFilePath(baseCommonJarPath+File.separator+jarFile);
						 moduleDependency.setMd5Value(getMd5Value(moduleNameDir+File.separator+"target/dependency-jars/"+jarFile));
						 commonJars.add(moduleDependency);
					 }
				 }
			}
			
			binaryAudit.setModuleData(moduleData);
			binaryAudits.add(binaryAudit);
		}
		
	}
	
	public String getMd5Value(String relativeFilePath) throws IOException {
		String absoluteFilePath = getBasePath()+"/"+relativeFilePath;
		try(FileInputStream fis = new FileInputStream(new File(absoluteFilePath))) {
			return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		}
	}
	
	private void createSiloName() {
		SiloNameReq siloNameReq = new SiloNameReq();
		siloNameReq.setSiloName(getSiloName());
		siloName.setSiloNameReq(siloNameReq);
		
	}
	public BigInteger getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(BigInteger buildNumber) {
		this.buildNumber = buildNumber;
	}

	public List<BinaryAudit> getBinaryAudits() {
		return binaryAudits;
	}

	public void setBinaryAudits(List<BinaryAudit> binaryAudits) {
		this.binaryAudits = binaryAudits;
	}
	

}
