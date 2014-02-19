package detect;

import categorize.CategoryName;

/**
 * Class to manipulate the test errors resulting in the current project.
 * @author Alysson Milanez and Dennis Souza
 * @version 1.0
 */
public class TestError {
	private String type = "";
	private String message = "";
	private String className = "";
	private String methodName = "";
	private String packageName = "";
	private String name = "";
	private String testFile = "";
	private boolean jmlRac = true;
	private boolean meaningless = false;
	private int numberRevealsNC = 0;

	/**
	 * The constructor of this class, receives a name, a message and a type error to the test.
	 * @param name = the name of current test.
	 * @param testFile = the name of the .java used for finding this test error.
	 * @param message = the message of current test error.
	 * @param type = the type of error occurred.
	 * @param details = more detailed info about the test error.
	 */
	public TestError(String name, String testFile, String message, String type, String details) {
		this.setTypeJmlc(type);
		this.setMessage(message);
		this.setClassName();
		this.setTestFile(testFile);
		this.setMethodName();
		this.setName(name);
		this.setPackage(details);
		this.setNumberRevealsNC(details);
	}

	/**
	 * The alternative constructor of this class. Takes only two arguments as parameter.
	 * @param message - the message of the error.
	 * @param type - the type of the error.
	 */
	public TestError(String message, String type){
		this.setTypeOpenjml(type);
		this.setMessage(message);
		this.setClassName();
		this.setMethodName();
	}
	
	/**
	 * Method that returns the test name.
	 * @return = the name of the current test.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method used to set the name of current test.
	 * @param name = the new name of the current test.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method used to set the type of nonconformance discovered using the jmlc compiler.
	 * @param type = the type resulting of compiler message error.
	 */
	public void setTypeJmlc(String type) {
		if (!type.contains("jmlrac")) {
			jmlRac = false;
			this.type = "";
		}
		if (type.contains("Invariant")) {
			this.type = CategoryName.INVARIANT;
		} else if (type.contains("Postcondition")) {
			this.type = CategoryName.POSTCONDITION;
		} else if (type.contains("Precondition")) {
			if (type.contains("Entry")) {
				meaningless = true;
				this.type = CategoryName.MEANINGLESS;
			} else
				this.type = CategoryName.PRECONDITION;
		} else if (type.contains("Constraint")) {
			this.type = CategoryName.CONSTRAINT;
		} else this.type = CategoryName.EVALUATION;
	}
	
	/**
	 * Method used to set the type of nonconformance discovered using the openjml compiler.
	 * @param type = the type resulting of compiler message error.
	 */
	public void setTypeOpenjml(String type){
		if (type.toLowerCase().contains("invariant")) {
			this.type = CategoryName.INVARIANT;
		} else if (type.toLowerCase().contains("postcondition")) {
			this.type = CategoryName.POSTCONDITION;
		} else if (type.toLowerCase().contains("precondition")) {
			this.type = CategoryName.PRECONDITION;
		} else if (type.toLowerCase().contains("constraint")) {
			this.type = CategoryName.CONSTRAINT;
		} else this.type = CategoryName.EVALUATION;
	}

	/**
	 * Method that returns the type of current error.
	 * @return = the error of current test case.
	 */
	public String getType() {
		return type;
	}
	/**
	 * Method that returns if the current error is a nonconformance.
	 * @return = if current error is a nonconformance. 
	 */
	public boolean isNonconformance(){
		return (isJmlRac() && !isMeaningless());
	}

	/**
	 * Method that returns if the error is or isn't a JML RAC error.
	 * @return the boolean that informs if the error is a JML RAC error.
	 */
	public boolean isJmlRac() {
		return this.jmlRac;
	}

	/**
	 * Method that returns if the error is or isn't a meaningless error.
	 * @return the boolean that informs if the error is a meaningless error.
	 */
	public boolean isMeaningless() {
		return this.meaningless;
	}

