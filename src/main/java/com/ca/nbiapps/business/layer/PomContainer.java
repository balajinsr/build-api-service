package com.ca.nbiapps.business.layer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.nbiapps.model.ResponseBuilder;


/**
 * 
 * @author sandipbose
 * @author Balaji
 * 
 * Container to hold information about a given POM.
 * Also has helper methods which can be invoked to check the correctness of the pom inline with our build system
 * 
 */

public class PomContainer {
	private static final Logger logger = LoggerFactory.getLogger(PomContainer.class);
	private Model pomModel = null;
	private List<Dependency> dependencyList;
	private List<Plugin> pluginList;
	private String absolutePomFileLocation;
	private List<Model> predecessorModels;
	
	public PomContainer(String absolutePomFileLocation) throws FileNotFoundException, IOException, XmlPullParserException{
		this.absolutePomFileLocation = absolutePomFileLocation;
		File pomFileObj = new File(absolutePomFileLocation);
		pomModel = createModelFromPomFile(pomFileObj);
		predecessorModels = getPredecessorModels(pomModel, pomFileObj);
		initPlugins();
	}
	
	public PomContainer(byte [] srcPomContent) throws FileNotFoundException, IOException, XmlPullParserException{
		pomModel = createModelFromPomFile(srcPomContent);
		initPlugins();
	}


	private void initPlugins() {
		Build build = pomModel.getBuild();
		if (pomModel.getDependencyManagement() != null){
			// if dependencyManagement is present
			dependencyList = pomModel.getDependencyManagement().getDependencies();
			// placeholder version will be replaced.
			if(build != null) {
				pluginList = build.getPluginManagement().getPlugins();
			}
		}else{
			// if dependency is present
			dependencyList = pomModel.getDependencies();
			if(build != null) {
				pluginList = build.getPlugins();
			}
		}
	}
	
	public Model getPOMModel(byte [] srcPomContent) throws IOException, XmlPullParserException {
		return createModelFromPomFile(srcPomContent);
	}
	
	/**
	 * Create a list of Model's which are predecessor of this POM. Includes the Model for this this POM as well.
	 * @param pomModel
	 * @param absolutePomFileLocation
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private List<Model> getPredecessorModels(Model pomModel, File absolutePomFileLocation) throws IOException, XmlPullParserException{
		List<Model> models;
		Parent parent = pomModel.getParent();
		if (parent!=null){
			String relativePath = parent.getRelativePath(); 
			File relativeFileObj = null;
			if (relativePath != null ){ 
				relativePath = "/../pom.xml";
				relativeFileObj = new File(absolutePomFileLocation.getParentFile()+relativePath);
			}
			models = getPredecessorModels(createModelFromPomFile(relativeFileObj), relativeFileObj); 
		}else{
			models = new ArrayList<>();
		}
		models.add(pomModel);	
		return models;
	}
	
	public int getModuleDepth() {
		return predecessorModels.size();
	}
	
	private Model createModelFromPomFile(byte [] absolutePomFileLocation) throws IOException, XmlPullParserException{
		Model pomModel = null;
		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(absolutePomFileLocation);
			pomModel = pomReader.read(is);
		} finally {
			if(is != null) {
				is.close();
			}
		}
		return pomModel;
	}
	
	/**
	 * Create the Model Object from the pom file
	 * @param absolutePomFileLocation
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private Model createModelFromPomFile(File absolutePomFileLocation) throws IOException, XmlPullParserException{
		Model pomModel = null;
		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(absolutePomFileLocation);
			pomModel = pomReader.read(fileReader);
		} finally {
			if(fileReader != null) {
				fileReader.close();
			}
		}
		return pomModel;
	}
	
	public String getVersion() {
		return pomModel.getVersion();
	}
	
	public String getTwoLevelNonCommonModuleVersion() {
		int moduleLevel = getModuleDepth();
		if(moduleLevel > 2) {
			Model twoLevelModel = predecessorModels.get(moduleLevel - 2);
			return twoLevelModel.getVersion();
		} else {
			return pomModel.getVersion();
		}
	}
	
	public boolean isTwoLevelNonCommonModule() {
		int moduleLevel = getModuleDepth();
		if(moduleLevel > 2) {
			Properties prop = predecessorModels.get(moduleLevel - 2).getProperties();
			return (prop!=null && "false".equals(prop.get("is_common_module")));
		}
		return false;
	}
	
	public String getArtifactId() {
		return pomModel.getArtifactId();
	}
	
	/**
	 * Convert the dependencyList to contain Dependency Objects to String Objects
	 * @return
	 */
	public List<String> getDependencyList(){
		return dependencyList.stream()
				 .map(s -> s.getVersion() == null ? 
						 s.getGroupId()+":"+s.getArtifactId() : 
							 s.getGroupId()+":"+s.getArtifactId()+":"+s.getVersion())
				 .collect(toList());
	}
	
