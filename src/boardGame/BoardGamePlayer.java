package boardGame;

import boardGame.heuristics.BoardGameHeuristic;

/**
 * Generic Interface for a simulated Board Game Player
 * 
 * @author johnso17
 */
public interface BoardGamePlayer<T extends BoardGameState> {
	
	/**
	 * Updates the current BoardGameState with an action by this BoardGamePlayer
	 * 
	 * @param current BoardGameState to be evaluated
	 * @return BoardGameState updated with the action of this Player
	 */
	public T takeAction(T current);
	
	/**
	 * Sets the Heuristic for the BoardGamePlayer
	 * 
	 * @param bgh BoardGameHeuristic to be used by the Player
	 */
	public void setHeuristic(BoardGameHeuristic<T> bgh);
	
}