	/**
	 * Method used to set the message corresponding at the current test error.
	 * @param message = the message corresponding at the test error.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Method that returns the message of current test error.
	 * @return the message of test error.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Method that returns the String that corresponds to the current test error.
	 */
	public String toString() {
		return name + ", "+message+", type: "+type;
	}
	
	/**
	 * Method that sets the class name for the current test error.
	 */
	public void setClassName(){
		String result = "";
		String aux = this.message;
		String[] text = aux.split(" ");
		if(this.type.equals(CategoryName.EVALUATION)) result = text[3].substring(text[3].indexOf(";")+1, text[3].indexOf(".java"));
		result = text[2].substring(0, text[2].indexOf("."));
		this.className = result;
	}
	
	/**
	 * Method that returns the class name for the current test error.
	 * @return - the string corresponding to the class name with nonconformance.
	 */
	public String getClassName(){
		return this.className;
	}
	
	
	/**
	 * Method that sets the method name for the current test error.
	 */
	public void setMethodName(){
		String result = "";
		String aux = this.message;
		String[] text = aux.split(" ");
		if (this.type.equals(CategoryName.PRECONDITION) || this.type.equals(CategoryName.POSTCONDITION)) {
			result = text[2].substring(text[2].indexOf(".")+1, text[2].length());
		} else if(this.type.equals(CategoryName.INVARIANT)){
			if(text[2].contains("init")) result = getClassName();
			result = text[2].substring(text[2].lastIndexOf(".")+1, text[2].indexOf("@"));
		} else if(this.type.equals(CategoryName.CONSTRAINT)){
			result = text[2].substring(text[2].lastIndexOf(".")+1, text[2].indexOf("@"));
		}
		this.methodName = result;
	}
	
	/**
	 * Method that returns the method name for the current test error.
	 * @return - the string corresponding to the method name with nonconformance.
	 */
	public String getMethodName(){
		return this.methodName;
	}
	
	/**
	 * Method that defines the complete package name of the Class tested for the current test error.
	 * @param details It's the string containing more detailed info about the test case.
	 */
	public void setPackage(String details) {
		int firstIndex = details.indexOf("at ");
		int lastIndex = details.indexOf("." + this.className + ".");
		if(lastIndex != -1)
			this.packageName = details.substring(firstIndex + 3, lastIndex);
	}

	/**
	 * Method that returns the full package name for the current test error.
	 * @return Package name for the current test error.
	 */
	public String getPackageName(){
		return this.packageName;
	}
	
	/**
	 * Method that returns the name of the java file that contains the current test case.
	 * @return the name of the java file that contains the current test case.
	 */
	public String getTestFile() {
		return testFile;
	}

	/**
	 * Method that sets the name of the java file that contains the current test case.
	 * @param testFile the name of the java file.
	 */
	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	/**
	 * Method that returns the number of line where current error was found.
	 * @return Line where current test Error was found in the Test File.
	 */
	public int getNumberRevealsNC() {
		return this.numberRevealsNC;
	}
	
	/**
	 * Method that sets the line where current error was found.
	 * @param details The details from error, where line was specified.
	 */
	public void setNumberRevealsNC(String details) {
		int firstIndex = details.lastIndexOf(".java:");
		Integer aux = new Integer(details.substring(firstIndex+6, details.lastIndexOf(")"))); 
		this.numberRevealsNC = aux.intValue();
	}	

	@Override
	public boolean equals(Object obj) {
		if((obj instanceof TestError) 
				&& ((TestError) obj).getType().equalsIgnoreCase(this.getType()) 
				&& ((TestError) obj).getClassName().equalsIgnoreCase(this.getClassName())
				&& ((TestError) obj).getMethodName().equalsIgnoreCase(this.getMethodName())
				&& ((TestError) obj).getPackageName().equalsIgnoreCase(this.getPackageName()))
		{ 
			return true;
		} else {
			return false;
	
		}
	}

	@Override
	public int hashCode() {
		return getType().length()+getMethodName().length()+getClassName().length();
	}

}
