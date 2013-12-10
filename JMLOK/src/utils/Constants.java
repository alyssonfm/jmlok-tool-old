package utils;

/**
 * Class to storage the main constants used in the JMLOK project.
 * @author Alysson Milanez
 * @version 1.0
 * 
 */
public class Constants {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "jmlOK";
	public static final String SOURCE_BIN = TEMP_DIR + FILE_SEPARATOR + "bin";
	public static final String JML_BIN = TEMP_DIR + FILE_SEPARATOR + "jmlBin";
	public static final String CLASSES = TEMP_DIR + FILE_SEPARATOR  + "classes.txt";
	public static final String TESTS = TEMP_DIR + FILE_SEPARATOR + "tests";
	public static final String TEST_DIR = TESTS + FILE_SEPARATOR + "src";
	public static final String TEST_BIN = TESTS + FILE_SEPARATOR + "bin";
	public static final String TEST_FILE = TEST_DIR + FILE_SEPARATOR + "RandoopTest0.java";
	public static final String TEST_RESULTS = TEST_DIR + FILE_SEPARATOR + "TEST-RandoopTest.xml";
	public static final String RESULTS = TEMP_DIR+FILE_SEPARATOR+"results.xml";
	public static final int JMLC_COMPILER = 0;
	public static final int OPENJML_COMPILER = 1;
	public static final String JMLC_SRC = "C:\\JML\\bin";
	public static final String OPENJML_SRC = "C:\\openjml";
	
}
