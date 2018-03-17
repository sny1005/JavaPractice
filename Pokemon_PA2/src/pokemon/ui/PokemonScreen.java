package pokemon.ui;

import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pokemon.Cell;
import pokemon.Coordinate;
import pokemon.Game;
import pokemon.GameObject;
import pokemon.Map;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Station;

public class PokemonScreen extends Application {
	/**
	 * width of the window
	 */
	private static int W = 800;

	/**
	 * height of the window
	 */
	private static int H = 400;


	// this define the size of one CELL
	private static int STEP_SIZE = 40;
	
	// urls of the avatar images
	private static final String frontf = new File("icons/front.png").toURI().toString();
	private static final String backf = new File("icons/back.png").toURI().toString();
	private static final String leftf = new File("icons/left.png").toURI().toString();
	private static final String rightf = new File("icons/right.png").toURI().toString();

	// url of the exit image
	private static final String exitf = new File("icons/exit.png").toURI().toString();
	
	// url of the battle imgae
	private static final String caughtBattle = new File("battles/caught.gif").toURI().toString();
	private static final String failedBattle = new File("battles/failed.gif").toURI().toString();

	private Image front;
	private Image back;
	private Image left;
	private Image right;

	private ImageView avatar;

	private Player player;
	private Coordinate playerCurLoc;
	private double currentPosx = 0;
	private double currentPosy = 0;

	private Scene scene;
	private Group mapGroup;
	private AnimationTimer timer;
	private Label gameStatus;
	private Button pauseBtn;
	private Button resumeBtn;

	private java.util.concurrent.Executor executor;
	
	protected Map map;

	// these booleans correspond to the key pressed by the user
	boolean goUp, goDown, goRight, goLeft;

	private boolean stop = false;
	private boolean pause = false;


	// initializer block
	{
		front = new Image(frontf);
		back = new Image(backf);
		left = new Image(leftf);
		right = new Image(rightf);
	}

