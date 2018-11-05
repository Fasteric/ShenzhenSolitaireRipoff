package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DragonLockButton extends ImageView {
	
	Image disable, idle, hover;
	
	public DragonLockButton(Image disable, Image idle, Image hover) {
		
		super(disable);
		this.disable = disable;
		this.idle = idle;
		this.hover = hover;
		
	}
	
}
