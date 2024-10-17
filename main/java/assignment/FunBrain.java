package assignment;

import java.util.*;

/**
 * A Lame Brain implementation for JTetris; tries all possible places to put the
 * piece (but ignoring rotations, because we're lame), trying to minimize the
 * total height of pieces on the board.
 */
public class FunBrain implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;
    // The x and y changes for the four adjacent positions around a square
    private static final int[][] d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    
    /**
     * Decide what the next move should be based on the state of the board.
     * @param currentBoard the current board
     * @return the next move
     */
    public Board.Action nextMove(Board currentBoard) {
        // Fill the options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);
        
        // Test the other rotations of the current piece
        Board clockwiseBoard = currentBoard.testMove(Board.Action.CLOCKWISE);
        Board counterBoard = currentBoard.testMove(Board.Action.COUNTERCLOCKWISE);
        firstMoves.add(Board.Action.CLOCKWISE);
        
        // Replace the first action of the rotated boards with the correct rotation
        replaceOptions(clockwiseBoard, Board.Action.CLOCKWISE);
        replaceOptions(counterBoard, Board.Action.COUNTERCLOCKWISE);
        replaceOptions(clockwiseBoard.testMove(Board.Action.CLOCKWISE), Board.Action.CLOCKWISE);
        double best = Integer.MIN_VALUE;
        int bestIndex = 0;

        // Check all the options and get the one with the highest score
        int maxCleared = currentBoard.getRowsCleared();
        for (int i = 0; i < options.size(); i++) {
            double score = scoreBoard(options.get(i));
            // Prioritize options that clear the most rows
            int rowsCleared = options.get(i).getRowsCleared();
            if( rowsCleared > maxCleared) {
                best = scoreBoard(options.get(i));
                bestIndex = i;
                maxCleared = rowsCleared;
            }
            else if (rowsCleared == maxCleared && score > best) {
                best = score;
                bestIndex = i;
            }
        }
        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndex);
    }

    /**
     * Replace the first move for all options of the given board with the given action
     * @param board the board to test
     * @param action the action to replace with
     */
    private void replaceOptions(Board board, Board.Action action) {
        int temp = options.size();
        enumerateOptions(board);
        while(firstMoves.size() > temp) {
            firstMoves.remove(firstMoves.size()-1);
        }
        while(firstMoves.size() < options.size()) {
            firstMoves.add(action);
        }
    }

    /**
     * Test all the places we can put the current Piece.
     * @param currentBoard the board to test with
     */
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);

        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }
    }

    /**
     * Assign the given board a score, with a higher score indicating a better board
     * @param newBoard the board to score
     * @return the board's score
     */
    public double scoreBoard(Board newBoard) {
        int overlap = 0;
        int wallOverlap = 0;
        int gaps = 0;
        // Find the minimum height of any column
        ArrayList<Integer> minHeights = new ArrayList<>();
        double minHeightAverage;
        for(int x = 0; x < newBoard.getWidth(); x++){
            minHeights.add(newBoard.getColumnHeight(x));
        }
        Collections.sort(minHeights);
        minHeightAverage = (minHeights.get(0)+minHeights.get(1)+minHeights.get(2))/3.0;
        for(int y = 0; y < newBoard.getHeight(); y++){
            for(int x = 0; x < newBoard.getWidth(); x++){
                // Count the number of gaps, defined as empty spaces with a piece above
                if(newBoard.getGrid(x, y) == null){
                    if(newBoard.getColumnHeight(x) > y) gaps++;
                    continue;
                }
                
                // Count the number of overlaps between pieces
                for(int k = 0; k < d.length; k++){
                    int nx = x + d[k][0];
                    int ny = y + d[k][1];
                    if(nx < 0 || nx >= newBoard.getWidth() || ny < 0){
                        wallOverlap++;
                        continue;
                    }
                    if(ny >= newBoard.getHeight() || newBoard.getGrid(nx, ny) == null){
                        continue;
                    }
                    overlap++;
                }
            }
        }
        // Reward the board for overlaps and a higher min height
        // Penalize the board for gaps and a higher max height
        int wallWeight = 3;
        int gapWeight = 40;
        int maxHeightWeight = 10;
        int minHeightWeight = 20;
        return (overlap + wallOverlap * wallWeight - gaps * gapWeight) * (600 - (newBoard.getMaxHeight() * maxHeightWeight - minHeightAverage * minHeightWeight));
    }
    
    
}