	public List<Dependency> getDependencies(){
		return dependencyList;
	}
	
	/**
	 * Returns the list of Child Modules if present
	 * @return
	 */
	public List<String> getChildModules(){
		return pomModel.getModules();
	}
	
	/**
	 * artifactId for a deliverable JAR should start with nbiservice-.....
	 * @return
	 */
	public ResponseBuilder isArtifactIdNomenclatureCorrect(){
		if (pomModel.getPackaging() !=null) {
			if("jar".equalsIgnoreCase(pomModel.getPackaging()) || (pomModel.getParent() != null)) {
				if (pomModel.getArtifactId().startsWith("nbiservice-")){
					return sendResult(true);
				} else {
					return sendResult(false,pomDetails()+" ArtifactID MUST start with 'nbiservice-'");
				}
			} 
		} else  if(pomModel.getPackaging() == null) {
			return sendResult(false,pomDetails()+" <packaging> tag should mandatory.");
		}
		return sendResult(true);
	}
	
	/**
	 * Checks in a module project whether this is the parent pom or not
	 * @return
	 */
	public ResponseBuilder isParentPom(){
		if (pomModel.getParent() == null)
			return sendResult(true);
		return sendResult(false, pomDetails()+" is Not Parent");
	}
	
	
	public boolean isTwoLevelModule() {
		List<String> childModuleList = getChildModules();
		return pomModel.getParent() != null && childModuleList != null && !childModuleList.isEmpty();
	}
	
	/**
	 * Checks whether finalName is present or not
	 * @return
	 */
	public ResponseBuilder checkFinalName(){
		Build build = pomModel.getBuild();
		if (build != null && build.getFinalName() != null)
			return sendResult(false, pomDetails()+" Contains <finalName> tag, which is not allowed");
		return sendResult(true);
	}
	
	/**
	 * Is the pom marked to be a SNAPSHOT copy
	 * @return
	 */
	public ResponseBuilder isReleaseVersion(){
		if(pomModel.getVersion() == null)
			return sendResult(true);
		if (pomModel.getVersion().endsWith("-SNAPSHOT"))
			return sendResult(false, pomDetails()+" Version contains -SNAPSHOT, which is not allowed");
		return sendResult(true);
	}
	
	/**
	 * Check whether in the same pom you have two entries with the same groupId and artifactId.
	 * Do note you might have <dependency> or <dependencyManagement> sections in a pom
	 * @return
	 */
	public ResponseBuilder isDuplicateDependencyPresent(){
		if (dependencyList == null){
			return sendResult(true);
		}
		if ((dependencyList.stream()
				.map(s -> s.getGroupId()+":"+s.getArtifactId())
				.collect(toSet())).size() 
					== 
					dependencyList.size())
				return sendResult(true);
			else
				return sendResult(false, pomDetails()+ " Duplicate Dependencies (groupid,artifactid) are NOT allowed");
	}
	
	/**
	 * If the pom is Parent in a multi module project, all dependencies should have version
	 * If the pom is Child in a multi module project, none of the dependencies should have version
	 * @return
	 */
	public ResponseBuilder isDependencyVersioningNomenclatureCorrect(){
		if (dependencyList == null){
			return sendResult(true);
		}
		boolean tmpResults = false;
		if (isParentPom().isResult()){
			tmpResults = dependencyList.stream()
							.allMatch(s -> s.getVersion() != null);
			if (tmpResults)
					return sendResult(true);
			else
				return sendResult(false, pomDetails()+ " <Version> for ALL Dependencies is MANDATE in this pom, which is not present");
		}else{
			tmpResults = dependencyList.stream()
					        .allMatch(s -> s.getVersion() == null);
			if (tmpResults)
				return sendResult(true);
			else
				return sendResult(false, pomDetails()+ " <Version> for None of the Dependencies are allowed in this pom.");
		}
	}
	
