package controller;

import java.util.Set;

import utils.FileUtil;
import detect.Detect;
import detect.TestError;

public class Controller {
	
	public static void fillGui(int compiler, String source, String lib, String time){
		setSystemVariableClassPath(lib);
		//setSystemVariableClassPath(lib);
		//Set<TestError> errors = fulfillDetectFase(compiler, source, lib, time);
	}
	
	private static void setSystemVariableClassPath(String libFolder) {
		String pathVar = FileUtil.getListPathPrinted(libFolder);
		System.setProperty("CLASSPATH", pathVar);
		System.out.println(pathVar);
	}

	private static Set<TestError> fulfillDetectFase(int compiler, String source, String lib, String time){
		Detect d = new Detect(compiler);
		Set<TestError> errors = d.detect(source, lib, time);
		return errors;
	}
	
}
