package pokemon;
import java.util.HashSet;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


/**
 * The class is used for recording the status of the player, 
 * including the current location, the caught Poke�ons, the number of Poke balls and the path visited.
 * @author SNYMac
 *
 */
public class Player implements Cloneable{
	private HashSet<String> typeSet;
	private int maxCp;
	
	private Coordinate curCoor;
	private IntegerProperty numPokeBall;
	private IntegerProperty numPokeCaught;
	private IntegerProperty score;

	{
		typeSet = new HashSet<>();
		
		numPokeBall = new SimpleIntegerProperty(0);
		numPokeCaught = new SimpleIntegerProperty(0);
		score = new SimpleIntegerProperty(0);
	}

	public Player(){};
	
	public Player(Coordinate coor){
		curCoor = coor;
	}

	public Coordinate getCurLoc(){ return curCoor; }

	public int getScore(){ return score.get(); }

	public void reduceScore(){ score.set(score.get() - 1); }

	public IntegerProperty getScoreProperty(){ return score; }

	public IntegerProperty getNumPokeBallProperty(){ return numPokeBall; }

	public IntegerProperty getNumPokeCaughtProperty(){ return numPokeCaught; }


	/**
	 * Collect the pokeballs in the station and deactivate the station if needed
	 * @param stn	the station that the player stepped on
	 * @return	true if pokeballs are successfully collected
	 */
	public void collectStation(Station stn){
		if(stn.isActive() && !stn.isRespawning()) {
			numPokeBall.set(numPokeBall.get() + stn.getnumBall());
			score.set(score.get() + stn.getnumBall());

			stn.setRespawning(true);
		}
	}
	
	//TODO: need to rewrite
	/**
	 * Try to catch the pokemon and deactivate the pokemon if needed
	 * @param poke	the pokemon object
	 * @return	true if pokemon is caught successfully
	 */
	public boolean catchPokemon(Pokemon poke){
		if (numPokeBall.get() >= poke.getReqBall() && !poke.isRespawning() && poke.isActive()){
			// handle increase caught pokemons
			int incInScore = 5 - poke.getReqBall();
			
			// handle unique poke type
			if (typeSet.add(poke.getType()))
				incInScore += 10;
			
			// handle max pokemon cp
			int cpDiff = poke.getCp() - maxCp;
			if (cpDiff > 0){
				incInScore += cpDiff;
				maxCp = poke.getCp();
			}

			// increase the score property
			score.set(score.get() + incInScore);
			
			// increase num of pokemon caught
			numPokeCaught.set((numPokeCaught.get() + 1));

			// reduce current pokeball
			numPokeBall.set(numPokeBall.get() - poke.getReqBall());
			poke.setActive(false);
			
			return true;
		}
		return false;
	}
}
