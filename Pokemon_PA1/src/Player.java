import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * The class is used for recording the status of the player, 
 * including the current location, the caught Poke�ons, the number of Poke balls and the path visited.
 * @author SNYMac
 *
 */
public class Player implements Cloneable{
	private Cell baseCell;
	private Stack<Pokemon> caughtPoke;
	private Stack<Coordinate> path;
	private int numPokeBall = 0;
	private int numStep = 0;
	private int[][] score;
	
	//debug
	public void printAllScore(){
		for (int i=0;i<score.length; i++){
			for (int j=0;j<score[0].length; j++){
				System.out.print(score[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public Player(){
		caughtPoke = new Stack<>();
		path = new Stack<>();
	}
	
	public Player(int rows, int cols, Cell base){
		this();
		baseCell = base;
		Coordinate c = baseCell.getCoor();
		path.push(c);
		
		//initialize score at each cell
		score = new int[rows][cols];
		for (int i=0; i<score.length; i++){
			for (int j=0; j<score[0].length; j++)
				score[i][j] = Integer.MIN_VALUE;
		}
		
		//initialize score at base cell
		score[c.getRow()][c.getCol()] = 0;
	}
	
	public Stack<Coordinate> getPath(){ return path; }
	
	public int getScore(Coordinate c){ return score[c.getRow()][c.getCol()]; }
		
	public Cell getBaseCell(){ return baseCell; }
	
	/**
	 * @return	the formatted current necessary properties of the player for output in String
	 */
	public String formattedPlayerInfo(){
		return (numPokeBall + ":" + caughtPoke.size() + ":" + numDistinctPokeType() + ":" + maxPokeCp());
	}
	
	/**
	 * Calculate the score based on the state of the player
	 * @return	the calculated score
	 */
	public int calcScore(){
		return numPokeBall + (5 * caughtPoke.size()) + (10 * numDistinctPokeType()) + maxPokeCp() - numStep;
	}
	
	/**
	 * Update the score at a specific cell if the new score is better
	 * @param coor	the coordinate of the cell to be updated
	 * @param newScore	the calculated new score
	 * @return	true if the score is updated
	 */
	public boolean updateCellScore(Coordinate coor, int newScore){
		int currScore = score[coor.getRow()][coor.getCol()];
				
		if (newScore > currScore){
			score[coor.getRow()][coor.getCol()] = newScore;
			return true;
		}
		return false;
	}
	
	/**
	 * change the score of a specific cell, this should be used only when the previous value is known
	 * @param c	the coordinate of the cell
	 * @param oldScore	previous version of the score stored outside the Player class
	 */
	public void revertScore(Coordinate c, int oldScore){ score[c.getRow()][c.getCol()] = oldScore; }
	
	/**
	 * Try to catch the pokemon and deactivate the pokemon if needed
	 * @param poke	the pokemon object
	 * @return	true if pokemon is caught successfully
	 */
	public boolean catchPokemon(Pokemon poke){
		if (numPokeBall >= poke.getReqBall()/* && poke.isActive()*/){
			caughtPoke.push(poke);
			numPokeBall -= poke.getReqBall();
			poke.setActive(false);
			
//			System.out.println("caught" + poke.name);
			
			return true;
		}
		return false;
	}
	
	/**
	 * Collect the pokeballs in the station and deactivate the station if needed
	 * @param stn	the station that the player stepped on
	 * @return	true if pokeballs are successfully collected
	 */
	public void collectStation(Station stn){
//			System.out.println("get " + stn.getnumBall());
			
			numPokeBall += stn.getnumBall();
			stn.setActive(false);
	}
	
	/**
	 * revert the state of the player
	 * @param gobj	the GameObject that holds the information to revert the player
	 */
	public void rollback(GameObject gobj){
		gobj.setActive(true);
		if (gobj instanceof Pokemon){
//			System.out.println("release " + ((Pokemon)gobj).name);
			
			numPokeBall += caughtPoke.pop().getReqBall();
		}
		else if (gobj instanceof Station){
//			System.out.println("release " + ((Station)gobj).getnumBall());
			
			numPokeBall -= ((Station)gobj).getnumBall();
		}
	}
	
	/**
	 * Store the path and increment the step counter
	 * @param coor
	 */
	public void recordPath(Coordinate coor){
//		System.out.println("push " + coor);
		
		path.push(coor);
		numStep++;
	}
	
	/**
	 * remove the latest cell coordinate of the path stored
	 */
	public void revertPath(){
//		System.out.println("pop");

		path.pop();
		numStep--;
	}
		
	/**
	 * Overridden clone method for Player
	 */
	@Override
	protected Player clone(){
		Player p;
		try {
			p = (Player) super.clone();
			p.caughtPoke = (Stack<Pokemon>) this.caughtPoke.clone();
			p.path = (Stack<Coordinate>) this.path.clone();
			return p;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @return	number of distinct type of pokemons caught
	 */
	private int numDistinctPokeType(){
		Set<String> distinctType = new HashSet<>();
		
		for (Pokemon p: caughtPoke){
			distinctType.add(p.getType());
		}
		
		return distinctType.size();
	}
	
	/**
	 * @return the maximum CP of pokemons caught
	 */
	private int maxPokeCp(){
		int maxCp = 0;
		
		for (Pokemon p: caughtPoke){
			if (maxCp < p.getCp())
				maxCp = p.getCp();
		}
		
		return maxCp;
	}
}
