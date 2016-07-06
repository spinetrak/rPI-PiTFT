package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage stage) {


    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
      xAxis, yAxis);

    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
    series.setName("voltage");
    // populating the series with data
    series.getData().add(new XYChart.Data<>(1, 23));
    series.getData().add(new XYChart.Data<>(2, 114));
    series.getData().add(new XYChart.Data<>(3, 15));
    series.getData().add(new XYChart.Data<>(4, 124));
    series.getData().add(new XYChart.Data<>(5, 23));
    series.getData().add(new XYChart.Data<>(6, 114));
    series.getData().add(new XYChart.Data<>(7, 15));
    series.getData().add(new XYChart.Data<>(8, 124));
    series.getData().add(new XYChart.Data<>(9, 23));
    series.getData().add(new XYChart.Data<>(10, 114));
    series.getData().add(new XYChart.Data<>(11, -15));
    series.getData().add(new XYChart.Data<>(12, 124));
    series.getData().add(new XYChart.Data<>(13, 23));
    series.getData().add(new XYChart.Data<>(14, -114));
    series.getData().add(new XYChart.Data<>(15, 15));
    series.getData().add(new XYChart.Data<>(16, 124));
    series.getData().add(new XYChart.Data<>(17, -23));
    series.getData().add(new XYChart.Data<>(18, 114));
    series.getData().add(new XYChart.Data<>(19, -15));
    series.getData().add(new XYChart.Data<>(20, -124));
    Scene scene = new Scene(lineChart, 320, 240);
    lineChart.getData().add(series);


    stage.setScene(scene);
    stage.setFullScreen(true);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}