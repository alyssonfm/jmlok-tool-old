package categorize;

/**
 * Class that represents a nonconformance.
 * @author Alysson
 *
 */
public class Nonconformance {

	private Category type;
	private String cause;
	private String test;
	
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
}
