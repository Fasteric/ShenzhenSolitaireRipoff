package application;

import java.util.ArrayList;
import java.util.Random;

import application.Mahjong.MahjongSuit;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MahjongSolitaire extends Application {
	
	/*** LAYOUT SETUP ***/
	public static final double LEFT_PADDING 	= 30;
	public static final double TOP_PADDING 		= 60;
	public static final double CARD_WIDTH 		= 142;
	public static final double CARD_SPACE 		= 160;
	public static final double LARGE_SHIFT 		= 30;
	public static final double SMALL_SHIFT 		= -3;
	public static final double PLAY_X 			= LEFT_PADDING;
	public static final double PLAY_Y 			= 350;
	public static final double TEMP_X 			= LEFT_PADDING;
	public static final double TEMP_Y 			= TOP_PADDING;
	public static final double KEEP_X 			= LEFT_PADDING + 5 * CARD_SPACE;
	public static final double KEEP_Y 			= TOP_PADDING;
	public static final double DECK_X 			= LEFT_PADDING + 4 * CARD_SPACE;
	public static final double DECK_Y 			= TOP_PADDING;
	public static final int ANIMATION_MILLIS	= 0; // unset unapply;
	
	
	private static boolean actionLock;
	private static int pendingAnimation = 0;
	
	private static PlayStack[] playStacks = new PlayStack[8];
	private static TempStack[] tempStacks = new TempStack[3];
	private static KeepStack[] keepStacks = new KeepStack[3];
	private static LockButton[] lockButtons = new LockButton[3];

	private static Button newgameBtn = new Button("New game");
	private static Button howtoBtn = new Button("Instruction");
	private static ImageView howtoImgView = new ImageView();
	
	/*** start ***/
	public static Pane pane = new Pane();
	
	@Override
	public void start(Stage primaryStage) {
		
		Scene scene = new Scene(pane, 1366, 768);
		
		primaryStage.setTitle("MahjongSolitaire");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		load();
		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/*** CONTROL ***/
	private static ArrayList<Mahjong> deck = deckBuilder();
	
	public static void load() { // run once
		
		actionLock = true;
		
		for (int i = 0; i < 8; i++) playStacks[i] = new PlayStack(PLAY_X + i * CARD_SPACE, PLAY_Y);
		for (int i = 0; i < 3; i++) tempStacks[i] = new TempStack(TEMP_X + i * CARD_SPACE, TEMP_Y);
		for (int i = 0; i < 3; i++) keepStacks[i] = new KeepStack(KEEP_X + i * CARD_SPACE, KEEP_Y, MahjongSuit.fromInt(i+1));
		
		String path = "res/images/%s.png";
		lockButtons[2] = new LockButton(
				new Image(ClassLoader.getSystemResource(String.format(path, "LockRedDisable")).toString()),
				new Image(ClassLoader.getSystemResource(String.format(path, "LockRedHover")).toString()),
				new Image(ClassLoader.getSystemResource(String.format(path, "LockRedIdle")).toString()),
				LEFT_PADDING + 3 * CARD_SPACE, TOP_PADDING + 0 * 60, MahjongSuit.Chun
				);
		lockButtons[1] = new LockButton(
				new Image(ClassLoader.getSystemResource(String.format(path, "LockGreenDisable")).toString()),
				new Image(ClassLoader.getSystemResource(String.format(path, "LockGreenHover")).toString()),
				new Image(ClassLoader.getSystemResource(String.format(path, "LockGreenIdle")).toString()),
				LEFT_PADDING + 3 * CARD_SPACE, TOP_PADDING + 1 * 60, MahjongSuit.Hatsu
				);
		lockButtons[0] = new LockButton(
				new Image(ClassLoader.getSystemResource(String.format(path, "LockWhiteDisable")).toString()),
				new Image(ClassLoader.getSystemResource(String.format(path, "LockWhiteHover")).toString()),
				new Image(ClassLoader.getSystemResource(String.format(path, "LockWhiteIdle")).toString()),
				LEFT_PADDING + 3 * CARD_SPACE, TOP_PADDING + 2 * 60, MahjongSuit.Haku
				);
		
		pane.getChildren().addAll(playStacks);
		pane.getChildren().addAll(tempStacks);
		pane.getChildren().addAll(keepStacks);
		pane.getChildren().addAll(lockButtons);
		
		newgameBtn.setTranslateX(LEFT_PADDING);
		newgameBtn.setTranslateY(10);
		newgameBtn.setPrefSize(CARD_WIDTH, TOP_PADDING / 2);
		newgameBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (isActionLock()) return;
				reset();
			}
		});
		pane.getChildren().add(newgameBtn);
		
		howtoBtn.setTranslateX(LEFT_PADDING + 1 * CARD_SPACE);
		howtoBtn.setTranslateY(10);
		howtoBtn.setPrefSize(CARD_WIDTH, TOP_PADDING / 2);
		pane.getChildren().add(howtoBtn);
		
		setup();
		
	}
	
	public static void reset() {
		
		actionLock = true;
		
		pane.getChildren().removeAll(deck);
		
		for (int i = 0; i < 8; i++) playStacks[i].clear();
		for (int i = 0; i < 3; i++) tempStacks[i].clear();
		for (int i = 0; i < 3; i++) keepStacks[i].clear();
		for (int i = 0; i < 3; i++) lockButtons[i].disable();
		
		setup();
		
	}
	
	public static void setup() {
		
		addPendingAnimation();
		
		pane.getChildren().addAll(deck);
		
		Random random = new Random();
		
		for (int i = 0; i < 512; i++) {
			int a = random.nextInt(deck.size());
			int b = random.nextInt(deck.size());
			Mahjong temp = deck.get(a);
			deck.set(a, deck.get(b));
			deck.set(b, temp);
		}
		
		SequentialTransition seq = new SequentialTransition();
		
		for (int i = 0; i < deck.size(); i++) {
			Mahjong card = deck.get(i);
			card.setTranslate(DECK_X, DECK_Y);
			PlayStack stack = playStacks[i % 8];
			TranslateTransition tt = new TranslateTransition(Duration.millis(100), card);
			tt.setToX(stack.getTranslateX());
			tt.setToY(stack.getTopTranslateY());
			seq.getChildren().add(tt);
			stack.addCard(card);
		}
		
		seq.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				removePendingAnimation();
				update();
			}
		});
		
		seq.play();
		
		actionLock = false;
		
	}
	
	public static void update() {
		// INCOMPLETE
		System.out.println("update");
		
		ArrayList<Mahjong> tops = fetchTop();
		
		if (isComplete(tops)) return;
		
		if (executeKeep(tops)) return;
		
		updateDragonLock(tops);
		
	}
	
	
	private static ArrayList<Mahjong> fetchTop() {
		ArrayList<Mahjong> tops = new ArrayList<>();
		for (int i = 0; i < 8; i++) if (!playStacks[i].isEmpty()) tops.add(playStacks[i].getTopCard());
		for (int i = 0; i < 3; i++) if (!tempStacks[i].isDragonLock() && !tempStacks[i].isEmpty()) tops.add(tempStacks[i].getTopCard());
		return tops;
	}
	
	private static boolean isComplete(ArrayList<Mahjong> tops) {
		if (!tops.isEmpty()) return false;
		for (int i = 0; i < 3; i++) if (keepStacks[i].getTopCard().getRank() != 9) return false;
		return true;
	}
	
	private static boolean executeKeep(ArrayList<Mahjong> tops) {
		
		int lowestKeep = 9;
		for (int i = 0; i < 3; i++) if (keepStacks[i].getTopRank() < lowestKeep) lowestKeep = keepStacks[i].getTopRank();
		
		ArrayList<Mahjong> lowestCards = new ArrayList<>();
		int lowestRank = 10;
		for (Mahjong card : tops) {
			
			int rank = card.getRank();
			
			if (rank < lowestRank) {
				lowestRank = card.getRank();
				lowestCards.clear();
			}
			if (rank == lowestRank) {
				lowestCards.add(card);
			}
			
		}
		
		if (lowestRank > lowestKeep + 1) return false;
		if (lowestCards.size() == 0) return false;
		
		addPendingAnimation();
		
		for (Mahjong card : lowestCards) {
			
			KeepStack stack = keepStacks[card.getSuit().toInt() - 1]; // always access valid keepStack
			
			if (card.getRank() - stack.getTopRank() == 1) {
				
				card.getOccupiedStack().removeTopCard();
				
				TranslateTransition tt = new TranslateTransition(Duration.millis(200), card);
				tt.setToX(stack.getTranslateX());
				tt.setToY(stack.getTopTranslateY());
				tt.setOnFinished(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						removePendingAnimation();
						update();
					}
				});
				
				stack.addCard(card);
				
				tt.play();
				
				return true;
				
			}
		}
		
		return false;
		
	}
	
	private static void updateDragonLock(ArrayList<Mahjong> tops) {
		
		for (int i = 0; i < 3; i++) lockButtons[i].disable();
		
		int[] dragonCount = new int[3];
		for (Mahjong card : tops) {
			int suit = card.getSuit().toInt();
			if (suit >= 4) {
				dragonCount[suit % 4]++;
				if (dragonCount[suit % 4] == 4) {
					lockButtons[suit % 4].enable();
				}
			}
		}
		
	}
	
	public static boolean executeDragonLock(MahjongSuit dragon) {
		
		TempStack stack = null;
		
		for (int i = 0; i < 3; i++) {
			if (tempStacks[i].isEmpty()) {
				stack = tempStacks[i];
				break;
			}
			if (tempStacks[i].getTopCard().getSuit() == dragon) {
				stack = tempStacks[i];
				break;
			}
		}
		
		if (stack == null) {
			System.out.println("NULLSTACK EXECUTEDRAGONLOCK IS NOT POSSIBLE");
			return false;
		}
		
		stack.setDragonLock(dragon);
		
		ArrayList<Mahjong> topCards = fetchTop();
		ArrayList<Mahjong> lockCards = new ArrayList<>();
		for (Mahjong card : topCards) {
			if (card.getSuit() != dragon) continue;
			card.getOccupiedStack().removeTopCard();
			lockCards.add(card);
		}
		
		animateDrop(lockCards, stack, 100);
		
		for (Mahjong card : lockCards) stack.addCard(card);
		
		return true;
		
	}
	
	/*** INTERFACE ***/
	public static void addPendingAnimation() {
		pendingAnimation++;
	}
	public static void removePendingAnimation() {
		pendingAnimation--;
	}
	public static boolean isActionLock() {
		return actionLock || pendingAnimation != 0;
	}
	
	public static Dropable getDropStack(double x, double y) {
		for (PlayStack s : playStacks) if (s.inSnapRange(x, y)) return s;
		for (TempStack s : tempStacks) if (s.inSnapRange(x, y)) return s;
		for (KeepStack s : keepStacks) if (s.inSnapRange(x, y)) return s;
		return null;
	}
	
	public static void animateDrop(ArrayList<Mahjong> dropCards, Stack dropStack, int ms) {
		
		addPendingAnimation();
		
		double x = dropStack.getTranslateX();
		double y = dropStack.getTopTranslateY();
		double shift = dropStack.getShift();
		
		ParallelTransition par = new ParallelTransition();
		
		for (int i = 0; i < dropCards.size(); i++) {
			TranslateTransition tt = new TranslateTransition(Duration.millis(ms), dropCards.get(i));
			tt.setToX(x);
			tt.setToY(y + i * shift);
			par.getChildren().add(tt);
		}
		
		par.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				removePendingAnimation();
				update();
			}
		});
		
		par.play();
		
	}
	
	
	private static ArrayList<Mahjong> deckBuilder() {
		
		ArrayList<Mahjong> deck = new ArrayList<>();
		
		String path = "res/images/%s.png";

		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin1")).toString()), MahjongSuit.Pin, 1));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin2")).toString()), MahjongSuit.Pin, 2));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin3")).toString()), MahjongSuit.Pin, 3));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin4")).toString()), MahjongSuit.Pin, 4));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin5")).toString()), MahjongSuit.Pin, 5));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin6")).toString()), MahjongSuit.Pin, 6));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin7")).toString()), MahjongSuit.Pin, 7));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin8")).toString()), MahjongSuit.Pin, 8));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Pin9")).toString()), MahjongSuit.Pin, 9));

		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man1")).toString()), MahjongSuit.Man, 1));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man2")).toString()), MahjongSuit.Man, 2));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man3")).toString()), MahjongSuit.Man, 3));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man4")).toString()), MahjongSuit.Man, 4));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man5")).toString()), MahjongSuit.Man, 5));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man6")).toString()), MahjongSuit.Man, 6));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man7")).toString()), MahjongSuit.Man, 7));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man8")).toString()), MahjongSuit.Man, 8));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Man9")).toString()), MahjongSuit.Man, 9));

		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou1")).toString()), MahjongSuit.Sou, 1));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou2")).toString()), MahjongSuit.Sou, 2));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou3")).toString()), MahjongSuit.Sou, 3));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou4")).toString()), MahjongSuit.Sou, 4));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou5")).toString()), MahjongSuit.Sou, 5));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou6")).toString()), MahjongSuit.Sou, 6));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou7")).toString()), MahjongSuit.Sou, 7));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou8")).toString()), MahjongSuit.Sou, 8));
		deck.add(new Mahjong(new Image(ClassLoader.getSystemResource(String.format(path, "Sou9")).toString()), MahjongSuit.Sou, 9));

		Image haku = new Image(ClassLoader.getSystemResource(String.format(path, "Haku")).toString());
		deck.add(new Mahjong(haku, MahjongSuit.Haku, 11));
		deck.add(new Mahjong(haku, MahjongSuit.Haku, 11));
		deck.add(new Mahjong(haku, MahjongSuit.Haku, 11));
		deck.add(new Mahjong(haku, MahjongSuit.Haku, 11));
		
		Image hatsu = new Image(ClassLoader.getSystemResource(String.format(path, "Hatsu")).toString());
		deck.add(new Mahjong(hatsu, MahjongSuit.Hatsu, 12));
		deck.add(new Mahjong(hatsu, MahjongSuit.Hatsu, 12));
		deck.add(new Mahjong(hatsu, MahjongSuit.Hatsu, 12));
		deck.add(new Mahjong(hatsu, MahjongSuit.Hatsu, 12));
		
		Image chun = new Image(ClassLoader.getSystemResource(String.format(path, "Chun")).toString());
		deck.add(new Mahjong(chun, MahjongSuit.Chun, 13));
		deck.add(new Mahjong(chun, MahjongSuit.Chun, 13));
		deck.add(new Mahjong(chun, MahjongSuit.Chun, 13));
		deck.add(new Mahjong(chun, MahjongSuit.Chun, 13));
		
		return deck;
		
	}
	
}
