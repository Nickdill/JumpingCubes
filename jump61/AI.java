package jump61;

import java.util.ArrayList;

/** An automated Player.
 *  @author Nick Dill
 */
class AI extends Player {

    /** Time allotted to all but final search depth (milliseconds). */
    private static final long TIME_LIMIT = 15000;

    /** Number of calls to minmax between checks of elapsed time. */
    private static final long TIME_CHECK_INTERVAL = 10000;

    /** Number of milliseconds in one second. */
    private static final double MILLIS = 1000.0;

    /** coordinates of a players move. */
    private ArrayList<Integer> moves = new ArrayList<Integer>();

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    /** Makes a move as the AI player. */
    void makeMove() {
        Game game = getGame();
        Board boardx = new MutableBoard(getBoard());
        int move = minmax(getSide(), boardx, 3, 3, moves);
        game.makeMove(move);
        game.message("%s moves %d %d.%n", getSide(), boardx.row(move),
            boardx.col(move));
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of DEPTH
     *  (where DEPTH == 0 denotes statically evaluating just the next move).
     *  If MOVESLIST is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int minmax(Side p, Board b, int depth, int cutoff,
        ArrayList<Integer> movesList) {

        int bval;
        int val;
        int best;
        int movenum = b.numPieces();

        if (depth < 0) {
            return staticEval(p, b);
        }
        if (p == getSide()) {
            best = 0;
            bval = Integer.MIN_VALUE;
            for (int i = 0; i < movenum; i += 1) {
                if (b.isLegal(p, i)) {
                    b.addSpot(p, i);
                    val = minmax(p.opposite(), b, depth - 1, cutoff, moves);
                    b.undo();
                    if (val > bval) {
                        best = i;
                        bval = val;
                    }
                }
            }
            if (depth == cutoff || depth == cutoff - 1) {
                if (bval == Integer.MIN_VALUE) {
                    for (int i = 0; i < movenum; i += 1) {
                        if (b.isLegal(p, i)) {
                            return i;
                        }
                    }
                } else {
                    return best;
                }
            }
            return bval;
        } else {
            bval = Integer.MAX_VALUE;
            for (int i = 0; i < movenum; i += 1) {
                if (b.isLegal(p, i)) {
                    b.addSpot(p, i);
                    val = minmax(p.opposite(), b, depth - 1, cutoff, moves);
                    b.undo();
                    if (val < bval) {
                        bval = val;
                    }
                }
            }
            return bval;
        }
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side p, Board b) {
        return (b.numOfSide(p) - b.numOfSide(p.opposite()));
    }

}