	@Override
	public void start(Stage stage) throws Exception {

		map = Game.initialize();
		
		// initialize player
		player = new Player(map.getBase());
		playerCurLoc = player.getCurLoc();
		currentPosx = playerCurLoc.getCol()*STEP_SIZE;
		currentPosy = playerCurLoc.getRow()*STEP_SIZE;

		// at the beginning lets set the image of the avatar front
		avatar = new ImageView(front);
		avatar.setFitHeight(STEP_SIZE);
		avatar.setFitWidth(STEP_SIZE);
		avatar.setPreserveRatio(true);

		// setting up the map
		mapGroup = new Group();

		for (int i=0, y=0; i<map.getNumOfRows(); i++, y+=STEP_SIZE){
			for (int j=0, x=0; j<map.getNumOfCols(); j++, x+=STEP_SIZE){
				Cell cell = map.getCell(i, j);
				if (cell != null && cell.getGameObj() != null) {
					ImageView view = cell.getGameObj().getImageView();
					view.relocate(x, y);

					mapGroup.getChildren().add(view);
				}
			}
		}
		
		ImageView view = new ImageView(new Image(exitf));
		view.setFitHeight(40);
		view.setFitWidth(40);
		view.setPreserveRatio(true);
		view.relocate(map.getDestination().getCol()*40, map.getDestination().getRow()*40);
		mapGroup.getChildren().add(view);

		avatar.relocate(currentPosx, currentPosy);
		mapGroup.getChildren().add(avatar);
		mapGroup.requestFocus();

		// setting up the information panel
		VBox infoPanel = new VBox(20);
		setUpInfoPanel(infoPanel);

		// setting up container for everything to be displayed
		HBox container = new HBox();
		container.setPadding(new Insets(10, 10, 10, 10));
		container.setSpacing(50);
		container.getChildren().addAll(mapGroup, infoPanel);

		// create scene with W and H and color of backgorund
		scene = new Scene(container, Color.SANDYBROWN);

		// add listener on key pressing
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (stop || pause)
					return;

				switch (event.getCode()) {
				case UP:
					goUp = true;
					avatar.setImage(back);
					break;
				case DOWN:
					goDown = true;
					avatar.setImage(front);
					break;
				case LEFT:
					goLeft = true;
					avatar.setImage(left);
					break;
				case RIGHT:
					goRight = true;
					avatar.setImage(right);
					break;
				default:
					break;
				}
			}
		});

		// add listener key released
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(pause)
					return;

				switch (event.getCode()) {
				case UP:
					goUp = false;
					break;
				case DOWN:
					goDown = false;
					break;
				case LEFT:
					goLeft = false;
					break;
				case RIGHT:
					goRight = false;
					break;
				default:
					break;
				}
				stop = false;
			}
		});

		//show the window
		stage.setScene(scene);
		stage.show();

		//start running the threads
		executor = java.util.concurrent.Executors.newFixedThreadPool(4, r -> {
			Thread t = java.util.concurrent.Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		});

		PokemonTask.setPokeTaskMap(map);
		for (Pokemon p: map.getPokearr()){
			executor.execute(new PokemonTask(p));
//			Thread thread = new Thread(new PokemonTask(p));
//			thread.setName(p.getPokeName());
//			thread.setDaemon(true);
//			thread.start();
		}
		StationTask.setStnTaskMap(map);
		for (Station s: map.getStnarr()){
			executor.execute(new StationTask(s));
//			Thread thread = new Thread(new StationTask(s));
//			thread.setDaemon(true);
//			thread.start();
		}

		// it will execute this periodically
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (stop || pause)
					return;

				int dx = 0, dy = 0;
				if (goUp) {
					Cell cell = map.getCell(playerCurLoc, 1);
					if (checkPassable(cell)){
						dy -= (STEP_SIZE);
						playerCurLoc = cell.getCoor();
					}
					else
						return;
				} else if (goDown) {
					Cell cell = map.getCell(playerCurLoc, 3);
					if (checkPassable(cell)) {
						dy += (STEP_SIZE);
						playerCurLoc = cell.getCoor();
					}
					else
						return;
				} else if (goRight) {
					Cell cell = map.getCell(playerCurLoc, 2);
					if (checkPassable(cell)) {
						dx += (STEP_SIZE);
						playerCurLoc = cell.getCoor();
					}
					else
						return;
				} else if (goLeft) {
					Cell cell = map.getCell(playerCurLoc, 4);
					if (checkPassable(cell)) {
						dx -= (STEP_SIZE);
						playerCurLoc = cell.getCoor();
					}
					else
						return;
				} else {
					// no key was pressed, return...
					checkCollision();
					return;
				}
				moveAvatarBy(dx, dy);
				checkCollision();
			}
		};
		// start the timer
		timer.start();
	}

	private void checkCollision(){
		
		GameObject obj = map.getCell(playerCurLoc).getGameObj();
		if (obj != null && obj.isActive()){
			if (obj instanceof Pokemon){
				if (player.catchPokemon((Pokemon) obj)) {           //pokemon is caught successfully
					this.removeImageView(obj.getImageView());
					map.deleteGameObjectFromMap(obj);
					gameStatus.setText("Pokemon Caught!");
					gameStatus.setTextFill(Color.GREEN);

					pause = true;
					enterBattleScene(true);
				}
				else {                                              //not enough pokeball to catch the pokemon
					if(!((Pokemon) obj).isRespawning()){
						
						obj.getImageView().setVisible(false);
						((Pokemon) obj).setRespawning(true);
						gameStatus.setText("NOT enough pokemon ball");
						gameStatus.setTextFill(Color.RED);

						pause = true;
						enterBattleScene(false);
					}
				}
			}
			else if (obj instanceof Station) {
				// same procedure as pokemon in stationTask
				player.collectStation((Station) obj);
				obj.getImageView().setVisible(false);
			}
		}
		else if(obj == null){
			if (playerCurLoc.equals(map.getDestination())){
				gameStatus.setText("end game!");
				gameStatus.setTextFill(Color.GREEN);
				resumeBtn.setDisable(true);
				pauseBtn.setDisable(true);
				scene.setOnKeyPressed(null);
				scene.setOnKeyReleased(null);
				timer.stop();
				timer = null;
				
				// terminate all threads safely
				for (Pokemon p: map.getPokearr()){
					p.setActive(false);
				}
				for (Station s: map.getStnarr()){
					s.setActive(false);
				}
			}
		}
	}

	private void enterBattleScene(boolean success) {
		// pause everything
		for(Pokemon p: map.getPokearr())
			p.setPaused(true);
		for(Station s: map.getStnarr())
			s.setPaused(true);

		Stage stage = new Stage();
		// play battle video using extra window
		Thread thr = new Thread(()->{
			ImageView battleView = new ImageView();
			if (success)
				battleView.setImage(new Image(caughtBattle));
			else
				battleView.setImage(new Image(failedBattle));
			Pane pane = new Pane();
			pane.getChildren().add(battleView);

			Platform.runLater(()->{
					Scene scene = new Scene(pane);
					stage.setScene(scene);
					stage.show();
			});

            try {
	            if(success)
		            Thread.sleep(13540);
	            else
					Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Platform.runLater(()->stage.close());

			// resume everything
			for(Pokemon p: map.getPokearr())
				p.setPaused(false);
			for(Station s: map.getStnarr())
				s.setPaused(false);
			pause = false;

			System.gc();
        });

		thr.start();
	}

	private void setUpInfoPanel(VBox infoPanel) {
		Label score = new Label("Current score: 0");                        //Score display
		IntegerProperty scoreProperty = player.getScoreProperty();
		scoreProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				score.textProperty().set("Current score: " + newValue.intValue());
			}
		});

		Label numPoke = new Label("# of Pokemons caught: 0");               //Number of Pokemon display
		IntegerProperty pokeNumProperty = player.getNumPokeCaughtProperty();
		pokeNumProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				numPoke.textProperty().set("# of Pokemons caught: " + newValue.intValue());
			}
		});

		Label numBall = new Label("# of Pokeball owns: 0");                 //Number of Pokeball display
		IntegerProperty ballNumProperty = player.getNumPokeBallProperty();
		ballNumProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				numBall.textProperty().set("# of Pokeballs owns: " + newValue.intValue());
			}
		});

		gameStatus = new Label();
		gameStatus.setMinWidth(140);

		resumeBtn = new Button("Resume");
		resumeBtn.setOnAction( (evt) -> {
				for(Pokemon p: map.getPokearr())
					p.setPaused(false);
				for(Station s: map.getStnarr())
					s.setPaused(false);
				pause = false;
			}
		);

		pauseBtn = new Button("Pause");
		pauseBtn.setOnAction( (evt) -> {
				for(Pokemon p: map.getPokearr())
					p.setPaused(true);
				for(Station s: map.getStnarr())
					s.setPaused(true);
				pause = true;
			}
		);

		HBox buttons = new HBox();
		buttons.getChildren().addAll(resumeBtn, pauseBtn);

		infoPanel.getChildren().addAll(score, numPoke, numBall, gameStatus, buttons);
	}

	private boolean checkPassable(Cell cell) {
		if (cell == null)
			return false;
        else if (cell.getGameObj() == null || cell.getGameObj().isPassable())
			return true;

		return false;
	}

	private void moveAvatarBy(int dx, int dy) {
		final double cx = avatar.getBoundsInLocal().getWidth() / 2;
		final double cy = avatar.getBoundsInLocal().getHeight() / 2;
		double x = cx + avatar.getLayoutX() + dx;
		double y = cy + avatar.getLayoutY() + dy;
		moveAvatar(x, y);
	}

	private void moveAvatar(double x, double y) {
		final double cx = avatar.getBoundsInLocal().getWidth() / 2;
		final double cy = avatar.getBoundsInLocal().getHeight() / 2;

		if (x - cx >= 0 && x + cx <= W && y - cy >= 0 && y + cy <= H) {
            // relocate ImageView avatar
			avatar.relocate(x - cx, y - cy);
			
			//update position
			currentPosx = x - cx;
			currentPosy = y - cy;

			//decrease the score by 1
			player.reduceScore();

			// I moved the avatar lets set stop at true and wait user release the key :)
			stop = true;

			// reset game status display
			gameStatus.setText("");
		}
	}
	
	
	private void removeImageView(ImageView view){
		mapGroup.getChildren().remove(view);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
