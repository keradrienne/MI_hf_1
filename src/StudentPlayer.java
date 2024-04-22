//
// Megjegyzés: a megoldásom elkészítésében egyéb internetes forrásokat is használtam pl: chatGPT 4, Mesterséges Intelligencia Almanach
//

/**
 * Inherited class of Player
 */
public class StudentPlayer extends Player{
    private int depthLimit = 5; // Depth limit
    private int bestMoveColumn; // The best column, where to put the token

    //Values used for the scoring system
    final int FOUR_IN_A_ROW_VALUE = 10000;
    final int THREE_IN_A_ROW_VALUE = 100;
    final int TWO_IN_A_ROW_VALUE = 10;

    /**
     * Constructor for StudentPlayer
     * @param playerIndex  number of player
     * @param boardSize size of the board
     * @param nToConnect number of tokens to be connected
     */
    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    /**
     * Step function
     * @param board instance of the game board
     * @return the best step
     */
    @Override
    public int step(Board board) {
        bestMoveColumn = -1;
        double value = maxAction(board, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        return bestMoveColumn;
    }

    /**
     * Minimax function -> max value function
     * @param board instance of the game board
     * @param alpha alpha-beta pruning
     * @param beta alpha-beta pruning
     * @param depth depth limit for depth-limited search
     * @return best move
     */
    private double maxAction(Board board, double alpha, double beta, int depth) {
        // End test
        if (board.gameEnded() || depth == depthLimit) {
            return evaluateBoard(board);
        }

        // Calculating the value of the leaf
        double value = Double.NEGATIVE_INFINITY;
        for (int column : board.getValidSteps()) {  // Iterating over possible steps
            Board childBoard = new Board(board);    // New board for simulating the step
            childBoard.step(playerIndex, column);   // Simulating the step without altering the original board
            double childValue = minAction(childBoard, alpha, beta, depth + 1); // Value of the leaf
            if (childValue > value) {
                value = childValue;
                if (depth == 0) { // Checking if we are at the topmost level of our recursive search
                    bestMoveColumn = column;
                }
            }

            // Alpha-beta pruning
            alpha = Math.max(alpha, value);
            if (value >= beta) {
                break;
            }
        }
        return value;
    }

    /**
     * Minimax function -> min value function, symmetrical to the maxAction function
     * @param board instance of the game board
     * @param alpha alpha-beta pruning
     * @param beta alpha-beta pruning
     * @param depth depth limit for depth-limited search
     * @return best move
     */
    private double minAction(Board board, double alpha, double beta, int depth) {
        // End test
        if (board.gameEnded() || depth == depthLimit) {
            return evaluateBoard(board);
        }

        // Calculating the value of the leaves
        double value = Double.POSITIVE_INFINITY;
        int opponentIndex = (playerIndex == 1) ? 2 : 1; // Determine opponent's index
        for (int column : board.getValidSteps()) {      // Iterating over possible steps
            Board childBoard = new Board(board);        // New board for simulating the step
            childBoard.step(opponentIndex, column);     // Simulating the step without altering the original board
            double childValue = maxAction(childBoard, alpha, beta, depth + 1); // value of the leaf
            if (childValue < value) {
                value = childValue;
            }

            // Alpha-beta pruning
            beta = Math.min(beta, value);
            if (alpha >= value) {
                break;
            }
        }
        return value;
    }

    /**
     * Calculates a heuristic score for the board's current state
     * @param board instance of the game board
     * @return score for the current state of the board
     */
    private double evaluateBoard(Board board) {
        int score = 0;

        // Check for 4-in-a-row or 3-in-a-row or 2-in-a-row for both players
        for (int i = 0; i < boardSize[0]; i++) {
            for (int j = 0; j < boardSize[1]; j++) {
                if (board.getState()[i][j] != 0) {  // Check for non-empty cells
                    // Check horizontal, vertical, and both diagonals
                    // (board.getState()[i][j] == playerIndex ? 1 : -1) ensures that if the direction is favorable for the player,
                    // we add to the score, but if it's favorable for the opponent, we subtract from the score
                    score += evaluateDirection(board, i, j, 1, 0) * (board.getState()[i][j] == playerIndex ? 1 : -1); // horizontal
                    score += evaluateDirection(board, i, j, 0, 1) * (board.getState()[i][j] == playerIndex ? 1 : -1); // vertical
                    score += evaluateDirection(board, i, j, 1, 1) * (board.getState()[i][j] == playerIndex ? 1 : -1); // diagonal from top-left to bottom-right
                    score += evaluateDirection(board, i, j, 1, -1) * (board.getState()[i][j] == playerIndex ? 1 : -1); // diagonal from bottom-left to top-right
                }
            }
        }

        return score;
    }

    /**
     * Calculates the value of a sequence of tokens in a specified direction
     * @param board instance of the game board
     * @param row stating row
     * @param col stating col
     * @param deltaRow direction
     * @param deltaCol direction
     * @return the largest number of tokens in a row
     */
    private int evaluateDirection(Board board, int row, int col, int deltaRow, int deltaCol) {
        int length = 1;
        int token = board.getState()[row][col];

        for (int i = 1; i < 4; i++) { //Explore the next three cells in the given direction
            if (row + i * deltaRow >= 0 && row + i * deltaRow < boardSize[0] //We check if the next cell in the direction is within the board's boundaries
                    && col + i * deltaCol >= 0 && col + i * deltaCol < boardSize[1]
                    && board.getState()[row + i * deltaRow][col + i * deltaCol] == token) { // then check if this next cell has the same token as our starting cell
                length++;
            } else {
                break; // If either condition fails, we exit the loop
            }
        }

        // Return value based on sequence length
        switch (length) {
            case 4: return FOUR_IN_A_ROW_VALUE;
            case 3: return THREE_IN_A_ROW_VALUE;
            case 2: return TWO_IN_A_ROW_VALUE;
            default: return 0;
        }
    }
}
