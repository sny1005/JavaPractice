package pokemon;

/**
 * This class holds a GameObject and its corresponding location with Coordinate object
 * @author SNYMac
 *
 */
public class Cell{
	private GameObject gameObj;
	private Coordinate coor;

	
	public Cell() {	}
	
	public Cell(int row, int col, GameObject obj){
		coor = new Coordinate(row, col);
		if (obj == null)
			return;
		gameObj = obj;
		gameObj.setCoor(coor);
	}
	
	public synchronized GameObject getGameObj(){
		return gameObj;
	}

	public synchronized void setGameObj(GameObject gobj){ gameObj = gobj; }
	
	public String toString(){
		if (gameObj == null)
			return " ";

		return gameObj.toString();
	}

	public Coordinate getCoor(){ return coor; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (coor == null) {
			if (other.coor != null)
				return false;
		} else if (!coor.equals(other.coor))
			return false;
		return true;
	}

	
}
