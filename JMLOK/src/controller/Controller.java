package controller;

import gui.CategorizationScreenAdvisorFrame;
import gui.DetectionScreenAdvisorFrame;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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
		final List<Nonconformance> nonconformance = fulfillCategorizePhase(errors, source);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CategorizationScreenAdvisorFrame frame = new CategorizationScreenAdvisorFrame(nonconformance);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void setSystemVariableClassPath(String libFolder) {
		String pathVar = FileUtil.getListPathPrinted(libFolder);
		try {
			String s = "cmd /c SETX CLASSPATH \""+ pathVar + "\" -m";
			Runtime.getRuntime().exec(s);
			System.out.println(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Set<TestError> fulfillDetectPhase(int compiler, String source, String lib, String time){
		Detect d = new Detect(compiler);
		Set<TestError> errors = d.detect(source, lib, time);
		return errors;
	}
	
	private static List<Nonconformance> fulfillCategorizePhase(Set<TestError> errors, String source) {
		Categorize c = new Categorize();
		List<Nonconformance> x = new ArrayList<Nonconformance>();
		for(Nonconformance n : c.categorize(errors, source))
			x.add(n);
		return x;
	}

}
