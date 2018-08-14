public class Deadwood {
	public Deadwood(){
	}

	/*
	Creates a new GameSystem object.
	Then calls the newGame function in the game system to create a new game
	passing in the board from BoardLayersListener

	 */
	public static void main(String[] args){
		// Creates a new game
		GameSystem the_system = new GameSystem();
		BoardLayersListener board = new BoardLayersListener(the_system);
		board.setVisible(true);
		the_system.newGame(board);
	}
}
