package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main extends Application
{
  private static final int MAX_DATA_POINTS = 240;
  private int _xSeriesData = 0;
  private ExecutorService _executor;
  private ConcurrentLinkedQueue<Power> _powerQueue;
  private NumberAxis _xAxis;
  private NumberAxis _yAxis;
  private XYChart.Series<Number, Number> _voltageSeries;
  private XYChart.Series<Number, Number> _upperSeries;
  private XYChart.Series<Number, Number> _middleSeries;
  private XYChart.Series<Number, Number> _lowerSeries;
  private LineChart<Number, Number> _lineChart;

  private void init(final Stage stage_)
  {
    _powerQueue = new ConcurrentLinkedQueue<>();
    _voltageSeries = new XYChart.Series<>();
    _upperSeries = new XYChart.Series<>();
    _middleSeries = new XYChart.Series<>();
    _lowerSeries = new XYChart.Series<>();

    _xAxis = new NumberAxis(0,MAX_DATA_POINTS,MAX_DATA_POINTS/10);
    _xAxis.setTickLabelsVisible(false);
    _xAxis.setTickUnit(10);
    _xAxis.setForceZeroInRange(false);
    _xAxis.setAutoRanging(false);

    _yAxis = new NumberAxis();
    _yAxis.setLowerBound(4.25);
    _yAxis.setUpperBound(5.75);
    _yAxis.setTickUnit(0.25);
    _yAxis.setForceZeroInRange(false);
    _yAxis.setAutoRanging(false);

    _lineChart = new LineChart<>(_xAxis, _yAxis);
    _lineChart.setCreateSymbols(false);
    _lineChart.setLegendVisible(false);
    _lineChart.setAnimated(false);
    _lineChart.setHorizontalGridLinesVisible(true);
    _lineChart.getData().addAll(_voltageSeries,_upperSeries,_middleSeries,_lowerSeries);


    final BorderPane border = new BorderPane();

    final FlowPane top = new FlowPane(Orientation.HORIZONTAL);
    top.setPadding(new Insets(5));

    for(final String label : new String[]{"[10:11:44]","[4807.038 N]","[1131.000 E]","[1600.7 M]", "[2345678]", "[23.6 C°]", "[cpu 17%]", "[hd 23%]", "[bat 100.00%]", "[308.19 mA]"})
    {
      final Text text = new Text(label);
      text.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
      top.getChildren().add(text);
    }
    border.setTop(top);

    final HBox bottom = new HBox();
    bottom.setPadding(new Insets(5));
    bottom.setSpacing(5);
    bottom.setAlignment(Pos.CENTER_RIGHT);

    for(final String label : new String[]{"Exit","Restart","Shutdown"})
    {
      final Button button = new Button(label);
      button.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
      bottom.getChildren().add(button);
    }
    border.setBottom(bottom);

    border.setCenter(_lineChart);

    final Scene scene = new Scene(border, 320, 240);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }

  @Override
  public void start(final Stage stage_) {
    init(stage_);
    stage_.show();

    _executor = Executors.newCachedThreadPool(new ThreadFactory() {
      @Override
      public Thread newThread(final Runnable runnable_) {
        final Thread thread = new Thread(runnable_);
        thread.setDaemon(true);
        return thread;
      }
    });

    final AddToQueue addToQueue = new AddToQueue();
    _executor.execute(addToQueue);
    prepareTimeline();
  }

  private class AddToQueue implements Runnable
  {
    public void run()
    {
      try
      {
        _powerQueue.add(getPower());
        Thread.sleep(100);
        _executor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        ex_.printStackTrace();
      }
    }
  }

  private void prepareTimeline()
  {
    new AnimationTimer()
    {
      @Override
      public void handle(final long now_)
      {
        addDataToSeries();
      }
    }.start();
  }

  private void addDataToSeries()
  {
    while (!_powerQueue.isEmpty())
    {
      final Power power = _powerQueue.remove();
      _voltageSeries.getData().add(new XYChart.Data<>(_xSeriesData++, power.getVoltage()));
      _upperSeries.getData().add(new XYChart.Data<>(_xSeriesData, 5.25));
      _middleSeries.getData().add(new XYChart.Data<>(_xSeriesData, 5.00));
      _lowerSeries.getData().add(new XYChart.Data<>(_xSeriesData, 4.75));

      if(Power.BATTERY.equals(power.getSource()))
      {
        _voltageSeries.nodeProperty().get().setStyle("-fx-stroke: red;");
      }
      else if(Power.PRIMARY.equals(power.getSource()))
      {
        _voltageSeries.nodeProperty().get().setStyle("-fx-stroke: green;");
      }

      if (_voltageSeries.getData().size() > MAX_DATA_POINTS)
      {
        _voltageSeries.getData().remove(0, _voltageSeries.getData().size() - MAX_DATA_POINTS);
      }
      _xAxis.setLowerBound(_xSeriesData - MAX_DATA_POINTS);
      _xAxis.setUpperBound(_xSeriesData - 1);
    }
  }

  private class Power
  {
    public final static String PRIMARY = "P";
    public final static String BATTERY = "B";
    final String _source;

    public double getVoltage()
    {
      return _voltage;
    }

    public String getSource()
    {
      return _source;
    }

    final double _voltage;

    public Power()
    {
      _source = Math.random() > 0.5? PRIMARY:BATTERY;
      _voltage = Math.random()+4.5;
    }
  }

  private Power getPower()
  {
    return new Power();
  }

  public static void main(String[] args_)
  {
    launch(args_);
  }
}
