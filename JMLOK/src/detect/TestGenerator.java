package detect;

import utils.Constants;

/**
 * Class to generate test cases using the randoop tool.
 * @author Alysson Milanez
 * @version 1.0
 */
public class TestGenerator {

	/**
	 * Method to generate the test cases.
	 * @param timeout = the limit of time used in the process of test generation.
	 */
	public static void generateTests(String timeout) {

		randoop.main.Main main = new randoop.main.Main();
		String[] argsRandoop = { "gentests", "--classlist="+Constants.CLASSES,                    
                      		"--timelimit=" + timeout, "--log=filewriter", 
				"--junit-output-dir=" + Constants.TEST_DIR };
		main.nonStaticMain(argsRandoop);
	}
	
	/**
	 * Method that calls the method used to generate the test cases to test the SUT.
	 * @param args = the list of arguments used in the process of tests generation.
	 */
	public static void main(String[] args) {
		String timeout = args[0];
        generateTests(timeout);
	}
	
}
