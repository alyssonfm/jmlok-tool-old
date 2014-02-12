package categorize;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaFileObject.Kind;

import org.jmlspecs.openjml.API;
import org.jmlspecs.openjml.Factory;
import org.jmlspecs.openjml.IAPI;
import org.jmlspecs.openjml.JmlTree;
import org.jmlspecs.openjml.JmlTree.JmlClassDecl;
import org.jmlspecs.openjml.JmlTree.JmlMethodDecl;
import org.jmlspecs.openjml.JmlTree.JmlSpecificationCase;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Kinds.KindName;
import com.sun.tools.javac.tree.JCTree;

import utils.Constants;
import utils.FileUtil;

/**
 * Class used to search some conditions that we use to determinate which is the
 * likely cause for a nonconformance.
 * 
 * @author Alysson Milanez and Dennis Souza.
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
	 * Constructs an PatternsTool object with the directory of the src project
	 * paste
	 * 
	 * @param dir
	 *            directory of the src project paste
	 */
	public PatternsTool(String dir) {
		this.srcDir = dir;
	}

	/**
	 * Method that returns the complete path to class, whose class name was
	 * received as parameter.
	 * 
	 * @param className
	 *            - the name of the class.
	 * @return - the full path to the file.
	 */
	private String getPathFromFile(String className) {
		String name = className.replace('.', '/');
		name += ".java";
		return srcDir + Constants.FILE_SEPARATOR + name;
	}

	/**
	 * Checks if there is an attribute on the method declaration that is on
	 * precondition specification.
	 * 
	 * @param classname
	 *            name of the class searched
	 * @param methodname
	 *            name of the method searched
	 * @return true if some attribute on the method declaration are located on
	 *         the precondition expression or false.
	 */
	public boolean isAtrInPrecondition(String classname, String methodname) {
		String lines = FileUtil.readFile(getPathFromFile(classname));
		String precondition = conditionsJML(classname, methodname,
				PRECONDITION_INDICATOR, lines);
		for (String s : getParameterNames(classname, methodname, lines)) {
			if (precondition.contains(s))
				return true;
		}
		return false;
	}

	/**
	 * Checks if there is an attribute on the method declaration that is on
	 * postcondition specification.
	 * 
	 * @param classname
	 *            name of the class searched
	 * @param methodname
	 *            name of the method searched
	 * @return true if some attribute on the method declaration are located on
	 *         the postcondition expression or false.
	 */
	public boolean isAtrInPostcondition(String classname, String methodname) {
		String lines = FileUtil.readFile(getPathFromFile(classname));
		String postcondition = conditionsJML(classname, methodname,
				POSTCONDITION_INDICATOR, lines);
		for (String s : getParameterNames(classname, methodname, lines)) {
			if (postcondition.contains(s))
				return true;
		}
		return false;
	}

	/**
	 * Checks if there is an variable from the class that are on the
	 * precondition specification expression.
	 * 
	 * @param classname
	 *            name of the class searched
	 * @param methodname
	 *            name of the method searched
	 * @return true if the variable are present of false
	 */
	public boolean isVariableInPrecondition(String classname, String methodname) {
		String lines = FileUtil.readFile(getPathFromFile(classname));
		String precondition = conditionsJML(classname, methodname,
				PRECONDITION_INDICATOR, lines);
		for (String var : FileUtil.getVariablesFromClass(classname)) {
			if (precondition.contains(var))
				return true;
		}
		return false;
	}

	/**
	 * Checks if there is an variable from the class that is on the
	 * postcondition specification expression.
	 * 
	 * @param classname
	 *            name of the class searched
	 * @param methodname
	 *            name of the method searched
	 * @return true if the variable are present of false
	 */
	public boolean isVariableInPostcondition(String classname, String methodname) {
		String lines = FileUtil.readFile(getPathFromFile(classname));
		String postcondition = conditionsJML(classname, methodname,
				POSTCONDITION_INDICATOR, lines);
		for (String var : FileUtil.getVariablesFromClass(classname)) {
			if (postcondition.contains(var))
				return true;
		}
		return false;
	}

	/**
	 * Creates an String that contains all of JML conditions from an given type
	 * in a determined method from a determined class.
	 * 
	 * @param classname
	 *            Name of the class searched
	 * @param methodname
	 *            Name of the method searched
	 * @param conditionsearched
	 *            The keywords type from the JML conditions.
	 * @param lines
	 *            Code from class searched.
	 * @return A String containing JML Conditions from the type choose.
	 */
	private String conditionsJML(String classname, String methodname,
			String conditionsearched, String lines) {
		Pattern captureCon = captureJMLFromAnMethodAndAtr(classname, methodname);
		Pattern captureConType = Pattern.compile(conditionsearched
				+ "\\s+([^;]*);", Pattern.DOTALL);
		Matcher cond = captureCon.matcher(lines);
		cond.find();
		String conditions = cond.group(cond.groupCount() - 1);
		cond = captureConType.matcher(conditions);
		String toReturn = "";
		while (cond.find()) {
			toReturn += cond.group(1) + ", ";
		}
		return toReturn;
	}

	/**
	 * Creates a Pattern that can be used to retrieve all of JML specification
	 * from a given method and its attributes, of a given class.
	 * 
	 * @param classname
	 *            Name of the class searched
	 * @param methodname
	 *            Name of the method searched
	 * @return A Pattern to capture JML expressions and attributes from an
	 *         method.
	 */
	private Pattern captureJMLFromAnMethodAndAtr(String classway,
			String methodname) {
		Pattern captureCon = Pattern.compile(".*public\\s+class\\s+"
				+ getClassName(classway) + ".*\\s*\\{.*?(/[/*]@.*?)"
				+ "(?:private|public|protected|\\s*)\\s+[\\w]+\\s+"
				+ methodname + "\\s*\\(([^()]*)\\)\\{", Pattern.DOTALL);

		return captureCon;
	}

	/**
	 * Method that return code from the constructor of an specified class.
	 * 
	 * @param classway
	 *            The package and classname information of the class.
	 * @param lines
	 *            The code in a String.
	 * @return String containing constructor code.
	 */
	private String getConstructorCode(String classway, String lines) {
		String classname = getClassName(classway);
		Pattern captureCon = Pattern.compile(".*public\\s+class\\s+"
				+ classname + ".*\\s*\\{.*?"
				+ "(?:private|public|protected|\\s*)\\s+" + classname
				+ "\\s*\\([^()]*\\)\\{", Pattern.DOTALL);
		Matcher codeMet = captureCon.matcher(lines);
		codeMet.find();
		int startIndex = codeMet.end();
		int countDelimiters = 1, flagString = 1;
		String toReturn = "";
		for (int i = startIndex; i < lines.length(); i++) {
			if (lines.charAt(i) == '{' && flagString == 1)
				countDelimiters++;
			else if (lines.charAt(i) == '}' && flagString == 1)
				countDelimiters--;
			else if (lines.charAt(i) == '"' && flagString == 1)
				flagString = 0;
			else if (lines.charAt(i) == '\'' && flagString == 1)
				flagString = -1;

			if (flagString == 1 && countDelimiters != 0)
				toReturn += lines.charAt(i);

			if ((flagString == 0 && lines.charAt(i - 1) != '\\' && lines
					.charAt(i) == '"')
					|| (flagString == -1 && lines.charAt(i - 1) != '\\' && lines
							.charAt(i) == '\''))
				flagString = 1;

			if (countDelimiters == 0)
				return toReturn;
		}
		return toReturn.trim();
	}

	/**
	 * Method used to check if a likely cause of a nonconformance is weak
	 * precondition.
	 * 
	 * @param className
	 *            - the name of the class that contains a nonconformance.
	 * @param methodName
	 *            - the name of the method that contains a nonconformance.
	 * @return true if the precondition of the current method is weak, false
	 *         otherwise.
	 */
	public boolean checkWeakPrecondition(String className, String methodName) {
		return isRequiresTrue(className, methodName)
				|| isAttModifiedOnMethod(className, methodName);
	}

	/**
	 * Method that checks if there are some parameter of the method modified in
	 * the method body.
	 * 
	 * @param className
	 *            - the name of the class that contains a nonconformance.
	 * @param methodName
	 *            - the name of the method that contains a nonconformance.
	 * @return true if there are some parameter modified, false otherwise.
	 */
	private boolean isAttModifiedOnMethod(String className, String methodName) {
		String lines = FileUtil.readFile(getPathFromFile(className));
		String parameters = getParametersInGroup(className, methodName, lines);
		String codeMethod = getCodeMethod(className, methodName, lines);
		Pattern getMod = Pattern.compile(parameters
				+ "\\s+(?:<<=|>>=|>>>=|\\|=|&=|\\+=|-=|/=|\\*=|%=|=)[^;]+;|"
				+ "\\+\\+" + parameters + "|--" + parameters + "|" + parameters
				+ "\\+\\+|" + parameters + "--");
		Matcher takeMod = getMod.matcher(codeMethod);
		if (takeMod.find())
			return true;
		else
			return false;
	}

	/**
	 * Get the code from a specified method in a class.
	 * 
	 * @param className
	 *            The name of the class searched.
	 * @param methodName
	 *            The name of the method searched.
	 * @param lines
	 *            The code of the whole class.
	 * @return
	 */
	private String getCodeMethod(String className, String methodName,
			String lines) {
		Pattern getMet = captureJMLFromAnMethodAndAtr(className, methodName);
		Matcher codeMet = getMet.matcher(lines);
		codeMet.find();
		int startIndex = codeMet.end();
		int countDelimiters = 1, flagString = 1;
		String toReturn = "";
		for (int i = startIndex; i < lines.length(); i++) {
			if (lines.charAt(i) == '{' && flagString == 1)
				countDelimiters++;
			else if (lines.charAt(i) == '}' && flagString == 1)
				countDelimiters--;
			else if (lines.charAt(i) == '"' && flagString == 1)
				flagString = 0;
			else if (lines.charAt(i) == '\'' && flagString == 1)
				flagString = -1;

			if (flagString == 1 && countDelimiters != 0)
				toReturn += lines.charAt(i);

			if ((flagString == 0 && lines.charAt(i - 1) != '\\' && lines
					.charAt(i) == '"')
					|| (flagString == -1 && lines.charAt(i - 1) != '\\' && lines
							.charAt(i) == '\''))
				flagString = 1;

			if (countDelimiters == 0)
				return toReturn;
		}
		return toReturn.trim();
	}

	/**
	 * Method that group parameters from an class and the attributes from one
	 * method, in a disjoint regex non-countable group.
	 * 
	 * @param className
	 *            Name of the class searched.
	 * @param methodName
	 *            Name of the method searched.
	 * @param lines
	 *            Code of the class searched.
	 * @return String of the form (?:var1|var2|...|varz)
	 */
	private String getParametersInGroup(String className, String methodName,
			String lines) {
		String toReturn = "(?:";
		for (String s : getParameterNames(className, methodName, lines)) {
			toReturn += s.trim() + "\\.\\w+|" + s.trim() + "|";
		}
		for (String s : FileUtil.getVariablesFromClass(className)) {
			toReturn += s.trim() + "\\.\\w+|" + s.trim() + "|";
		}
		if (toReturn == "(?:")
			return "";
		else
			return toReturn.substring(0, toReturn.length() - 1) + ")";
	}

	/**
	 * Checks if in one of the JML conditions there are an requires true, that
	 * being when there are an requires true; or requires (* ... *); (informal)
	 * or an Absence of requires, which by default are require true.
	 * 
	 * @param className
	 *            Name of the class searched.
	 * @param methodName
	 *            Name of the method searched.
	 * @return true if requires true were found or false.
	 */
	private boolean isRequiresTrue(String className, String methodName) {
		String lines = FileUtil.readFile(getPathFromFile(className));
		String precondition = conditionsJML(className, methodName,
				PRECONDITION_INDICATOR, lines);
		if (precondition == "")
			return true;
		String[] list = precondition.split(",");
		for (String string : list) {
			if (string.trim() == "true"
					|| string.trim().matches("\\(\\*.*\\*\\)"))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the constructor has any variable non-initialized, null value.
	 * 
	 * @param className
	 *            Name of the class searched.
	 * @return True, if it has some, or False.
	 */
	public boolean checkNull(String className) {
		String lines = FileUtil.readFile(getPathFromFile(className));
		String constructor = getConstructorCode(className, lines);
		ArrayList<String> varList = FileUtil.getVariablesFromClass(className);
		if (constructor == "" && varList.size() > 0) {
			return true;
		}
		for (String var : FileUtil.getVariablesFromClass(className)) {
			Pattern cond = Pattern.compile(var + "\\s*=\\s*(?!null\\b)[^=;]+;"
					+ "|this\\." + var + "\\s*=\\s*(?!null\\b)[^=;]+;");
			Matcher poss = cond.matcher(constructor);
			if (!poss.find())
				return true;
		}
		return false;
	}

	/**
	 * Auxiliary method used to get the class name only, without informations
	 * about packages from this class.
	 * 
	 * @param className
	 *            - the full name of this class (with information about
	 *            packages)
	 * @return the name of the class.
	 */
	private String getClassName(String className) {
		return className.substring(className.lastIndexOf(".") + 1,
				className.length());
	}

	/**
	 * Auxiliary method used to get all names of parameters from a method
	 * received as parameter.
	 * 
	 * @param className
	 *            - the name of the class that contains the method that will get
	 *            the names of parameters.
	 * @param methodName
	 *            - the name of the method whose parameters' names we want.
	 * @return the list of parameters' names from the method.
	 */
	private ArrayList<String> getParameterNames(String className,
			String methodName, String lines) {
		ArrayList<String> result = new ArrayList<String>();
		Pattern captureAtt = captureJMLFromAnMethodAndAtr(className, methodName);
		Matcher condAtr = captureAtt.matcher(lines);
		condAtr.find();
		String attributeDeclaration = condAtr.group(condAtr.groupCount());
		Matcher searchAtr = captureAttributes.matcher(attributeDeclaration);
		while (searchAtr.find()) {
			result.add(searchAtr.group(1));
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
	//	Examinator p = new Examinator("/home/quantus/git/jmlok-tool/src/");
		//p.setPrincipalClassName("sampleExample.Carro");
		//System.out.println(p.checkStrongPrecondition("Carro"));
	}
}
