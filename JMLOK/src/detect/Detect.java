package detect;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import utils.Constants;
import utils.FileUtil;

import org.jmlspecs.openjml.utils.*;

public class Detect {

	private boolean isJMLC;
	private boolean isOpenJML;
	private String jmlLib;
	private File jmlokDir = new File(Constants.TEMP_DIR);
	private File javaBin = new File(Constants.SOURCE_BIN);
	private File jmlBin = new File(Constants.JML_BIN);
	private File testSource = new File(Constants.TEST_DIR);
	private File testBin = new File(Constants.TEST_BIN);
	
	public Detect(int comp) {
		while (!jmlokDir.exists()) {
			jmlokDir.mkdirs();
		}
		switch (comp) {
		case Constants.JMLC_COMPILER:
			isJMLC = true;
			jmlLib = "C:\\JML";
			break;
		case Constants.OPENJML_COMPILER:
			isOpenJML = true;
			jmlLib = "C:\\openjml";
			break;
		default:
			break;
		}
	}
	
	public Set<TestError> detect(String source, String lib, String timeout) {
		execute(source, lib, timeout);
		ResultProducer r = new ResultProducer();
		if(isJMLC) return r.generateResult(Constants.JMLC_COMPILER);
		else return r.generateResult(Constants.OPENJML_COMPILER);
	}
	
	public void execute(String sourceFolder, String libFolder, String timeout) {
		File classListFile = getClassListFile(sourceFolder);
		createDirectories();
		cleanDirectories();
		javaCompile(sourceFolder, libFolder);
		jmlCompile(sourceFolder);
		generateTests(libFolder, timeout, classListFile.getAbsolutePath());
		runTests(libFolder);
	}

	private static File getClassListFile(String sourceFolder) {
		List<String> listClassNames = FileUtil.listNames(sourceFolder, "", ".java");
		StringBuffer lines = new StringBuffer();
		for (String className : listClassNames) {
			className = className + "\n";
			lines.append(className);
		}
		return FileUtil.makeFile(Constants.CLASSES, lines.toString());
	}
	
	public void createDirectories(){
		while (!javaBin.exists()) {
			javaBin.mkdirs();
		}
		while (!jmlBin.exists()) {
			jmlBin.mkdirs();
		}
		while (!testSource.exists()) {
			testSource.mkdirs();
		}
		while (!testBin.exists()) {
			testBin.mkdirs();
		}
	}
	
	public void cleanDirectories(){
		try {
			FileUtils.cleanDirectory(javaBin);
			FileUtils.cleanDirectory(jmlBin);
			FileUtils.cleanDirectory(testSource);
			FileUtils.cleanDirectory(testBin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void javaCompile(String sourceFolder, String libFolder){
		jmlLib = jmlLib + libFolder;
		File buildFile = new File("ant\\javaCompile.xml");
		Project p = new Project();
		p.setUserProperty("source_folder", sourceFolder);
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("compile_project");
	}
	
	public void generateTests(String libFolder, String timeout, String classList){
		jmlLib = jmlLib + libFolder;
		File buildFile = new File("ant\\generateTests.xml");
		Project p = new Project();
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);
		p.setUserProperty("classlist", classList);
		p.setUserProperty("timeout", timeout);
		p.setUserProperty("tests_folder", Constants.TESTS);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("compile_tests");
	}
	
	public void jmlCompile(String sourceFolder){
		if(isJMLC){
			File buildFile = new File("ant\\jmlcCompiler.xml");
			Project p = new Project();
			p.setUserProperty("source_folder", sourceFolder);
			p.setUserProperty("jmlBin", Constants.JML_BIN);
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget("jmlc");
		} else if(isOpenJML){
			File buildFile = new File("ant\\openjmlCompiler.xml");
			Project p = new Project();
			p.setUserProperty("source_folder", sourceFolder);
			p.setUserProperty("jmlBin", Constants.JML_BIN);
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget("openJML");
		}
	}
	
	public void runTests(String libFolder){
		File buildFile = new File("ant\\runTests.xml");
		Project p = new Project();
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlBin", Constants.JML_BIN);
		if(isJMLC) p.setUserProperty("jmlCompiler", Constants.JMLC_SRC);
		else if(isOpenJML) p.setUserProperty("jmlCompiler", Constants.OPENJML_SRC);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("run_tests");
	}
	
	public void execute2(String sourceFolder, String libFolder, String timeout){
		File classListFile = getClassListFile(sourceFolder);
		createDirectories();
		cleanDirectories();
		jmlCompile(sourceFolder);
		runJMLRAC();
	}
	
	public void execute3(String sourceFolder, String libFolder, String timeout) {
		File classListFile = getClassListFile(sourceFolder);
		//createDirectories();
		//cleanDirectories();
		javaCompile(sourceFolder, libFolder);
		//jmlCompile(sourceFolder);
		generateTests(libFolder, timeout, classListFile.getAbsolutePath());
		runTests(libFolder);
	}
	
	public void runJMLRAC(){
		File buildFile = new File("ant\\jmlrac.xml");
		Project p = new Project();
		p.setUserProperty("jmlBin", Constants.JML_BIN);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("jmlrac");
	}
	
	public static void main(String[] args) {
		// usar apenas o jmlc agora.
		Detect d = new Detect(0);
		d.execute3("C:\\Car", "", "1");
	}
}
