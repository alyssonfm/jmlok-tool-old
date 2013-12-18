package categorize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsTool {
	// Used to read and group Class and method detected in
	public Pattern captureClassMethodInPrePos = Pattern
			.compile("([A-Z][\\w]*)[\\.]([\\w]+)");
	// Used to capture attributes from an method declaration
	public Pattern captureAttributes = Pattern
			.compile("\\s*[a-zA-Z]\\w*\\s+(\\w+)\\s*,?");
	public boolean isAtrInPrecondition(String classname, String methodname,
			File code) throws FileNotFoundException {
		// First, capture Precondition and the Attributes declaration from the
		// method desired
		Pattern capturePreAtr = Pattern.compile(
				"//@\\s*(?:requires|pre)(.*);[.\\s]*"
						+ "(?:private|public|protected)\\s+[\\w]+\\s+"
						+ methodname + "\\s*\\((.*)\\)\\{", Pattern.DOTALL);
		FileReader fr = new FileReader(code);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		try {
			while (br.ready()) {
				sb.append(br.readLine() + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Matcher condAtr = capturePreAtr.matcher(sb.toString());
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

	public boolean isAtrInPoscondition(String classname, String methodname,
			File code) {
		// First, capture Precondition and the Attributes declaration from the
		// method desired
		Pattern capturePreAtr = Pattern.compile("public\\s+class\\s+"
				+ classname + "\\s*{" + ".*//@\\s*(?ensures|pos)(.*);"
				+ ".*(?private|public|protected)\\s+[\\w]+\\s+" + methodname
				+ "\\s*\\((.*)\\){");
		Matcher condAtr = capturePreAtr.matcher(code.toString());
		String poscondition = condAtr.group();
		String attributeDeclaration = condAtr.group();
		Matcher searchAtr = captureAttributes.matcher(attributeDeclaration);
		while (searchAtr.find())
			if (poscondition.matches("[^\\w]" + searchAtr.group() + "[^\\w]"))
				return true;

		return false;
	}

	public static void main(String[] args) {
		PatternsTool p = new PatternsTool();
		try {
			System.out
					.println(p
							.isAtrInPrecondition(
									"Carro",
									"g",
									new File(
											"/home/quantus/useful_paste/sampleExample/sampleExample/Carro.java")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}