package categorize;

public class Evaluation implements Category{

	public String causeToString() {
		return Cause.NOT_EVAL_EXP;
	}

	public String getType() {
		return CategoryName.EVALUATION;
	}

}
