package categorize;

import java.io.IOException;

import utils.Constants;
import utils.FileUtil;

/**
 * Class that represents a nonconformance.
 * @author Alysson Milanez and Dennis Souza.
 *
 */
public class Nonconformance {

	private Category type;
	private String cause = "";
	private String test = "";
	private String testFile = "";
	private String className = "";
	private String methodName = "";
	private String packageName = "";
	private String linesFromTestFile = "";
	private String methodCalling = "";
	private String message;
	
	public Nonconformance() {
	}
	
	public String getType() {
		return type.getType();
	}

	public void setType(Category type) {
		this.type = type;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTestFile() {
		return testFile;
	}

	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getLinesFromTestFile() {
		return linesFromTestFile;
	}

	public void setLinesFromTestFile(String testFile, int specifiedLine) {
		try {
			this.linesFromTestFile = FileUtil.testCaseContent(testFile, specifiedLine);
		} catch (IOException e) {
			this.linesFromTestFile = "Error when reading the test files.";
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if((obj instanceof Nonconformance) 
				&& ((Nonconformance) obj).getType().equals(this.getType()) 
				&& ((Nonconformance) obj).getCause().equalsIgnoreCase(this.getCause())
				&& ((Nonconformance) obj).getTest().equalsIgnoreCase(this.getTest())
				&& ((Nonconformance) obj).getTestFile().equalsIgnoreCase(this.getTestFile())
				&& ((Nonconformance) obj).getClassName().equalsIgnoreCase(this.getClassName())
				&& ((Nonconformance) obj).getMethodName().equalsIgnoreCase(this.getMethodName())
				&& ((Nonconformance) obj).getPackageName().equalsIgnoreCase(this.getPackageName()))
		{ 
			return true;
		} else {
			return false;
	
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cause == null) ? 0 : cause.hashCode());
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result
				+ ((testFile == null) ? 0 : testFile.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public void setMethodCalling(int lineOfErrorInJava, String sourceFolder) {
		if(lineOfErrorInJava == -1)
			this.methodCalling = "";
		else if(lineOfErrorInJava == 0)
			this.methodCalling = "\"\\o/, Do not find me.\"";
		else{
			String name = (this.packageName + "." + this.className).replace('.', '/');
			name += ".java";
		    this.methodCalling = FileUtil.readSingleLineOfFile(sourceFolder + Constants.FILE_SEPARATOR + name, lineOfErrorInJava);
		    int temp = this.methodCalling.lastIndexOf("{");
		    if(temp != -1)
		    	this.methodCalling = this.methodCalling.substring(0, this.methodCalling.lastIndexOf("{")).trim();
		}
	}

	public String getMethodCalling() {
		return this.methodCalling;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
