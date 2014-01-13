package categorize;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Constants;
import utils.FileUtil;

/**
 * Class used to search some conditions that we use to determinate which is the likely cause for a nonconformance.
 * @author Alysson Milanez and Dênnis Souza.
 *
 */
public class PatternsTool {
	private static String PRECONDITION_INDICATOR = "(?:requires|pre|requires_redundantly|pre_redundantly)";
	private static String POSTCONDITION_INDICATOR = "(?:ensures|post|ensures_redundantly|post_redundantly)";
	private static String INVARIANT_INDICATOR = "(?:invariant|invariant_redundantly)";
	private static String CONSTRAINT_INDICATOR = "(?:constraint|constraint_redundantly)";
	private String srcDir;
	// Used to read and group Class and method detected in
	public Pattern captureClassMethodInPrePos = Pattern
			.compile("([A-Z][\\w]*)[\\.]([\\w]+)");
	// Used to capture attributes from an method declaration
	public Pattern captureAttributes = Pattern
			.compile("\\s*[a-zA-Z]\\w*\\s+(\\w+)\\s*,?");
	/**
	 * Constructs an PatternsTool object with the directory of the src project paste
	 * @param dir directory of the src project paste
	 */
	public PatternsTool(String dir){
		this.srcDir = dir;
	}
	/**
	 * Method that returns the complete path to class, whose class name was received as parameter.
	 * @param className - the name of the class.
	 * @return - the full path to the file.
	 */
	private String getPathFromFile(String className){
		String name = className.replace('.', '/');
		name += ".java";
		return srcDir+Constants.FILE_SEPARATOR+name;
	}
	
