
/**
 * The class determines whether the object can be passed through and if the GameObject is currently active
 * @author SNYMac
 *
 */
public class GameObject {
	private final boolean canPass;
	private boolean active;
	
	public GameObject(boolean passable, boolean isActive){
		canPass = passable;
		active = isActive;
	}
	
	public String toString(){
		if (canPass)
			return " ";
		else
			return "#";
	}
	
	public boolean isPassable(){ return canPass; }	
	
	public void setActive(boolean b){ active = b; }
	
	public boolean isActive(){ return active; }

}
