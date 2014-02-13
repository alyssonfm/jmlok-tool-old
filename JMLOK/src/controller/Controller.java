package controller;

import gui.CategorizationScreenAdvisorFram;
import gui.DetectionScreenAdvisorFrame;

import java.awt.EventQueue;
import java.io.PrintStream;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;

import categorize.Categorize;
import categorize.Nonconformance;
import utils.FileUtil;
import detect.Detect;
import detect.TestError;

public class Controller {
	
	private static Set<TestError> errors;
	private static Set<Nonconformance> nonconformities;
	private static String source;
	
	public static void prepareToDetectPhase(int compiler, String sourceFolder, String lib, String time){
		setSystemVariableClassPath(lib);
		source = sourceFolder;
		showDetectionScreen(compiler, lib, time);
	}
	
	private static void showDetectionScreen(int compiler, String lib, String time) {
	    final ByteArrayOutputStream caos = setToolsForDetectionScreen(compiler, lib, time);
	    final int numNC = errors.size();
	    EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DetectionScreenAdvisorFrame frame = new DetectionScreenAdvisorFrame(caos, numNC);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static ByteArrayOutputStream setToolsForDetectionScreen(int compiler, String lib, String time) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		errors = fulfillDetectPhase(compiler, source, lib, time);
		System.out.flush();
	    System.setOut(old);
	    return baos;
	}

	public static void showCategorizationScreen() {
		Set<Nonconformance> nonconformance = fulfillCategorizePhase(errors, source);
		CategorizationScreenAdvisorFram.main();
	}

	private static void setSystemVariableClassPath(String libFolder) {
		String pathVar = FileUtil.getListPathPrinted(libFolder);
		System.setProperty("CLASSPATH", pathVar);
	}

	private static Set<TestError> fulfillDetectPhase(int compiler, String source, String lib, String time){
		Detect d = new Detect(compiler);
		Set<TestError> errors = d.detect(source, lib, time);
		return errors;
	}
	
	private static Set<Nonconformance> fulfillCategorizePhase(Set<TestError> errors, String source) {
		Categorize c = new Categorize();
		return c.categorize(errors, source);
	}

}
