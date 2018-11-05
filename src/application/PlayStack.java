package application;

public class PlayStack extends Stack implements Relocatable {

	public PlayStack(double translateX, double translateY) {
		super(translateX, translateY, 30);
	}

	@Override
	public boolean allowDrag(int index) {
		Mahjong belowCard;
		Mahjong aboveCard = cards.get(index);
		for (int i = index + 1; i < stackSize; i++) {
			belowCard = aboveCard;
			aboveCard = cards.get(i); System.out.println("allowDrag: " + aboveCard + ", " + belowCard);
			if (!isValidStack(belowCard, aboveCard)) return false;
		}
		return true;
	}

	@Override
	public boolean allowDrop(DragStack dragStack) {
		if (isEmpty()) return true;
		System.out.println("allowDrop: " + getTopCard() + " " + dragStack.bottom() + " ");
		if (!isValidStack(getTopCard(), dragStack.bottom())) return false;
		return true;
	}
	
	@Override
	public void drag(int index) {
		DragStack dragStack = new DragStack(getTranslateX(), getTranslateY(index), this);
		for (int i = index; i < stackSize; i++) dragStack.addCard(cards.get(i));
		for (int i = stackSize - 1; i >= index; i--) cards.remove(i);
		stackSize -= stackSize - index;
	}
	
	@Override
	public void drop(DragStack dragStack) {
		for (Mahjong card : dragStack.clear()) addCard(card);
	}
	
	
	private static boolean isValidStack(Mahjong below, Mahjong above) {
		if (below.isDragon() || above.isDragon()) return false;
		if (below.getSuit() == above.getSuit()) return false;
		if (below.getRank() - above.getRank() != 1) return false;
		return true;
	}
	
}
