package pokemon;

import javafx.application.Platform;
import javafx.scene.image.*;

/**
 * The class determines whether the object can be passed through and if the GameObject is currently active
 * @author SNYMac
 *
 */
public class GameObject{
	private final boolean canPass;

	private boolean active;
	private Coordinate coor;

	protected ImageView imageV;

	{
		imageV = new ImageView();
		imageV.setFitHeight(40);
		imageV.setFitWidth(40);
		imageV.setPreserveRatio(true);
	}
	
	public GameObject(boolean passable, boolean isActive){
		canPass = passable;
		active = isActive;
	}
	
	public Coordinate getCoor(){ return coor; }
	
	public void setCoor(Coordinate coor){ 
		this.coor = coor;
		Platform.runLater(() -> imageV.relocate(coor.getCol()*40, coor.getRow()*40));
	}

	public ImageView getImageView(){ return imageV; }

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
