package categorize;

import java.util.HashSet;
import java.util.Set;

import detect.Detect;
import detect.TestError;

/**
 * Class used to categorize the nonconformances discovered into the SUT.
 * @author Alysson Milanez
 *
 */
public class Categorize {

	/**
	 * Method that receives the source folder, the library folder, the timeout and the compiler and 
	 * returns a set of nonconformances with category and likely cause. This is the principal method of the Categorize module, 
	 * because in this method we categorize all nonconformances discovered in Detect module.
	 * @param sourceFolder - the source folder of the SUT.
	 * @param libFolder - the external libraries needed for the current SUT.
	 * @param timeout - the time used to tests generation - for the tests generation.
	 * @param compiler - the compiler that will be used for conformance check of this SUT.
	 * @return a set of nonconformances with categories and likely causes.
	 */
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
				nonconformances.add(n);
				break;

			case CategoryName.INVARIANT:
				n.setType(new Invariant());
				n.setTest(te.getName());
				n.setCause(categorizeInvariant(te, sourceFolder));
				nonconformances.add(n);
				break;
				
			case CategoryName.CONSTRAINT:
				n.setType(new Constraint());
				n.setTest(te.getName());
				n.setCause(categorizeConstraint(te, sourceFolder));
				nonconformances.add(n);
				break;
				
			case CategoryName.EVALUATION:
				n.setCause(categorizeEvaluation(te, sourceFolder));
				n.setTest(te.getName());
				n.setType(new Evaluation());
				nonconformances.add(n);
				break;
				
			default:
				break;
			}
		}
		return nonconformances;
	}
	/**
	 * Method that adds a likely cause for a nonconformance of precondition. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this precondition error.
	 */
	private static String categorizePrecondition(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.isAtrInPrecondition(e.getClassName(), e.getMethodName())) result = Cause.STRONG_PRE;
		else if(p.isVariableInPrecondition(e.getClassName(), e.getMethodName())) result = Cause.STRONG_PRE;
		else result = Cause.WEAK_POST;
		return result;
	}
	
	/**
	 * Method that adds a likely cause for a nonconformance of postcondition. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this postcondition error.
	 */
	private static String categorizePostcondition(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName())) result = Cause.WEAK_PRE;
		else result = Cause.STRONG_POST;
		return result;
	}
	
	/**
	 * Method that adds a likely cause for a nonconformance of invariant. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this invariant error.
	 */
	private static String categorizeInvariant(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkNull(e.getClassName(), e.getMethodName())) result = Cause.NULL_RELATED;
		else if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName())) result = Cause.WEAK_PRE;
		else result = Cause.STRONG_INV;
		return result;
	}
	
	/**
	 * Method that adds a likely cause for a nonconformance of history constraint. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this history constraint error.
	 */
	private static String categorizeConstraint(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkNull(e.getClassName(), e.getMethodName())) result = Cause.NULL_RELATED;
		else if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName())) result = Cause.WEAK_PRE;
		else result = Cause.STRONG_CONST;
		return result;
	}
	
	/**
	 * Method that adds a likely cause for a nonconformance of evaluation. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this evaluation error.
	 */
	private static String categorizeEvaluation(TestError e, String sourceFolder){
		String result = "This an expression that "+Cause.NOT_EVAL_EXP;
		return result;
	}
	
	public static void main(String[] args) {
		//System.out.println(categorizePrecondition(new TestError("t", "by method sample.Carro.g", "jmlrac Precondition error"), "C:\\users\\Alysson\\Desktop"));		
		// eh bom sempre comentarmos os codigos de teste antes de fazer push e commit.
	}
}
