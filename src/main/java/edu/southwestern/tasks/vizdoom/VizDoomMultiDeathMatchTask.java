package edu.southwestern.tasks.vizdoom;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Triple;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomMultiDeathMatchTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomMultiDeathMatchTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/multi.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/multi_deathmatch.wad");
		game.setDoomMap("map01");
	}

	@Override
	public String[] sensorLabels() {
		return getSensorLabels(Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				(Parameters.parameters.integerParameter("doomInputWidth") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				(Parameters.parameters.integerParameter("doomInputHeight") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.MOVE_BACKWARD);
		
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		
		game.addAvailableButton(Button.ATTACK);
		
		game.addAvailableButton(Button.TURN_LEFT_RIGHT_DELTA);
		game.addAvailableButton(Button.LOOK_UP_DOWN_DELTA);
		
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move Forward");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0 }, "Move Left");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0, 0, 0 }, "Move Right");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0, 0, 0 }, "Move Backward");
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0 }, "Turn Left");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0, 0, 0 }, "Turn Right");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1, 0, 0 }, "Attack");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 0 }, "Turn left/right delta");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1 }, "Look up/down delta");
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.HEALTH);
		game.addAvailableGameVariable(GameVariable.AMMO3);
	}

	@Override
	public double[] getInputs(GameState s) {
		double[] inputs = getInputs(s, Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				Parameters.parameters.integerParameter("doomInputWidth"), 
				Parameters.parameters.integerParameter("doomInputHeight"), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
		if(Parameters.parameters.integerParameter("doomInputPixelSmudge") > 1){
			return smudgeInputs(inputs, Parameters.parameters.integerParameter("doomInputWidth"), 
					Parameters.parameters.integerParameter("doomInputHeight"), 
					Parameters.parameters.integerParameter("doomInputColorVal"), 
					Parameters.parameters.integerParameter("doomInputPixelSmudge"));
		}else{
			return inputs;
		}
	}

	@Override
	public void setRewards() {
		game.setDeathPenalty(1);
	}

	@Override
	public int numInputs() {
		int smudge = Parameters.parameters.integerParameter("doomInputPixelSmudge");
		int width = Parameters.parameters.integerParameter("doomInputWidth") / smudge;
		int height = Parameters.parameters.integerParameter("doomInputHeight") / smudge;
		
		if(Parameters.parameters.integerParameter("doomInputColorVal") == 3){
			return (width * height * 3);
		}
		return (width * height);
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:2100",
				"task:edu.southwestern.tasks.vizdoom.VizDoomMultiDeathMatchTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomMultiDeathMatchTask<TWEANN> vd = new VizDoomMultiDeathMatchTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[9];
		if(Parameters.parameters.booleanParameter("hyperNEAT")){
			action[0] = rawOutputs[1]; // Forward
			action[1] = rawOutputs[3]; // Left
			action[2] = rawOutputs[5]; // Right
			action[3] = rawOutputs[7]; // Backward
			action[4] = rawOutputs[9]; // Turn Left
			action[5] = rawOutputs[10]; // Turn Right
			action[6] = rawOutputs[11]; // Attack
			action[7] = rawOutputs[12]; // Turn left/right delta
			action[8] = rawOutputs[13]; // Look up/down delta
		} else {
			action[0] = rawOutputs[0]; // Forward
			action[1] = rawOutputs[1]; // Left
			action[2] = rawOutputs[2]; // Right
			action[3] = rawOutputs[3]; // Backward
			action[4] = rawOutputs[4]; // Turn Left
			action[5] = rawOutputs[5]; // Turn Right
			action[6] = rawOutputs[6]; // Attack
			action[7] = rawOutputs[7]; // Turn left/right delta
			action[8] = rawOutputs[8]; // Look up/down delta
		}
		return action;
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		
		outputs.add(new Triple<String, Integer, Integer>("D-Pad Outputs", 3, 3));
		outputs.add(new Triple<String, Integer, Integer>("C-Stick Outputs", 2, 1));
		outputs.add(new Triple<String, Integer, Integer>("Button Output", 1, 1));
		outputs.add(new Triple<String, Integer, Integer>("Misc. Outputs", 2, 1));
		
		return outputs;
	}
	
	public void addDeadNeurons(List<Substrate> subs){
		Substrate dPad = null; // Stores the Substrate associated with the D-Pad
		
		for(Substrate sub : subs){
			if(sub.getName().equals("D-Pad Outputs")){
				dPad = sub;
			}
		}
		
		// Corners and center of D-pad are not used
		dPad.addDeadNeuron(0,0);
		dPad.addDeadNeuron(0,2);
		dPad.addDeadNeuron(1,1);
		dPad.addDeadNeuron(2,0);
		dPad.addDeadNeuron(2,2);
	}
	
	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		
		outputs.add("D-Pad Outputs");
		outputs.add("C-Stick Outputs");
		outputs.add("Button Output");
		outputs.add("Misc. Outputs");
		
		return outputs;
	}
}
