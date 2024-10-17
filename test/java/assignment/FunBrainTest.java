import assignment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import static assignment.Piece.PieceType.*;
import static assignment.Board.Action.*;
import static java.time.Clock.tick;
import static org.junit.jupiter.api.Assertions.*;

class FunBrainTest {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    TetrisBoard board;
    FunBrain brain;
            
    @BeforeEach
    void setUp() {
        board = new TetrisBoard(WIDTH, HEIGHT);
        brain = new FunBrain();
    }
    
    @Test
    void nextMove() {
        // Prioritizes clearing rows
        Point pos1 = new Point(0, 0);
        Point pos2 = new Point(2, 0);
        Point pos3 = new Point(4, 0);
        Point pos4 = new Point(3, 6);
        board.nextPiece(new TetrisPiece(SQUARE), pos1);
        board.move(DROP);
        board.nextPiece(new TetrisPiece(SQUARE), pos2);
        board.move(DROP);
        board.nextPiece(new TetrisPiece(SQUARE), pos3);
        board.move(DROP);

        board.nextPiece(new TetrisPiece(STICK).clockwisePiece(), pos4);
        assertEquals(CLOCKWISE, brain.nextMove(board));
        board.move(brain.nextMove(board));
        board.move(DOWN);
        while(brain.nextMove(board) != DROP){
            assertEquals(RIGHT, brain.nextMove(board));
            board.move(brain.nextMove(board));
            board.move(DOWN);
        }
        
        // Normal case
        board = new TetrisBoard(WIDTH, HEIGHT);
        board.nextPiece(new TetrisPiece(RIGHT_L), pos1);
        board.move(DROP);
        
        board.nextPiece(new TetrisPiece(SQUARE), pos4);
        assertEquals(RIGHT, brain.nextMove(board));
    }

    @Test
    void scoreBoard() {
        assertEquals(0, brain.scoreBoard(board));
        
        Point pos1 = new Point(0, 0);
        Point pos2 = new Point(3, 0);
        board.nextPiece(new TetrisPiece(RIGHT_DOG), pos1);
        board.move(DROP);
        board.nextPiece(new TetrisPiece(SQUARE), pos2);
        board.move(DROP);
        
        assertEquals(-5220, brain.scoreBoard(board));
    }
}