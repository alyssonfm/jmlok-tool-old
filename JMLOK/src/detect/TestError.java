package detect;

import categorize.CategoryName;

/**
 * Class to manipulate the test errors resulting in the current project
 * @author Alysson Milanez
 * @version 1.0
 */
public class TestError {
	private String type;
	private String message;
	private String name = "";
	private boolean jmlRac = true;
	private boolean meaningless = false;

	/**
	 * The constructor of this class, receives a name, a message and a error type to the test.
	 * @param name = the name of current test.
	 * @param message = the message of current test error.
	 * @param errorType = the type of error occurred.
	 */
	public TestError(String name, String message, String type) {
		this.setMessage(message);
		this.setTypeJmlc(type);
		this.setName(name);
	}
	
	/**
	 * The alternative constructor of this class. Takes only two arguments as parameter.
	 * @param message - the message of the error.
	 * @param type - the type of the error.
	 */
	public TestError(String message, String type){
		this.setMessage(message);
		this.setTypeOpenjml(type);
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
	 * Method to set the type of error occurred.
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
		} else if (type.contains("Evaluation")) {
			this.type = CategoryName.EVALUATION;
		} else if (type.contains("Constraint")) {
			this.type = CategoryName.CONSTRAINT;
		}
	}
	
	public void setTypeOpenjml(String type){
		if (type.contains("invariant")) {
			this.type = CategoryName.INVARIANT;
		} else if (type.contains("postcondition")) {
			this.type = CategoryName.POSTCONDITION;
		} else if (type.contains("precondition")) {
			if (type.contains("Entry")) {
				meaningless = true;
				this.type = CategoryName.MEANINGLESS;
			} else
				this.type = CategoryName.PRECONDITION;
		} else if (type.contains("evaluation")) {
			this.type = CategoryName.EVALUATION;
		} else if (type.contains("constraint")) {
			this.type = CategoryName.CONSTRAINT;
		}
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
	 * Method that returns if the error is a JML RAC error or no.
	 * @return the boolean that informs if the error is a JML RAC error.
	 */
	public boolean isJmlRac() {
		return this.jmlRac;
	}

	/**
	 * Method that returns if the error is a meaningless error or no.
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
	 * Method that returns the name of current test error.
	 */
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if((obj instanceof TestError) 
				&& ((TestError) obj).getMessage().equalsIgnoreCase(this.getMessage()) 
				&& ((TestError) obj).getType().equalsIgnoreCase(this.getType()))
		{ 
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getMessage().length()+getType().length();
	}
	
	

}
