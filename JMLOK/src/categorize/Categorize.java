package categorize;

import java.util.HashSet;
import java.util.Set;

import detect.Detect;
import detect.TestError;

/**
 * Class used to categorize the nonconformances discovered into the SUT.
 * @author Alysson Milanez and Dennis Souza.
 *
 */
public class Categorize {

	private Examinator examine; 
	
	/**
	 * Method that receives the set of nonconformances, and the source folder and 
	 * returns a set of nonconformances with category and likely cause. This is the principal method of the Categorize module, 
	 * because in this method we categorize all nonconformances discovered in Detect module.
	 * @param errors - the set of nonconformances detected by the Detect module.
	 * @param sourceFolder - the source folder of the SUT.
	 * @return a set of nonconformances with categories and likely causes.
	 */
	public Set<Nonconformance> categorize(Set<TestError> errors, String sourceFolder){
		Set<Nonconformance> nonconformances = new HashSet<Nonconformance>();
		this.examine = new Examinator(sourceFolder);
		for(TestError te : errors){
			Nonconformance n = new Nonconformance();
			switch (te.getType()) {
			case CategoryName.PRECONDITION:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Precondition());
				n.setTest(te.getName());
				n.setCause(categorizePrecondition(te, sourceFolder));
				n.setTestFile(te.getTestFile());
				n.setLinesFromTestFile(te.getTestFile(), te.getNumberRevealsNC());
				nonconformances.add(n);
				break;
				
			case CategoryName.POSTCONDITION:
				n.setClassName(te.getClassName());
				n.setMethodName(te.getMethodName());
				n.setPackageName(te.getPackageName());
				n.setType(new Postcondition());
				n.setTest(te.getName());
				n.setCause(categorizePostcondition(te, sourceFolder));
				n.setTestFile(te.getTestFile());
				n.setLinesFromTestFile(te.getTestFile(), te.getNumberRevealsNC());
				nonconformances.add(n);
				break;

			case CategoryName.INVARIANT:
				/*
				n.setType(new Invariant());
				n.setTest(te.getName());
				n.setCause(categorizeInvariant(te, sourceFolder));
				n.setTestFile(te.getTestFile());
				nonconformances.add(n);
				*/
				break;
				
			case CategoryName.CONSTRAINT:
				/*
				n.setType(new Constraint());
				n.setTest(te.getName());
				n.setCause(categorizeConstraint(te, sourceFolder));
				n.setTestFile(te.getTestFile());
				nonconformances.add(n);
				*/
				break;
				
			case CategoryName.EVALUATION:
				/*
				n.setCause(categorizeEvaluation(te, sourceFolder));
				n.setTest(te.getName());
				n.setType(new Evaluation());
				n.setTestFile(te.getTestFile());
				nonconformances.add(n);
				*/
				break;
				
			default:
				break;
			}
		}
		return nonconformances;
	}
	/**
	 *Method that returns a likely cause for a nonconformance of precondition. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this precondition error.
	 */
	private String categorizePrecondition(TestError e, String sourceFolder){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		if(this.examine.checkStrongPrecondition(e.getMethodName())) 
			return Cause.STRONG_PRE;
		else 
			return Cause.WEAK_POST;
	}
	
	/**
	 *Method that returns a likely cause for a nonconformance of postcondition. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this postcondition error.
	 */
	private String categorizePostcondition(TestError e, String sourceFolder){
		if(e.getPackageName() == "")
			this.examine.setPrincipalClassName(e.getClassName());
		else
			this.examine.setPrincipalClassName(e.getPackageName() + "." + e.getClassName());
		if(this.examine.checkWeakPrecondition(e.getMethodName()))
			return Cause.WEAK_PRE;
		else
			return Cause.STRONG_POST;
	}
	
	/**
	 *Method that returns a likely cause for a nonconformance of invariant. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this invariant error.
	 */
	private String categorizeInvariant(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkNull(e.getClassName())) 
			result = Cause.NULL_RELATED;
		else if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName())) 
			result = Cause.WEAK_PRE;
		else 
			result = Cause.STRONG_INV;
		return result;
	}
	
	/**
	 *Method that returns a likely cause for a nonconformance of history constraint. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this history constraint error.
	 */
	private String categorizeConstraint(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkNull(e.getClassName())) 
			result = Cause.NULL_RELATED;
		else if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName())) 
			result = Cause.WEAK_PRE;
		else 
			result = Cause.STRONG_CONST;
		return result;
	}
	
	/**
	 *Method that returns a likely cause for a nonconformance of evaluation. Receives a test error - the nonconformance - and 
	 * the source folder that contains the class that has a nonconformance.
	 * @param e - the nonconformance
	 * @param sourceFolder - the folder that contains the class with a nonconformance.
	 * @return the string that corresponds the likely cause for this evaluation error.
	 */
	private String categorizeEvaluation(TestError e, String sourceFolder){
		String result = "";
		PatternsTool p = new PatternsTool(sourceFolder);
		if(p.checkWeakPrecondition(e.getClassName(), e.getMethodName())) 
			result = Cause.WEAK_PRE;
		else 
			result = Cause.STRONG_POST;
		return result;
	}
	
}
