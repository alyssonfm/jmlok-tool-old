package categorize;

import java.util.HashSet;
import java.util.Set;

import utils.Constants;
import detect.Detect;
import detect.TestError;

public class Categorize {

	public static Set<Nonconformance> categorize(String sourceFolder, String libFolder, String timeout, int compiler){
		Set<Nonconformance> nonconformances = new HashSet<Nonconformance>();
		Nonconformance n = new Nonconformance();
		Detect d = new Detect(compiler);
		Set<TestError> errors = d.detect(sourceFolder, libFolder, timeout);
		for(TestError te : errors){
			switch (te.getType()) {
			case CategoryName.PRECONDITION:
				n.setType(new Precondition());
				n.setTest(te.getName());
				n.setCause(categorizePrecondition(te, sourceFolder));
				nonconformances.add(n);
				break;
				
			case CategoryName.POSTCONDITION:
				n.setType(new Postcondition());
				n.setTest(te.getName());
				n.setCause(categorizePostcondition(te, sourceFolder));
				break;

			case CategoryName.INVARIANT:
				n.setType(new Invariant());
				n.setTest(te.getName());
				n.setCause(categorizeInvariant(te, sourceFolder));
				break;
				
			case CategoryName.CONSTRAINT:
				n.setType(new Constraint());
				n.setTest(te.getName());
				n.setCause(categorizeConstraint(te, sourceFolder));
				break;
				
			case CategoryName.EVALUATION:
				n.setCause(categorizeEvaluation(te, sourceFolder));
				n.setTest(te.getName());
				n.setType(new Evaluation());
				break;
				
			default:
				break;
			}
		}
		return nonconformances;
	}
	/**
	 * Method that adds a likely cause for a nonconformance of precondition. Receives a test error - the nonconformance - and the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this precondition error.
	 */
	private static String categorizePrecondition(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.isAtrInPrecondition(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.STRONG_PRE;
		else if(p.isVariableInPrecondition(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.STRONG_PRE;
		else result = Cause.WEAK_POST;
		return result;
	}
	
	private static String categorizePostcondition(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.WEAK_PRE;
		else result = Cause.STRONG_POST;
		return result;
	}
	
	private static String categorizeInvariant(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkNull(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.NULL_RELATED;
		else if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.WEAK_PRE;
		else result = Cause.STRONG_INV;
		return result;
	}
	
	private static String categorizeConstraint(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkNull(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.NULL_RELATED;
		else if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName(), sourceFolder)) result = Cause.WEAK_PRE;
		else result = Cause.STRONG_CONST;
		return result;
	}
	
	private static String categorizeEvaluation(TestError e, String sourceFolder){
		String result = "This an expression that "+Cause.NOT_EVAL_EXP;
		return result;
	}
	
	public static void main(String[] args) {
		//categorize("C:\\Car", "", "1", Constants.JMLC_COMPILER);
		// eh bom sempre comentarmos os codigos de teste antes de fazer push e commit.
	}
}
