import java.util.*;

public class FiveInARow {

	int boardWidth = 6;
	int boardHeight = 7;
	int boardSize = boardWidth * boardHeight;
	
	public static void main(String[] args) {
		FiveInARow game = new FiveInARow();
		char[] board = new char[game.boardSize];
		Arrays.fill(board, '-');
		
		Scanner scanner = new Scanner(System.in);boolean isPlayerTurn = true;
		
		while (true) {
			game.printBoard(board);
            
			if (game.hasWon(board, 'O')) {
				System.out.println("You won");
				break;
			} else if (game.hasWon(board, 'X')) {
				System.out.println("Computer won");

			} else if (game.isBoardFull(board)) {
				System.out.println("Tied");
				break;
			}
			
			if (isPlayerTurn) {
				int col;
				int width = game.boardWidth-1;
				
				do {
					System.out.println(" \n\nVälj en kolumn (0 - " + width + "): ");
					col = scanner.nextInt();
				
				} while (col < 0 || col >= game.boardWidth || !game.isValidMove(board, col));
                
                game.makeMove(board, col, 'O');
                
			} else {
				System.out.println("\n\nComputer is thinking..");
			int bestMove = game.getBestMove(board);
			
			game.makeMove(board, bestMove, 'X');
			
			}
			isPlayerTurn = !isPlayerTurn;
        }
		scanner.close();
    }
      
    /**
     * Evaluates the board using mini-max algorithm together with alpha beta pruning
     * Return the best move for a player on the board
     * 
     * @param board is the current board, represented of a char array filled with X or O, for each player or - if a space is empty.
     * @param depth is how many moves ahead we want to calculate for each player
     * @param isMaximizing är om vi maximerar eller minimerar - dator eller människa
     * @param alpha - Alpha value for pruning, initial call set to negative infinity
     * @param beta - Beta value for pruning, initial call positive infinity
     * @return
     */
    
	private int minimax(char[] board, int depth, boolean isMaximizing, int alpha, int beta) {
		if (hasWon(board, 'O')) return 1000;
		if (hasWon(board, 'X')) return -1000;
		if (isBoardFull(board) || depth == 0) return evaluateBoard(board);

		if (isMaximizing) {
			int maxEval = Integer.MIN_VALUE;
			for (int move : getPossibleMoves(board)) {
				board[move] = 'O';
				int eval = minimax(board, depth - 1, false, alpha, beta);
				board[move] = '-';
				maxEval = Math.max(maxEval, eval);
                
				// pruning
				alpha = Math.max(alpha, eval);
				if (beta <= alpha) break;
			}
			
			return maxEval;
            
		} else {
			int minEval = Integer.MAX_VALUE;
			for (int move : getPossibleMoves(board)) {
				board[move] = 'X';
				int eval = minimax(board, depth - 1, true, alpha, beta);
				board[move] = '-';
				minEval = Math.min(minEval, eval);
                
				
				// pruning
				beta = Math.min(beta, eval);
				if (beta <= alpha) break;
			}
			
			return minEval;
		}
	}

	private int evaluateBoard(char[] board) {
		int score = 0;
        
		for (int index = 0; index < boardSize; index++) {
			char piece = board[index];
			if (piece == 'O' || piece == 'X') { // only pieces
				int modifier = (piece == 'O') ? 1 : -1; // O = 1, X = -1
                
				int[] directions = new int[] {1, boardWidth, boardWidth + 1, boardWidth - 1}; // array to iterate vertical, etc.
                
				for (int step : directions) {
					score += modifier * evaluatePattern(board, index, step, piece);
				}
			}
		}
        
		return score;
	}

