package application;

import application.Mahjong.MahjongSuit;

public class KeepStack extends Stack implements Dropable {
	
	private final MahjongSuit keptSuit;

	public KeepStack(double translateX, double translateY, MahjongSuit keptSuit) {
		super(translateX, translateY, -3);
		this.keptSuit = keptSuit;
	}
	
	
	public MahjongSuit getKeptSuit() {
		return keptSuit;
	}

	
	@Override
	public boolean allowDrop(DragStack dragStack) {
		if (!dragStack.hasOneCard()) return false;
		if (dragStack.bottom().getSuit() != keptSuit) return false;
		if (dragStack.bottom().getRank() - getTopRank() != 1) return false;
		return true;
	}
	
	@Override
	public void drop(DragStack dragStack) {
		addCard(dragStack.clear().get(0));
	}
	
	
	public int getTopRank() {
		if (isEmpty()) return 0;
		return getTopCard().getRank();
	}

}
