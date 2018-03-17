package pokemon;

import java.util.ArrayList;
import java.util.Random;

/**
 * The class is defined to record the information of all the cells of the map, 
 * including walls, Pokemon cells, Station cells and empty cells.
 * @author SNYMac
 *
 */
public class Map {
	private ArrayList<Pokemon> pokearr = new ArrayList<>();
	private ArrayList<Station> stnarr = new ArrayList<>();
	
	// storage for empty cell
	private ArrayList<Coordinate> emptyCellCoor = new ArrayList<>();

	private Cell[][] map;			// this should store a "Cell" object
	private int pokeNum;
	private int stnNum;
	private Coordinate base;
	private Coordinate destination;


	/**
	 * Constructor of Map
	 * @param r	number of rows
	 * @param c	number of columns
	 */
	public Map(int r, int c){
		map = new Cell[r][c];
	}
	
	public int getNumOfRows(){
		return map.length;
	}
	
	public int getNumOfCols(){
		return map[0].length;
	}
		
	
	/**
	 * Setter function
	 * @param r	row index
	 * @param c	column index
	 * @param obj	object type in the maze
	 * @throws Exception the char obj is in incorrect format
	 */
	public void setObj(int r, int c, char obj) throws Exception{
		switch (obj){
			case 'P':
				pokeNum++;
				Pokemon poke = new Pokemon();
				pokearr.add(poke);
				map[r][c] = new Cell(r, c, poke);
				break;
			case 'S':
				stnNum++;
				Station stn = new Station();
				stnarr.add(stn);
				map[r][c] = new Cell(r, c, stn);
				break;
			case '#':
				map[r][c] = new Cell(r, c, new Wall());
				break;
			case 'D':
				map[r][c] = new Cell(r, c, null);
				destination = map[r][c].getCoor();
				break;
			case 'B':
				map[r][c] = new Cell(r, c, null);
				base = map[r][c].getCoor();
				break;
			case ' ':
				map[r][c] = new Cell(r, c, null);
				emptyCellCoor.add(map[r][c].getCoor());
				break;
			default:
				throw new Exception("Invalid maze format!");
		}
	}

	
	/**
	 * Base getter function
	 * @return	the Cell representing the base
	 */
	public Coordinate getBase(){
		return base;
	}
	
	/**
	 * Destination getter function
	 * @return	the Cell representing the destination
	 */
	public Coordinate getDestination(){
		return destination;
	}
	
	/**
	 * Cell getter function
	 * @param row	row index
	 * @param col	col index
	 * @return	the Cell specified by row and col
	 */
	public Cell getCell(int row, int col){
		if (row < 0 || row >= map.length || col < 0 || col >= map[0].length)
			return null;
		else
			return map[row][col]; 
	}
	
	
	public Cell getCell(int row, int col, int dir){
		switch(dir){
			case 1:
				row -= 1;
				break;
			case 2:
				col += 1;
				break;
			case 3:
				row += 1;
				break;
			case 4:
				col -= 1;
				break;
		}
		
		if (row < 0 || row >= map.length || col < 0 || col >= map[0].length)
			return null;
		else
			return map[row][col]; 
	}

	public Cell getCell(Coordinate coor){
		return this.getCell(coor.getRow(), coor.getCol());
	}

	public Cell getCell(Coordinate coor, int dir){
		return this.getCell(coor.getRow(), coor.getCol(), dir);
	}
	
	public int getPokeNum(){
		return pokeNum;
	}

	public int getStnNum(){
		return stnNum;
	}

	public ArrayList<Pokemon> getPokearr(){ return pokearr; }

	public ArrayList<Station> getStnarr(){ return stnarr; }

	public synchronized ArrayList<Coordinate> getEmptyCellCoor(){ return emptyCellCoor; }

	private void addEmptyCellCoor(Coordinate coor){
		emptyCellCoor.add(coor);
		map[coor.getRow()][coor.getCol()].setGameObj(null);
	}

	private boolean removeEmptyCellCoor(Coordinate coor, GameObject gObj){
		map[coor.getRow()][coor.getCol()].setGameObj(gObj);
		return emptyCellCoor.remove(coor);
	}

	/**
	 * print the entire map
	 */
	public void printMap(){
		for (int r=0; r<map.length; r++){
			for (int c=0; c<map[r].length; c++){
				System.out.print(map[r][c]);
			}
			System.out.println();
		}
	}
	
	public synchronized void deleteGameObjectFromMap(GameObject gObj){
		addEmptyCellCoor(gObj.getCoor());
	}
	
	public synchronized Coordinate teleportGameObject(GameObject gObj){
		//random relocate image view
		int index = new Random().nextInt(emptyCellCoor.size());
		Coordinate destCoor = emptyCellCoor.get(index);
		removeEmptyCellCoor(destCoor, gObj);

		addEmptyCellCoor(gObj.getCoor());
		return  destCoor;
	}
	
	public synchronized Coordinate moveGameObject(Coordinate start, int dir){
		GameObject gameObj = map[start.getRow()][start.getCol()].getGameObj();
		if (gameObj == null) {         //safety check
			System.err.println("Current cell's gameobject is null!");
			return null;
		}

		int row = start.getRow();
		int col = start.getCol();

		switch(dir) {
			case 1:                     //go up
				row -= 1;
				break;
			case 2:                     //go right
				col += 1;
				break;
			case 3:
				row += 1;               //go down
				break;
			case 4:
				col -= 1;               //go left
				break;
			default:
				System.err.println("Invalid direction parameter");
				return null;
		}

		//check out-of-bound
		if(row < 0 || row >= map.length || col < 0 || col >= map[0].length)
			return null;

		Cell dest = map[row][col];

		//the destination cell is already occupied or it is the destination cell
		if (dest.getGameObj() != null || dest.getCoor().equals(destination)) {
			return null;
		}
		else{
			// obtain the empty cell first
			removeEmptyCellCoor(dest.getCoor(), gameObj);
			// then release the current cell
			addEmptyCellCoor(gameObj.getCoor());
			gameObj.setCoor(dest.getCoor());
			return dest.getCoor();
		}
	}
	

}
