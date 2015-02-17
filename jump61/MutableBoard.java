package jump61;

import java.util.Stack;
import static jump61.Side.*;
import static jump61.Square.square;

/** A Jump61 board state that may be modified.
 *  @author Nick Dill
 */
class MutableBoard extends Board {

    /** Stores the size of the board. */
    private int _size = 0;

    /** Data structure for the mutable board. */
    private Square[][] _board = new Square[0][0];

    /** Temporary board used for making copies. */
    private Square[][] _tmp = new Square[0][0];


    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _size = N;
        _board = new Square[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                _board[r][c] = square(WHITE, 1);
            }
        }
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear. */
    MutableBoard(Board board0) {
        _size = board0.size();
        copy(board0);
    }

    @Override
    void clear(int N) {
        _size = N;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                _board[r][c] = square(WHITE, 1);
            }
        }
        announce();
    }

    @Override
    void copy(Board board) {
        _board = new Square[_size][_size];
        for (int r = 0; r < _size; r++) {
            for (int c = 0; c < _size; c++) {
                _board[r][c] = board.get(r + 1, c + 1);
            }
        }
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history.  Assumes BOARD and I have the same size. Modified to return
     *  a copy of a board. */
    private Square[][] internalCopy(Square[][] board) {
        _tmp = new Square[_size][_size];
        for (int r = 0; r < _size; r++) {
            for (int c = 0; c < _size; c++) {
                _tmp[r][c] = _board[r][c];
            }
        }
        return _tmp;
    }

    @Override
    int size() {
        return _size;
    }

    @Override
    Square get(int n) {
        return _board[row(n) - 1][col(n) - 1];
    }

    @Override
    Square get(int r, int c) {
        return _board[r - 1][c - 1];
    }

    @Override
    int numOfSide(Side side) {
        int count = 0;
        for (int i = 0; i < _size * _size; i++) {
            if (get(i).getSide() == side) {
                count++;
            }
        }
        return count;
    }

    @Override
    int numPieces() {
        int count = 0;
        for (int i = 0; i < _size * _size; i++) {
            count += get(i).getSpots();
        }
        return count;
    }

    @Override
    void addSpot(Side player, int r, int c) {
        history.push(internalCopy(_board));
        if ((r > 0 && c > 0) && (r <= _size && c <= _size)) {
            int tmp = sqNum(r, c);
            int spot = get(tmp).getSpots();
            _board[r - 1][c - 1] = square(player, spot + 1);
            checkSize(tmp, player);
            announce();
        }
    }

    /** Checks if adding a SIDE spot at IND overfills a square. */
    void checkSize(int ind, Side side) {
        int stack = get(ind).getSpots();
        if (stack > neighbors(ind)) {
            int x = row(ind);
            int y = col(ind);
            _board[row(ind) - 1][col(ind) - 1] = square(side, 1);
            doMath(side, x - 1, y);
            doMath(side, x + 1, y);
            doMath(side, x, y - 1);
            doMath(side, x, y + 1);
        }
    }

    /** Adds a PLAYER spot to R C without adding an extra
        move to the undo history. */
    void doMath(Side player, int r, int c) {
        if (getWinner() != null) {
            getWinner();
        } else {
            if ((r > 0 && c > 0) && (r <= _size && c <= _size)) {
                int tmp = sqNum(r, c);
                int spot = get(tmp).getSpots();
                _board[r - 1][c - 1] = square(player, spot + 1);
                checkSize(tmp, player);
            }
        }
    }

    @Override
    void addSpot(Side player, int n) {
        addSpot(player, row(n), col(n));
        announce();
    }

    @Override
    void set(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), square(player, num));
    }

    @Override
    void set(int n, int num, Side player) {
        internalSet(n, square(player, num));
        announce();
    }

    @Override
    void undo() {
        _board = history.pop();
    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
    }

    /** Set the contents of the square with index IND to SQ. Update counts
     *  of numbers of squares of each color.  */
    private void internalSet(int ind, Square sq) {
        _board[row(ind) - 1][col(ind) - 1] = sq;
    }

    /** Notify all Observers of a change. */
    private void announce() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoard)) {
            return obj.equals(this);
        } else {

            return false;
        }
    }

    @Override
    public int hashCode() {

        return 0;
    }

    /** Stores a history of moves for the undo command. */
    private Stack<Square[][]> history = new Stack<Square[][]>();

}
