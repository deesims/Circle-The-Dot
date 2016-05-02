import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.io.IOException;
import java.io.*;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import javax.swing.*;
import java.io.File; 

/**
 * The class <b>GameController</b> is the controller of the game. It implements
 * the interface ActionListener to be called back when the player makes a move. It computes
 * the next step of the game, and then updates model and view.
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 */


public class GameController implements ActionListener {

    /**
     * Reference to the view of the game
     */
    private GameView gameView;

    /**
     * Reference to the model of the game
     */
    private GameModel gameModel;
    
    
    
    private String fileName = "savedGame.ser"; // saved game file name
    private LinkedStack<GameModel> undo_stack = new LinkedStack<GameModel>(); // undo stack
    private LinkedStack<GameModel> redo_stack = new LinkedStack<GameModel>(); // redo stack 


    /**
     * Constructor used for initializing the controller. It creates the game's view
     * and the game's model instances
     *
     * @param size
     *            the size of the board on which the game will be played
     */
    public GameController(int size) {
        File saved = new File(fileName);
        if (saved.exists()){
               try{
                     ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName));
                     gameModel = (GameModel) is.readObject();
                     System.out.println("Saved game restored...");
                     is.close();
                     saved.delete();
               } catch (IOException r) {
                     r.printStackTrace();
               } catch (ClassNotFoundException r){
                     r.printStackTrace();
               }
               gameView = new GameView(gameModel,this);
               gameView.update();
        } else {
              gameModel = new GameModel(size);
              gameView = new GameView(gameModel, this);
              gameView.update();
        }
        
    }



    /**
     * resets the game
     */
    public void reset(){
        gameModel.reset();
        gameView.update();
        undo_stack = new LinkedStack<GameModel>();
        redo_stack = new LinkedStack<GameModel>();
        gameView.buttonRedo.setEnabled(false);
        gameView.buttonUndo.setEnabled(false);
    }
    

    /**
     * Callback used when the user clicks a button or one of the dots.
     * Implements the logic of the game
     *
     * @param e
     *            the ActionEvent
     */

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof DotButton) {
            DotButton clicked = (DotButton)(e.getSource());

        	if (gameModel.getCurrentStatus(clicked.getColumn(),clicked.getRow()) ==
                    GameModel.AVAILABLE){
                GameModel previousModel = gameModel.clone(gameModel); // clone the previous model to get the last step 
                gameModel.select(clicked.getColumn(),clicked.getRow());
                oneStep(); // do the step 
                undo_stack.push(previousModel); // push the previous_model onto the stack;
                gameView.buttonUndo.setEnabled(true);
                while(!redo_stack.isEmpty()){
                     redo_stack.pop();
                }
                gameView.buttonRedo.setEnabled(false);
               
                
                
                
                
                
            }
        } else if (e.getSource() instanceof JButton) {
        
            JButton clicked = (JButton)(e.getSource());

            if (clicked.getText().equals("Quit")) {
                 try {
                     ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fileName));
                     os.writeObject(gameModel);
                     System.out.println("Game saved...");
                     os.close();
                 } catch (IOException f){
                     f.printStackTrace();
                 }
                 System.exit(0);
            } else if (clicked.getText().equals("Reset")){
                reset();
            } else if (clicked.getText().equals("Undo")){
                redo_stack.push(gameModel);
                gameView.buttonRedo.setEnabled(true);
                gameModel = undo_stack.pop(); // push it off the stack
                gameView.updateModel(gameModel); // updates model for gameView and Boardview
                gameView.update();
                if (undo_stack.isEmpty()){
                     gameView.buttonUndo.setEnabled(false);
                }
                if (redo_stack.isEmpty()){
                     gameView.buttonRedo.setEnabled(false);
                }
            } else if (clicked.getText().equals("Redo")){
                undo_stack.push(gameModel);
                gameView.buttonUndo.setEnabled(true);
                gameModel = redo_stack.pop();
                gameView.updateModel(gameModel);
                gameView.update();
                gameView.buttonRedo.setEnabled(true);
                if (undo_stack.isEmpty()){
                     gameView.buttonUndo.setEnabled(false);
                }
                if (redo_stack.isEmpty()){
                     gameView.buttonRedo.setEnabled(false);
                }
            }
        }
    }

    /**
     * Computes the next step of the game. If the player has lost, it
     * shows a dialog offering to replay.
     * If the user has won, it shows a dialog showing the number of
     * steps that had been required in order to win.
     * Else, it finds one of the shortest path for the blue dot to
     * exit the board and moves it one step in that direction.
     */
    private void oneStep(){
        Point currentDot = gameModel.getCurrentDot(); 
        if(isOnBorder(currentDot)) {
            gameModel.setCurrentDot(-1,-1);
            gameView.update();

            Object[] options = {"Play Again",
                    "Quit"};
            int n = JOptionPane.showOptionDialog(gameView,
                    "You lost! Would you like to play again?",
                    "Lost",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if(n == 0){
                reset();
            } else{
                System.exit(0);
            }
        }
        else{
            Point direction = findDirection();
            if(direction.getX() == -1){
                gameView.update();
                Object[] options = {"Play Again",
                        "Quit"};
                int n = JOptionPane.showOptionDialog(gameView,
                        "Congratualtions, you won in " + gameModel.getNumberOfSteps()
                            +" steps!\n Would you like to play again?",
                        "Won",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if(n == 0){
                    reset();
                } else{
                    System.exit(0);
                }
            }
            else{
                gameModel.setCurrentDot(direction.getX(), direction.getY());
                gameView.update();
            }
        }

    }

    /**
     * Does a ``breadth-first'' search from the current location of the blue dot to find
     * one of the shortest available path to exit the board.
     *
     * @return the location (as a Point) of the next step for the blue dot toward the exit.
     * If the blue dot is encircled and cannot exit, returns an instance of the class Point
     * at location (-1,-1)
     */

    private Point findDirection(){
        boolean[][] blocked = new boolean[gameModel.getSize()][gameModel.getSize()];

        for(int i = 0; i < gameModel.getSize(); i ++){
            for (int j = 0; j < gameModel.getSize(); j ++){
                blocked[i][j] =
                    !(gameModel.getCurrentStatus(i,j) == GameModel.AVAILABLE);
            }
        }

        Queue<Pair<Point>> myQueue = new LinkedQueue<Pair<Point>>();

        LinkedList<Point> possibleNeighbours = new  LinkedList<Point>();

        // start with neighbours of the current dot
        // (note: we know the current dot isn't on the border)
        Point currentDot = gameModel.getCurrentDot();

        possibleNeighbours = findPossibleNeighbours(currentDot, blocked);

        // adding some non determinism into the game !
        java.util.Collections.shuffle(possibleNeighbours);

        for(int i = 0; i < possibleNeighbours.size() ; i++){
            Point p = possibleNeighbours.get(i);
            if(isOnBorder(p)){
                return p;
            }
            myQueue.enqueue(new Pair<Point>(p,p));
            blocked[p.getX()][p.getY()] = true;
        }


        // start the search
        while(!myQueue.isEmpty()){
            Pair<Point> pointPair = myQueue.dequeue();
            possibleNeighbours = findPossibleNeighbours(pointPair.getFirst(), blocked);

            for(int i = 0; i < possibleNeighbours.size() ; i++){
                Point p = possibleNeighbours.get(i);
                if(isOnBorder(p)){
                    return pointPair.getSecond();
                }
                myQueue.enqueue(new Pair<Point>(p,pointPair.getSecond()));
                blocked[p.getX()][p.getY()]=true;
            }

       }

        // could not find a way out. Return an outside direction
        return new Point(-1,-1);

    }
    
   /**
     * Helper method: checks if a point is on the border of the board
     *
     * @param p
     *            the point to check
     *
     * @return true iff p is on the border of the board
     */

    private boolean isOnBorder(Point p){
        return (p.getX() == 0 || p.getX() == gameModel.getSize() - 1 ||
                p.getY() == 0 || p.getY() == gameModel.getSize() - 1 );
    }

   /**
     * Helper method: find the list of direct neighbours of a point that are not
     * currenbtly blocked
     *
     * @param point
     *            the point to check
     * @param blocked
     *            a 2 dimentionnal array of booleans specifying the points that
     *              are currently blocked
     *
     * @return an instance of a LinkedList class, holding a list of instances of
     *      the class Points representing the neighbours of parameter point that
     *      are not currently blocked.
     */
    private LinkedList<Point> findPossibleNeighbours(Point point,
            boolean[][] blocked){

        LinkedList<Point> list = new LinkedList<Point>();
        int delta = (point.getY() %2 == 0) ? 1 : 0;
        if(!blocked[point.getX()-delta][point.getY()-1]){
            list.add(new Point(point.getX()-delta, point.getY()-1));
        }
        if(!blocked[point.getX()-delta+1][point.getY()-1]){
            list.add(new Point(point.getX()-delta+1, point.getY()-1));
        }
        if(!blocked[point.getX()-1][point.getY()]){
            list.add(new Point(point.getX()-1, point.getY()));
        }
        if(!blocked[point.getX()+1][point.getY()]){
            list.add(new Point(point.getX()+1, point.getY()));
        }
        if(!blocked[point.getX()-delta][point.getY()+1]){
            list.add(new Point(point.getX()-delta, point.getY()+1));
        }
        if(!blocked[point.getX()-delta+1][point.getY()+1]){
            list.add(new Point(point.getX()-delta+1, point.getY()+1));
        }
        return list;
    }


}
