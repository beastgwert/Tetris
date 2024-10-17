package assignment;

import java.awt.*;
import java.util.Arrays;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for its
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do pre-computation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {
    
    private static final TetrisPiece[] pool = new TetrisPiece[PieceType.values().length];
    public static final int ROTS = 4;
    private final Piece next;
    private final PieceType type;
    private final Point[] body;
    private final int rotation;
    private final int[] skirt;
    private final int D;
    
    /**
     * Construct a tetris piece of the given type. The piece should be in its spawn orientation,
     * i.e., a rotation index of 0.
     */
    public TetrisPiece(PieceType type) {
        // Check for a previous construction of the piece
        if(pool[type.ordinal()] != null) {
            TetrisPiece piece = pool[type.ordinal()];
            this.D = piece.D;
            this.type = piece.type;
            this.body = piece.body;
            this.rotation = piece.rotation;
            this.skirt = piece.skirt;
            this.next = piece.next;
            return;
        }
        D = type.getBoundingBox().width;
        rotation = 0;
        body = type.getSpawnBody();
        skirt = makeSkirt(body);
        this.type = type;
        
        // Calculate all rotations of the piece
        Point[][] rotations = new Point[ROTS][body.length];
        rotations[0] = body;
        for(int i=1; i<ROTS; i++){
            rotations[i] = rotateClockwise(rotations[i-1]);
        }
        Piece[] pieces = new Piece[ROTS];
        pieces[0] = this;
        for(int i = ROTS - 1; i > 0; i--){
            pieces[i] = new TetrisPiece(type, rotations[i], i, makeSkirt(rotations[i]), pieces[(i + 1) % ROTS]);
        }
        this.next = pieces[1];
        
        // Store the construction for later use
        pool[type.ordinal()] = this;
    }

    /**
     * Construct a TetrisPiece with the given parameters
     * @param type the piece type
     * @param body the points in the body
     * @param rotation the rotation index
     * @param skirt the lowest y value for each x
     * @param next the clockwise rotation
     */
    public TetrisPiece(PieceType type, Point[] body, int rotation, int[] skirt, Piece next){
        D = type.getBoundingBox().width;
        this.type = type;
        this.body = body;
        this.rotation = rotation;
        this.skirt = skirt;
        this.next = next;
    }

    /**
     * Calculate the skirt of the given body, or the lowest y value for each x
     * @param body the body
     * @return the skirt
     */
    private int[] makeSkirt(Point[] body) {
        int[] skirt = new int[D];
        Arrays.fill(skirt, Integer.MAX_VALUE);
        for (Point point : body) {
            skirt[point.x] = Math.min(skirt[point.x], point.y);
        }
        return skirt;
    }

    /**
     * Rotate the given points clockwise with math
     * @param points the points to rotate
     * @return the rotated points
     */
    private Point[] rotateClockwise(Point[] points) {
        Point[] newPoints = new Point[points.length];
        for(int i=0; i < points.length; i++){
            int x = points[i].y;
            int y = D - points[i].x - 1;
            newPoints[i] = new Point(x, y);
        }
        return newPoints;
    }

    /**
     * Get the type of the piece
     * @return the piece type
     */
    @Override
    public PieceType getType() {
        return type;
    }

    /**
     * Get the rotation index of the piece
     * @return the rotation index
     */
    @Override
    public int getRotationIndex() {
        return rotation;
    }

    /**
     * Get the clockwise rotation of the piece
     * @return the clockwise rotation
     */
    @Override
    public Piece clockwisePiece() {
        return next;
    }

    /**
     * Get the counterclockwise rotation of the piece
     * @return the counterclockwise rotation
     */
    @Override
    public Piece counterclockwisePiece() {
        // The counterclockwise rotation is the same as the triple clockwise rotation
        return next.clockwisePiece().clockwisePiece();
    }

    /**
     * Get the width of the piece's bounding box
     * @return the width
     */
    @Override
    public int getWidth() {
        return D;
    }

    /**
     * Get the height of the piece's bounding box
     * @return the height
     */
    @Override
    public int getHeight() {
        return D;
    }

    /**
     * Get the points in the body of the piece
     * @return the body
     */
    @Override
    public Point[] getBody() {
        return body;
    }

    /**
     * Get the skirt of the piece
     * @return the skirt
     */
    @Override
    public int[] getSkirt() {
        return skirt;
    }

    /**
     * Check whether the piece is equal to the given Object
     * @param other the Object to compare against
     * @return whether the Object is equal
     */
    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;
        
        // Check whether the two bodies are the same
        return Arrays.equals(body, otherPiece.getBody());
    }
}
