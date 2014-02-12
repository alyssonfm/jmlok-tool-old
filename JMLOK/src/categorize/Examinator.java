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

import utils.Constants;
import utils.FileUtil;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCIdent;
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
	 * @return Complete name of the principal classe examined.
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
	 * Checks if the Precondition clauses from an method are too strong.
	 * @param methodName Name of the method studied.
	 * @return true if the Precondition clauses are too strong, or false.
	 */
	public boolean checkStrongPrecondition(String methodName){
		try {
			return isAtrAndVarInPrecondition(this.getPrincipalClassName(), methodName, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Checks if there is an attribute or a variable from the class on 
	 * the method declaration that is on precondition specification.
	 * 
	 * @param classname
	 *            name of the class searched
	 * @param methodname
	 *            name of the method searched
	 * @return true if some attribute on the method declaration are located on
	 *         the precondition expression or false.
	 * @throws Exception When the API can't be created.
	 */
	private boolean isAtrAndVarInPrecondition(String className, String methodName, boolean isJMLFile) {
		java.io.File file = getFileToInvestigate(className, isJMLFile);
		if(!file.exists())
			return false;
		JmlClassDecl ourClass = takeClassFromFile(file, className);
		if(ourClass == null)
			return false;
		if(methodName.equals(getOnlyClassName(className)))
			methodName = "<init>";
		updateVariables(className);
		List<JmlTree.JmlMethodDecl> ourMethods = takeMethodsFromClass(ourClass, methodName);
		if(!isJMLFile && examineAllClassAssociated(className, methodName))
			return true;
		if(ourMethods == null)
			return false;
		for (JmlMethodDecl anMethod : ourMethods) {
			if (anMethod.cases != null)	
				for (com.sun.tools.javac.util.List<JmlMethodClause> traversing = anMethod.cases.cases.head.clauses; 
						!traversing.isEmpty(); traversing = traversing.tail) {
					if (traversing.head.token.equals(JmlToken.REQUIRES)) {
						for (com.sun.tools.javac.util.List<JCVariableDecl> acessing = anMethod.params; 
								!acessing.isEmpty(); acessing = acessing.tail)
							if(verifyFieldsInClause(traversing, acessing.head.name.toString(), true))
								return true;
						for (String var : this.variables) 
							if(verifyFieldsInClause(traversing, var, false))
								return true;
					}
				}
		}
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
			e.printStackTrace();
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
	 * Do the operations from isAtrAndVarInPrecondition in all interfaces, superclasses
	 * or .jml files associated with the .java who called it.
	 * @param className The name of the class who called it.
	 * @param methodName The method searched;
	 * @return true if it in any of the associated, they got one field in the desired clause.
	 */
	private boolean examineAllClassAssociated(String className, String methodName) {
		ArrayList<String> interfacesOfClass = FileUtil.getInterfacesPathFromClass(className);
		if(!interfacesOfClass.isEmpty())
			for (String i : interfacesOfClass)
				if(isAtrAndVarInPrecondition(i, methodName, false))
					return true;
		String superClassOfClass = FileUtil.getSuperclassPathFromClass(className, srcDir);
		if(!(superClassOfClass == ""))
			if(isAtrAndVarInPrecondition(superClassOfClass, methodName, false))
				return true;
		if(isAtrAndVarInPrecondition(className, methodName, true))
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
		if (((JmlTree.JmlMethodClauseExpr) traversing.head).expression instanceof JCBinary) {
			if (isThereInMiniTree(toTest,(JCBinary) ((JmlMethodClauseExpr) traversing.head).expression))
				return true;
			if(!isFieldParameterOfMethod && isThereInMiniTree("this." + toTest,	(JCBinary) ((JmlTree.JmlMethodClauseExpr) traversing.head).expression))
				return true;
		} else if ((((JmlMethodClauseExpr) traversing.head).expression) instanceof JCIdent){
			if(((JCIdent) ((JmlMethodClauseExpr) traversing.head).expression).name.toString() == toTest)
				return true;
			if (!isFieldParameterOfMethod && ((JCIdent) ((JmlTree.JmlMethodClauseExpr) traversing.head).expression).name.toString() == "this." + toTest)
				return true;
		}
		return false;
	}
	/**
	 * This function walks into a tree with nodes of type JCBinary to search for 
	 * a string as one of the elements of the tree.
	 * @param toTest The String searched as Element in the tree.
	 * @param expression The bifurcation tree searched.
	 * @return True if the string was found, or False.
	 */
	private boolean isThereInMiniTree(String toTest, JCBinary expression) {
		if(expression.rhs instanceof JCBinary){
			if(isThereInMiniTree(toTest, (JCBinary) expression.rhs))
				return true;
		}else if(expression.rhs instanceof JCIdent)
			if(((JCIdent) expression.rhs).name.toString().equals(toTest))
				return true;
		
		if(expression.lhs instanceof JCBinary){
			if(isThereInMiniTree(toTest, (JCBinary) expression.lhs))
				return true;
		}else if(expression.lhs instanceof JCIdent)
			if(((JCIdent) expression.lhs).name.toString().equals(toTest))
				return true;
		return false;
	}
}
