package assignment;

import java.awt.*;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {
    Piece.PieceType[][] board;
    Piece curr;
    Point pos;
    Result lastRes;
    Action lastAct;
    int rowsCleared;
    int maxHeight;
    int[] colHeight;
    int[] rowWidth;

    /**
     * Constructs a TetrisBoard by copying the given board
     * @param B the board to copy
     */
    public TetrisBoard(Board B){
        board = new Piece.PieceType[B.getWidth()][B.getHeight()];
        for(int i=0; i<B.getWidth(); i++){
            for(int j=0; j<B.getHeight(); j++){
                board[i][j] = B.getGrid(i, j);
            }
        }
        curr = B.getCurrentPiece();
        pos = B.getCurrentPiecePosition();
        lastRes = B.getLastResult();
        lastAct = B.getLastAction();
        rowsCleared = B.getRowsCleared();
        maxHeight = B.getMaxHeight();
        colHeight = new int[B.getWidth()];
        rowWidth = new int[B.getHeight()];
        for(int i=0; i<colHeight.length; i++){
            colHeight[i] = B.getColumnHeight(i);
        }
        for(int i=0; i<rowWidth.length; i++){
            rowWidth[i] = B.getRowWidth(i);
        }
    }

    /**
     * Constructs a TetrisBoard with the given width and height
     * @param width the width
     * @param height the height
     */
    public TetrisBoard(int width, int height) {
        board = new Piece.PieceType[width][height];
        rowsCleared = 0;
        maxHeight = 0;
        colHeight = new int[width];
        rowWidth = new int[height];
    }

    /**
     * Update the board with the given action and return its result
     * @param act the action
     * @return the result
     */
    @Override
    public Result move(Action act) { 
        lastAct = act;
        lastRes = Result.NO_PIECE;
        // Check if a piece is present
        if (curr == null) {
            return Result.NO_PIECE;
        }

        // Call the corresponding method for each action
        switch (act) {
            case LEFT:
                lastRes = shift(-1, 0, act);
                break;
            case RIGHT:
                lastRes = shift(1, 0, act);
                break;
            case DOWN:
                lastRes = shift(0, -1, act);
                break;
            case DROP:
                lastRes = drop();
                break;
            case CLOCKWISE:
                lastRes = rotateClockwise();
                break;
            case COUNTERCLOCKWISE:
                lastRes = rotateCounterClockwise();
                break;
            case NOTHING:
                lastRes = Result.SUCCESS;
                break;
        }
        return lastRes; 
    }

    /**
     * Check for any filled rows and clear them if necessary
     */
    private void clearRows(){
        TreeSet<Integer> Ys = new TreeSet<>(Collections.reverseOrder());
        for(Point p: curr.getBody()){
            Ys.add(pos.y + p.y);
        }
        
        // Iterate from the highest y to the lowest
        for(int y : Ys){
            if(rowWidth[y] == getWidth()){
                deleteRow(y);
            }
        }
    }

    /**
     * Delete the given row
     * @param y the row to delete
     */
    private void deleteRow(int y){
        // Shift all elements above the row down one row
        for(int i = y + 1; i <= board[0].length; i++){
            if(rowWidth[i-1] == 0) break;
            rowWidth[i-1] = 0;
            for(int j = 0; j < board.length; j++){
                if(i == board[0].length){
                    board[j][i-1] = null;
                }
                else{
                    board[j][i-1] = board[j][i];
                    if(board[j][i] != null)
                        rowWidth[i-1]++;
                }
            }
        }
        
        // Update instance variables
        rowsCleared++;
        maxHeight--;
        for(int i = 0; i < getWidth(); i++) {
            colHeight[i] = 0;
            for(int j = getHeight()-1; j >= 0; j--) {
                if(board[i][j] != null) {
                    colHeight[i] = j + 1;
                    break;
                }
            }
        }
    }

    /**
     * Drop the current piece to the correct position
     * @return OUT_BOUNDS if out of bounds, otherwise PLACE
     */
    private Result drop() {
        set(null);
        Point temp = pos;
        pos = new Point(pos.x, dropHeight(curr, pos.x));
        // Check for out of bounds
        if(!check(0, 0)) {
            pos = temp;
            set(curr.getType());
            return Result.OUT_BOUNDS;
        }
        set(curr.getType());
        place();
        return Result.PLACE;
    }

    /**
     * Place the current piece
     */
    private void place() {
        // Update instance variables
        for(Point p : curr.getBody()) {
            int x = pos.x + p.x, y = pos.y + p.y;
            maxHeight = Math.max(maxHeight, y + 1);
            colHeight[x] = Math.max(colHeight[x], y + 1);
            rowWidth[y]++;
        }
        clearRows();
    }

    /**
     * Shift the current piece by the given dx and dy
     * @param dx the change in x
     * @param dy the change in y
     * @param act the action
     * @return the result of the shift
     */
    private Result shift(int dx, int dy, Action act) {
        // Remove the original piece
        if(act == Action.LEFT || act == Action.RIGHT || act == Action.DOWN)
            set(null);
        
        // Check for out of bounds
        if(!check(dx, dy)) {
            set(curr.getType());
            // Place if shifted down
            if(act == Action.DOWN) {
                place();
                return Result.PLACE;
            }
            return Result.OUT_BOUNDS;
        }
        
        // Add the shifted piece
        pos = new Point(pos.x + dx, pos.y + dy);
        set(curr.getType());
        return Result.SUCCESS;
    }

    /**
     * Rotate the current piece clockwise, applying wall kicks as necessary
     * @return the result of the rotation
     */
    private Result rotateClockwise() {
        Point[] kicks;
        if(curr.getType() == Piece.PieceType.STICK) {
            kicks = Piece.I_CLOCKWISE_WALL_KICKS[curr.getRotationIndex()];
        } else {
            kicks = Piece.NORMAL_CLOCKWISE_WALL_KICKS[curr.getRotationIndex()];
        }
        set(null);
        Piece temp = curr;
        curr = curr.clockwisePiece();
        // Try each wall kick one by one
        for(Point kick : kicks) {
            if(check(kick.x, kick.y)) {
                // Shift the piece if the kick is successful
                return shift(kick.x, kick.y, Action.CLOCKWISE);
            }
        }
        // Reset the piece and return out of bounds if no kicks work
        curr = temp;
        set(curr.getType());
        return Result.OUT_BOUNDS;
    }

    /**
     * Rotate the current piece counterclockwise, applying wall kicks as necessary
     * @return the result of the rotation
     */
    private Result rotateCounterClockwise() {
        Point[] kicks;
        if(curr.getType() == Piece.PieceType.STICK) {
            kicks = Piece.I_COUNTERCLOCKWISE_WALL_KICKS[curr.getRotationIndex()];
        } else {
            kicks = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[curr.getRotationIndex()];
        }
        set(null);
        Piece temp = curr;
        curr = curr.counterclockwisePiece();
        // Try each wall kick one by one
        for(Point kick : kicks) {
            if(check(kick.x, kick.y)) {
                return shift(kick.x, kick.y, Action.CLOCKWISE);
            }
        }
        // Return out of bounds if no kicks work
        curr = temp;
        set(curr.getType());
        return Result.OUT_BOUNDS;
    }

    /**
     * Check if the current piece can be shifted by the given dx and dy
     * @param dx the change in x
     * @param dy the change in y
     * @return whether the piece can be shifted
     */
    private boolean check(int dx, int dy) {
        int x = pos.x + dx, y = pos.y + dy;
        for(Point point : curr.getBody()) {
            int nx = x + point.x;
            int ny = y + point.y;
            // Check for out of bounds
            if(nx < 0 || ny < 0 || nx >= getWidth() || ny >= getHeight())
                return false;
            // Check for intersection with existing piece
            if(board[nx][ny] != null)
                return false;
        }
        return true;
    }

    /**
     * Set the body of the current piece to the given value
     * @param val the value
     */
    private void set(Piece.PieceType val) {
        int x = pos.x, y = pos.y;
        for(Point point : curr.getBody()) {
            board[x + point.x][y + point.y] = val;
        }
    }

    /**
     * Return the board if the given action were called
     * @param act the action
     * @return a mutated copy of the board
     */
    @Override
    public Board testMove(Action act) {
        // Copy the current board
        Board newBoard = new TetrisBoard(this);
        newBoard.move(act);
        return newBoard;
    }

    /**
     * Get the current piece
     * @return the current piece
     */
    @Override
    public Piece getCurrentPiece() {
        return curr; 
    }

    /**
     * Get the current piece's position
     * @return the current position
     */
    @Override
    public Point getCurrentPiecePosition() {
        return pos; 
    }

    /**
     * Add a piece to the board at the given position
     * @param p the piece
     * @param spawnPosition the position
     */
    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        curr = p;
        pos = spawnPosition;
        // Check for out of bounds
        if(!check(0, 0)) {
            curr = null;
            pos = null;
            throw new IllegalArgumentException("Spawn position out of bounds");
        }
        set(p.getType());
    }

    /**
     * Check if this board is equal to the given Object
     * @param other the Object to compare against
     * @return whether the Object is equal
     */
    @Override
    public boolean equals(Object other) {
        // Check for the same runtime class
        if(!(other instanceof TetrisBoard))
            return false;
        TetrisBoard b = (TetrisBoard) other;
        // Check for the same width and height
        if(b.getWidth() != getWidth() || b.getHeight() != getHeight()) return false;
        // Check each element
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                if(getGrid(x, y) != b.getGrid(x, y))
                    return false;
            }
        }
        return true; 
    }

    /**
     * Get the last result of the move method
     * @return the last result
     */
    @Override
    public Result getLastResult() {
        return lastRes; 
    }

    /**
     * Get the last action called
     * @return the last action
     */
    @Override
    public Action getLastAction() {
        return lastAct; 
    }

    /**
     * Get the total number of rows cleared
     * @return the number of cleared rows
     */
    @Override
    public int getRowsCleared() {
        return rowsCleared; 
    }

    /**
     * Get the width of the board
     * @return the width
     */
    @Override
    public int getWidth() {
        return board.length; 
    }

    /**
     * Get the height of the board
     * @return the height
     */
    @Override
    public int getHeight() {
        return board[0].length; 
    }

    /**
     * Get the maximum height of any column in the board
     * @return the max height
     */
    @Override
    public int getMaxHeight() {
        return maxHeight; 
    }

    /**
     * Get the y value where the given piece would land if dropped at the given x
     * @param piece the piece to drop
     * @param x the column to drop at
     * @return the landing y value
     */
    @Override
    public int dropHeight(Piece piece, int x) {
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < piece.getWidth(); i++) {
            int dy = piece.getSkirt()[i];
            if(dy == Integer.MAX_VALUE) continue;
            int height = getColumnHeight(x + i) - dy;
            max = Math.max(max, height);
        }
        return max;
    }

    /**
     * Get the height of the given column
     * @param x the column number
     * @return the height
     */
    @Override
    public int getColumnHeight(int x) {
        return colHeight[x]; 
    }

    /**
     * Get the width of the given row
     * @param y the row number
     * @return the width
     */
    @Override
    public int getRowWidth(int y) {
        return rowWidth[y]; 
    }

    /**
     * Get the PieceType at the given x and y if present
     * @param x the x value
     * @param y the y value
     * @return the PieceType at the position, or null
     */
    @Override
    public Piece.PieceType getGrid(int x, int y) {
        return board[x][y]; 
    }
}
