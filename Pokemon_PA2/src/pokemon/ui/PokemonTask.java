package pokemon.ui;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import pokemon.Coordinate;
import pokemon.Map;
import pokemon.Pokemon;

/**
 * Created by Stanley on 11/9/2016.
 */
class PokemonTask implements Runnable{
	private static final int MIN_SLEEP_TIME = 1000;
	private static final int MAX_SLEEP_TIME = 2000;
	private static final int MIN_RESPAWN_TIME = 3;
	private static final int MAX_RESPAWN_TIME = 5;

	private static Map map;
	private Pokemon poke;
	
	public static void setPokeTaskMap(Map m){ map = m; }
	
	PokemonTask(Pokemon poke){
		this.poke = poke;
	}
	
	@Override
	public void run() {
		Random r = new Random();
		
		while(poke.isActive()){

			try {
				if (poke.isRespawning()){
					//sleep
					TimeUnit.SECONDS.sleep(r.nextInt(MAX_RESPAWN_TIME - MIN_RESPAWN_TIME) + MIN_RESPAWN_TIME);

					//pause the task
					while(poke.isPaused())
						TimeUnit.SECONDS.sleep(1);

					//exit immediately if object is not active anymore
					if(!poke.isActive())
						break;

					poke.setCoor(map.teleportGameObject(poke));
					Platform.runLater(() -> poke.getImageView().setVisible(true));
					poke.setRespawning(false);
				}
				else {
					TimeUnit.MILLISECONDS.sleep(r.nextInt(MAX_SLEEP_TIME - MIN_SLEEP_TIME) + MIN_SLEEP_TIME);
					while(poke.isPaused())
						TimeUnit.SECONDS.sleep(1);

					//exit immediately if object is not active anymore
					if(!poke.isActive())
						break;

					int dir;
					int count = 0;                  //to prevent over CPU consumption, break and sleep after 100 loops (should be more than enough to generate a valid move if such move exists)
					Coordinate destCoor;
					do {
						dir = r.nextInt(4) + 1;
						destCoor = map.moveGameObject(poke.getCoor(), dir);

						++count;
					} while (destCoor == null && count < 100);

					if(count >= 100) {
						System.err.println("valid move not found, putting thread to 5s sleep...");
						TimeUnit.SECONDS.sleep(5);
					}
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
	}
}
