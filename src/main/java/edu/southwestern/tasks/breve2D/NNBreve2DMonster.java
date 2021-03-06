package edu.southwestern.tasks.breve2D;

import edu.southwestern.breve2D.Breve2DGame;
import edu.southwestern.breve2D.agent.Agent;
import edu.southwestern.breve2D.agent.AgentController;
import edu.southwestern.breve2D.agent.Breve2DAction;
import edu.southwestern.breve2D.dynamics.Breve2DDynamics;
import edu.southwestern.breve2D.dynamics.RammingDynamics;
import edu.southwestern.breve2D.sensor.RaySensor;
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.networks.ModeSelector;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.Plot;

import java.util.Arrays;

/**
 * Class controlling a Monster agent in the Breve2D world based on
 * my earlier Breve Monsters work.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public class NNBreve2DMonster<T extends Network> extends Organism<T>implements AgentController {

	public static final double VERY_CLOSE_PLAYER_DISTANCE = 40;
	public static final double VERY_CLOSE_MONSTER_DISTANCE = 80;
	public static final int NUM_OUTPUTS = 2;
	private final int absence;
	private final int teamIndex;
	private final Network nn;
	@SuppressWarnings("unused")
	private final int largeDistance;

	protected DrawingPanel networkInputs;
	
	NNBreve2DMonster(int index, Genotype<T> genotype) {
		super(genotype);
		this.teamIndex = index;
		nn = (Network) this.getGenotype().getPhenotype();
		if (CommonConstants.monitorInputs) {
			DrawingPanel panel = new DrawingPanel(Plot.BROWSE_DIM, (int) (Plot.BROWSE_DIM * 3.5), ("Monster " + index));
			networkInputs = panel;
			panel.setLocation(index * (Plot.BROWSE_DIM + 10), 0);
			Offspring.fillInputs(panel, genotype);
		}
		absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
		largeDistance = Math.min(Breve2DGame.SIZE_X, Breve2DGame.SIZE_Y);
	}

	@Override
	public Breve2DAction getAction(Breve2DGame game) {
		boolean monsterResponds = monsterResponds(game.dynamics);
		boolean playerResponds = playerResponds(game.dynamics);
		Agent monster = game.getMonster(teamIndex);
		Agent player = game.getPlayer();

		double[] inputs = new double[game.dynamics.numInputSensors()];
		int in = 0;
		inputs[in++] = 1.0; // bias
		// inputs[in++] = circleScale(Util.signedAngleDifference(monster.getOppositeHeading(),player.getHeading()));
		inputs[in++] = circleScale(CartesianGeometricUtilities.signedAngleDifference(monster.getHeading(), player.getHeading()));
		// inputs[in++] = circleScale(Util.signedAngleFromSourceHeadingToTarget(monster,player, monster.getOppositeHeading()));
		inputs[in++] = circleScale(CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster, player, monster.getHeading()));
		if (monsterResponds) {
			inputs[in++] = binarySensor(game.lastMonsterResponseToPlayer(teamIndex) == game.getTime());
			inputs[in++] = binarySensor(game.lastMonsterResponseToPlayer() == game.getTime());
		}
		if (playerResponds) {
			inputs[in++] = binarySensor(game.lastPlayerResponseToMonster(teamIndex) == game.getTime());
			inputs[in++] = binarySensor(game.lastPlayerResponseToMonster() == game.getTime());
			inputs[in++] = binarySensor(game.playerLocked());
		}

		// inputs[in++] = binarySenseor(distance < VERY_CLOSE_PLAYER_DISTANCE);
		// inputs[in++] = binarySenseor(distance < VERY_CLOSE_PLAYER_DISTANCE / 2);
		// inputs[in++] = binarySenseor(nearestDistance < VERY_CLOSE_PLAYER_DISTANCE);
		// inputs[in++] = binarySenseor(nearestDistance < VERY_CLOSE_PLAYER_DISTANCE / 2);
		inputs[in++] = binarySensor(CartesianGeometricUtilities.sourceHeadingTowardsTarget(player.getHeading(), player, monster, Math.PI / 2));

		// inputs[in++] = {"get-close-sword-impulse",INPUT_ON,{},{}} onto additional-input-info.
		// inputs[in++] = {"get-very-close-sword-impulse",INPUT_ON,{},{}} onto additional-input-info.

		// Proximity
		// inputs[in++] = Util.scaleAndInvert(distance, largeDistance);
		// inputs[in++] = Util.scaleAndInvert(nearestDistance, largeDistance);

		// ArrayList<Agent> proximityList = game.monstersByDistanceFrom(monster);
		// double nearestOtherMonsterDistance = proximityList.size() > 1 ? proximityList.get(1).distance(monster) : Double.MAX_VALUE;
		//
		// inputs[in++] = binarySenseor(nearestOtherMonsterDistance < VERY_CLOSE_MONSTER_DISTANCE);
		// inputs[in++] = binarySenseor(nearestOtherMonsterDistance < VERY_CLOSE_MONSTER_DISTANCE / 2);

		// boolean right = false;
		// boolean left = false;
		// for (int i = 1; i < proximityList.size(); i++) {
		//  right = right || Util.onSideOf(monster, monster.getHeading(), proximityList.get(i), true);
		//  left = left || Util.onSideOf(monster, monster.getHeading(), proximityList.get(i), false);
		// }
		// inputs[in++] = binarySenseor(right);
		// inputs[in++] = binarySenseor(left);

		for (int i = 0; i < game.getNumMonsters(); i++) {
			Agent other = game.getMonster(i);
			boolean dead = other.isDead();
			if (i == monster.getIdentifier()) {
				inputs[in++] = 0;
				inputs[in++] = 0;
			} else {
				// inputs[in++] = dead ? 0 : circleScale(Util.signedAngleDifference(monster.getOppositeHeading(), other.getHeading()));
				inputs[in++] = dead ? 0 : circleScale(CartesianGeometricUtilities.signedAngleDifference(monster.getHeading(), other.getHeading()));
				// inputs[in++] = dead ? 0 : circleScale(Util.signedAngleFromSourceHeadingToTarget(monster, other, monster.getOppositeHeading()));
				inputs[in++] = dead ? 0 : circleScale(CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster, other, monster.getHeading()));
			}
			if (playerResponds) {
				inputs[in++] = binarySensor(!dead && game.lastPlayerResponseToMonster(i) == game.getTime());
			}
		}

		for (int i = 0; i < game.numMonsterRays; i++) {
			RaySensor rs = game.getRaySensor(teamIndex, i);
			inputs[in++] = rs.sensingAgent(player) ? 1 : 0;
		}

		for (int i = 0; i < game.numMonsterRays; i++) {
			RaySensor rs = game.getRaySensor(teamIndex, i);
			inputs[in++] = rs.sensingAgent(game.getMonsters()) ? 1 : 0;
		}

		// for(int i = 0; i < sensorArraySize, i++) {
		//  inputs[in++] = {"get-sword-sensor",INPUT_ON,{i},{}}
		// }

		assert (in == inputs.length) : "Improper inputs for Breve. Only " + in + " inputs: " + Arrays.toString(inputs);

		if (nn.isMultitask()) {
			ModeSelector modeSelector = new BreveModeSelector(game.dynamics);
			nn.chooseMode(modeSelector.mode());
		}
		
		if (networkInputs != null) {
			TWEANN.inputPanel = networkInputs;
		}
		
		double[] outputs = nn.process(inputs);
		return new Breve2DAction(outputs[0], outputs[1]);
	}

	public static boolean monsterResponds(Breve2DDynamics dynamics) {
		return dynamics.senseMonsterResponseToPlayer()
				|| (dynamics instanceof RammingDynamics && ((RammingDynamics) dynamics).sensePlayerHasRam());
	}

	public static boolean playerResponds(Breve2DDynamics dynamics) {
		return dynamics.sensePlayerResponseToMonster()
				|| (dynamics instanceof RammingDynamics && ((RammingDynamics) dynamics).senseMonstersHaveRams());
	}

	public static String[] sensorLabels(Breve2DDynamics dynamics, int numMonsters) {
		boolean monsterResponds = monsterResponds(dynamics);
		boolean playerResponds = playerResponds(dynamics);

		String[] inputs = new String[dynamics.numInputSensors()];
		int in = 0;
		inputs[in++] = "Bias";
		inputs[in++] = "Diff Monster Heading to Player Heading";
		inputs[in++] = "Diff Monster Heading to Player Location";
		if (monsterResponds) {
			inputs[in++] = "This Monster Hit?";
			inputs[in++] = "Any Monster Hit?";
		}
		if (playerResponds) {
			inputs[in++] = "This Monster Ate?";
			inputs[in++] = "Any Monster Ate?";
			inputs[in++] = "Player Locked?";
		}

		// inputs[in++] = "This Monster Close to Player?";
		// inputs[in++] = "This Monster Very Close to Player?";
		// inputs[in++] = "Any Monster Close to Player?";
		// inputs[in++] = "Any Monster Very Close to Player?";
		inputs[in++] = "Player Facing This Monster?";

		// inputs[in++] = {"get-close-sword-impulse",INPUT_ON,{},{}} onto additional-input-info.
		// inputs[in++] = {"get-very-close-sword-impulse",INPUT_ON,{},{}} onto additional-input-info.

		// inputs[in++] = "This Monster's Proximity to Player";
		// inputs[in++] = "Any Monster's Proximity to Player";
		//
		// inputs[in++] = "This Monster Close to Other Monster?";
		// inputs[in++] = "This Monster Very Close to Other Monster?";
		//
		// inputs[in++] = "Other Monster on Right?";
		// inputs[in++] = "Other Monster on Left?";

		for (int i = 0; i < numMonsters; i++) {
			inputs[in++] = "Diff Monster Heading to Monster " + i + " Heading";
			inputs[in++] = "Diff Monster Heading to Monster " + i + " Location";
			if (playerResponds) {
				inputs[in++] = "Monster " + i + " Ate?";
			}
		}

		int numMonsterRays = Parameters.parameters.integerParameter("numMonsterRays");
		double spacing = Parameters.parameters.doubleParameter("monsterRaySpacing");

		double[] monsterRays = new double[numMonsterRays];
		int fanOut = 0;
		int sign = -1;
		for (int j = 0; j < numMonsterRays; j++) {
			monsterRays[j] = fanOut * spacing * sign;
			if (sign == -1) {
				fanOut++;
			}
			sign *= -1;
		}

		for (int i = 0; i < numMonsterRays; i++) {
			inputs[in++] = "Player Ray Trace at " + (monsterRays[i]);
		}

		for (int i = 0; i < numMonsterRays; i++) {
			inputs[in++] = "Monster Ray Trace at " + (monsterRays[i]);
		}

		return inputs;
	}

	public static double circleScale(double angle) {
		return angle / Math.PI;
	}

	public double binarySensor(boolean s) {
		return s ? 1 : absence;
	}

	@Override
	public void reset() {
		nn.flush();
	}
}
