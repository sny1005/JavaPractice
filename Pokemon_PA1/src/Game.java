import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;

/**
 * This the entry class of the whole program, which contains the main function.
 * It is also responsible for file input and output. It should contain a Map and a Player.
 * @author lhschan (20187854)
 *
 */

public class Game{
	private Map map;
	private Player player;
	private Player optPlayer;
	private static File outputFile;
	
	
	/**
	 * To parse in the data form the inputFile specified in main() and creates the Map,
	 * the Player and other GameObject in the game
	 * @param inputFile	File storing the information about the maze as specified in main()
	 * @throws Exception 
	 */
	public void initialize(File inputFile) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		// Read the first of the input file
		String line = br.readLine();
		int M = Integer.parseInt(line.split(" ")[0]);
		int N = Integer.parseInt(line.split(" ")[1]);
		
		// To do: define a map
		map = new Map(M, N);
		
		// Read the following M lines of the Map
		for (int i = 0; i < M; i++) {
			line = br.readLine();
			for (int j=0; j<line.length(); j++){
				map.setObj(i, j, line.charAt(j));
			}
		}
		
		//TODO:disable debug
//		map.printMap();
		
		// Find the number of stations and pokemons in the map 
		// Continue read the information of all the stations and pokemons by using br.readLine();
		int pokeNum = map.getPokeNum();
		int stnNum = map.getStnNum();
		
		
//		System.out.println(pokeNum + " " + stnNum);
		
		
		Pattern p = Pattern.compile("<(\\d+),\\s*(\\d+)>,\\s*(\\w+),\\s*(\\w+),\\s*(\\w+),\\s*(\\w+)");
		for (int i=0; i<pokeNum; i++){
			line = br.readLine();
			Matcher m = p.matcher(line);
	
			if (m.find()){
				Pokemon tp = (Pokemon) map.getCell(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)))
						.getGameObj();
				tp.setAttr(m.group(3), m.group(4), Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));
			}
			else{
				throw new Exception("Pokemon data parsing error!");
			}
		}
		
		p = Pattern.compile("<(\\d+),\\s*(\\d+)>,\\s*(\\w+)");
		for (int i=0; i<stnNum; i++){
			line = br.readLine();
			Matcher m = p.matcher(line);
	
			if (m.find()){
				Station ts = (Station) map.getCell(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)))
						.getGameObj();
				ts.setnumBall(Integer.parseInt(m.group(3)));
			}
			else{
				throw new Exception("Station data parsing error!");
			}
			
		}
		
		br.close();
		
		// initialize player
		player = new Player(M, N, map.getBase());
		
	}
	
	
	private void start(){
		// maze optimal path finding
		findPath(map.getDestination(), player);
	}
	
	
	/**
	 * Driver function for the recursive findPath(), used for handling the case where the player
	 * is at the starting base.
	 * @param destination	the Cell object where the destination is located
	 * @param p	the initialized Player 
	 */
	private void findPath(Cell destination, Player p){
		
		Coordinate coor = p.getBaseCell().getCoor();
		int row = coor.getRow();
		int col = coor.getCol();
		
		Cell left = map.getCell(row, col-1);
		Cell right = map.getCell(row, col+1);
		Cell up = map.getCell(row-1, col);
		Cell down = map.getCell(row+1, col);
		
		if (left != null && left.getGameObj().isPassable())
			findPath(left, destination, p);
		if (right != null && right.getGameObj().isPassable())
			findPath(right, destination, p);
		if (up != null && up.getGameObj().isPassable())
			findPath(up, destination, p);
		if (down != null && down.getGameObj().isPassable())
			findPath(down, destination, p);	
	}
	
	/**
	 * Recursive findpath algorithm to search for the destination in the maze. 
	 * The function will terminate upon reaching the destination cell or the new score when arriving at the Cell is lower than previously saved score.
	 * It saves the path and score in the Player object during execution and rollback when function terminates
	 * @param current	The Cell where the Player is located
	 * @param destination	The destination of the maze, remain unchanged during execution
	 * @param p The Player in the game
	 */
	private void findPath(Cell current, Cell destination, Player p){		
		//get information of currently located cell
		Coordinate coor = current.getCoor();
		p.recordPath(coor);
		int oldScore = p.getScore(coor);		
		
		//base case
		if (current.equals(destination)){			
//			System.out.println("base case");
//			System.out.println(oldScore + ", " + p.calcScore());
//			System.out.println(p.formattedPlayerInfo());
			
			//save the state of the player if a better score at the destination have been achieved
			boolean betterPath = p.updateCellScore(destination.getCoor(), p.calcScore());
			if (betterPath){
				optPlayer = p.clone();
			}

			p.revertPath();
			return;
		}
		
		//get cell gameobject
		GameObject gobj = current.getGameObj();
		
		//capture is set to true if the Pokemon is captured or supplies were obtained
		boolean capture = false;
		if (gobj.isActive()){
			//reach pokemon
			if (gobj instanceof Pokemon){
				capture = p.catchPokemon((Pokemon)gobj);			
			}
			//reach pokestop
			else if (gobj instanceof Station){
				capture = true;
				p.collectStation((Station)gobj);
			}
		}
		
		//check is the player is visiting a visited cell with lower score, end the current recursion if true
		if (!p.updateCellScore(coor, p.calcScore())){
			p.revertPath();
			return;
		}
		
		//proceed the recursion
		int row = coor.getRow();
		int col = coor.getCol();
		
		Cell left = map.getCell(row, col-1);
		Cell right = map.getCell(row, col+1);
		Cell up = map.getCell(row-1, col);
		Cell down = map.getCell(row+1, col);
		
		if (left != null && left.getGameObj().isPassable())
			findPath(left, destination, p);
		if (right != null && right.getGameObj().isPassable())
			findPath(right, destination, p);
		if (up != null && up.getGameObj().isPassable())
			findPath(up, destination, p);
		if (down != null && down.getGameObj().isPassable())
			findPath(down, destination, p);	
		
		//reset the state of the Pokemon or Station if they are used at this recursion
		if (capture){
			p.rollback(gobj);
		}
		
		p.revertScore(coor, oldScore);
		p.revertPath();
		return;
	}

	/**
	 * The function save the result of the findPath algorithm to the outputFile
	 * @param outputFile	File object created in the main() for saving the result
	 * @throws IOException
	 */
	private void gameResult(File outputFile) throws IOException{
		BufferedWriter bw = new BufferedWriter( new FileWriter(outputFile));
		bw.write(String.valueOf(optPlayer.getScore(map.getDestination().getCoor())));
		bw.newLine();
		bw.write(optPlayer.formattedPlayerInfo());
		bw.newLine();
		
		java.util.Stack<Coordinate> path = optPlayer.getPath();
		
		for(int i=0; ; i++){
			bw.write(path.get(i).toString());
			if (i == path.size()-1 )
				break;
			bw.write("->");
		}
		
		bw.close();
	}
	
	/**
	 * Entry point of the program and create a timer to write local optimal every 15s
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		//TODO:disable debug
//		java.io.PrintStream out = new java.io.PrintStream(new java.io.FileOutputStream("consoleOut.txt"));
//		System.setOut(out);
		
		File inputFile = new File("./customIn.txt");
		outputFile = new File("./customOut.txt");
		
		if (args.length > 0) {
			inputFile = new File(args[0]);
		} 

		if (args.length > 1) {
			outputFile = new File(args[1]);
		}
		
//		long startTime = System.currentTimeMillis();

		Game game = new Game();
		game.initialize(inputFile);
		
		//write result every 15s using timer schedule
		new Timer().schedule(new TimerTask() {
			public void run(){
				try {
					game.gameResult(outputFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 15000, 15000);
		
		game.start();
		
//		long endTime = System.currentTimeMillis();
//		System.out.println((endTime - startTime)/1000.0);
		
		//write result
		game.gameResult(outputFile);
		
		System.exit(0);
	}
	
}
