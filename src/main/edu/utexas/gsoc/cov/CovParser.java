/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.cov;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecFileLoader;

import edu.utexas.gsoc.utils.NumberPrinter;

public class CovParser {

	private final String title;

	private final File executionDataFile;
	private final File classesDirectory;

	private final String methodTested;
	private ExecFileLoader execFileLoader;

	/**
	 * Create a new generator based for the given project.
	 * 
	 * @param projectDirectory
	 */
	public CovParser(final File projectDirectory, String m) {
		this.title = projectDirectory.getName();
		this.executionDataFile = new File(projectDirectory, "jacoco.exec");
		this.classesDirectory = new File(projectDirectory, "classes");
		this.methodTested = m;
	}

	/**
	 * Create the report.
	 * 
	 * @throws IOException
	 */
	public void create() throws IOException {

		// Read the jacoco.exec file. Multiple data files could be merged
		// at this point
		loadExecutionData();

		// Run the structure analyzer on a single class folder to build up
		// the coverage model. The process would be similar if your classes
		// were in a jar file. Typically you would create a bundle for each
		// class folder and each jar you want in your report. If you have
		// more than one bundle you will need to add a grouping node to your
		// report
		final IBundleCoverage bundleCoverage = analyzeStructure();

		printCov(bundleCoverage);

	}

	private void printCov(final IBundleCoverage bundleCoverage)
			throws IOException {
		System.out.println(bundleCoverage.getInstructionCounter()
				.getTotalCount()
				+ "("
				+ NumberPrinter.formatPerc(bundleCoverage
						.getInstructionCounter().getCoveredRatio())
				+ ")\t"
				+ bundleCoverage.getBranchCounter().getTotalCount()
				+ "("
				+ NumberPrinter.formatPerc(bundleCoverage.getBranchCounter()
						.getCoveredRatio())
				+ ")\t"
				+ bundleCoverage.getLineCounter().getTotalCount()
				+ "("
				+ NumberPrinter.formatPerc(bundleCoverage.getLineCounter()
						.getCoveredRatio()) + ")");
		/*Collection<IPackageCoverage> pkgs = bundleCoverage.getPackages();
		for (IPackageCoverage pkg : pkgs) {
			for (IClassCoverage cls : pkg.getClasses()) {
				Collection<IMethodCoverage> meths = cls.getMethods();
				for (IMethodCoverage meth : meths) {
					double lineCov = meth.getLineCounter().getCoveredRatio();
					double instCov = meth.getInstructionCounter()
							.getCoveredRatio();
					double branchCov = meth.getBranchCounter()
							.getCoveredRatio();
					String mName = cls.getName() + "." + meth.getName() + "()";
					mName = mName.replace("/", ".");
					// System.out.println("method: "+mName);
					if (mName.equals(methodTested))
						System.out.println(mName + ": \t"
								+ meth.getInstructionCounter().getTotalCount()
								+ "(" + NumberPrinter.formatPerc(instCov)
								+ ")\t"
								+ meth.getBranchCounter().getTotalCount() + "("
								+ NumberPrinter.formatPerc(branchCov) + ")\t"
								+ meth.getLineCounter().getTotalCount() + "("
								+ NumberPrinter.formatPerc(lineCov) + ")");
				}
			}
		}*/
	}

	private void loadExecutionData() throws IOException {
		execFileLoader = new ExecFileLoader();
		execFileLoader.load(executionDataFile);
	}

	private IBundleCoverage analyzeStructure() throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(
				execFileLoader.getExecutionDataStore(), coverageBuilder);

		analyzer.analyzeAll(classesDirectory);

		return coverageBuilder.getBundle(title);
	}

	/**
	 * Starts the report generation process
	 * 
	 * @param args
	 *            Arguments to the application. This will be the location of the
	 *            eclipse projects that will be used to generate reports for
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final CovParser generator = new CovParser(new File(args[0]), args[1]);
		// new
		// File("/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/tcas/cov-subjects/v1/target"),
		// "tcas.TCAS.alt_sep_test()");
		generator.create();
	}

}