	/**
	 * None of the pom's should have the <scope> defined in the dependencies; other than the base pom for a given silo. 
	 * @return
	 */
	public ResponseBuilder isDependencyScopeNomenclatureCorrect(){
		if (dependencyList == null){
			return sendResult(true);
		}
		boolean tmpResults = false;
		if (!isParentPom().isResult()){
			
			tmpResults = dependencyList.stream()
					        .allMatch(s -> s.getScope() == null);
			if (tmpResults)
				return sendResult(true);
			else
				return sendResult(false, pomDetails()+ " <Scope> for None of the Dependencies are allowed in this pom.");
		} else {
			tmpResults = dependencyList.stream()
					        .anyMatch(s -> s.getArtifactId().startsWith("nbiservice-") && (s.getScope() == null || !"provided".equals(s.getScope().toLowerCase())));
			if (tmpResults)
				return sendResult(false, pomDetails()+ " <Scope> for all Issuer or common modules dependencies should be [provided] in this pom, which is not present.");
			else
				return sendResult(true);
		}
	}
	
	/**
	 * Use this in case you want to check whether the module version contains  '.' separated numerical characters
	 * This should be the case for sub modules.
	 * @return
	 */
	public ResponseBuilder isPomVersionCorrect(){
	    if((isTwoLevelModule() || isParentPom().isResult() || predecessorModels.size() ==2) && pomModel.getVersion() == null){
			return sendResult(false, pomDetails()+ " Setting POM Version is mandatory for this POM");
		} else if (predecessorModels.size() > 2 && pomModel.getVersion() != null && isTwoLevelNonCommonModule()){
			return sendResult(false, pomDetails()+ " Setting POM Version is not allowed at this level");
		} else {}
	    
	    if(pomModel.getVersion() == null && !isTwoLevelNonCommonModule()) {
	    	return sendResult(false, pomDetails()+ " Version should be Mandatory");
	    }
	    
	    if(pomModel.getVersion() != null) {
		Pattern p = Pattern.compile("[1-9]+(\\.[0-9]+)*");
		Matcher m = p.matcher(pomModel.getVersion());
			if (m.matches()){
				return sendResult(true);
			}else{
				return sendResult(false, pomDetails()+ " Version Used should follow x.y.z format; where x,y,z should be integers");
			}
	    }
	    return sendResult(true);
	}
	
	/**
	 * The Parent pom in a multi module setup should ONLY have dependencyManagement section.
	 * The Child module pom's  should ONLY have dependencies section.
	 * @return
	 */
	
	public ResponseBuilder isDependencyNomenclatureCorrect(){
		if (dependencyList == null){
			return sendResult(true);
		}
		if (isParentPom().isResult()){
			if (!pomModel.getDependencies().isEmpty()){
				return sendResult(false, pomDetails()+ " CANNOT contain <dependencies> outside <dependencyManagement> tag");
			}
		}else{
			if (pomModel.getDependencyManagement() != null){
				return sendResult(false, pomDetails()+ " CANNOT contain <dependencyManagement> tag");
			}
			
			if(isTwoLevelModule()) {
				if(pomModel.getDependencies() != null && !pomModel.getDependencies().isEmpty()) {
					return sendResult(false, pomDetails()+ " CANNOT contain <dependencies> tag");
				}
			}
		}
		return sendResult(true);
	}
	
	
	/**
	 * The Parent pom in a multi module setup should ONLY have pluginManagement section.
	 * The Child module pom's  should ONLY have plugins section.
	 * @return
	 */
	
	public ResponseBuilder isPluginNomenclatureCorrect(){
		if (pluginList == null){
			return sendResult(true);
		}
		Build build = pomModel.getBuild();
		if (isParentPom().isResult()) {
			if ( build != null && !build.getPlugins().isEmpty()){
				return sendResult(false, pomDetails()+ " Cannot contain <plugins> tag under <build> tag");
			}
		}else{
			if (build != null && build.getPluginManagement() != null){
				return sendResult(false, pomDetails()+ " Cannot contain <pluginManagement> tag");
			}
		}
		return sendResult(true);
	}
	
	/**
	 * The Parent pom in a multi module setup should ONLY have repositories section.
	 * The Child module pom's  should be inherited from parent pom.
	 * @return
	 */
	public ResponseBuilder isRepositoriesNomenclatureCorrect(){
		List<Repository> repos = pomModel.getRepositories();
		if (!isParentPom().isResult()) {	
			if (repos != null && !repos.isEmpty()){
				return sendResult(false, pomDetails()+ " Cannot contain <repositories> tag");
			}
		}
		return sendResult(true);
	}
	
