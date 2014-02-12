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

/**
 * Class used to detect nonconformances in Java/JML programs.
 * @author Alysson Milanez and Dennis Souza.
 * @version 1.0
 */
public class Detect {

	private boolean isLinux;
	private boolean isJMLC;
	private boolean isOpenJML;
	private String jmlLib;
	private File jmlokDir = new File(Constants.TEMP_DIR);
	private File javaBin = new File(Constants.SOURCE_BIN);
	private File jmlBin = new File(Constants.JML_BIN);
	private File testSource = new File(Constants.TEST_DIR);
	private File testBin = new File(Constants.TEST_BIN);
	
	/**
	 * The constructor of this class, creates a new instance of Detect class, creates the jmlok directory and set the JML compiler used.
	 * @param comp = the integer that indicates which JML compiler will be used.
	 */
	public Detect(int comp) {
		while (!jmlokDir.exists()) {
			jmlokDir.mkdirs();
		}
		switch (comp) {
		case Constants.JMLC_COMPILER:
			isJMLC = true;
			jmlLib = Constants.JMLC_LIB;
			break;
		case Constants.OPENJML_COMPILER:
			isOpenJML = true;
			jmlLib = Constants.OPENJML_SRC;
			break;
		default:
			break;
		}
		isLinux = System.getProperty("os.name").equals("Linux");
	}
	
	/**
	 * Method used to detect the nonconformances.
	 * @param source = the path to classes directory.
	 * @param lib = the path to external libraries directory.
	 * @param timeout = the time to tests generation.
	 * @return - The list of nonconformances detected.
	 */
	public Set<TestError> detect(String source, String lib, String timeout) {
		execute(source, lib, timeout);
		ResultProducer r = new ResultProducer();
		if(isJMLC) return r.generateResult(Constants.JMLC_COMPILER);
		else return r.generateResult(Constants.OPENJML_COMPILER);
	}
	
	/**
	 * Method that executes the scripts to conformance checking.
	 * @param sourceFolder = the path to source of files to be tested.
	 * @param libFolder = the path to external libraries needed for the current SUT.
	 * @param timeout = the time to tests generation.
	 */
	public void execute(String sourceFolder, String libFolder, String timeout) {
		getClassListFile(sourceFolder);
		createDirectories();
		cleanDirectories();
		javaCompile(sourceFolder, libFolder);
		jmlCompile(sourceFolder);
		generateTests(libFolder, timeout);
		runTests(libFolder);
	}

	/**
	 * Method used to list all classes present into the directory received as parameter.
	 * @param sourceFolder = the directory source of the files.
	 * @return - the file containing all classes.
	 */
	private File getClassListFile(String sourceFolder) {
		List<String> listClassNames = FileUtil.listNames(sourceFolder, "", ".java");
		StringBuffer lines = new StringBuffer();
		for (String className : listClassNames) {
			className = className + "\n";
			lines.append(className);
		}
		return FileUtil.makeFile(Constants.CLASSES, lines.toString());
	}
	
	/**
	 * Method used to creates all directories to be used by the tool.
	 */
	private void createDirectories(){
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
	
	/**
	 * Method used to clean all directories - for the case of several executions of the tool.
	 */
	private void cleanDirectories(){
		try {
			FileUtils.cleanDirectory(javaBin);
			FileUtils.cleanDirectory(jmlBin);
			FileUtils.cleanDirectory(testSource);
			FileUtils.cleanDirectory(testBin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to Java compilation of the files (needed for tests generation).
	 * @param sourceFolder = the path to source files.
	 * @param libFolder = the path to external libraries needed to Java compilation.
	 */
	public void javaCompile(String sourceFolder, String libFolder){
		jmlLib = jmlLib + libFolder;
		File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "javaCompile.xml");
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
	
	/**
	 * Method used to generate the tests to conformance checking.
	 * @param libFolder = the path to external libraries needed to tests generation and compilation.
	 * @param timeout = the time to tests generation.
	 */
	public void generateTests(String libFolder, String timeout){
		jmlLib = jmlLib + libFolder;
		File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "generateTests.xml");
		Project p = new Project();
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);
		p.setUserProperty("timeout", timeout);
		p.setUserProperty("tests_folder", Constants.TESTS);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("compile_tests");
	}
	
	/**
	 * Method used to do the JML compilation of the files.
	 * @param sourceFolder = the source of files to be compiled.
	 */
	public void jmlCompile(String sourceFolder){
		if(FileUtil.hasDirectories(sourceFolder)){
			if(isJMLC){
				File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "jmlcCompiler.xml");
				Project p = new Project();
				p.setUserProperty("source_folder", sourceFolder);
				p.setUserProperty("jmlBin", Constants.JML_BIN);
				p.setUserProperty("jmlcExec", (isLinux)?(Constants.JMLC_SRC + "jmlc-unix"):("jmlc.bat"));
				p.init();
				ProjectHelper helper = ProjectHelper.getProjectHelper();
				p.addReference("ant.projectHelper", helper);
				helper.parse(p, buildFile);
				p.executeTarget("jmlc");
			} else if(isOpenJML){
				File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "openjmlCompiler.xml");
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
		if(isJMLC){
			File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "jmlcCompiler2.xml");
			Project p = new Project();
			p.setUserProperty("source_folder", sourceFolder);
			p.setUserProperty("jmlBin", Constants.JML_BIN);
			p.setUserProperty("jmlcExec", (isLinux)?(Constants.JMLC_SRC + "jmlc-unix"):("jmlc.bat"));
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget("jmlc");
		} else if(isOpenJML){
			File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "openjmlCompiler2.xml");
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
	
	/**
	 * Method used to run the tests with the JML oracles.
	 * @param libFolder = the path to external libraries needed to tests execution.
	 */
	private void runTests(String libFolder){
		File buildFile = new File("ant" + Constants.FILE_SEPARATOR + "runTests.xml");
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
	
	/**
	 * Main method - used to test purposes.
	 * @param args
	 */
	public static void main(String[] args) {
		//usar apenas o jmlc agora.
		/*int x = 10;
		Detect d = new Detect(Constants.JMLC_COMPILER);
		d.detect("/home/quantus/useful_paste/sampleExample/", "", "1");*/
	}
}
