package pokemon;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Class Station is a special type of cell. Besides recording the location of the cell, it also records the information of a supply station, including the number of the provided balls.
 * @author SNYMac
 *
 */
public class Station extends GameObject{
	// urls of station image
	private static final String stationf = new File("icons/ball_ani.gif").toURI().toString();

	private int numBall;
	private boolean respawning = false;
	private boolean paused = false;

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * default constructor
	 */
	public Station(){
		super(true, true);
		imageV.setImage(new Image(stationf));
	}
	
	public void setnumBall(int numBall){
		this.numBall = numBall;
	}
	
	public int getnumBall(){ return numBall; }
	
	public String toString(){
		return "S";
	}

	public void setRespawning(boolean bool){ respawning = bool; }

	public boolean isRespawning(){ return respawning; }
}
