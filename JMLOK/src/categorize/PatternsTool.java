package categorize;

import java.io.File;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.FileUtil;

public class PatternsTool {
	// Used to read and group Class and method detected in
	public Pattern captureClassMethodInPrePos = Pattern
			.compile("([A-Z][\\w]*)[\\.]([\\w]+)");
	// Used to capture attributes from an method declaration
	public Pattern captureAttributes = Pattern
			.compile("\\s*[a-zA-Z]\\w*\\s+(\\w+)\\s*,?");
	// Used to capture all variables from an attribute declaration
	public Pattern captureVariables = Pattern.compile("(\\w+)\\s*[=]?\\s*\\w*[,]*");
	/**
	 * Checks if there is an attribute on the method declaration that is on
	 * precondition specification.
	 * @param classname name of the class searched
	 * @param methodname name of the method searched
	 * @param code file that contains the code where the search will be located
	 * @return true if some attribute on the method declaration are located on the precondition expression or false.
	 */
	public boolean isAtrInPrecondition(String classname, String methodname,	File code) {
		Pattern capturePreAtr = Pattern.compile(".*public\\s+class\\s+"
				+ classname + "\\s*\\{.*" + "//@\\s*(?:requires|pre)([^;]*);.*"
				+ "(?:private|public|protected|\\s*)\\s+[\\w]+\\s+" + methodname
				+ "\\s*\\(([^()]*)\\)\\{", Pattern.DOTALL);
		String lines = FileUtil.getContent(code);
		Matcher condAtr = capturePreAtr.matcher(lines);
		condAtr.find();
		String precondition = condAtr.group(1);
		String attributeDeclaration = condAtr.group(2);
		Matcher searchAtr = captureAttributes.matcher(attributeDeclaration);
		while (searchAtr.find()) {
			if (precondition.contains(searchAtr.group(1)))
				return true;
		}
		return false;
	}
	/**
	 * Checks if there is an attribute on the method declaration that is on
	 * postcondition specification.
	 * @param classname name of the class searched
	 * @param methodname name of the method searched
	 * @param code file that contains the code where the search will be located
	 * @return true if some attribute on the method declaration are located on the postcondition expression or false.
	 */

	public boolean isAtrInPostcondition(String classname, String methodname, File code) {
		Pattern capturePostAtr = Pattern.compile(".*public\\s+class\\s+"
				+ classname + "\\s*\\{.*" + "//@\\s*(?:ensures|post)([^;]*);.*"
				+ "(?:private|public|protected|\\s*)\\s+[\\w]+\\s+" + methodname
				+ "\\s*\\(([^()]*)\\)\\{", Pattern.DOTALL);
		String lines = FileUtil.getContent(code);
		Matcher condAtr = capturePostAtr.matcher(lines);
		condAtr.find();
		String postcondition = condAtr.group(1);
		String attributeDeclaration = condAtr.group(2);
		Matcher searchAtr = captureAttributes.matcher(attributeDeclaration);
		while (searchAtr.find()) {
			if (postcondition.contains(searchAtr.group(1)))
				return true;
		}
		return false;
	}
	public boolean isVariableInPrecondition(String classname, String methodname, File code){
		Pattern capturePreCon = Pattern.compile(".*public\\s+class\\s+"
				+ classname + "\\s*\\{.*" + "//@\\s*(?:requires|pre)([^;]*);.*"
				+ "(?:private|public|protected|\\s*)\\s+[\\w]+\\s+" + methodname
				+ "\\s*\\([^()]*\\)\\{", Pattern.DOTALL);
		String lines = FileUtil.getContent(code);
		Matcher condAtr = capturePreCon.matcher(lines);
		condAtr.find();
		String precondition = condAtr.group(1);
		Pattern captureAttributesDeclaration = Pattern.compile("(?:private|public|protected|\\s*)" 
				+ "\\s+[\\w]+([^;]*);");
		Matcher condDec = captureAttributesDeclaration.matcher(lines);
		while(condDec.find()){
			
		}
		return false;
	}
	public static void main(String[] args) throws ClassNotFoundException {
		PatternsTool p = new PatternsTool();
		System.out.println("True?= " + p.isAtrInPrecondition("Carro","g",
				new File("/home/quantus/useful_paste/sampleExample/sampleExample/Carro.java")));

		Class<?> use = Carro.class;
		Field[] fields = use.getDeclaredFields();
		for(Field f : fields){
			System.out.println(f.toString());
		}
	
	}

}