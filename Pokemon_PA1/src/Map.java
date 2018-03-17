
/**
 * The class is defined to record the information of all the cells of the map, 
 * including walls, Pokemon cells, Station cells and empty cells.
 * @author SNYMac
 *
 */
public class Map {
	private Cell[][] map;			// this should store a "Cell" object
	private int pokeNum;
	private int stnNum;
	private Cell base;
	private Cell destination;
	
	/**
	 * Constructor of Map
	 * @param r	number of rows
	 * @param c	number of columns
	 */
	public Map(int r, int c){
		map = new Cell[r][c];
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
				map[r][c] = new Cell(r, c, new Pokemon());
				break;
			case 'S':
				stnNum++;
				map[r][c] = new Cell(r, c, new Station());
				break;
			case '#':
				map[r][c] = new Cell(r, c, new GameObject(false, false));
				break;
			case 'D':
				destination = new Cell(r, c, new GameObject(true, false));
				map[r][c] = destination;
				break;
			case 'B':
				base = new Cell(r, c, new GameObject(true, false));
				map[r][c] = base;
				break;
			case ' ':
				map[r][c] = new Cell(r, c, new GameObject(true, false));
				break;
			default:
				throw new Exception("Invalid maze format!");
		}
	}
	
	/**
	 * Base getter function
	 * @return	the Cell representing the base
	 */
	public Cell getBase(){
		return base;
	}
	
	/**
	 * Destination getter function
	 * @return	the Cell representing the destination
	 */
	public Cell getDestination(){
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
	
	public int getPokeNum(){
		return pokeNum;
	}

	public int getStnNum(){
		return stnNum;
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
	/*
	public boolean createPoke(int row, int col, String name, String type, int cp, int reqBall){
		Pokemon poke;
		
		GameObject obj = map[row][col].getGameObj();
		if (obj instanceof Pokemon)
			poke = (Pokemon) obj;
		else
			return false;
		
		poke.setAttr(name, type, cp, reqBall);
		return true;
		
//		Pokemon poke = new Pokemon(row, col, name, type, cp, reqBall);
//		pokeList.add(poke);
	}
	
	public boolean createStn(int row, int col, int numBall){
		Station stn;
		
		GameObject obj = map[row][col].getGameObj();
		if (obj instanceof Station)
			stn = (Station) obj;
		else
			return false;
		
		stn.setnumBall(numBall);
		return true;
		
//		Station stn = new Station(row, col, numBall);
//		stnList.add(stn);
	}*/
}
