package categorize;


public class Precondition implements Category{

	@Override
	public String causeToString() {
		return Cause.STRONG_PRE;
	}

	@Override
	public String getType() {
		return CategoryName.PRECONDITION;
	}

}
