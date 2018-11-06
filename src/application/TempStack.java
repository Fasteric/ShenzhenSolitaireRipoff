package application;

import java.util.ArrayList;

import application.Mahjong.MahjongSuit;

public class TempStack extends Stack implements Relocatable {
	
	private boolean dragonLock;
	private MahjongSuit dragonType;

	
	public TempStack(double translateX, double translateY) {
		super(translateX, translateY, -3);
		dragonLock = false;
	}
	
	
	public boolean isDragonLock() {
		return dragonLock;
	}
	public MahjongSuit getDragonType() {
		return dragonType;
	}
	public void setDragonLock(MahjongSuit dragonType) {
		this.dragonType = dragonType;
		dragonLock = true;
	}
	
	
	@Override
	public boolean allowDrag(int index) {
		if (dragonLock) return false;
		return true;
	}

	@Override
	public boolean allowDrop(DragStack dragStack) {
		if (dragonLock) return false;
		if (!isEmpty()) return false;
		if (!dragStack.hasOneCard()) return false;
		return true;
	}
	
	@Override
	public void drag(int index) {
		DragStack dragStack = new DragStack(getTranslateX(), getTranslateY(index), this);
		dragStack.addCard(cards.remove(0));
		stackSize--;
	}
	
	@Override
	public void drop(DragStack dragStack) {
		addCard(dragStack.clear().get(0));
	}
	
	
	@Override
	public ArrayList<Mahjong> clear() {
		resetDragon();
		return super.clear();
	}
	
	public void resetDragon() {
		dragonLock = false;
	}

}
