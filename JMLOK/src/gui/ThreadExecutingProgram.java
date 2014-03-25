package gui;

import utils.Constants;
import controller.Controller;

/**
 * Class used to make thread control of GUI and the execution of the program, more easy. 
 * @author Dênnis Dantas.
 *
 */
public class ThreadExecutingProgram extends Thread {
	
	private String srcFolder;
	private String libFolder;
	private String timeout;

	/**
	 * Initialize the thread.
	 * @param srcFolder The source folder where program will operate.
	 * @param libFolder The lib folder which program execution depends.
	 * @param timeout The time(in seconds) where test will be generated.
	 */
	public ThreadExecutingProgram(String srcFolder, String libFolder, String timeout) {
		this.srcFolder = srcFolder;
		this.libFolder = libFolder;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		Controller.prepareToDetectPhase(Constants.JMLC_COMPILER, srcFolder, libFolder, timeout);
	}

}