	/**
	 * Checks if there is an attribute on the method declaration that is on
	 * precondition specification.
	 * @param classname name of the class searched
	 * @param methodname name of the method searched
	 * @return true if some attribute on the method declaration are located on the precondition expression or false.
	 */
	public boolean isAtrInPrecondition(String classname, String methodname) {
		Pattern capturePreAtr = captureJMLFromAnMethodAndAtr(classname, methodname);
		String lines = FileUtil.readFile(getPathFromFile(classname));
		Matcher condAtr = capturePreAtr.matcher(lines);
		condAtr.find();
		String attributeDeclaration = condAtr.group(condAtr.groupCount());
		Matcher searchAtr = captureAttributes.matcher(attributeDeclaration);
		String precondition = conditionsJML(classname, methodname, PRECONDITION_INDICATOR, lines);
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
	 * @return true if some attribute on the method declaration are located on the postcondition expression or false.
	 */
	public boolean isAtrInPostcondition(String classname, String methodname) {
		Pattern capturePostAtr = captureJMLFromAnMethodAndAtr(classname, methodname);
		String lines = FileUtil.readFile(getPathFromFile(classname));
		Matcher condAtr = capturePostAtr.matcher(lines);
		condAtr.find();
		String attributeDeclaration = condAtr.group(condAtr.groupCount());
		Matcher searchAtr = captureAttributes.matcher(attributeDeclaration);
		String postcondition = conditionsJML(classname, methodname, POSTCONDITION_INDICATOR, lines);
		while (searchAtr.find()) {
			if (postcondition.contains(searchAtr.group(1)))
				return true;
		}
		return false;
	}
	/**
	 * Checks if there is an variable from the class that are on the precondition
	 * specification expression.
	 * @param classname name of the class searched
	 * @param methodname name of the method searched
	 * @return true if the variable are present of false
	 */
	public boolean isVariableInPrecondition(String classname, String methodname){
		String lines = FileUtil.readFile(getPathFromFile(classname));
		String precondition = conditionsJML(classname, methodname, PRECONDITION_INDICATOR, lines);
		ArrayList<String> variables = FileUtil.getVariablesFromClass(classname);
		for (String var: variables) {
			if(Pattern.compile(var).matcher(precondition).find()) 
				return true;
		}
		return false;
	}
	/**
	 * Checks if there is an variable from the class that is on the postcondition
	 * specification expression.
	 * @param classname name of the class searched
	 * @param methodname name of the method searched
	 * @return true if the variable are present of false
	 */
	public boolean isVariableInPostcondition(String classname, String methodname){
		String lines = FileUtil.readFile(getPathFromFile(classname));
		String postcondition = conditionsJML(classname, methodname, POSTCONDITION_INDICATOR, lines);
		ArrayList<String> variables = FileUtil.getVariablesFromClass(classname);
		for (String var: variables) {
			if(Pattern.compile(var).matcher(postcondition).find())
				return true;
		}
		return false;
	}
	/**
	 * Creates an String that contains all of JML conditions from an given type
	 * in a determined method from a determined class.
	 * @param classname Name of the class searched
	 * @param methodname Name of the method searched
	 * @param conditionsearched The keywords type from the JML conditions.
	 * @param lines Code from class searched.
	 * @return A String containing JML Conditions from the type choose.
	 */
	private String conditionsJML(String classname, String methodname, String conditionsearched, String lines) {
		Pattern captureCon = captureJMLFromAnMethodAndAtr(classname, methodname);
		Pattern captureConType = Pattern.compile(conditionsearched + "\\s+([^;]*);", Pattern.DOTALL);
		Matcher cond = captureCon.matcher(lines);
		cond.find();
		String conditions = cond.group(cond.groupCount() - 1);
		cond = captureConType.matcher(conditions);
		String toReturn = "";
		while(cond.find()){
			toReturn += cond.group(1) + ", ";
		}
		return toReturn;
	}
	/**
	 * Creates a Pattern that can be used to retrieve all of JML specification
	 * from a given method and its attributes, of a given class.
	 * @param classname Name of the class searched
	 * @param methodname Name of the method searched
	 * @return A Pattern to capture JML expressions and attributes from an method.
	 */
	private Pattern captureJMLFromAnMethodAndAtr(String classname, String methodname){
		Pattern captureCon = Pattern.compile(".*public\\s+class\\s+"
				+ getClassName(classname) + ".*\\s*\\{.*?(/[/*]@.*?)"
				+ "(?:private|public|protected|\\s*)\\s+[\\w]+\\s+" + methodname
				+ "\\s*\\(([^()]*)\\)\\{", Pattern.DOTALL);
		
		return captureCon;
	}
	/**
	 * Method used to check if a likely cause of a nonconformance is weak precondition.
	 * @param className - the name of the class that contains a nonconformance.
	 * @param methodName - the name of the method that contains a nonconformance.
	 * @return true if the precondition of the current method is weak, false otherwise.
	 */
	public boolean checkWeakPrecondition(String className, String methodName) {
		return isRequiresTrue(className, methodName) || isAttModifiedOnMethod(className, methodName);
	}

	private boolean isAttModifiedOnMethod(String className, String methodName) {
		String lines = FileUtil.readFile(getPathFromFile(className));
		
		return false;
	}
	/**
	 * Checks if in one of the JML conditions there are an requires true, that being
	 * when there are an requires true; or requires (* ... *); (informal) or an
	 * Absence of requires, which by default are require true.
	 * @param className Name of the class searched.
	 * @param methodName Name of the method searched.
	 * @return true if requires true were found or false.
	 */
	private boolean isRequiresTrue(String className, String methodName) {
		String lines = FileUtil.readFile(getPathFromFile(className));
		String precondition = conditionsJML(className, methodName, PRECONDITION_INDICATOR, lines);
		if(precondition == "")
			return true;
		String[] list = precondition.split(",");
		for (String string : list) {
			if(string.trim() == "true" || string.trim().matches("\\(\\*.*\\*\\)") )
				return true;
		}
		return false;
	}

	public boolean checkNull(String className, String methodName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Auxiliary method used to get the class name only, without informations about packages from this class.
	 * @param className - the full name of this class (with information about packages)
	 * @return the name of the class.
	 */
	private String getClassName(String className){
		return className.substring(className.lastIndexOf(".")+1, className.length());
	}
	
	public static void main(String[] args){
		/*PatternsTool pt = new PatternsTool("C:\\users\\Alysson\\Desktop");
		System.out.println("True, " + String.valueOf(pt.isAtrInPrecondition("sample.Carro", "g")));
		System.out.println("False, " + String.valueOf(pt.isAtrInPostcondition("sample.Carro", "g")));
		System.out.println("True, " + String.valueOf(pt.isVariableInPrecondition("sample.Carro", "g")));
		System.out.println("True, "+ String.valueOf(pt.isVariableInPostcondition("sample.Carro", "g")));
		System.out.println("True, "+ String.valueOf(pt.isRequiresTrue("sample.Carro", "f")));*/
	}
}
