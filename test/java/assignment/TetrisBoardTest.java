import assignment.Board;
import assignment.Piece;
import assignment.TetrisBoard;
import assignment.TetrisPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static assignment.Piece.PieceType.*;
import static assignment.Board.Action.*;
import static assignment.Board.Result.*;
import static org.junit.jupiter.api.Assertions.*;

class TetrisBoardTest {
    private static final int WIDTH = 3;
    private static final int HEIGHT = 5;
    TetrisBoard board;

    @BeforeEach
    void setUp() {
        board = new TetrisBoard(WIDTH, HEIGHT);
    }
    
    private void check(Point pos, Piece.PieceType type) {
        for(Point p : type.getSpawnBody()) {
            assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), type);
        }
    }

    @Test
    void move() {
        assertEquals(board.move(DOWN), NO_PIECE);
        
        Point pos = new Point(0, 1);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));
        
        // right
        assertEquals(board.move(RIGHT), SUCCESS);
        pos.x++;
        check(pos, SQUARE);
        
        // right out of bounds
        assertEquals(board.move(RIGHT), OUT_BOUNDS);
        check(pos, SQUARE);

        // left
        assertEquals(board.move(LEFT), SUCCESS);
        pos.x--;
        check(pos, SQUARE);

        // left out of bounds
        assertEquals(board.move(LEFT), OUT_BOUNDS);
        check(pos, SQUARE);
        
        // down
        assertEquals(board.move(DOWN), SUCCESS);
        pos.y--;
        check(pos, SQUARE);

        // down place
        assertEquals(board.move(DOWN), PLACE);
        check(pos, SQUARE);
        
        // drop place
        setUp();
        pos = new Point(0, 2);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));
        
        assertEquals(board.move(DROP), PLACE);
        pos.y = 0;
        check(pos, SQUARE);
        
        // rotate clockwise
        setUp();
        pos = new Point(0, 0);
        board.nextPiece(new TetrisPiece(RIGHT_DOG), pos);
        for(int i = 1; i < TetrisPiece.ROTS; i++) {
            assertEquals(board.move(CLOCKWISE), SUCCESS);
            for(Point p : TetrisPieceTest.S_ROTS[i]) {
                assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), RIGHT_DOG);
            }
        }
        
        // I clockwise wall kick
        pos = new Point(0, 0);
        board = new TetrisBoard(WIDTH+2, HEIGHT+1);
        board.nextPiece(new TetrisPiece(STICK), new Point(0, -2));
        board.nextPiece(new TetrisPiece(STICK), pos);
        assertEquals(board.move(CLOCKWISE), SUCCESS);
        Point kick = Piece.I_CLOCKWISE_WALL_KICKS[0][4];
        pos = new Point(pos.x + kick.x, pos.y + kick.y);

        for(Point p : TetrisPieceTest.I_ROTS[1]) {
            assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), STICK);
        }
        
        // normal clockwise wall kick
        pos = new Point(1, 2);
        board = new TetrisBoard(WIDTH+2, HEIGHT+2);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 4));
        board.nextPiece(new TetrisPiece(SQUARE), new Point(3, 2));
        board.nextPiece(new TetrisPiece(SQUARE), new Point(3, 0));
        board.nextPiece(new TetrisPiece(RIGHT_DOG), pos);
        assertEquals(board.move(CLOCKWISE), SUCCESS);
        kick = Piece.NORMAL_CLOCKWISE_WALL_KICKS[0][4];
        pos = new Point(pos.x + kick.x, pos.y + kick.y);
        for(Point p : TetrisPieceTest.S_ROTS[1]) {
            assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), RIGHT_DOG);
        }
        
        // rotate clockwise out of bounds
        pos = new Point(0, 0);
        board = new TetrisBoard(WIDTH+2, HEIGHT);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 0));
        board.nextPiece(new TetrisPiece(SQUARE), new Point(2, 0));
        board.nextPiece(new TetrisPiece(STICK), pos);
        assertEquals(board.move(CLOCKWISE), OUT_BOUNDS);

        // rotate counterclockwise
        setUp();
        pos = new Point(0, 0);
        board.nextPiece(new TetrisPiece(RIGHT_DOG), pos);
        for(int i = 1; i < TetrisPiece.ROTS; i++) {
            assertEquals(board.move(COUNTERCLOCKWISE), SUCCESS);
            for(Point p : TetrisPieceTest.S_ROTS[TetrisPiece.ROTS - i]) {
                assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), RIGHT_DOG);
            }
        }

        // I counterclockwise wall kick
        pos = new Point(0, 1);
        board = new TetrisBoard(WIDTH+2, HEIGHT+1);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 0));
        board.nextPiece(new TetrisPiece(STICK), new Point(1, 2));
        board.nextPiece(new TetrisPiece(STICK), pos);
        assertEquals(board.move(COUNTERCLOCKWISE), SUCCESS);
        kick = Piece.I_COUNTERCLOCKWISE_WALL_KICKS[0][4];
        pos = new Point(pos.x + kick.x, pos.y + kick.y);

        for(Point p : TetrisPieceTest.I_ROTS[3]) {
            assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), STICK);
        }

        // normal counterclockwise wall kick
        pos = new Point(0, 2);
        board = new TetrisBoard(WIDTH+2, HEIGHT);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 1));
        board.nextPiece(new TetrisPiece(LEFT_L), pos);
        assertEquals(board.move(COUNTERCLOCKWISE), SUCCESS);
        kick = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[0][4];
        pos = new Point(pos.x + kick.x, pos.y + kick.y);
