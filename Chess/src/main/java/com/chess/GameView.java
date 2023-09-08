package com.chess;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameView {
    public Stage stage;
    public static final int SCREEN_HEIGHT = Chess.SCREEN_HEIGHT;
    public static final int SCREEN_WIDTH = Chess.SCREEN_WIDTH;
    public static final int TILE_SIZE = (SCREEN_HEIGHT) / 8;
    public static final int WIDTH = 8, HEIGHT = 8;
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();

    public void gameView(Stage stage, Chess mainView, boolean playWhite, boolean whiteToMove) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2D2D2D;");
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.stage = stage;

        GameEngine game = new GameEngine(playWhite, "classic", whiteToMove);

        drawBoard(game);

        root.getChildren().addAll(tileGroup, pieceGroup);

        // Buttons
        VBox buttonBox = new VBox();

        Button menuButton = new Button("Menu");
        menuButton.setPrefSize(300, 70);
        buttonBox.getChildren().add(menuButton);

        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setSpacing(10);

        buttonBox.setPadding(new Insets(0, 50, 30, 0));
        root.setRight(buttonBox);

        menuButton.setOnAction(actionEvent -> {
            root.setTop(null);
            mainView.start(stage);
        });

        stage.setScene(scene);
        stage.show();
    }

    private void drawBoard(GameEngine game) {
        String piece;
        ImageView image;
        for (int c = 0; c < HEIGHT; c++) {
            for (int r = 0; r < WIDTH; r++) {

                Tile tile = new Tile((r+c) % 2 == 0, r, c);
                tileGroup.getChildren().add(tile);
                piece = game.startBoard[r][c];

                if (!piece.equals("--")) {
                    Piece.Color color = piece.charAt(0) == 'w' ? Piece.Color.WHITE : Piece.Color.BLACK;
                    Piece.Type type = switch (piece.charAt(1)) {
                        case 'P' -> Piece.Type.PAWN;
                        case 'N' -> Piece.Type.KNIGHT;
                        case 'B' -> Piece.Type.BISHOP;
                        case 'Q' -> Piece.Type.QUEEN;
                        case 'K' -> Piece.Type.KING;
                        case 'R' -> Piece.Type.ROOK;
                        default -> throw new IllegalStateException("Unexpected value: " + piece.charAt(1));
                    };

                    image = createImage(piece);
                    Piece newPiece = new Piece(type, color, image, r, c, game);
                    game.board[r][c] = newPiece;
                }
            }
        }
    }

    public ImageView createImage(String piece) {
        Image image = new Image(getClass().getResource("/images/pieceStyle1/" + piece + ".png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        pieceGroup.getChildren().add(imageView);
        return imageView;
    }
}
