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
	 * @param classlist = the path to all class which will be generated tests.
	 * @param timeout = the limit of time used in the process of test generation.
	 */
	public static void generateTests(String classlist, String timeout) {

		randoop.main.Main main = new randoop.main.Main();
		String[] argsRandoop = { "gentests", "--classlist="+classlist,                    
                      		"--timelimit=" + timeout, "--log=filewriter", "--outputlimit=1",
				"--junit-output-dir=" + Constants.TEST_DIR }; //"--methodlist="C:\\methods.txt", "--classlist=" + classlist,
		main.nonStaticMain(argsRandoop);
	}
	
	public static void main(String[] args) {
		String classlist = args[0];
		String timeout = args[1];
		//String classes = "C:\\Users\\Alysson\\Desktop\\methodList.txt";
        generateTests(classlist, timeout);
	}
	
}
