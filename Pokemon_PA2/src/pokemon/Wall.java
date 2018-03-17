package pokemon;

import javafx.scene.image.Image;

import java.io.File;

public class Wall extends GameObject{
	// urls of wall image
	private static final String wallf = new File("icons/tree.png").toURI().toString();

	public Wall() {
		super(false, false);
		imageV.setImage(new Image(wallf));
	}

	public String toString(){
		return "#";
	}
}