//        for(int j = board.getHeight()-1; j >= 0; j--) {
//            for(int i = 0; i < board.getWidth(); i++) {
//                System.out.print(board.getGrid(i, j) + " ");
//            }
//            System.out.println();
//        }
        for(Point p : TetrisPieceTest.L_ROTS[3]) {
            assertEquals(board.getGrid(pos.x + p.x, pos.y + p.y), LEFT_L);
        }

        // rotate clockwise out of bounds
        pos = new Point(0, 0);
        board = new TetrisBoard(WIDTH+2, HEIGHT);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 0));
        board.nextPiece(new TetrisPiece(SQUARE), new Point(2, 0));
        board.nextPiece(new TetrisPiece(STICK), pos);
        assertEquals(board.move(COUNTERCLOCKWISE), OUT_BOUNDS);
        
        // nothing
        assertEquals(board.move(NOTHING), SUCCESS);
    }

    @Test
    void testMove() {
        Point pos = new Point(0, 0);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));
        
        Board oBoard = new TetrisBoard(board);
        Board nBoard = board.testMove(RIGHT);
        assertEquals(board, oBoard);
        
        int startX = 1;
        int startY = 0;
        for(Point p : SQUARE.getSpawnBody()) {
            assertEquals(nBoard.getGrid(startX + p.x, startY + p.y), SQUARE);
        }
    }

    @Test
    void getCurrentPiece() {
        Point pos = new Point(0, 1);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));
        
        assertEquals(new TetrisPiece(SQUARE), board.getCurrentPiece());
    }

    @Test
    void getCurrentPiecePosition() {
        Point pos = new Point(0, 1);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));

        assertEquals(new Point(0, 1), board.getCurrentPiecePosition());
    }

    @Test
    void nextPiece() {
        Point pos = new Point(0, 1);
        Point illegalPos = new Point(6, 0);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));
        
        check(pos, SQUARE);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> board.nextPiece(new TetrisPiece(SQUARE), illegalPos));
        assertEquals(e.getMessage(), "Spawn position out of bounds");
    }

    @Test
    void testEquals() {
        Board yB = new TetrisBoard(WIDTH, HEIGHT);
        assertEquals(board, yB);

        Board nB = new TetrisBoard(WIDTH + 1, HEIGHT + 1);
        assertNotEquals(board, nB);
        nB = new TetrisBoard(WIDTH, HEIGHT + 1);
        assertNotEquals(board, nB);

        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 0));
        nB = new TetrisBoard(WIDTH, HEIGHT);
        nB.nextPiece(new TetrisPiece(SQUARE), new Point(0, 0));
        assertEquals(board, nB);

        board.nextPiece(new TetrisPiece(SQUARE), new Point(0, 2));
        nB.nextPiece(new TetrisPiece(RIGHT_DOG), new Point(0, 1));
        assertNotEquals(board, nB);
        
        assertNotEquals(board, "yellow");
    }

    @Test
    void getLastResult() {
        board.move(RIGHT);
        assertEquals(NO_PIECE, board.getLastResult());
        
        Point pos = new Point(0, 0);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));

        // right (success)
        board.move(RIGHT);
        assertEquals(SUCCESS, board.getLastResult());
        pos.x++;

        // right (out of bounds)
        board.move(RIGHT);
        assertEquals(OUT_BOUNDS, board.getLastResult());
        
        // down (place)
        board.move(DOWN);
        assertEquals(PLACE, board.getLastResult());
    }

    @Test
    void getLastAction() {
        for(Board.Action action: Board.Action.values()) {
            board.move(action);
            assertEquals(action, board.getLastAction());
        }
    }

    @Test
    void getRowsCleared() {
        Point pos = new Point(0, -1);
        board.nextPiece(new TetrisPiece(RIGHT_L), new Point(pos));
        board.move(DOWN);

        assertEquals(1, board.getRowsCleared());

        board.nextPiece(new TetrisPiece(RIGHT_DOG), new Point(pos));
        board.move(DOWN);
        assertEquals(2, board.getRowsCleared());
    }

    @Test
    void getWidth() {
        assertEquals(WIDTH, board.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(HEIGHT, board.getHeight());
    }

    @Test
    void getMaxHeight() {
        assertEquals(0, board.getMaxHeight());
        board = new TetrisBoard(WIDTH + 1, HEIGHT);
        
        Point pos1 = new Point(0, 0);
        Point pos2 = new Point(2, 0);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos1));
        board.move(DOWN);
        
        assertEquals(2, board.getMaxHeight());

        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos2));
        board.move(DOWN);

        assertEquals(0, board.getMaxHeight());
        
        board = new TetrisBoard(WIDTH + 1, HEIGHT);
        
        board.nextPiece(new TetrisPiece(STICK).clockwisePiece(), new Point(-2, 0));
        board.move(DOWN);
        assertEquals(4, board.getMaxHeight());
        board.nextPiece(new TetrisPiece(STICK), new Point(0, 2));
        board.move(DOWN);
        assertEquals(4, board.getMaxHeight());
    }

    @Test
    void dropHeight() {
        Point pos = new Point(0, -1);
        assertEquals(0, board.dropHeight(new TetrisPiece(SQUARE), 0));
        
        board.nextPiece(new TetrisPiece(LEFT_L), pos);
        board.move(DOWN);
        assertEquals(1, board.dropHeight(new TetrisPiece(SQUARE), 0));
        assertEquals(0, board.dropHeight(new TetrisPiece(RIGHT_DOG).clockwisePiece(), -1));
    }

    @Test
    void getColumnHeight() {
        Point pos = new Point(0, -1);
        board.nextPiece(new TetrisPiece(RIGHT_DOG), new Point(pos));
        board.move(DOWN);
        
        assertEquals(1, board.getColumnHeight(0));
        assertEquals(2, board.getColumnHeight(1));
        assertEquals(2, board.getColumnHeight(2));
        
        board.nextPiece(new TetrisPiece(LEFT_DOG).counterclockwisePiece(), new Point(0, 1));
        board.move(DOWN);
        assertEquals(2, board.getColumnHeight(0));
        assertEquals(3, board.getColumnHeight(1));
        assertEquals(0, board.getColumnHeight(2));
    }

    @Test
    void getRowWidth() {
        Point pos = new Point(0, -1);
        board.nextPiece(new TetrisPiece(RIGHT_DOG), new Point(pos));
        board.move(DOWN);

        assertEquals(2, board.getRowWidth(0));
        assertEquals(2, board.getRowWidth(1));
        assertEquals(0, board.getRowWidth(2));

        board.nextPiece(new TetrisPiece(LEFT_DOG).counterclockwisePiece(), new Point(0, 1));
        board.move(DOWN);
        assertEquals(2, board.getRowWidth(0));
        assertEquals(2, board.getRowWidth(1));
        assertEquals(1, board.getRowWidth(2));
    }

    @Test
    void getGrid() {
        Point pos = new Point(0, 0);
        board.nextPiece(new TetrisPiece(SQUARE), new Point(pos));
        assertEquals(SQUARE, board.getGrid(0, 0));
        assertEquals(SQUARE, board.getGrid(1, 0));
        assertEquals(SQUARE, board.getGrid(0, 1));
        assertEquals(SQUARE, board.getGrid(1, 1));
        assertNull(board.getGrid(2, 2));
    }
}