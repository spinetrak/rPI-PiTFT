package net.spinetrak.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static javafx.application.Platform.exit;

public class Main extends Application
{
  private static final int MAX_DATA_POINTS = 320;
  private int _xSeriesData = 0;
  private ExecutorService _executor;
  private ConcurrentLinkedQueue<Power> _powerQueue;
  private NumberAxis _xPowerAxis;
  private NumberAxis _yPowerAxis;
  private XYChart.Series<Number, Number> _mainVoltageSeries;
  private XYChart.Series<Number, Number> _upperVoltageSeries;
  private XYChart.Series<Number, Number> _middleVoltageSeries;
  private XYChart.Series<Number, Number> _lowerVoltageSeries;
  private LineChart<Number, Number> _powerLineChart;

  private void init(final Stage stage_)
  {
    initPowerLinechart();

    final BorderPane border = new BorderPane();
    border.setTop(setTop());
    border.setBottom(setBottom());
    border.setCenter(_powerLineChart);

    final Scene scene = new Scene(border, 320, 240);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }

  private void initPowerLinechart()
  {
    _powerQueue = new ConcurrentLinkedQueue<>();
    _mainVoltageSeries = new XYChart.Series<>();
    _upperVoltageSeries = new XYChart.Series<>();
    _middleVoltageSeries = new XYChart.Series<>();
    _lowerVoltageSeries = new XYChart.Series<>();

    _xPowerAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    _xPowerAxis.setTickLabelsVisible(false);
    _xPowerAxis.setTickUnit(10);
    _xPowerAxis.setForceZeroInRange(false);
    _xPowerAxis.setAutoRanging(false);

    _yPowerAxis = new NumberAxis();
    _yPowerAxis.setLowerBound(4.25);
    _yPowerAxis.setUpperBound(5.75);
    _yPowerAxis.setTickUnit(0.25);
    _yPowerAxis.setForceZeroInRange(false);
    _yPowerAxis.setAutoRanging(false);

    _powerLineChart = new LineChart<>(_xPowerAxis, _yPowerAxis);
    _powerLineChart.setCreateSymbols(false);
    _powerLineChart.setLegendVisible(false);
    _powerLineChart.setAnimated(false);
    _powerLineChart.setHorizontalGridLinesVisible(true);
    _powerLineChart.getData().addAll(_mainVoltageSeries, _upperVoltageSeries, _middleVoltageSeries, _lowerVoltageSeries);
  }

  private HBox setBottom()
  {
    final HBox bottom = new HBox();
    bottom.setPadding(new Insets(5));
    bottom.setSpacing(5);
    bottom.setAlignment(Pos.CENTER_RIGHT);


    final Button exit = new Button("Exit");
    exit.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
    exit.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(final KeyEvent event_)
      {
        if(event_.getCode().equals(KeyCode.ENTER))
        {
          exit.setText("exiting...");
          exit();
          exit.setText("done...");
          System.exit(0);
        }
      }
    });

    bottom.getChildren().add(exit);

    final Button restart = new Button("Restart");
    restart.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
    restart.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(final KeyEvent event_)
      {
        if (event_.getCode().equals(KeyCode.ENTER))
        {
          final Runtime runtime = Runtime.getRuntime();
          try
          {
            final Process proc = runtime.exec("sudo shutdown -r now");
          }
          catch (final IOException ex_)
          {
            ex_.printStackTrace();
          }
          System.exit(0);
        }
      }
    });
    bottom.getChildren().add(restart);

    final Button shutdown = new Button("Shutdown");
    shutdown.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
    shutdown.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(final KeyEvent event_)
      {
        if (event_.getCode().equals(KeyCode.ENTER))
        {
          final Runtime runtime = Runtime.getRuntime();
          try
          {
            final Process proc = runtime.exec("sudo shutdown now");
          }
          catch (final IOException ex_)
          {
            ex_.printStackTrace();
          }
          System.exit(0);
        }
      }
    });
    bottom.getChildren().add(shutdown);
    return bottom;
  }

  private FlowPane setTop()
  {
    final FlowPane top = new FlowPane(Orientation.HORIZONTAL);
    top.setPadding(new Insets(5));

    for (final String label : new String[]{"[10:11:44]", "[4807.038 N]", "[1131.000 E]", "[1600.7 M]", "[2345678]", "[23.6 C°]", "[cpu 17%]", "[hd 23%]", "[bat 100.00%]", "[308.19 mA]"})
    {
      final Text text = new Text(label);
      text.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
      top.getChildren().add(text);
    }
    return top;
  }

  @Override
  public void start(final Stage stage_)
  {
    init(stage_);
    stage_.show();

    _executor = Executors.newCachedThreadPool(new ThreadFactory()
    {
      @Override
      public Thread newThread(final Runnable runnable_)
      {
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
      _mainVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData++, power.getVoltage()));
      _upperVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData, 5.25));
      _middleVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData, 5.00));
      _lowerVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData, 4.75));

      if (Power.BATTERY.equals(power.getSource()))
      {
        _mainVoltageSeries.nodeProperty().get().setStyle("-fx-stroke: red;");
      }
      else if (Power.PRIMARY.equals(power.getSource()))
      {
        _mainVoltageSeries.nodeProperty().get().setStyle("-fx-stroke: green;");
      }

      if (_mainVoltageSeries.getData().size() > MAX_DATA_POINTS)
      {
        _mainVoltageSeries.getData().remove(0, _mainVoltageSeries.getData().size() - MAX_DATA_POINTS);
      }
      _xPowerAxis.setLowerBound(_xSeriesData - MAX_DATA_POINTS);
      _xPowerAxis.setUpperBound(_xSeriesData - 1);
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
      _source = Math.random() > 0.5 ? PRIMARY : BATTERY;
      _voltage = Math.random() + 4.5;
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
