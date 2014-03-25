package categorize;

/**
 * An category of nonconformance, the Constraint Error.
 * @author Alysson
 *
 */
public class Constraint implements Category{

	public String getType() {
		return CategoryName.CONSTRAINT;
	}

}
