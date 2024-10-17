package assignment;


import javax.swing.*;

public class JBrainTetris extends JTetris{

    /**
     * Create a GUI with the JBrainTetris to allow the Brain to make moves
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }

    /**
     * Construct the JBrainTetris
     */
    public JBrainTetris() {
        super();

        /**
         * Unregister key handlers.
         */

        // LEFT
        registerKeyboardAction(e -> {},
                "left", KeyStroke.getKeyStroke('a'), WHEN_IN_FOCUSED_WINDOW);

        // RIGHT
        registerKeyboardAction(e -> {},
                "down", KeyStroke.getKeyStroke('s'), WHEN_IN_FOCUSED_WINDOW);

        // DOWN
        registerKeyboardAction(e -> {},
                "right", KeyStroke.getKeyStroke('d'), WHEN_IN_FOCUSED_WINDOW);

        // ROTATE
        registerKeyboardAction(e -> {},
                "counterclockwise", KeyStroke.getKeyStroke('q'), WHEN_IN_FOCUSED_WINDOW);

        // UNROTATE
        registerKeyboardAction(e -> {},
                "clockwise", KeyStroke.getKeyStroke('e'), WHEN_IN_FOCUSED_WINDOW);

        // DROP
        registerKeyboardAction(e -> {},
                "drop", KeyStroke.getKeyStroke('w'), WHEN_IN_FOCUSED_WINDOW);
        
        // Request the next move from the brain after every down tick
        FunBrain brain = new FunBrain();
        timer = new Timer(DELAY, e -> {
            tick(Board.Action.DOWN);
            tick(brain.nextMove(board));
        });
    }
}
