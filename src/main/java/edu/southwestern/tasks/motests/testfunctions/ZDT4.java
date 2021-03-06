package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT4 extends ZDT {

	public ZDT4() {
		super(10);
	}

	@Override
	public double[] getLowerBounds() {
		double[] result = new double[decisionVars];
		result[0] = 0;
		for (int i = 1; i < result.length; i++) {
			result[i] = -5;
		}
		// System.out.print(Arrays.toString(result));
		return result;
	}

	@Override
	public double[] getUpperBounds() {
		double[] result = new double[decisionVars];
		result[0] = 1;
		for (int i = 1; i < result.length; i++) {
			result[i] = 5;
		}
		// System.out.print(Arrays.toString(result));
		return result;
	}

	@SuppressWarnings("unchecked")
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions() {
		return new FitnessFunction[] { new ZDT4Function(false), new ZDT4Function(true) };
	}
}
