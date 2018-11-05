package application;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Mahjong extends ImageView {
	
	public enum MahjongSuit {
		
		Cat(0), Pin(1), Man(2), Sou(3), Haku(4), Hatsu(5), Chun(6);
		
		private int i;
		
		private MahjongSuit(int i) {
			this.i = i;
		}
		
		public int toInt() {
			return i;
		}
		
		public static MahjongSuit fromInt(int i) {
			switch (i) {
				case 1 : return MahjongSuit.Pin;
				case 2 : return MahjongSuit.Man;
				case 3 : return MahjongSuit.Sou;
				default : return MahjongSuit.Cat;
			}
		}
		
	}
	
	
	private MahjongSuit suit;
	private int rank;
	
	private Stack occupiedStack;
	
	private boolean dragLock;
	private double dX;
	private double dY;
	
	public Mahjong(Image image, MahjongSuit suit, int rank) {
		
		super(image);
		setPreserveRatio(true);
		setFitWidth(MahjongSolitaire.CARD_WIDTH);
		setTranslate(MahjongSolitaire.DECK_X, MahjongSolitaire.DECK_Y);
		
		this.suit = suit;
		this.rank = rank;
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Pressed " + event.getSource());
				
				int index = occupiedStack.indexOf((Mahjong)event.getSource());
				dragLock = false;
				if (MahjongSolitaire.isActionLock()) {
					dragLock = true; System.out.println("ActionLock");
				}
				else if (!(occupiedStack instanceof Dragable)) {
					dragLock = true; System.out.println("UnDragable" + occupiedStack);
				}
				else if (!((Dragable)occupiedStack).allowDrag(index)) {
					dragLock = true; System.out.println("AllowDragFalse");
				}

				System.out.println("dragLock: " + dragLock);
				
				if (dragLock) return;
				((Dragable)occupiedStack).drag(index);
				dX = event.getSceneX() - getTranslateX();
				dY = event.getSceneY() - getTranslateY();
			}
		});
		
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (dragLock) return;
				((DragStack)occupiedStack).update(event.getSceneX() - dX, event.getSceneY() - dY);
			}
		});
		
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Released");
				
				if (dragLock) return;
				
				DragStack dragStack = (DragStack)occupiedStack;
				
				Dropable dropStack = MahjongSolitaire.getDropStack(event.getSceneX() - dX, event.getSceneY() - dY);
				boolean dropFail = false;
				if (dropStack == null) {
					dropFail = true; System.out.println("NullDropStack");
				}
				else if (!dropStack.allowDrop(dragStack)) {
					dropFail = true; System.out.println("AllowDropFalse");
				}
				
				System.out.println("dropFail: " + dropFail);
				
				if (dropFail) {
					dragStack.dropFail();
				}
				else {
					dragStack.dropSuccess(dropStack); System.out.println("dropStack: " + dropStack);
					//MahjongSolitaire.update(); call upon dropSuccess
				}
				
				System.out.println();
			}
		});
		
	}
	
	
	public MahjongSuit getSuit() {
		return suit;
	}
	public int getRank() {
		return rank;
	}
	public Stack getOccupiedStack() {
		return occupiedStack;
	}
	public void setOccupiedStack(Stack occupiedStack) {
		this.occupiedStack = occupiedStack;
	}
	
	public boolean isDragon() {
		return suit == MahjongSuit.Haku || suit == MahjongSuit.Hatsu || suit == MahjongSuit.Chun;
	}
	
	public void setTranslate(double x, double y) {
		setTranslateX(x);
		setTranslateY(y);
	}
	
	public String toString() {
		return suit.toString() + rank;
	}

}
