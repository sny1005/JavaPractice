package pokemon;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.regex.*;

import pokemon.ui.PokemonList;
import pokemon.ui.PokemonScreen;

import javafx.scene.image.Image;

/**
 * This the entry class of the whole program, which contains the main function.
 * It is also responsible for file input and output. It should contain a Map and a Player.
 * @author lhschan (20187854)
 *
 */

public class Game{

	private static File inputFile;
	
	/**
	 * To parse in the data form the inputFile specified in main() and creates the Map,
	 * the Player and other GameObject in the game
	 * @throws Exception
	 */
	public static Map initialize() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		// Read the first of the input file
		String line = br.readLine();
		int M = Integer.parseInt(line.split(" ")[0]);
		int N = Integer.parseInt(line.split(" ")[1]);
		
		// define a map
		Map map = new Map(M, N);
		
		// Read the following M lines of the Map
		for (int i = 0; i < M; i++) {
			line = br.readLine();
			for (int j=0; j<line.length(); j++){
				map.setObj(i, j, line.charAt(j));
			}
		}
		
		// Find the number of stations and pokemons in the map 
		// Continue read the information of all the stations and pokemons by using br.readLine();
		int pokeNum = map.getPokeNum();
		int stnNum = map.getStnNum();

		Pattern p = Pattern.compile("<(\\d+),\\s*(\\d+)>,\\s*(\\w+),\\s*(\\w+),\\s*(\\w+),\\s*(\\w+)");
		for (int i=0; i<pokeNum; i++){
			line = br.readLine();
			Matcher m = p.matcher(line);
	
			if (m.find()){
				Pokemon tp = (Pokemon) map.getCell(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)))
						.getGameObj();
				tp.setAttr(m.group(3), m.group(4), Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));

				// construct url to the icon of the pokemon
				String path = "icons/" + PokemonList.getIdOfFromName(m.group(3)) + ".png";
				tp.setImageV(new Image(new File(path).toURI().toString()));

			}
			else{
				br.close();
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
				br.close();
				throw new Exception("Station data parsing error!");
			}
			
		}
		
		br.close();
		return map;
	}

	public static void main(String[] args){
		inputFile = new File("./sampleIn.txt");

		if (args.length > 0) {
			inputFile = new File(args[0]);
		}

		PokemonScreen.main(args);
	}
}
