import assignment.Piece;
import assignment.TetrisPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TetrisPieceTest {
    
    public static final Point[][] I_ROTS = {{new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2)}, 
                                             {new Point(2, 0), new Point(2, 1), new Point(2, 2), new Point(2, 3)},
                                             {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                                             {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}};

    public static final Point[][] L_ROTS =  {{new Point(0, 1), new Point(0, 2), new Point(1, 1), new Point(2, 1)},
                                              {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                                              {new Point(0, 1), new Point(1, 1), new Point(2, 0), new Point(2, 1)},
                                              {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(1, 2)}};

    public static final Point[][] O_ROTS =  {{new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                                              {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                                              {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                                              {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}};

    public static final Point[][] S_ROTS =  {{new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                                              {new Point(1, 1), new Point(1, 2), new Point(2, 0), new Point(2, 1)},
                                              {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                                              {new Point(0, 1), new Point(0, 2), new Point(1, 0), new Point(1, 1)}};

    public static final Point[][] T_ROTS =  {{new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 1)},
                                              {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 1)},
                                              {new Point(0, 1), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                                              {new Point(0, 1), new Point(1, 0), new Point(1, 1), new Point(1, 2)}};

    @Test
    void getType() {
        for(Piece.PieceType type :Piece.PieceType.values()) {
            Piece p = new TetrisPiece(type);
            for(int i = 0; i < TetrisPiece.ROTS; i++){
                assertEquals(p.getType(), type);
                p = p.clockwisePiece();
            }
        }
    }

    @Test
    void getRotationIndex() {
        Piece p = new TetrisPiece(Piece.PieceType.STICK);
        for(int i = 0; i < TetrisPiece.ROTS; i++) {
            assertEquals(p.getRotationIndex(), i);
            p = p.clockwisePiece();
        }
    }

    @Test
    void clockwisePiece() {
        for(Piece.PieceType type :Piece.PieceType.values())  {
            Piece p = new TetrisPiece(type);
            Piece np = p;
            for(int j = 0; j < TetrisPiece.ROTS; j++){
                Point[] body = p.getBody();
                Arrays.sort(body, (p1, p2) -> {
                    if(p1.x != p2.x) return p1.x - p2.x;
                    return p1.y - p2.y;
                });
                
                if (type.equals(Piece.PieceType.STICK)){
                    assertArrayEquals(body, I_ROTS[j]);
                } else if (type.equals(Piece.PieceType.LEFT_L)) {
                    assertArrayEquals(body, L_ROTS[j]);
                } else if (type.equals(Piece.PieceType.SQUARE)) {
                    assertArrayEquals(body, O_ROTS[j]);
                } else if (type.equals(Piece.PieceType.RIGHT_DOG)) {
                    assertArrayEquals(body, S_ROTS[j]);
                } else if (type.equals(Piece.PieceType.T)) {
                    assertArrayEquals(body, T_ROTS[j]);
                }
                p = p.clockwisePiece();
            }
            assertEquals(p, np);
        }
    }

    @Test
    void counterclockwisePiece() {
        for(Piece.PieceType type :Piece.PieceType.values())  {
            Piece p = new TetrisPiece(type);
            Piece np = p;
            for(int j = 0; j < TetrisPiece.ROTS; j++){
                int ind = (TetrisPiece.ROTS - j) % TetrisPiece.ROTS;
                Point[] body = p.getBody();
                Arrays.sort(body, (p1, p2) -> {
                    if(p1.x != p2.x) return p1.x - p2.x;
                    return p1.y - p2.y;
                });

                if (type.equals(Piece.PieceType.STICK)){
                    assertArrayEquals(body, I_ROTS[ind]);
                } else if (type.equals(Piece.PieceType.LEFT_L)) {
                    assertArrayEquals(body, L_ROTS[ind]);
                } else if (type.equals(Piece.PieceType.SQUARE)) {
                    assertArrayEquals(body, O_ROTS[ind]);
                } else if (type.equals(Piece.PieceType.RIGHT_DOG)) {
                    assertArrayEquals(body, S_ROTS[ind]);
                } else if (type.equals(Piece.PieceType.T)) {
                    assertArrayEquals(body, T_ROTS[ind]);
                }
                p = p.counterclockwisePiece();
            }
            assertEquals(p, np);
        }
    }

    @Test
    void getWidth() {
        for(Piece.PieceType type :Piece.PieceType.values()) {
            Piece p = new TetrisPiece(type);
            assertEquals(type.getBoundingBox().width, p.getWidth());
        }
    }

    @Test
    void getHeight() {
        for(Piece.PieceType type :Piece.PieceType.values()) {
            Piece p = new TetrisPiece(type);
            assertEquals(type.getBoundingBox().height, p.getHeight());
        }
    }

    @Test
    void getBody() {
        for(Piece.PieceType type :Piece.PieceType.values())  {
            Piece p = new TetrisPiece(type);
            
            Point[] body = p.getBody();
            Arrays.sort(body, (p1, p2) -> {
                if(p1.x != p2.x) return p1.x - p2.x;
                return p1.y - p2.y;
            });
                
            if (type.equals(Piece.PieceType.STICK)){
                assertArrayEquals(body, I_ROTS[0]);
            } else if (type.equals(Piece.PieceType.LEFT_L)) {
                assertArrayEquals(body, L_ROTS[0]);
            } else if (type.equals(Piece.PieceType.SQUARE)) {
                assertArrayEquals(body, O_ROTS[0]);
            } else if (type.equals(Piece.PieceType.RIGHT_DOG)) {
                assertArrayEquals(body, S_ROTS[0]);
            } else if (type.equals(Piece.PieceType.T)) {
                assertArrayEquals(body, T_ROTS[0]);
            }
        }
    }

    @Test
    void getSkirt() {
        // Stick 0
        Piece p = new TetrisPiece(Piece.PieceType.STICK);
        assertArrayEquals(new int[]{2, 2, 2, 2}, p.getSkirt());
        
        p = p.clockwisePiece();
        assertArrayEquals(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE}, p.getSkirt());
        
        p = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        assertArrayEquals(new int[]{1, 1, 2}, p.getSkirt());

        p = new TetrisPiece(Piece.PieceType.SQUARE);
        assertArrayEquals(new int[]{0, 0}, p.getSkirt());
    }

    @Test
    void testEquals() {
        Piece p1 = new TetrisPiece(Piece.PieceType.T);
        Piece p2 = new TetrisPiece(Piece.PieceType.T);
        Piece p3 = new TetrisPiece(Piece.PieceType.STICK);
        
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, "bobob");
        assertNotEquals(p1, "beastgert");
    }
}