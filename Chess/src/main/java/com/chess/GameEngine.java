package com.chess;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    public String[][] startBoard = new String[8][8];
    public Piece[][] board = new Piece[8][8];
    public List<Move> moveHistory = new ArrayList<>();
    public Move lastMove;
    public boolean playAsWhite;
    public boolean whiteToMove;
    private final int SCREEN_WIDTH = Chess.SCREEN_WIDTH;
    private final int SCREEN_HEIGHT = Chess.SCREEN_HEIGHT;
    private final int TILE_SIZE = GameView.TILE_SIZE;
    public Piece clickBuffer;
    public boolean gameEnded = false;
    public boolean checkMate = false;
    public boolean staleMate = false;
    public boolean humanVsAI;
    public boolean aiVsAi;

    public GameEngine (boolean playAsWhite, String boardStart, boolean whiteToMove, Scene scene, boolean humanPlaying, GameView gameView) {
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

        this.humanVsAI = humanPlaying;
        this.aiVsAi = !humanPlaying;
        gameLoop(scene, gameView);

    }

    public void gameLoop(Scene scene, GameView gameView) {

        new AnimationTimer() {
            public void handle(long now) {

                gameEnded = checkForCheckmateOrStalemate();
                if (gameEnded) {
                    stop();
                }

                if (humanVsAI) {
                    if ((whiteToMove && playAsWhite) || (!whiteToMove && !playAsWhite)) {
                        scene.setOnMouseClicked(event -> handleClick(event, gameView));
                    } else if (!gameEnded){
                        ChessAI agent = new ChessAI(4);
                        Move move = agent.calculateAIMove(board, getLegalMoves(), whiteToMove, playAsWhite);
                        move(move.startR, move.startC, move.endR, move.endC, gameView);
                    }
                } else {
                    scene.setOnMouseClicked(null);
                }

                if (aiVsAi && !gameEnded) {
                    ChessAI agent = new ChessAI(4);
                    Move move = agent.calculateAIMove(board, getLegalMoves(), whiteToMove, playAsWhite);
                    move(move.startR, move.startC, move.endR, move.endC, gameView);
                }

                scene.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case ESCAPE:
                            gameEnded = true;
                            break;
                    }
                });
            }
        }.start();
    }

    private void handleClick(MouseEvent event, GameView gameView) {

        if (event.getX() < SCREEN_WIDTH && event.getY() < SCREEN_HEIGHT) {
            int r = (int) event.getY() / TILE_SIZE;
            int c = (int) event.getX() / TILE_SIZE;

            if (r > 7 || c > 7) {
                return;
            }

            // Find the clicked piece, if any
            Piece clickedPiece = getPieceAt(r, c);

            // If a piece of the correct color is clicked, add it to the buffer
            if (clickedPiece != null && clickedPiece.color == (whiteToMove ? Piece.Color.WHITE : Piece.Color.BLACK)) {
                clickBuffer = clickedPiece;
                gameView.hideHighlights();
                gameView.drawHighlights(clickedPiece, clickedPiece.locC * TILE_SIZE, clickedPiece.locR * TILE_SIZE, this);
            }
            // If a second click is made on an empty tile or an enemy piece, and there is already a piece in the buffer
            else if (clickBuffer != null) {
                boolean moveSuccess = move(clickBuffer.locR, clickBuffer.locC, r, c, gameView);
                if (moveSuccess) {
                    clickBuffer = null;
                    gameView.hideHighlights();
                } else {
                    // If the move wasn't successful, check if a new valid piece was selected
                    if (clickedPiece != null && clickedPiece.color == (whiteToMove ? Piece.Color.WHITE : Piece.Color.BLACK)) {
                        clickBuffer = clickedPiece;
                        gameView.hideHighlights();
                        gameView.drawHighlights(clickedPiece, clickedPiece.locC * TILE_SIZE, clickedPiece.locR * TILE_SIZE, this);
                    }
                }
            }
        }
    }

    public boolean move(int startR, int startC, int endR, int endC, GameView gameView) {
        Piece piece = board[startR][startC];
        Piece capturedPiece = getPieceAt(endR, endC);

        if (piece == null) {
            return false;
        }

        List<Move> validMoves = getLegalMoves();

        Move attemptedMove = new Move(piece, capturedPiece, startR, startC, endR, endC);

        // En Passant check
        if (piece.type == Piece.Type.PAWN && startC != endC && capturedPiece == null) {
            if (lastMove != null && startR == lastMove.endR
                    && endC == lastMove.endC
                    && Math.abs(endR - lastMove.endR) == 1) {
                capturedPiece = lastMove.movedPiece;
                board[lastMove.endR][lastMove.endC] = null;
                attemptedMove = new Move(piece, capturedPiece, startR, startC, endR, endC);
            }
        }

        // Castle Move Creation
        if (piece.type == Piece.Type.KING && Math.abs(endC - startC) > 1) {
            attemptedMove = getAttempedCastleMove(piece, endC);
        }


        if (piece != null && validMoves.contains(attemptedMove) && !validMoves.isEmpty()) {
            // Update the previous position to be empty
            board[startR][startC] = null;

            // Set the new position on the board while
            board[endR][endC] = piece;

            // Update the piece's coordinates
            piece.locC = endC;
            piece.locR = endR;

            // Update the piece's image position
            piece.image.setX(endC * GameView.TILE_SIZE);
            piece.image.setY(endR * GameView.TILE_SIZE);

            // Remove image from captured piece
            if (capturedPiece != null) {
                gameView.removeImage(capturedPiece.image);
            }

            // Castle move, move rook
            Piece rook = attemptedMove.rookMoved;
            if (rook != null) {
                int rEndR = attemptedMove.rEndR;
                int rEndC = attemptedMove.rEndC;
                // update rook initial location, actual location, image location
                board[rook.locR][rook.locC] = null;
                board[rEndR][rEndC] = rook;
                rook.locR = rEndR;
                rook.locC = rEndC;
                rook.image.setX(rEndC * GameView.TILE_SIZE);
                rook.image.setY(rEndR * GameView.TILE_SIZE);
            }

            // Pawn promotion
            if (piece.type == Piece.Type.PAWN && (endR == 0 || endR == 7)) {
                String color = piece.color == Piece.Color.WHITE ? "w" : "b";
                //String pType = gameView.isHumanPlaying ? gameView.openPromoteWin(color) : "Q";
                String pType = "Q";
                switch (pType) {
                    case "Q" -> piece.type = Piece.Type.QUEEN;
                    case "B" -> piece.type = Piece.Type.BISHOP;
                    case "N" -> piece.type = Piece.Type.KNIGHT;
                    case "R" -> piece.type = Piece.Type.ROOK;
                }
                gameView.removeImage(piece.image);
                piece.image = gameView.createImage(color + pType);
                piece.image.setX(piece.locC * GameView.TILE_SIZE);
                piece.image.setY(piece.locR * GameView.TILE_SIZE);
            }

            // Store move in move list
            moveHistory.add(attemptedMove);
            this.lastMove = attemptedMove;
            gameView.drawLastMoveHighlights(this);

            // Switch turns
            this.whiteToMove = !this.whiteToMove;
            return true;    // Move made
        } else {
            return false;   // Move not made
        }
    }

    public List<Move> getLegalMoves() {
        List<Move> moves = loadMoves(true);
        moves.removeIf(this::resultsInCheck);
        return moves;
    }

    private List<Move> loadMoves(boolean forMoving) {
        // If forMoving is true, moves are for all pieces of that turn
        // If forMoving is false, moves are for opposing pieces
        List<Move> possibleMoves = new ArrayList<>();
        Piece piece;
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                piece = getPieceAt(r, c);
                if (piece != null) {
                    if (forMoving) {
                        if (whiteToMove && piece.color == Piece.Color.WHITE) {
                            possibleMoves.addAll(piece.loadPsudoMoves());
                        } else if (!whiteToMove && piece.color == Piece.Color.BLACK) {
                            possibleMoves.addAll(piece.loadPsudoMoves());
                        }
                        // Get castle moves
                        if (piece.type == Piece.Type.KING) {
                            possibleMoves.addAll(getCastleMoves(piece));
                        }

                    } else {
                        if (whiteToMove && piece.color == Piece.Color.BLACK) {
                            possibleMoves.addAll(piece.loadPsudoMoves());
                        } else if (!whiteToMove && piece.color == Piece.Color.WHITE) {
                            possibleMoves.addAll(piece.loadPsudoMoves());
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    private boolean resultsInCheck(Move move) {
        Piece movedPiece = move.movedPiece;
        Piece capturedPiece = move.capturedPiece;

        // original positions
        int startR = movedPiece.locR;
        int startC = movedPiece.locC;

        // Temporarily move the piece on the board
        board[movedPiece.locR][movedPiece.locC] = null;
        movedPiece.locR = move.endR;
        movedPiece.locC = move.endC;
        board[movedPiece.locR][movedPiece.locC] = movedPiece;

        // Get king position
        int[] kingLocation = getKingLocations();
        int kingR, kingC;
        if (movedPiece.type == Piece.Type.KING) {
            kingR = move.endR;
            kingC = move.endC;
        } else {
            kingR = whiteToMove ? kingLocation[0] : kingLocation[2];
            kingC = whiteToMove ? kingLocation[1] : kingLocation[3];
        }

        // Get all opponent moves
        List<Move> opponentMoves = loadMoves(false);

        // Check if any opponent move can attack the king
        for (Move oppMove : opponentMoves) {
            if (oppMove.endR == kingR && oppMove.endC == kingC) {
                // Restore the board
                board[movedPiece.locR][movedPiece.locC] = null;
                movedPiece.locR = startR;
                movedPiece.locC = startC;
                board[startR][startC] = movedPiece;
                if (capturedPiece != null) {
                    board[capturedPiece.locR][capturedPiece.locC] = capturedPiece;
                }
                return true;
            }
        }

        // Restore the board
        board[movedPiece.locR][movedPiece.locC] = null;
        movedPiece.locR = startR;
        movedPiece.locC = startC;
        board[movedPiece.locR][movedPiece.locC] = movedPiece;
        if (capturedPiece != null) {
            board[capturedPiece.locR][capturedPiece.locC] = capturedPiece;
        }
        return false;
    }

    // Looks for if player is in check
    private boolean inCheck() {
        int[] kingLocation = getKingLocations();
        boolean[][] attackedSquares = attackedSquares();
        return attackedSquares[kingLocation[0]][kingLocation[1]] || attackedSquares[kingLocation[2]][kingLocation[3]];
    }

    private boolean[][] attackedSquares() {
        boolean[][] attackedSquares = new boolean[8][8];
        List<Move> oppMoves = loadMoves(false);

        for (Move move : oppMoves) {
            attackedSquares[move.endR][move.endC] = true;
        }

        return attackedSquares;
    }

    private int[] getKingLocations() {
        int[] locations = new int[4];
        Piece piece;
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                piece = getPieceAt(r, c);
                if (piece != null) {
                    if (piece.type == Piece.Type.KING && piece.color == Piece.Color.WHITE) {
                        locations[0] = r;
                        locations[1] = c;
                    } else if (piece.type == Piece.Type.KING && piece.color == Piece.Color.BLACK) {
                        locations[2] = r;
                        locations[3] = c;
                    }
                }
            }
        }

        return locations;
    }

    public Piece getPieceAt(int r, int c) {
        return board[r][c];
    }

    public boolean checkForCheckmateOrStalemate() {
        List<Move> legalMoves = getLegalMoves();
        if (legalMoves.isEmpty()) {
            if (inCheck()) {
                this.checkMate = true;
                System.out.println("Checkmate");
            } else {
                this.staleMate = true;
                System.out.println("Stalemate");
            }
            return true;
        } else if (checkPiecesLeft()) {
            this.staleMate = true;
            System.out.println("Stalemate");
            return true;
        }
        return false;
    }

    public boolean checkPiecesLeft() {
        List<Piece> piecesLeft = new ArrayList<>();
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                Piece piece = getPieceAt(r, c);
                if (piece != null) {
                    piecesLeft.add(piece);
                }
            }
        }

        return piecesLeft.size() == 2;
    }

    public List<Move> getLegalMovesForPiece(Piece piece) {
        List<Move> LegalMoves = getLegalMoves();
        List<Move> validPieceMoves = new ArrayList<>();

        for (Move move : LegalMoves) {
            if (piece.locC == move.movedPiece.locC && piece.locR == move.movedPiece.locR) {
                validPieceMoves.add(move);
            }
        }

        return validPieceMoves;
    }

    private Move getAttempedCastleMove(Piece piece, int endC) {
        Move attempedCastleMove = new Move(null, null, -1, -1, -1, -1);
        int kingRank = piece.locR;
        Piece rook;

        if (this.playAsWhite) {
            // Queen side
            if (endC == 2) {
                rook = getPieceAt(kingRank, 0);
                attempedCastleMove = new Move(piece, null, kingRank, 4, kingRank, endC, rook, kingRank, 3);
            }
            // King side
            else if (endC == 6) {
                rook = getPieceAt(kingRank, 7);
                attempedCastleMove = new Move(piece, null, kingRank, 4, kingRank, endC, rook, kingRank, 5);
            }
        } else {
            // King side
            if (endC == 1) {
                rook = getPieceAt(kingRank, 0);
                attempedCastleMove = new Move(piece, null, kingRank, 3, kingRank, endC, rook, kingRank, 2);
            }
            // Queen side
            else if (endC == 5) {
                rook = getPieceAt(kingRank, 7);
                attempedCastleMove = new Move(piece, null, kingRank, 3, kingRank, endC, rook, kingRank, 4);
            }
        }

        return attempedCastleMove;
    }

    private List<Move> getCastleMoves(Piece currentKing) {
        List<Move> castleMoves = new ArrayList<>();
        boolean queenSide = true, kingSide = true;
        boolean queenSideClear = false, kingSideClear = false;
        int kingRank = currentKing.locR;

        // Check all last moves to see if this king has move
        for (Move move : moveHistory) {
            Piece lastPieceMoved = move.movedPiece;

            // Check if king has moved
            if (lastPieceMoved == currentKing) {
                return new ArrayList<>();
            }

            if (this.playAsWhite) {
                // Check if queen side rook has moved
                if (move.startR == kingRank && move.startC == 0) {
                    queenSide = false;
                }
                // Check if king side rook has moved
                if (move.startR == kingRank && move.startC == 7) {
                    kingSide = false;
                }
            } else {
                // Check if king side rook has moved
                if (move.startR == kingRank && move.startC == 0) {
                    kingSide = false;
                }
                // Check if queen side rook has moved
                if (move.startR == kingRank && move.startC == 7) {
                    queenSide = false;
                }
            }

            // Castle not possible, both rooks moved
            if (!queenSide && !kingSide) {
                return new ArrayList<>();
            }

        }

        // Check kings rank to ensure path is clear
        if (this.playAsWhite) {
            if (kingRank == 0) {
                if (board[0][1] == null && board[0][2] == null && board[0][3] == null) {
                    queenSideClear = true;
                }
                if (board[0][5] == null && board[0][6] == null) {
                    kingSideClear = true;
                }
            } else if (kingRank == 7) {
                if (board[7][1] == null && board[7][2] == null && board[7][3] == null) {
                    queenSideClear = true;
                }
                if (board[7][5] == null && board[7][6] == null) {
                    kingSideClear = true;
                }
            }
        } else {
            // Playing as black
            if (kingRank == 0) {
                if (board[0][1] == null && board[0][2] == null) {
                    kingSideClear = true;
                }
                if (board[0][4] == null && board[0][5] == null && board[0][6] == null) {
                    queenSideClear = true;
                }
            } else if (kingRank == 7) {
                if (board[7][1] == null && board[7][2] == null) {
                    kingSideClear = true;
                }
                if (board[7][4] == null && board[7][5] == null && board[7][6] == null) {
                    queenSideClear = true;
                }
            }
        }

        // look at opp moves to see if path is still clear of attacked squares
        List<Move> opponentMoves = loadMoves(false);
        for (Move oppMove : opponentMoves) {
            int endR = oppMove.endR;
            int endC = oppMove.endC;

            if (this.playAsWhite) {
                // Check queen side
                if (queenSideClear && endR == kingRank && (endC == 2 || endC == 3 || endC == 4)) {
                    queenSideClear = false;
                }
                // Check king side
                if (kingSideClear && endR == kingRank && (endC == 4 || endC == 5 || endC == 6)) {
                    kingSideClear = false;
                }
            } else {
                // Check king side
                if (kingSideClear && endR == kingRank && (endC == 0 || endC == 1 || endC == 2)) {
                    kingSideClear = false;
                }
                // Check queen side
                if (queenSideClear && endR == kingRank && (endC == 4 || endC == 5 || endC == 6)) {
                    queenSideClear = false;
                }
            }
        }

        // Create and append moves if castle is possible
        if (this.playAsWhite) {
            if (queenSide && queenSideClear && !inCheck() && !resultsInCheck(new Move(currentKing, null, kingRank, 4, kingRank, 2))) {
                Piece rook = getPieceAt(kingRank, 0);
                castleMoves.add(new Move(currentKing, null, kingRank, 4, kingRank, 2, rook, kingRank, 3));
            }

            if (kingSide && kingSideClear && !inCheck() && !resultsInCheck(new Move(currentKing, null, kingRank, 4, kingRank, 6))) {
                Piece rook = getPieceAt(kingRank, 7);
                castleMoves.add(new Move(currentKing, null, kingRank, 4, kingRank, 6, rook, kingRank, 5));
            }
        } else {
            if (queenSide && queenSideClear && !inCheck() && !resultsInCheck(new Move(currentKing, null, kingRank, 3, kingRank, 5))) {
                Piece rook = getPieceAt(kingRank, 7);
                castleMoves.add(new Move(currentKing, null, kingRank, 3, kingRank, 5, rook, kingRank, 4));
            }

            if (kingSide && kingSideClear && !inCheck() && !resultsInCheck(new Move(currentKing, null, kingRank, 3, kingRank, 1))) {
                Piece rook = getPieceAt(kingRank, 0);
                castleMoves.add(new Move(currentKing, null, kingRank, 3, kingRank, 1, rook, kingRank, 2));
            }
        }

        return castleMoves;
    }
}
