package application;

import java.util.ArrayList;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class DragStack extends Stack {
	
	private Relocatable formerStack;
	
	public DragStack(double translateX, double translateY, Relocatable formerStack) {
		super(translateX, translateY, 30); System.out.println("DragStack ctor formerStack: " + formerStack);
		this.formerStack = formerStack;
	}
	
	
	public Mahjong bottom() {
		return cards.get(0);
	}
	
	public boolean hasOneCard() {
		return stackSize == 1;
	}
	
	public void update(double x, double y) {
		for (int i = 0; i < stackSize; i++) cards.get(i).setTranslate(x, y + i * shift);
	}
	
	public void dropSuccess(Dropable dropStack) {
		MahjongSolitaire.animateDrop(cards, (Stack)dropStack, 10); // drop contain clear(); // why not simply getCards(); ?
		dropStack.drop(this);
	}
	
	public void dropFail() {
		MahjongSolitaire.animateDrop(cards, (Stack)formerStack, 100);
		formerStack.drop(this);
	}
	
	
	
}
