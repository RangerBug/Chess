package com.chess;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    public String[][] startBoard = new String[8][8];
    public Piece[][] board = new Piece[8][8];
    public List<Move> moveHistory = new ArrayList<>();
    public Move lastMove;
    public boolean playAsWhite;
    public boolean whiteToMove;

    public GameEngine (boolean playAsWhite, String boardStart, boolean whiteToMove) {
        this.playAsWhite = playAsWhite;
        this.whiteToMove = whiteToMove;

        if (boardStart.equals("classic") && playAsWhite) {
            startBoard = new String[][] {
                    {"bR", "bN", "bB", "bQ", "bK", "bB", "bN", "bR"},
                    {"bP", "bP", "bP", "bP", "bP", "bP", "bP", "bP"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"wP", "wP", "wP", "wP", "wP", "wP", "wP", "wP"},
                    {"wR", "wN", "wB", "wQ", "wK", "wB", "wN", "wR"}
            };

        } else if (boardStart.equals("classic") && !playAsWhite) {
            startBoard = new String[][] {
                    {"wR", "wN", "wB", "wK", "wQ", "wB", "wN", "wR"},
                    {"wP", "wP", "wP", "wP", "wP", "wP", "wP", "wP"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"--", "--", "--", "--", "--", "--", "--", "--"},
                    {"bP", "bP", "bP", "bP", "bP", "bP", "bP", "bP"},
                    {"bR", "bN", "bB", "bK", "bQ", "bB", "bN", "bR"}
            };
        }

    }

    public Piece getPieceAt(int r, int c) {
        return board[r][c];
    }

}
