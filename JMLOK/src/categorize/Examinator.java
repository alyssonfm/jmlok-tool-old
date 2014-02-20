package categorize;

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.openjml.Factory;
import org.jmlspecs.openjml.IAPI;
import org.jmlspecs.openjml.JmlToken;
import org.jmlspecs.openjml.JmlTree;
import org.jmlspecs.openjml.JmlTree.JmlClassDecl;
import org.jmlspecs.openjml.JmlTree.JmlMethodClause;
import org.jmlspecs.openjml.JmlTree.JmlMethodClauseExpr;
import org.jmlspecs.openjml.JmlTree.JmlMethodDecl;
import org.jmlspecs.openjml.JmlTree.JmlSingleton;

import utils.Constants;
import utils.FileUtil;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * Class used to do some operations on the AST that we use to 
 * determinate which is the likely cause for a nonconformance.
 * 
 * @author Alysson Milanez and Dennis Souza.
 * 
 */
public class Examinator {
	
	private String srcDir;
	private String principalClassName;
	private ArrayList<String> variables;
	
	public enum Operations {
		ATR_VAR_IN_PRECONDITION, REQUIRES_TRUE, ATR_MOD, NULL_RELATED 		
	}
	
	/**
	 * Constructs an PatternsTool object with the directory of the src project paste.
	 * 
	 * @param dir
	 *            directory of the src project paste
	 */
	public Examinator(String dir) {
		this.srcDir = dir;
	}
	/**
	 * Get the complete name of the principal class examined. 
	 * @return Complete name of the principal class examined.
	 */
	public String getPrincipalClassName() {
		return principalClassName;
	}
	/**
	 * Change the principal class examined from this object.
	 * @param principalClassName
	 */
	public void setPrincipalClassName(String principalClassName) {
		this.principalClassName = principalClassName;
		this.variables = FileUtil.getVariablesFromClass(principalClassName);
	}
	/**
	 * Method that returns the complete java path to class, whose class name was
	 * received as parameter.
	 * 
	 * @param className
	 *            - the name of the class.
	 * @return - the full path to the file.
	 */
	private String getJavaPathFromFile(String className) {
		String name = className.replace('.', '/');
		name += ".java";
		return srcDir + Constants.FILE_SEPARATOR + name;
	}
	/**
	 * Method that returns the complete jml path to class, whose class name was
	 * received as parameter.
	 * 
	 * @param className
	 *            - the name of the class.
	 * @return - the full path to the file.
	 */
	private String getJmlPathFromFile(String className) {
		String name = className.replace('.', '/');
		name += ".jml";
		return srcDir + Constants.FILE_SEPARATOR + name;
	}
	/**
	 * Method that add variables from superclass not added before to variables
	 * list of the class.
	 * @param classname Name of the class desired to update the variables from.
	 */
	private void updateVariables(String classname) {
		if(!classname.equals(this.getPrincipalClassName()))
			for(String s : FileUtil.getVariablesFromClass(classname))
				if(!this.variables.contains(s))
					this.variables.add(s);
	}
	/**
	 * Method that gets the class name and return it without the package names.
	 * @param className The name of the class.
	 * @return The class name without the package name.
	 */
	private String getOnlyClassName(String className){
		return className.substring(className.lastIndexOf(".") + 1);
	}
	/**
	 * Checks if the Precondition clauses from a method are too strong.
	 * @param methodName Name of the method studied.
	 * @return true if the Precondition clauses are too strong, or false.
	 */
	public boolean checkStrongPrecondition(String methodName){
		if(methodName.equals(getOnlyClassName(this.getPrincipalClassName())))
			methodName = "<init>";
		try {
			return examinePrecondition(this.getPrincipalClassName(), methodName, false, Operations.ATR_VAR_IN_PRECONDITION);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Realize some examinations on precondition clauses in a desired class,
	 * and its related class, like interfaces or superclasses. 
	 * 
	 * @param classname
	 *            name of the class searched
	 * @param methodname
	 *            name of the method searched
	 * @param typeOfExamination
	 * 			  operation desired to do on the method
	 * @return true if some attribute on the method declaration are located on
	 *         the precondition expression or false.
	 * @throws Exception When the API can't be created.
	 */
	private boolean examinePrecondition(String className, String methodName, boolean isJMLFile, Operations typeOfExamination) {
		JmlClassDecl ourClass = takeClassFromFile(getFileToInvestigate(className, isJMLFile), className);
		if(ourClass == null)
			return false;
		updateVariables(className);
		if(!isJMLFile && examineAllClassAssociated(className, methodName, typeOfExamination))
			return true;
		return examineMethods(takeMethodsFromClass(ourClass, methodName), typeOfExamination);
	}
	
	private boolean examineMethods(List<JmlMethodDecl> ourMethods, Operations typeOfExamination){
		if(ourMethods != null){
			for (JmlMethodDecl anMethod : ourMethods) {
				if (anMethod.cases != null){
					com.sun.tools.javac.util.List<JmlMethodClause> clauses = anMethod.cases.cases.head.clauses;
					for (com.sun.tools.javac.util.List<JmlMethodClause> traversing = clauses; !traversing.isEmpty(); traversing = traversing.tail) {
						if (traversing.head.token.equals(JmlToken.REQUIRES)) {
							switch (typeOfExamination) {
							case ATR_VAR_IN_PRECONDITION:
								if(isAtrAndVarInPrecondition(anMethod, traversing))
									return true;
								break;
							case REQUIRES_TRUE:
								if(requiresTrue(anMethod, traversing))
									return true;
								break;
							default:
								break;
							}
						}
					}
					if(typeOfExamination == Operations.REQUIRES_TRUE)
						return true;
				}else if(typeOfExamination == Operations.REQUIRES_TRUE){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean requiresTrue(JmlMethodDecl anMethod, com.sun.tools.javac.util.List<JmlMethodClause> traversing) {
		JCTree expression = ((JmlMethodClauseExpr) traversing.head).expression;
		return hasTrueValue(expression) != 0;
	}
	
	private int hasTrueValue(JCTree expression) {
		if(expression instanceof JCParens){
			if(hasTrueValue(((JCParens)expression).expr) != 0)
				return 1;
		}else if(expression instanceof JCLiteral){
			return (int) ((JCLiteral) expression).value;
		}else if(expression instanceof JmlSingleton){
			return 1;
		}else if(expression instanceof JCBinary){
			int rexp = hasTrueValue(((JCBinary) expression).rhs);
			int lexp = hasTrueValue(((JCBinary) expression).lhs);
			switch (((JCBinary) expression).getTag()) {
			case JCTree.OR:
				return ((rexp != 0) || (lexp != 0)) ? 1 : 0;
			case JCTree.AND:
				return ((rexp != 0) && (lexp != 0)) ? 1 : 0;
			case JCTree.EQ:
				return ((rexp != 0) == (lexp != 0)) ? 1 : 0;
			case JCTree.NE:
				return ((rexp != 0) != (lexp != 0)) ? 1 : 0;
			case JCTree.LT:
				return (rexp < lexp) ? 1 : 0;
			case JCTree.GT:
				return (rexp > lexp) ? 1 : 0;
			case JCTree.LE:
				return (rexp <= lexp) ? 1 : 0;
			case JCTree.GE:
				return (rexp >= lexp) ? 1 : 0;
			default:
				break;
			}
		}
		return 0;
	}
	private boolean isAtrAndVarInPrecondition(JmlMethodDecl anMethod, com.sun.tools.javac.util.List<JmlMethodClause> traversing){
		for (com.sun.tools.javac.util.List<JCVariableDecl> acessing = anMethod.params; 
				!acessing.isEmpty(); acessing = acessing.tail)
			if(verifyFieldsInClause(traversing, acessing.head.name.toString(), true))
				//this.violatedClause = traversing.toString();
				return true;
		for (String var : this.variables) 
			if(verifyFieldsInClause(traversing, var, false))
				//this.violatedClause = traversing.toString();
				return true;
		return false;
	}
	/**
	 * This method creates an File object desired with classname and type of the
	 * file, if it's .jml or .java file.
	 * @param classname Complete name of the class desired to examinate.
	 * @param isJMLFile If it's an .jml or .java file.
	 * @return The File created.
	 */
	private java.io.File getFileToInvestigate(String classname, boolean isJMLFile) {
		if(isJMLFile)
			return new java.io.File(getJmlPathFromFile(classname));
		else
			return new java.io.File(getJavaPathFromFile(classname));
	}
	/**
	 * Take the Class desired from an file or return null.
	 * @param f File to search.
	 * @param className Name of the class to be searched.
	 * @return null if none class was found, otherwise the class founded.
	 */
	private JmlClassDecl takeClassFromFile(java.io.File f, String className){
		IAPI app;
		try {
			if(!f.exists())
				throw new Exception("The File acessed " + f.getName() + "does not exist.");
			app = Factory.makeAPI();
			List<JmlTree.JmlCompilationUnit> ast = app.parseFiles(f);
			com.sun.tools.javac.util.List<JCTree> acesser;
			JmlTree.JmlClassDecl ourClass = null;
			for (acesser = ast.get(0).defs; !acesser.isEmpty(); acesser = acesser.tail)
				if(acesser.head.getKind().equals(Tree.Kind.CLASS) || acesser.head.getKind().equals(Tree.Kind.INTERFACE)){
					ourClass = (JmlClassDecl) acesser.head;
					if(ourClass.name.toString().equals(getOnlyClassName(className)))
						return ourClass;
				}
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * Take all methods with the same name desired from an class, and return listed.
	 * @param clazz The class to walk.
	 * @param methodName The name of the method desired.
	 * @return An list of methods with the name desired.
	 */
	private List<JmlTree.JmlMethodDecl> takeMethodsFromClass(JmlTree.JmlClassDecl clazz, String methodName){
		List<JmlTree.JmlMethodDecl> methods = new ArrayList<JmlTree.JmlMethodDecl>();
		com.sun.tools.javac.util.List<JCTree> traverser;
		for (traverser = clazz.defs; !traverser.isEmpty(); traverser = traverser.tail)
			if(traverser.head.getKind().equals(Tree.Kind.METHOD) 
			   && ((JmlMethodDecl)traverser.head).name.toString().equals(methodName)){
				methods.add((JmlMethodDecl) traverser.head);
			}
		return methods;
	}
	/**
	 * Do the operations from examinePrecondition in all interfaces, superclasses
	 * or .jml files associated with the .java who called it.
	 * @param className The name of the class who called it.
	 * @param methodName The method searched;
	 * @param typeOfExamination If its RequiresTrue ou itsAtrVarInPrecondition
	 * @return true if it in any of the associated, they got one field in the desired clause.
	 */
	private boolean examineAllClassAssociated(String className, String methodName, Operations typeOfExamination) {
		ArrayList<String> interfacesOfClass = FileUtil.getInterfacesPathFromClass(className);
		if(!interfacesOfClass.isEmpty())
			for (String i : interfacesOfClass)
				if(examinePrecondition(i, methodName, false, typeOfExamination))
					return true;
		String superClassOfClass = FileUtil.getSuperclassPathFromClass(className, srcDir);
		if(!(superClassOfClass == ""))
			if(examinePrecondition(superClassOfClass, methodName, false, typeOfExamination))
				return true;
		if(examinePrecondition(className, methodName, true, typeOfExamination))
			return true;
		return false;
	}
	/**
	 * Verify all components of the Method Clause, if it's Binary or Ident type, that there are at least
	 * one field equals to the String toTest given.
	 * @param traversing Method Clause to verify.
	 * @param toTest Name of the field to compare.
	 * @param isFieldParameterOfMethod true, if the type of the field are just methods parameters or false. 
	 * @return true if it was found any component of the clause equal the name of the field. 
	 */
	private boolean verifyFieldsInClause(com.sun.tools.javac.util.List<JmlMethodClause> traversing, String toTest, boolean isFieldParameterOfMethod) {
		if (isThereInMiniTree(toTest,((JmlMethodClauseExpr) traversing.head).expression))
			return true;
		if(!isFieldParameterOfMethod && (isThereInMiniTree("this." + toTest,((JmlMethodClauseExpr) traversing.head).expression)
									  || isThereInMiniTree(toTest, ((JmlMethodClauseExpr) traversing.head).expression)))
			return true;
		return false;
	}
	/**
	 * This function walks into a tree with nodes of type JCBinary to search for 
	 * a string as one of the elements of the tree.
	 * @param toTest The String searched as Element in the tree.
	 * @param expression The bifurcation tree searched.
	 * @return True if the string was found, or False.
	 */
	private boolean isThereInMiniTree(String toTest, JCTree expression) {
		if(expression instanceof JCParens){
			if(isThereInMiniTree(toTest, ((JCParens) expression).expr))
				return true;
		} else if(expression instanceof JCIdent){
			if(((JCIdent) expression).name.toString().equals(toTest))
				return true;
		} else if(expression instanceof JCBinary){
			if(isThereInMiniTree(toTest, ((JCBinary) expression).rhs))
				return true;
			if(isThereInMiniTree(toTest, ((JCBinary) expression).lhs))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the Precondition clauses from a method are too weak.
	 * @param methodName Name of the method studied.
	 * @return true if the Precondition clauses are too weak, false otherwise.
	 */
	public boolean checkWeakPrecondition(String methodName) {
		if(isRequiresTrue(methodName)) return true;
		if(isAttModifiedOnMethod(methodName)) return true;
		return false;
	}
	
	/**
	 * Method that checks if there are some parameter of the method modified in
	 * the method body.
	 * @param methodName the name of the method that contains a nonconformance.
	 * @return true if there are some parameter modified, false otherwise.
	 */
	private boolean isAttModifiedOnMethod(String methodName) {
		return false;
	}
	
	/**
	 * Checks if in one of the JML conditions there are an requires true, that
	 * being when there are an requires true; or requires (* ... *); (informal)
	 * or an Absence of requires, which by default are require true.
	 * @param methodName Name of the method searched.
	 * @return true if requires true were found, false otherwise.
	 */
	private boolean isRequiresTrue(String methodName) {
		return false;
	}
}
