package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * A chart that fills in the area between a line of data points and the axes.
 * Good for comparing accumulated totals over time.
 *
 * @see javafx.scene.chart.Chart
 * @see javafx.scene.chart.Axis
 * @see javafx.scene.chart.NumberAxis
 * @related charts/line/LineChart
 * @related charts/scatter/ScatterChart
 */
public class Main extends Application {



    @Override public void start(Stage primaryStage) throws Exception {
        int width = 240;
        int height = 320;

        primaryStage.setTitle("DoorNFX");

        Pane root = new Pane();

        root.setPrefWidth(width);
        root.setPrefHeight(height);

        Label statusBox = new Label();
        statusBox.setPrefWidth(210);
        statusBox.setPrefHeight(35);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setTextFill(Color.web("#bbbbff"));
        statusBox.setLayoutY(5);
        statusBox.setLayoutX(15);
        statusBox.setText("Tag in");
        root.getChildren().add(statusBox);



        root.getChildren().add(createAndRegisterNumButton("<", 250, 15));
        root.getChildren().add(createAndRegisterNumButton("0", 250, 90));
        root.getChildren().add(createAndRegisterNumButton("!", 250, 165));

        root.getChildren().add(createAndRegisterNumButton("7", 185, 15));
        root.getChildren().add(createAndRegisterNumButton("8", 185, 90));
        root.getChildren().add(createAndRegisterNumButton("9", 185, 165));

        root.getChildren().add(createAndRegisterNumButton("4", 120, 15));
        root.getChildren().add(createAndRegisterNumButton("5", 120, 90));
        root.getChildren().add(createAndRegisterNumButton("6", 120, 165));

        root.getChildren().add(createAndRegisterNumButton("1", 55, 15));
        root.getChildren().add(createAndRegisterNumButton("2", 55, 90));
        root.getChildren().add(createAndRegisterNumButton("3", 55, 165));


        Scene mainScene = new Scene(root, width, height);
        mainScene.setCursor(Cursor.NONE);


        primaryStage.setScene(mainScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private Button createAndRegisterNumButton(String caption, int y, int x) {
        Button roundButton = new Button();

        roundButton.setStyle(
          "-fx-background-radius: 5em; " +
            "-fx-min-width: 60px; " +
            "-fx-min-height: 60px; " +
            "-fx-max-width: 60px; " +
            "-fx-max-height: 60px; " +
            "-fx-font-size: 18pt; -fx-font-weight:bold; "
        );

        roundButton.setText(caption);
        roundButton.setLayoutX(x);
        roundButton.setLayoutY(y);
        roundButton.setFocusTraversable(false);

        return roundButton;
    }

    public static void main(String[] args) {
        launch(args);
    }

}