	private ResponseBuilder sendResult(boolean result){
		return sendResult(result, "");
	}
	
	private ResponseBuilder sendResult(boolean result, String resultDesc){
		return new ResponseBuilder(result, resultDesc);
	}
	
	private String pomDetails(){
		return "{ GroupId = "+pomModel.getGroupId()+" , ArtifactId = "+pomModel.getArtifactId()+" , Version = "+pomModel.getVersion()+" , File Path = "+this.absolutePomFileLocation+" }";
	}
	
	/**
	 * Compare child module dependencies with parent pom, if any one dependency inherited with "not provided" dependency,
	 * so child module should contain copy-dependency plugin under build tag. 
	 * @return
	 */
	public ResponseBuilder isDependencyPluginPresent(){
		if (isParentPom().isResult()){
			if (pluginList.stream()
				.anyMatch(s -> s.getGroupId().equals("org.apache.maven.plugins") 
						&& s.getArtifactId().equals("maven-dependency-plugin"))){
				return sendResult(true);
			}else{
				return sendResult(false, pomDetails() + " Its MANDATORY to have [ maven-dependency-plugin ] for this pom");
			}
		}else{
			//get the base pom
			Model baseModel = predecessorModels.stream()
								.filter(s -> s.getParent()==null)
								.findAny()
								.get();
			
			//get the Dependencies in string format for dependencies having scope NOT provided (will include dependency with no scope)
			List<String> parentNonProvidedDependencyList = baseModel.getDependencyManagement().getDependencies().stream()
											.filter(s -> s.getScope()==null || !s.getScope().equals("provided"))
											.map(s -> s.getGroupId()+":"+s.getArtifactId())
											.collect(toList());
			// check the pom in question against the base pom to see if a dependency matches
			if (dependencyList.stream()
								.map(s -> s.getGroupId()+":"+s.getArtifactId())
								.anyMatch(s -> parentNonProvidedDependencyList.contains(s))){
					
				if (pluginList != null && pluginList.stream()
						.anyMatch(s -> s.getGroupId().equals("org.apache.maven.plugins") 
								&& s.getArtifactId().equals("maven-dependency-plugin"))){
						return sendResult(true);
					}else{
						return sendResult(false, pomDetails() + " Its MANDATORY to have [ maven-dependency-plugin ] for this pom");
					}				
			}
		}
		return sendResult(true);
	}
	
