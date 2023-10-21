package com.chess;

import java.util.List;
import java.util.Random;

public class ChessAI {
    public int depth;
    public ChessAI(int depth) {
        this.depth = depth;
    }
    public Move calculateAIMove(Piece[][] board, List<Move> moves, boolean whiteToMove, boolean playAsWhite){
        System.out.println(scoreBoard(board, whiteToMove, playAsWhite));
        return findRandomMove(moves);
    }

    public Move findRandomMove(List<Move> moves) {
        Random rand = new Random();
        int randomInt = rand.nextInt(moves.size());
        return moves.get(randomInt);
    }

    public Move getBestMove(Piece[][] initBoard, List<Move> moves, boolean whiteToMove, boolean playAsWhite) {
        Move bestMove = moves.get(0);
        int bestScore = 0;
        for (Move move : moves) {
            int value = 0;
            if (value > bestScore) {
                bestMove = move;
            }
        }
        return bestMove;
    }



    private int scoreBoard(Piece[][] board, boolean whiteToMove, boolean playAsWhite) {
        int score = 0;
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                Piece piece = board[r][c];
                if (piece == null) continue;

                int pieceScore = getPieceScore(piece.type, r, c, whiteToMove, playAsWhite);
                if ((whiteToMove && piece.color == Piece.Color.WHITE) || (!whiteToMove && piece.color == Piece.Color.BLACK)) {
                    score += pieceScore;
                } else if ((!whiteToMove && piece.color == Piece.Color.WHITE) || (whiteToMove && piece.color == Piece.Color.BLACK)) {
                    score -= pieceScore;
                }
            }
        }
        return score;
    }

    private int getPieceScore(Piece.Type type, int r, int c, boolean whiteToMove, boolean playAsWhite) {
        return switch (type) {
            case KING -> whiteToMove ? kingAsWScores[r][c] : kingAsBScores[r][c];
            case QUEEN -> 100 + queenScores[r][c];
            case BISHOP -> 30 + bishopScores[r][c];
            case KNIGHT -> 30 + knightScores[r][c];
            case ROOK -> 50 + rookScores[r][c];
            case PAWN -> 10 + (playAsWhite ? playAsScores[r][c] : oppScores[r][c]);
            default -> 0;
        };
    }


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
