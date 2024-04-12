package game;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Game extends Application {

    private List<Button> circles;
    private List<Line> lines;
    private BorderPane root;
    private int gridSizeX = 10;
    private int gridSizeY = 10;
    private Pane canvas;
    private TextField xField;
    private TextField yField;
    private int turn = 0;
    private boolean startedGame = false;
    private Image icon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        createNewGameField();
        createGameCanvas();
        createControlPanel(primaryStage);
        verifyIcon(primaryStage);
        setScene(primaryStage);
        drawBoard();
    }

    private void setScene(Stage primaryStage) {
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stone Road Game");
        primaryStage.show();
    }

    private void verifyIcon(Stage primaryStage) {
        File file = new File("C:\\Users\\iusti\\Proiecte\\JavaLab\\Lab6\\src\\main\\Pictures\\rock.png");

        if (file.exists()) {
            icon = new Image(file.toURI().toString());
        }
        primaryStage.getIcons().add(icon);
    }

    private void createGameCanvas() {
        canvas = new Pane();
        BorderPane.setAlignment(canvas, Pos.CENTER);
        BorderPane.setMargin(canvas, new Insets(10));
        root.setCenter(canvas);
    }

    private void createNewGameField() {

        VBox configPanel = new VBox();
        configPanel.setPadding(new Insets(10));
        configPanel.setSpacing(10);
        configPanel.setAlignment(Pos.CENTER);

        HBox xyBox = new HBox();
        xyBox.setSpacing(10);
        xyBox.setAlignment(Pos.CENTER);
        xField = new TextField("10");
        yField = new TextField("10");
        xyBox.getChildren().addAll(new Label("X:"), xField, new Label("Y:"), yField);

        Button newGameButton = new Button("New Game");
        newGameButton.setOnAction(e -> {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            gridSizeX = x;
            gridSizeY = y;
            turn = 0;
            startedGame = false;
            root.setCenter(canvas);
            drawBoard();
        });

        HBox newGameBox = new HBox(newGameButton);
        newGameBox.setAlignment(Pos.CENTER);
        configPanel.getChildren().addAll(xyBox, newGameBox);
        root.setTop(configPanel);
    }

    private void createControlPanel(Stage primaryStage) {
        HBox controlPanel = new HBox();
        controlPanel.setPadding(new Insets(10));
        controlPanel.setSpacing(10);
        controlPanel.setAlignment(Pos.CENTER);

        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");
        Button exitButton = new Button("Exit");

        exitButton.setOnAction(e -> primaryStage.close());
        controlPanel.getChildren().addAll(loadButton, saveButton, exitButton);
        root.setBottom(controlPanel);

        saveButton.setOnAction(e -> saveGameState());
        loadButton.setOnAction(e -> loadGameState());
    }

    private void drawBoard() {
        double cellWidth = canvas.getWidth() / gridSizeX;
        double cellHeight = canvas.getHeight() / gridSizeY;

        canvas.getChildren().clear();
        circles = new ArrayList<>();
        lines = new ArrayList<>();

        createCircles(cellWidth, cellHeight);
        createLines(cellWidth, cellHeight);
        canvas.getChildren().addAll(circles);
    }

    private void createCircles(double cellWidth, double cellHeight) {
        for (int i = 0; i < gridSizeX; i++) {
            for (int j = 0; j < gridSizeY; j++) {
                double centerX = i * cellWidth + cellWidth / 2;
                double centerY = j * cellHeight + cellHeight / 2;
                Button button = new Button();
                button.setShape(new Circle(20));
                button.setMinSize(20, 20);
                button.setMaxSize(20, 20);
                button.setLayoutX(centerX - 9);
                button.setLayoutY(centerY - 9);

                button.setOnAction(e -> {
                    try {
                        handleCircleClick(button);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                button.setDisable(true);
                circles.add(button);
            }
        }
    }

    private void createLines(double cellWidth, double cellHeight) {
        for (int j = 0; j < gridSizeY; j++) {
            for (int i = 0; i < gridSizeX - 1; i++) {
                double startX = i * cellWidth + cellWidth / 2;
                double startY = j * cellHeight + cellHeight / 2;
                double endX = (i + 1) * cellWidth + cellWidth / 2;
                double endY = j * cellHeight + cellHeight / 2;
                Line line = new Line(startX, startY, endX, endY);
                if (Math.random() < 0.5) {
                    line.setStrokeWidth(5);
                    checkAndDisableButton(startX, startY, endX, endY, circles);
                }
                canvas.getChildren().add(line);
                lines.add(line);
            }
        }

        for (int i = 0; i < gridSizeX; i++) {
            for (int j = 0; j < gridSizeY - 1; j++) {
                double startX = i * cellWidth + cellWidth / 2;
                double startY = j * cellHeight + cellHeight / 2;
                double endX = i * cellWidth + cellWidth / 2;
                double endY = (j + 1) * cellHeight + cellHeight / 2;
                Line line = new Line(startX, startY, endX, endY);
                if (Math.random() < 0.5) {
                    line.setStrokeWidth(5);
                    checkAndDisableButton(startX, startY, endX, endY, circles);
                }
                canvas.getChildren().add(line);
                lines.add(line);
            }
        }
    }

    private void checkAndDisableButton(double startX, double startY, double endX, double endY, List<Button> circles) {
        for (Button button : circles) {
            double buttonCenterX = button.getLayoutX() + 9;
            double buttonCenterY = button.getLayoutY() + 9;
            if ((buttonCenterX == startX && buttonCenterY == startY) || (buttonCenterX == endX && buttonCenterY == endY)) {
                button.setDisable(false);
            }
        }
    }

    private boolean isPreasable(Button button) {
        if (button.getBackground().getFills().getFirst().getFill() != Color.RED && button.getBackground().getFills().getFirst().getFill() != Color.BLUE)
            return true;
        return false;
    }

    private void handleCircleClick(Button button) throws InterruptedException {
        if (!startedGame) {
            startedGame = true;
        }
        if (button.getBackground().getFills().getFirst().getFill() != Color.RED && button.getBackground().getFills().get(0).getFill() != Color.BLUE)
            if (turn % 2 == 0) {
                if (button.getBackground().getFills().getFirst().getFill() != Color.RED)
                    button.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                if (button.getBackground().getFills().getFirst().getFill() != Color.BLUE)
                    button.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        if (startedGame) {
            checkButtons(button);
        }
        turn++;
    }


    void checkButtons(Button button) {
        boolean happened = true;
        for (Button b : circles) {
            if (isPreasable(b))
                b.setDisable(true);
        }

        for (Line line : lines) {
            if (line.getStrokeWidth() == 5) {
                double startX = line.getStartX();
                double startY = line.getStartY();
                double endX = line.getEndX();
                double endY = line.getEndY();
                Button button1 = null;
                Button button2 = null;

                for (Button b : circles) {
                    double buttonCenterX = b.getLayoutX() + 9;
                    double buttonCenterY = b.getLayoutY() + 9;

                    if (buttonCenterX == startX && buttonCenterY == startY) {
                        button1 = b;
                    }

                    if (buttonCenterX == endX && buttonCenterY == endY) {
                        button2 = b;
                    }
                }

                if (button1 != null && button2 != null) {
                    boolean button1IsPressable = isPreasable(button1);
                    boolean button2IsPressable = isPreasable(button2);

                    boolean button1ColorDifferent = button1.getBackground().getFills().getFirst().getFill().equals(button.getBackground().getFills().getFirst().getFill());
                    boolean button2ColorDifferent = button2.getBackground().getFills().getFirst().getFill().equals(button.getBackground().getFills().getFirst().getFill());

                    if ((button1IsPressable && button2ColorDifferent) || (button2IsPressable && button1ColorDifferent)) {
                        button1.setDisable(false);
                        button2.setDisable(false);
                        happened = false;

                    }
                }
            }
        }
        isGameOver(button, happened);
    }

    private void isGameOver(Button button, boolean happened) {
        if (happened) {
            root.getChildren().remove(canvas);
            StackPane gameWonPane = new StackPane();
            setGameWonPane(button, gameWonPane);
            root.setCenter(gameWonPane);
        }
    }

    private void setGameWonPane(Button button, StackPane gameWonPane) {
        Paint fill;
        Text message = new Text();
        message.setTextAlignment(TextAlignment.CENTER);
        message.setFont(Font.font("Arial", 28));
        message.setStyle("-fx-font-weight: bold;");
        message.setFill(Color.BLACK);
        message.setStroke(Color.WHITE);
        message.setStrokeWidth(1.5);

        fill = button.getBackground().getFills().getFirst().getFill();
        if (fill.equals(Color.RED)) {
            message.setText("Red won!");
            message.setFill(Color.RED);
        } else if (fill.equals(Color.BLUE)) {
            message.setText("Blue won!");
            message.setFill(Color.BLUE);
        }

        gameWonPane.getChildren().add(message);

        StackPane.setAlignment(message, Pos.CENTER);
        StackPane.setMargin(message, new Insets(10));

        BorderPane.setAlignment(gameWonPane, Pos.CENTER);
        BorderPane.setMargin(gameWonPane, new Insets(10));
    }

    private void saveGameState() {
        try {
            String saveFolderPath = "C:\\Users\\iusti\\Proiecte\\JavaLab\\Lab6\\src\\main\\SaveFiles\\";
            String imagePath = "C:\\Users\\iusti\\Proiecte\\JavaLab\\Lab6\\src\\main\\Pictures\\";
            saveCanvasAsImage(imagePath + "game.png");

            String filename = saveFolderPath + "game.ser";

            FileOutputStream fileOut = new FileOutputStream(filename);

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            List<Boolean> linesCopy = new ArrayList<>();
            List<Integer> circlesCopy = new ArrayList<>();
            for (Line line : lines) {
                if (line.getStrokeWidth() == 5)
                    linesCopy.add(true);
                else
                    linesCopy.add(false);
            }
            for (Button circle : circles) {
                if (circle.getBackground().getFills().getFirst().getFill() == Color.RED)
                    circlesCopy.add(1);
                else if (circle.getBackground().getFills().getFirst().getFill() == Color.BLUE)
                    circlesCopy.add(2);
                else
                    circlesCopy.add(0);
            }
            GameStatus gameStatus = new GameStatus(linesCopy, circlesCopy, turn, startedGame);
            out.writeObject(gameStatus);

            out.close();
            fileOut.close();

            System.out.println("Game state saved successfully.");
            System.out.println("File saved at: " + filename);
            System.out.println("Game status: " + startedGame);
            System.out.println("Turn: " + turn);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadGameState() {
        try {
            File file = new File("C:\\Users\\iusti\\Proiecte\\JavaLab\\Lab6\\src\\main\\SaveFiles\\game.ser");
            if (file.exists()) {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                GameStatus loadedGame = (GameStatus) in.readObject();
                in.close();
                fileIn.close();

                extractElements(loadedGame);
                for (Line line : lines) {
                    checkAndDisableButton(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), circles);
                }
                turn = loadedGame.turn();
                startedGame = loadedGame.startedGame();

                System.out.println("Game state loaded successfully.");
            } else {
                System.out.println("No saved game state found.");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void extractElements(GameStatus loadedGame) {

        List<Boolean> linesCopy = loadedGame.lines();
        List<Integer> circlesCopy = loadedGame.circles();

        List<Line> updatedLines = new ArrayList<>();
        List<Button> updatedCircles = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            if (linesCopy.get(i)) {
                line.setStrokeWidth(5);
            } else {
                line.setStrokeWidth(1);
            }
            updatedLines.add(line);
        }

        for (int i = 0; i < circles.size(); i++) {
            Button circle = circles.get(i);
            int colorCode = circlesCopy.get(i);
            switch (colorCode) {
                case 1:
                    circle.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
                case 2:
                    circle.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
                default:
                    circle.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
            }
            updatedCircles.add(circle);
        }

        canvas.getChildren().clear();
        canvas.getChildren().addAll(updatedLines);
        canvas.getChildren().addAll(updatedCircles);
        turn = loadedGame.turn();
        startedGame = loadedGame.startedGame();
        System.out.println("Game status: " + startedGame);
        System.out.println("Turn: " + turn);
    }

    void saveCanvasAsImage(String filePath) {
        WritableImage writableImage = canvas.snapshot(new SnapshotParameters(), null);
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            System.out.println("Canvas saved as image successfully.");
            System.out.println("File saved at: " + filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