	public List<Dependency> getAddedDependencies(PomContainer other) {
		List<Dependency> addedDependencies = null;
		if(this.isParentPom().isResult()) {
			addedDependencies = other.getDependencies().stream().filter(o1 -> this.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& o2.getVersion().equals(o1.getVersion())
					&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
		} else {
			addedDependencies = other.getDependencies().stream().filter(o1 -> this.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
		}
		return addedDependencies;
	}
	
	
	public List<Dependency> getRemovedDependencies(PomContainer other) {
		List<Dependency> removedDependencies = null;
		if(this.isParentPom().isResult()) {
			removedDependencies = this.getDependencies().stream().filter(o1 -> other.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& o2.getVersion().equals(o1.getVersion())
					&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
		} else {
			removedDependencies = this.getDependencies().stream().filter(o1 -> other.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId())
					&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
		}
		return removedDependencies;
	}
	
	public void printDependenciesChanges(List<Dependency> removedOriginalDependencies,  List<Dependency> addedModifiedDependencies) {
		List<Dependency> modifiedDependencies = null;
		if(this.isParentPom().isResult()) {
			modifiedDependencies = removedOriginalDependencies.stream().filter(o1 -> addedModifiedDependencies.stream().allMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
					&& o2.getArtifactId().equals(o1.getArtifactId()))).collect(Collectors.toList());
		} 
		getNewlyAddedDependencies(addedModifiedDependencies, modifiedDependencies);
	}
	
	private void getNewlyAddedDependencies(List<Dependency> addedModifiedDependencies,  List<Dependency> modifiedDependencies) {
		List<Dependency> addedModDependencies = new ArrayList<>(addedModifiedDependencies);
		if(!addedModDependencies.isEmpty() && !modifiedDependencies.isEmpty()) {
			boolean condition = addedModDependencies.size() > modifiedDependencies.size();
			boolean isDone = condition ? addedModDependencies.removeAll(modifiedDependencies) : modifiedDependencies.removeAll(addedModDependencies);
			if(isDone) {
				if(condition) {
					logger.info("Newly added dependencies: "+addedModDependencies.toString());
				} else {
					logger.info("Newly added dependencies: "+modifiedDependencies.toString());
				}
			} else {
				logger.info("Unable to remove from dependencies list. Verify it.");
			}
		}
	}
	
	public List<String> getAddedModules(PomContainer other) {
		if(this.isParentPom().isResult()) {
			return other.getChildModules().stream().filter(o1 -> this.getChildModules().stream().noneMatch(o2 -> o2.equals(o1))).collect(Collectors.toList());
		} 
		return null;
	}
	
	public List<String> getRemovedModules(PomContainer other) {
		if(this.isParentPom().isResult()) {
			return this.getChildModules().stream().filter(o1 -> other.getChildModules().stream().noneMatch(o2 -> o2.equals(o1))).collect(Collectors.toList());
		} 
		return null;
	}
	/*
	public static void main(String ...args) throws Exception {
		PomContainer srcPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/NBI-Applications-SECURE5/pom.xml"); 
		PomContainer deskPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/NBI-Applications-SECURE5/pom-dest.xml"); 
		
		
		List<String> srcModuleList = srcPomContainer.getChildModules();
		List<String> destModuleList = deskPomContainer.getChildModules();
		
		List<String> removedModule = srcModuleList.stream().filter(o1 -> destModuleList.stream().noneMatch(o2 -> o2.equals(o1))
				).collect(Collectors.toList());
		
		List<String> addedModule = destModuleList.stream().filter(o1 -> srcModuleList.stream().noneMatch(o2 -> o2.equals(o1))
				).collect(Collectors.toList());
		System.out.println("removedModules: "+removedModule);
		System.out.println("addedModules: "+addedModule);
		
		List<Dependency> srcDepList = srcPomContainer.getDependencies();
		List<Dependency> destDepList = deskPomContainer.getDependencies();
		
		
		List<Dependency> removed = srcDepList.stream().filter(o1 -> destDepList.stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& o2.getVersion().equals(o1.getVersion())
				&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
		
		System.out.println("removed:::"+removed.toString());
		for(Dependency de  : removed) {
			System.out.println(de.getArtifactId()+":"+de.getScope());
		}
		
		
		List<Dependency> added = destDepList.stream().filter(o1 -> srcDepList.stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& o2.getVersion().equals(o1.getVersion())
				&& (o2.getScope() == o1.getScope() || o2.getScope().equals(o1.getScope())))).collect(Collectors.toList());
		
		System.out.println("added:::"+added.toString());
		
		for(Dependency de  : added) {
			System.out.println(de.getArtifactId()+":"+de.getScope());
		}	
		
		
		PomContainer srcInheritPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/NBI-Applications-SECURE5/OTPSampleImplementation/pom.xml");
		PomContainer destInheritPomContainer = new PomContainer("C:/work-ca/GITHUB-Enterprise-Developer/NBI-Applications-SECURE5/OTPSampleImplementation/dest-inherit.xml"); 
		
		List<Dependency> addedInheritDependencies = destInheritPomContainer.getDependencies().stream().filter(o1 -> srcInheritPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
		System.out.println("Inherit pom - Added dependencies to dest-pom.xml - "+addedInheritDependencies.toString());
		
		
		List<Dependency> removedInheritDependencies = srcInheritPomContainer.getDependencies().stream().filter(o1 -> destInheritPomContainer.getDependencies().stream().noneMatch(o2 -> o2.getGroupId().equals(o1.getGroupId())
				&& o2.getArtifactId().equals(o1.getArtifactId())
				&& (o2.getVersion() == o1.getVersion() || o2.getVersion().equals(o1.getVersion())))).collect(Collectors.toList());
		System.out.println("Inherit pom - Removed dependencies from dest-pom.xml - "+removedInheritDependencies.toString());
		
	}*/
	
//	public static void main(String[] str){
//		PomContainer parse = null;
//		try {
//			parse = new PomContainer("/Users/sandipbose/Downloads/CodeN/toBeDeleted/NBI-Applications-SECUREDEMO/MultLevelModule/pom.xml");
//		} catch (IOException | XmlPullParserException e) {
//			e.printStackTrace();
//		}
//		System.out.println(parse.isPomVersionCorrect());
//	}
}
