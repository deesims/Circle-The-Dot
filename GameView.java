import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class GameView extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Reference to the view of the board
     */
    private BoardView board;
    private GameModel gameModel;
    public JButton buttonRedo;
    public JButton buttonUndo;
 
  
    /**
     * Constructor used for initializing the Frame
     * 
     * @param model
     *            the model of the game (already initialized)
     * @param gameController
     *            the controller
     */

    public GameView(GameModel model, GameController gameController) {
        super("Circle the Dot");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setBackground(Color.WHITE);

        gameModel = model;
    	board = new BoardView(model, gameController);
    	add(board, BorderLayout.CENTER);

 
        JButton buttonReset = new JButton("Reset");
        buttonReset.setFocusPainted(false);
        buttonReset.addActionListener(gameController);

        JButton buttonExit = new JButton("Quit");
        buttonExit.setFocusPainted(false);
        buttonExit.addActionListener(gameController);
        
        buttonUndo = new JButton("Undo");
        buttonUndo.setFocusPainted(false);
        buttonUndo.setEnabled(false);
        buttonUndo.addActionListener(gameController);

       buttonRedo = new JButton("Redo");
       buttonRedo.setFocusPainted(false);
       buttonRedo.setEnabled(false);
       buttonRedo.addActionListener(gameController);

    	JPanel control = new JPanel();
    	control.setBackground(Color.WHITE);
        control.add(buttonReset);
        control.add(buttonExit);
        control.add(buttonUndo);
        control.add(buttonRedo);
    	add(control, BorderLayout.SOUTH);

    	pack();
    	setResizable(false);
    	setVisible(true);

    }


    public void update(){
        board.update();
  
    }
    
    /** 
    
    * Updates the gameModel for gameView and BoardView 
    *
    * @param model 
    *
    *
    */
    
    public void updateModel(GameModel model){ 
       gameModel = model;
       board.updateModel2(model); 
    }
    
    


}
