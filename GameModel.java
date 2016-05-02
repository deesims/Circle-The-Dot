import java.util.Random;
import java.io.Serializable;

public class GameModel implements Cloneable, Serializable {


    /**
     * predefined values to capture the state of a point
     */
    public static final int AVAILABLE   = 0;
    public static final int SELECTED    = 1;
    public static final int DOT         = 2;

    /**
     * The probability that an available cell will be initially selected
     */
    private static final int INITIAL_PROBA = 10;
    

    /**
     * The size of the game.
     */
    private  int sizeOfGame;
 
    /**
     * A 2 dimentionnal array of sizeOfGame*sizeOfGame recording the state of each dot
     */
    private int[][] model;


   /**
     * The current position of the blue dot
     */
    private Point currentDot;

   /**
     * The number of steps played since the last reset
     */
    private int numberOfSteps;
 

    private Random generator;
    



    /**
     * Constructor to initialize the model to a given size of board.
     * 
     * @param size
     *            the size of the board
     */
    public GameModel(int size) {
        numberOfSteps = 0;
        generator = new Random();
        sizeOfGame = size;

        reset();
    }


    /**
     * Resets the model to (re)start a game. The previous game (if there is one)
     * is cleared up . The blue dot is positioned as per instructions, and each 
     * dot of the board is either AVAILABLE, or SELECTED (with
     * a probability 1/INITIAL_PROBA). The number of steps is reset.
     */
    public void reset(){

        model = new int[sizeOfGame][sizeOfGame];

        for(int i = 0; i < sizeOfGame; i++){
            for(int j = 0; j < sizeOfGame; j++){
                model[i][j] = AVAILABLE;
            }
        }

        // on a odd board, put the current dot randomly on a centered square of
        // 2 by 2, on an even board, put the current dot randomly on a centered 
        // square of 3 by 3

        if(sizeOfGame%2 == 0){
            currentDot = new Point(sizeOfGame/2 - generator.nextInt(2),
                sizeOfGame/2 - generator.nextInt(2));
        } else{
            currentDot = new Point(sizeOfGame/2 + 1 - generator.nextInt(3),
                sizeOfGame/2 + 1 - generator.nextInt(3));
        }

        model[currentDot.getX()][currentDot.getY()] = DOT;

        for(int i = 0; i < sizeOfGame; i++){
            for(int j = 0; j < sizeOfGame; j++){
                if(!( i == currentDot.getX() && j == currentDot.getY())){
                    if(generator.nextInt(INITIAL_PROBA) == 0){
                        model[i][j] = SELECTED;
                    }
                }
            }
        }

        numberOfSteps = 0;
    }


    /**
     * Getter method for the size of the game
     * 
     * @return the value of the attribute sizeOfGame
     */   
    public int getSize(){
        return sizeOfGame;
    }

   /**
     * Getter method for the ``model'' array
     * 
     * @return the model array
     */   
    public int[][] getModel(){
        return model;
    }



    /**
     * returns the current status (AVAILABLE, SELECTED or DOT) of a given dot in the game
     * 
     * @param i
     *            the x coordinate of the dot
     * @param j
     *            the y coordinate of the dot
     * @return the status of the dot at location (i,j)
     */   
    public int getCurrentStatus(int i, int j){
        return model[i][j];
    }


    /**
     * Sets the status of the dot at coordinate (i,j) to SELECTED, and 
     * increases the number of steps by one
     * 
     * @param i
     *            the x coordinate of the dot
     * @param j
     *            the y coordinate of the dot
     */   
    public void select(int i, int j){
        model[i][j] = SELECTED;
        numberOfSteps++;
    }
    
    public void setAvailable(int i, int j){
       model[i][j] = AVAILABLE;
    }

    /**
     * Puts the blue dot at coordinate (i,j). Clears the previous location 
     * of the blue dot. If the i coordinate is "-1", it means that the blue 
     * dot exits the board (the player lost)
     *
     * @param i
     *            the new x coordinate of the blue dot
     * @param j
     *            the new y coordinate of the blue dot
     */   
    public void setCurrentDot(int i, int j){
        model[currentDot.getX()][currentDot.getY()] = AVAILABLE;
        // pass on "-1" to remove the current dot at the end of the game
        if(i != -1) {
            model[i][j] = DOT;
            currentDot.reset(i,j);
        }
    }

    /**
     * Getter method for the current blue dot
     * 
     * @return the location of the curent blue dot
     */   
    public Point getCurrentDot(){
        return currentDot;
    }
    

    /**
     * Getter method for the current number of steps
     * 
     * @return the current number of steps
     */   
    public int getNumberOfSteps(){
        return numberOfSteps;
    }
    
    public GameModel clone(GameModel previous_model){
       GameModel cloned_game = new GameModel(sizeOfGame);
       int[][] previous_board = previous_model.getModel();
       for(int x=0; x < previous_board.length; x++){
              for(int y=0; y < previous_board[x].length; y++){
                     if (previous_model.getCurrentStatus(x,y) == SELECTED){
                            cloned_game.select(x,y);
                     } else if (previous_model.getCurrentStatus(x,y) == DOT){
                            cloned_game.setCurrentDot(x,y);
                     } else if (previous_model.getCurrentStatus(x,y) == AVAILABLE){
                            cloned_game.setAvailable(x,y);
                     }
              
              }
       }

       return cloned_game;
       
    }



}
