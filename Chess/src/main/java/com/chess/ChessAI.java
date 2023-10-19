package com.chess;

import java.util.List;
import java.util.Random;

public class ChessAI {
    public Move calculateAIMove(Piece[][] board, List<Move> moves){

        return findRandomMove(moves);
    }

    public Move findRandomMove(List<Move> moves) {
        Random rand = new Random();
        int randomInt = rand.nextInt(moves.size());
        return moves.get(randomInt);
    }

    /*
    private int scoreBoard(GameState aiGameState) {
        int score = 0;
        int pieceScore = 0;
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                Piece piece = aiGameState.getPieceAt(r, c);
                Piece.Color color = aiGameState.whiteToMove ? Piece.Color.WHITE : Piece.Color.BLACK;
                if (piece.color == color) {
                    switch (piece.type) {
                        case KING ->
                            int posScore;
                            if aiGameState.playAsWhite ? posScore = kingAsWScores[r][c] : posScore = kingAsBScores[r][c];
                                pieceScore = posScore;
                        case QUEEN -> pieceScore = 100;
                        case BISHOP -> pieceScore = 30;
                        case KNIGHT -> pieceScore = 30;
                        case ROOK -> pieceScore = 50;
                        case PAWN -> pieceScore = 10;
                    }
                    score += pieceScore;
                }
            }
        }

    }
    */

    int[][] kingAsWScores = {
            {1, 1, 5, 1, 1, 1, 5, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 5, 1, 1, 1, 5, 1}};

    int[][] kingAsBScores = {
            {1, 5, 1, 1, 1, 5, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 5, 1, 1, 1, 5, 1, 1}};

    int[][] queenScores = {
            {4, 3, 2, 1, 1, 2, 3, 4},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {2, 3, 3, 1, 1, 3, 3, 2},
            {1, 2, 1, 1, 1, 1, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1},
            {2, 3, 3, 1, 1, 3, 3, 2},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {4, 3, 2, 1, 1, 2, 3, 4}};

    int[][] knightScores = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}};

    int[][] rookScores = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}};

    int[][] bishopScores = {
            {4, 3, 2, 1, 1, 2, 3, 4},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {2, 3, 4, 3, 3, 4, 3, 2},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {2, 3, 4, 3, 3, 4, 3, 2},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {4, 3, 2, 1, 1, 2, 3, 4}};

    int[][] playAsScores = {
            {10, 10, 10, 10, 10, 10, 10, 10},
            {8, 8, 8, 8, 8, 8, 8, 8},
            {5, 6, 6, 7, 7, 6, 6, 5},
            {2, 3, 3, 5, 5, 3, 3, 2},
            {1, 2, 3, 5, 5, 3, 2, 1},
            {1, 1, 2, 2, 2, 2, 1, 1},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    int[][] oppScores = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {1, 1, 2, 2, 2, 2, 1, 1},
            {1, 2, 3, 5, 5, 3, 2, 1},
            {2, 3, 3, 5, 5, 3, 3, 2},
            {5, 6, 6, 7, 7, 6, 6, 5},
            {8, 8, 8, 8, 8, 8, 8, 8},
            {10, 10, 10, 10, 10, 10, 10, 10}};
}




/* TODO:
Main Screen
- play button
- game history button
- settings
- leaderboard button

Game Screen
- board
- past moves
- menu/quit button

--------
Backend Game at game start
- Game Setup
    - Load board image
    - Load pieces
        - Create pieces
        - Load piece images
    - Load players as ? human (Player1 = True)

- Game Loop
    - Check if AI or human is playing
    - Action handlers
        - move control
    - end game calculation
    - turn handler

--- End Game ---

End Game Screen
- End game text (win or stalemate)
- Enter name for leader board
- Save game in history button
- Game review arrows
- Main Menu Button
 */
