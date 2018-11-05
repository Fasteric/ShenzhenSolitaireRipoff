package application;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public abstract class Stack extends ImageView {
	
	// ALL HAIL THE INVARIANT
	
	private static final double SNAP_RADIUS = 50;
	
	protected ArrayList<Mahjong> cards;
	protected final double shift;
	protected int stackSize;
	
	
	public Stack(double translateX, double translateY, double shift) {
		
		super(new Image(ClassLoader.getSystemResource("res/images/Empty.png").toString()));
		
		setPreserveRatio(true);
		setFitWidth(MahjongSolitaire.CARD_WIDTH);
		setTranslateX(translateX);
		setTranslateY(translateY);
		
		cards = new ArrayList<>();
		this.shift = shift;
		
	}
	
	public double getTranslateY(int index) {
		return getTranslateY() + index * shift;
	}
	
	public double getTopTranslateY() {
		return getTranslateY(stackSize);
	}
	
	public double getShift() {
		return shift;
	}
	
	public boolean isEmpty() {
		return stackSize == 0;
	}
	
	public int indexOf(Mahjong card) {
		return cards.indexOf(card);
	}
	
	public Mahjong getTopCard() {
		return cards.get(stackSize - 1);
	}
	
	public Mahjong removeTopCard() {
		return cards.remove(--stackSize);
	}
	
	public boolean inSnapRange(double x, double y) { // to be fix
		double dX = getTranslateX() - x;
		double dY = getTopTranslateY() - y;
		return dX * dX + dY * dY < SNAP_RADIUS * SNAP_RADIUS;
	}
	
	
	public void addCard(Mahjong card) {
		cards.add(card);
		card.setOccupiedStack(this);
		card.toFront();
		stackSize++;
	}
	
	public ArrayList<Mahjong> clear() {
		ArrayList<Mahjong> returnList = new ArrayList<>(cards);
		cards.clear();
		stackSize = 0;
		return returnList;
	}
	
	
	public String toString() {
		return this.getClass().toString() + " " + getTranslateX() + " " + getTranslateY();
	}
	
}
