package application;

public interface Dropable {
	
	boolean allowDrop(DragStack dragStack);
	
	void drop(DragStack dragStack);
	
}
