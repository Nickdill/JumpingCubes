package jump61;

/** A Player that gets its moves from manual input.
 *  @author Nick Dill
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Side color) {
        super(game, color);
    }

    @Override
    /** Retrieve moves using getGame().getMove() until a legal one is found and
     *  make that move in getGame().  Report erroneous moves to player. */
    void makeMove() {
        Game game = getGame();
        if (game.getMove(coords)) {
            game.makeMove(coords[0], coords[1]);
        }
    }

    /** Coordinates of humanplayers move. */
    private int[] coords = new int[2];

}