	private int evaluatePattern(char[] board, int index, int step, char player) {
		int count = 0;
		int empty = 0;
        
		for (int i = 0; i < 5; i++) {
			int current = index + i * step;
			if (
					current < 0
					|| current >= boardSize
            		|| (i > 0 && (current % boardWidth == 0 
            		|| current % boardWidth == boardWidth - 1))) 
			{
				return 0; // Outside board
			}
            
			if (board[current] == player) {
				count++;
			}
			
			else if (board[current] == '-') {
				empty++;
			}
			
			else return 0; // Opponents piece - can't form win
		}
		
		return count == 5 ? 100 : (count == 4 && empty == 1) ? 50 : count; // 5 in a row = 100, 4 in a row + emppty = 50, else return count.
	}

    
	private boolean hasWon(char[] board, char player) {
        // horisontal
		
		for (int row = 0; row < boardHeight; row++) {
			for (int col = 0; col <= boardWidth - 5; col++) {
				int idx = row * boardWidth + col;
				if (board[idx] == player && board[idx+1] == player && board[idx+2] == player &&
					board[idx+3] == player && board[idx+4] == player) {
					return true;
				}
			}
		}
        
        // vertical
		for (int col = 0; col < boardWidth; col++) {
			for (int row = 0; row <= boardHeight - 5; row++) {
				int idx = row * boardWidth + col;
				if (board[idx] == player && board[idx+boardWidth] == player && 
					board[idx+2*boardWidth] == player && board[idx+3*boardWidth] == player && 
					board[idx+4*boardWidth] == player) {
					return true;
				}
			}
		}
        
        // diagonal \
		for (int row = 0; row <= boardHeight - 5; row++) {
			for (int col = 0; col <= boardWidth - 5; col++) {
				int idx = row * boardWidth + col;
                // Kontrollera att index + steg inte går utanför brädet
				if (board[idx] == player && 
					idx + boardWidth + 1 < boardSize && board[idx + boardWidth + 1] == player &&
					idx + 2 * (boardWidth + 1) < boardSize && board[idx + 2 * (boardWidth + 1)] == player &&
					idx + 3 * (boardWidth + 1) < boardSize && board[idx + 3 * (boardWidth + 1)] == player &&
					idx + 4 * (boardWidth + 1) < boardSize && board[idx + 4 * (boardWidth + 1)] == player) 
				{	
					return true;
				}
			}
		}

        // diagonal /
		for (int row = 0; row <= boardHeight - 5; row++) {
			for (int col = 4; col < boardWidth; col++) {
				int idx = row * boardWidth + col;
                // check if index is out of board
				if (board[idx] == player && 
					idx - boardWidth + 1 >= 0 && board[idx - boardWidth + 1] == player &&
					idx - 2 * (boardWidth - 1) >= 0 && board[idx - 2 * (boardWidth - 1)] == player &&
					idx - 3 * (boardWidth - 1) >= 0 && board[idx - 3 * (boardWidth - 1)] == player &&
					idx - 4 * (boardWidth - 1) >= 0 && board[idx - 4 * (boardWidth - 1)] == player) 
				{
					return true;
				}
			}
		}

		return false;  // if no row is found
	}

	private int getBestMove(char[] board) {
		int bestMove = -1;
		int bestScore = Integer.MIN_VALUE;

		for (int col = 0; col < boardWidth; col++) {
			if (!isValidMove(board, col)) continue; // Skip illegal moves
 
			makeMove(board, col, 'O');
			int score = minimax(board, 6, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
			undoMove(board, col); // undo

			if (score > bestScore) {
				bestScore = score;
				bestMove = col; // save col instead of index
			}
		}

		return bestMove;
	}
    
	private void undoMove(char[] board, int col) {
		for (int row = 0; row < boardHeight; row++) {
			int index = row * boardWidth + col;
			if (board[index] != '-') {
				board[index] = '-'; // undo
				break;
			}
		}
	}

	private boolean isValidMove(char[] board, int col) {
		for (int row = boardHeight - 1; row >= 0; row--) {
			int index = row * boardWidth + col;
			if (board[index] == '-') {
				return true; // if its empty is legal
			}
		}
		return false; // if not empty space its illegal
	}

	private void printBoard(char[] board) {
		for (int row = 0; row < boardHeight; row++) {
			for (int col = 0; col < boardWidth; col++) {
				System.out.print(board[row * boardWidth + col] + " ");
			}
			System.out.println();
		}
		for(int i = 0; i < boardWidth; i++) {
			System.out.print(i + " ");
		}
	}

	private void makeMove(char[] board, int col, char player) {
		for (int row = boardHeight - 1; row >= 0; row--) {
			int index = row * boardWidth + col;
			if (board[index] == '-') {
				board[index] = player;
				break;
			}
		}
	}


	private boolean isBoardFull(char[] board) {
		return !new String(board).contains("-");
	}


	private List<Integer> getPossibleMoves(char[] board) {
		List<Integer> possibleMoves = new ArrayList<>();
        
		for (int col = 0; col < boardWidth; col++) {
			for (int row = boardHeight - 1; row >= 0; row--) {
				int index = row * boardWidth + col;
				if (board[index] == '-') {
					possibleMoves.add(col); // only col not index
					break;
				}
			}
		}
		return possibleMoves;
	}
}
