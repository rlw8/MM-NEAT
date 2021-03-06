package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class SCH implements FunctionOptimizationSet {

	public double[] getLowerBounds() {
		return new double[] { -Math.pow(10, 3) };
	}

	public double[] getUpperBounds() {
		return new double[] { Math.pow(10, 3) };
	}

	@SuppressWarnings("unchecked")
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions() {
		return new FitnessFunction[] { new SCHFunction(false), new SCHFunction(true) };
	}

	public double[] frontDecisionValuesInTermsOfFirst(double x1) {
		return new double[] { x1 };
	}

	public double[] frontDecisionValuesBoundsOfFirst() {
		return new double[] { 0, 2 };
	}
}
