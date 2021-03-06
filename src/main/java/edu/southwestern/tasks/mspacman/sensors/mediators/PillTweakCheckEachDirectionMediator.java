/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionPillsBeforeJunctionBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.*;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingThreatGhostDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class PillTweakCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

	public PillTweakCheckEachDirectionMediator() {
		int direction = -1;
		blocks.add(new BiasBlock());
		// Distances
		blocks.add(new VariableDirectionPillDistanceBlock(direction));
		blocks.add(new VariableDirectionThreatGhostDistanceBlock(direction));
		blocks.add(new VariableDirectionIncomingThreatGhostDistanceBlock(direction));
		blocks.add(new VariableDirectionKStepPillCountBlock(direction));
		blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, true));
		blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, false));
		blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
		blocks.add(new VariableDirectionCountJunctionOptionsBlock());
		blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
		blocks.add(new VariableDirectionPillsBeforeJunctionBlock(direction));

	}
}
