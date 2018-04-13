/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.inv.instrument;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTTreeParser {

	public static String origJavaFile;
	public static String genJavaFile;
	public static String invFile;
	public static String srcMethod;
	public static Map<String, String> typeMap;
	public static int importLine;
	public static int startLine;
	public static int endLine;
	public static String retType;
	private static Logger logger = Logger.getLogger(ASTTreeParser.class);

	public ASTTreeParser(String origClass, String genClass, String invFile,
			String srcMeth) {
		origJavaFile = origClass;
		genJavaFile = genClass;
		this.invFile = invFile;
		srcMethod = srcMeth;
		typeMap = new HashMap<String, String>();
		retType = "void";
		initializeTypeMap();
	}

	public void initializeTypeMap() {
		typeMap.put("this.Reaction_Jet_Control_100000006_class_member0.", "");
	}

	public void parseAST() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		File dir = new File(origJavaFile).getParentFile();
		String fileUnderTest = new File(origJavaFile).getName();
		for (File file : dir.listFiles()) {
			String fileName = file.getName();
			if (!fileName.endsWith(".java"))
				continue;
			parser.setSource(getFileContents(file));
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			final Name pkgName = cu.getPackage().getName();
			logger.debug("compilation unit package: " + pkgName);
			List<TypeDeclaration> types = cu.types();
	

			if (fileName.equals(fileUnderTest)) {
				importLine = cu.getLineNumber(types.get(0).getStartPosition()) - 1;
				//System.out.println("importLine: "+importLine);
				cu.accept(new ASTVisitor() {
					String typeName = "";

					// record type name information
					public boolean visit(TypeDeclaration node) {
						typeName = node.getName().toString();
						logger.debug("entering type: " + typeName);
						return true;
					}

					// record the type information for the fields in the method
					// under
					// test
					public boolean visit(FieldDeclaration node) {
						Type t = node.getType();
						int m = node.getModifiers();
						String prefix = "";
						prefix = "this.";
						// if the field is static, use full name to reference
						// the
						// field
						if (Modifier.isStatic(m)) {
							prefix = pkgName + "." + typeName + ".";
						}
						// if the field is not from the type under test

						List<VariableDeclarationFragment> fields = node
								.fragments();
						for (VariableDeclarationFragment field : fields) {
							typeMap.put(prefix + field.getName(), t.toString());
							logger.debug("visiting field: " + prefix
									+ field.getName());
							logger.debug("storing type: " + t.toString());
							// System.out.println("Field Declaration of name "
							// + field.getName() + " and type " + t +
							// " at line "
							// + cu.getLineNumber(node.getStartPosition()));
						}
						return false;
					}

					// record the type information for the local variables in
					// the
					// method
					// under test
					public boolean visit(VariableDeclarationStatement node) {
						List<VariableDeclarationFragment> vars = node
								.fragments();
						Type t = node.getType();
						for (VariableDeclarationFragment v : vars) {
							SimpleName name = v.getName();
							typeMap.put(name.toString(), t.toString());
							logger.debug("visiting local variable declaration: "
									+ name);
							logger.debug("storing type: " + t.toString());
							// System.out.println("Declaration of '" +
							// name.getIdentifier()
							// + "' with type " + t + " at line"
							// + cu.getLineNumber(name.getStartPosition()));
						}
						return false; // do not continue
					}

					public boolean visit(MethodDeclaration node) {
						Map<String, String> curMap = new HashMap<String, String>();
						SimpleName name = node.getName();
						logger.debug("visiting method: " + name.toString());

						List<SingleVariableDeclaration> paras = node
								.parameters();
						String fullName = name + "(";
						int i = 0;
						// iterate over each parameter to form the method full
						// name
						for (SingleVariableDeclaration para : paras) {
							Type type = para.getType();
							fullName += type;
							if ((i++) < paras.size() - 1)
								fullName += ", ";
						}
						fullName += ")";

						// check if the current method is the method under test
						if (srcMethod.endsWith(fullName)) {

							for (SingleVariableDeclaration para : paras) {
								SimpleName pname = para.getName();
								Type type = para.getType();
								curMap.put(pname.toString(), type.toString());
								logger.debug("visiting parameter: "
										+ pname.toString());
								logger.debug("storing type: " + type);
							}

							// get the return type if any
							if (node.getReturnType2() != null) {
								retType = node.getReturnType2().toString();
								logger.debug("method return type not void: "
										+ retType);
							}
							// put all the parameter type info into the global
							// type
							// map
							typeMap.putAll(curMap);
							// get the line info of the current method
							int sLine = cu.getLineNumber(node.getBody()
									.getStartPosition());
							int eLine = cu.getLineNumber(node
									.getStartPosition() + node.getLength());
							startLine = sLine;
							endLine = eLine;
							logger.debug("source range for method under test: ");
							logger.debug("starting line: " + sLine);
							logger.debug("ending line: " + eLine);

							return true;
						}
						// System.out.println(fullName);
						return false;
					}

					/*
					 * public boolean visit(SimpleName node) { if
					 * (this.names.contains(node.getIdentifier())) {
					 * System.out.println("Usage of '" + node + "' at line " +
					 * cu.getLineNumber(node.getStartPosition())); } return
					 * true; }
					 */
				});
			} else {
				cu.accept(new ASTVisitor() {
					String typeName = "";

					// record type name information
					public boolean visit(TypeDeclaration node) {
						typeName = node.getName().toString();
						logger.debug("entering other type: " + typeName);
						return true;
					}

					// record the type information for the fields in the method
					// under
					// test
					public boolean visit(FieldDeclaration node) {
						Type t = node.getType();
						int m = node.getModifiers();
						String prefix = typeName + ".";
						List<VariableDeclarationFragment> fields = node
								.fragments();
						for (VariableDeclarationFragment field : fields) {
							typeMap.put(prefix + field.getName(), t.toString());
							logger.debug("visiting field: " + prefix
									+ field.getName());
							logger.debug("storing type: " + t.toString());
							// System.out.println("Field Declaration of name "
							// + field.getName() + " and type " + t +
							// " at line "
							// + cu.getLineNumber(node.getStartPosition()));
						}
						return false;
					}
				});
			}
		}
	}

	// create the ASTParser which will be a CompilationUnit
	public CompilationUnit createParser(char[] contents) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(contents);
		parser.setResolveBindings(true);
		CompilationUnit parse = (CompilationUnit) parser.createAST(null);
		logger.debug("create ast tree");
		return parse;
	}

	// read the input java file as char array
	public char[] getFileContents(File file) {
		// char array to store the file contents in
		char[] contents = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				// append the content and the lost new line.
				sb.append(line + "\n");
			}
			contents = new char[sb.length()];
			sb.getChars(0, sb.length() - 1, contents, 0);

			assert (contents.length > 0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return contents;
	}

}
