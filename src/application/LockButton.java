package application;

import application.Mahjong.MahjongSuit;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LockButton extends ImageView {
	
	private Image disable, idle, hover;
	private boolean enable;
	private MahjongSuit dragon;
	
	public LockButton(Image disable, Image hover, Image idle, double translateX, double translateY, MahjongSuit dragon) {
		
		super(disable);
		this.disable = disable;
		this.idle = idle;
		this.hover = hover;
		this.dragon = dragon;
		setTranslateX(translateX);
		setTranslateY(translateY);
		enable = false;
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (!enable || MahjongSolitaire.isActionLock()) return;
				enable = false;
				update();
				MahjongSolitaire.executeDragonLock(dragon);
			}
		});
		
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				update();
				if (enable) setImage(disable);
			}
		});

		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				update();
			}
		});
		
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				update();
				if (enable) setImage(hover);
			}
		});
		
		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				update();
			}
		});
		
	}
	
	
	public void disable() {
		enable = false;
		setImage(disable);
	}
	public void enable() {
		enable = true;
		setImage(idle);
	}
	
	private void update() {
		if (enable) setImage(idle);
		else setImage(disable);
	}
	
}
