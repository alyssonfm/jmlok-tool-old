package categorize;

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
	
	
	public Nonconformance() {
	}
	
	public Category getType() {
		return type;
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
}
