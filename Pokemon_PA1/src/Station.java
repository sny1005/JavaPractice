/**
 * Class Station is a special type of cell. Besides recording the location of the cell, it also records the information of a supply station, including the number of the provided balls.
 * @author SNYMac
 *
 */
public class Station extends GameObject{
	private int numBall;
	
	/**
	 * default constructor
	 */
	public Station(){
		super(true, true);
	}
	
	public void setnumBall(int numBall){
		this.numBall = numBall;
	}
	
	public int getnumBall(){ return numBall; }
	
	public String toString(){
		return "S";
	}
}
