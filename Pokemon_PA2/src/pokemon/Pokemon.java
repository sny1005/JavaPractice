package pokemon;

import javafx.scene.image.Image;


/**
 * Class Pokemon is a special type of cell. Besides recording the location of the cell, 
 * it also records the information of the properties of a Poke�on, 
 * including the name, type, combat power and required balls to catch it.
 * @author SNYMac
 *
 */
public class Pokemon extends GameObject{
	private String name;
	private String type;
	private int cp;
	private int reqBall;
	private boolean respawning = false;
	private boolean paused = false;

	
	public Pokemon(){
		super(true, true);
	}
	
	public Pokemon(String name, String type, int cp, int reqBall){
		super(true, true);
		this.name = name;
		this.type = type;
		this.cp = cp;
		this.reqBall = reqBall;
	}
	
	public String toString(){
		return "P";
	}

	public void setImageV(Image img){ imageV.setImage(img); }


	/**
	 * Setter method for Pokemon
	 * @param name	name of the pokemon
	 * @param type	type of the pokemon
	 * @param cp	cp of the pokemon
	 * @param reqBall	required balls to caatch the pokemon
	 */
	public void setAttr(String name, String type, int cp, int reqBall){
		this.name = name;
		this.type = type;
		this.cp = cp;
		this.reqBall = reqBall;
	}
	
	public String getType(){ return type; }
	
	public int getCp(){ return cp; }
	
	public int getReqBall(){ return reqBall; }

	public String getPokeName(){ return name; }

	public void setRespawning(boolean bool){ respawning = bool; }

	public boolean isRespawning(){ return respawning; }

	public void setPaused(boolean bool){ paused = bool; }

	public boolean isPaused() {	return paused;	}
}
