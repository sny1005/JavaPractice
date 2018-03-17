package pokemon.ui;

import javafx.application.Platform;
import pokemon.Station;
import pokemon.Map;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stanley on 11/9/2016.
 */
class StationTask implements Runnable {
	private static final int MIN_RESPAWN_TIME = 5;          // in seconds
	private static final int MAX_RESPAWN_TIME = 10;         // in seconds

	private static Map map;
	private Station stn;

	public static void setStnTaskMap(Map m){ map = m; }

	StationTask(Station stn){
		this.stn = stn;
	}

	@Override
	public void run() {
		Random r = new Random();

		while(stn.isActive()){

			try {
				if (stn.isRespawning()){
					//sleep
					TimeUnit.SECONDS.sleep(r.nextInt(MAX_RESPAWN_TIME - MIN_RESPAWN_TIME) + MIN_RESPAWN_TIME);
					while(stn.isPaused())
						TimeUnit.SECONDS.sleep(1);

					//exit immediately if object is not active anymore
					if(!stn.isActive())
						break;

					stn.setCoor(map.teleportGameObject(stn));
					Platform.runLater(() -> stn.getImageView().setVisible(true));
					stn.setRespawning(false);
				}
				else{
					TimeUnit.SECONDS.sleep(1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
	}
}
