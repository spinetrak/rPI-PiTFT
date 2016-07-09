package net.spinetrak.rpitft.ui;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.Power;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static javafx.application.Platform.exit;

public class Main extends Application
{
  public static final String FX_STROKE_BLACK = "-fx-stroke: black;";
  public static final String FX_STROKE_GREEN = "-fx-stroke: green;";
  public static final String FX_STROKE_RED = "-fx-stroke: red;";
  private static final int MAX_DATA_POINTS = 320;
  private Text _altitude;
  private boolean _batteryAlert = false;
  private Text _batteryCapacity;
  private Text _batteryPower;
  private Text _cpu;
  private boolean _cpuAlert = false;
  private ConcurrentLinkedQueue<Device> _deviceQueue;
  private Text _disk;
  private boolean _diskAlert = false;
  private ExecutorService _executor;
  private Text _latitude;
  private Text _longitude;
  private XYChart.Series<Number, Number> _lowerVoltageSeries;
  private XYChart.Series<Number, Number> _mainVoltageSeries;
  private XYChart.Series<Number, Number> _middleVoltageSeries;
  private LineChart<Number, Number> _powerLineChart;
  private ConcurrentLinkedQueue<Power> _powerQueue;
  private Text _temperature;
  private boolean _temperatureAlert = false;
  private Text _time;
  private Text _trackPoints;
  private XYChart.Series<Number, Number> _upperVoltageSeries;
  private NumberAxis _xPowerAxis;
  private int _xSeriesData = 0;
  private NumberAxis _yPowerAxis;

  public static void main(String[] args_)
  {
    launch(args_);
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

  private void addDataToSeries()
  {
    while (!_powerQueue.isEmpty())
    {
      final Power power = _powerQueue.remove();

      _batteryCapacity.setText(String.format("[%.2f%% bat]", power.getCapacity()));
      _batteryPower.setText(String.format("[%.2f mA]", power.getPower()));
      _mainVoltageSeries.getData().add(new XYChart.Data<Number, Number>(_xSeriesData++, power.getVoltage()));
      _upperVoltageSeries.getData().add(new XYChart.Data<Number, Number>(_xSeriesData, 5.25));
      _middleVoltageSeries.getData().add(new XYChart.Data<Number, Number>(_xSeriesData, 5.00));
      _lowerVoltageSeries.getData().add(new XYChart.Data<Number, Number>(_xSeriesData, 4.75));

      if (Power.BATTERY.equals(power.getSource()) && !_batteryAlert)
      {
        _batteryAlert = true;
        _mainVoltageSeries.nodeProperty().get().setStyle(FX_STROKE_RED);
        _batteryPower.setStyle(FX_STROKE_RED);
        _batteryCapacity.setStyle(FX_STROKE_RED);
      }
      else if (Power.PRIMARY.equals(power.getSource()) && _batteryAlert)
      {
        _batteryAlert = false;
        _mainVoltageSeries.nodeProperty().get().setStyle(FX_STROKE_GREEN);
        _batteryPower.setStyle(FX_STROKE_BLACK);
        _batteryCapacity.setStyle(FX_STROKE_BLACK);
      }

      if (_mainVoltageSeries.getData().size() > MAX_DATA_POINTS)
      {
        _mainVoltageSeries.getData().remove(0, _mainVoltageSeries.getData().size() - MAX_DATA_POINTS);
      }
      _xPowerAxis.setLowerBound(_xSeriesData - MAX_DATA_POINTS);
      _xPowerAxis.setUpperBound(_xSeriesData - 1);
    }
    while (!_deviceQueue.isEmpty())
    {
      final Device device = _deviceQueue.remove();
      final float cpu = device.getCpu();
      final float disk = device.getDisk();
      final float temperature = device.getTemperature();

      _cpu.setText(String.format("[%.2f%% cpu]", cpu));
      if (cpu >= 90 && !_cpuAlert)
      {
        _cpuAlert = true;
        _cpu.setStyle(FX_STROKE_RED);
      }
      else if (cpu < 90 && _cpuAlert)
      {
        _cpuAlert = false;
        _cpu.setStyle(FX_STROKE_BLACK);
      }

      _disk.setText(String.format("[%.2f hd]", disk));
      if (disk >= 90 && !_diskAlert)
      {
        _diskAlert = true;
        _disk.setStyle(FX_STROKE_RED);
      }
      else if (disk < 90 && _diskAlert)
      {
        _diskAlert = false;
        _disk.setStyle(FX_STROKE_BLACK);
      }

      _temperature.setText(String.format("[%.2f C°]", temperature));
      if (temperature >= 75 && !_temperatureAlert)
      {
        _temperatureAlert = true;
        _temperature.setStyle(FX_STROKE_RED);
      }
      else if (temperature < 75 && _temperatureAlert)
      {
        _temperatureAlert = false;
        _temperature.setStyle(FX_STROKE_BLACK);
      }
    }

  }

  private Button getExitButton()
  {
    final Image exitImg = new Image(getClass().getResourceAsStream("/exit.png"));
    final Button exit = new Button();
    exit.setGraphic(new ImageView(exitImg));

    exit.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(final KeyEvent event_)
      {
        if (event_.getCode().equals(KeyCode.ENTER))
        {
          exit.setText("exiting...");
          exit();
          exit.setText("done...");
          System.exit(0);
        }
      }
    });
    return exit;
  }

  private Button getRestartButton()
  {
    final Image restartImg = new Image(getClass().getResourceAsStream("/restart.png"));
    final Button restart = new Button();
    restart.setGraphic(new ImageView(restartImg));
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
    return restart;
  }

  private Button getShutdownButton()
  {
    final Image shutdownImg = new Image(getClass().getResourceAsStream("/shutdown.png"));
    final Button shutdown = new Button();
    shutdown.setGraphic(new ImageView(shutdownImg));
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
    return shutdown;
  }

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
    _deviceQueue = new ConcurrentLinkedQueue<>();
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
    _yPowerAxis.setLowerBound(4.6);
    _yPowerAxis.setUpperBound(5.4);
    _yPowerAxis.setTickUnit(0.05);
    _yPowerAxis.setForceZeroInRange(false);
    _yPowerAxis.setAutoRanging(false);

    _powerLineChart = new LineChart<>(_xPowerAxis, _yPowerAxis);
    _powerLineChart.setCreateSymbols(false);
    _powerLineChart.setLegendVisible(false);
    _powerLineChart.setAnimated(false);
    _powerLineChart.setHorizontalGridLinesVisible(true);
    _powerLineChart.getData().addAll(_mainVoltageSeries, _upperVoltageSeries, _middleVoltageSeries,
                                     _lowerVoltageSeries);
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

  private HBox setBottom()
  {
    final HBox bottom = new HBox();
    bottom.setPadding(new Insets(5));
    bottom.setSpacing(5);
    bottom.setAlignment(Pos.CENTER_RIGHT);

    final Button exit = getExitButton();
    bottom.getChildren().add(exit);
    final Button restart = getRestartButton();
    bottom.getChildren().add(restart);

    final Button shutdown = getShutdownButton();
    bottom.getChildren().add(shutdown);
    return bottom;
  }

  private FlowPane setTop()
  {
    final FlowPane top = new FlowPane(Orientation.HORIZONTAL);
    top.setPadding(new Insets(5));

    _time = new Text("[hh:mm:ss]");
    top.getChildren().add(_time);

    _latitude = new Text("[xxxx.xxx N]");
    top.getChildren().add(_latitude);

    _longitude = new Text("[xxxx.xxx E]");
    top.getChildren().add(_longitude);

    _altitude = new Text("[xxxx.x M]");
    top.getChildren().add(_altitude);

    _trackPoints = new Text("[xxxxxxx]");
    top.getChildren().add(_trackPoints);

    _temperature = new Text("[xx.x C°]");
    top.getChildren().add(_temperature);

    _cpu = new Text("[xx.x% cpu]");
    top.getChildren().add(_cpu);

    _disk = new Text("[xx% hd]");
    top.getChildren().add(_disk);

    _batteryCapacity = new Text("[xxx.xx% bat]");
    top.getChildren().add(_batteryCapacity);

    _batteryPower = new Text("[xxx.xxx mA]");
    top.getChildren().add(_batteryPower);
    return top;
  }

  private class AddToQueue implements Runnable
  {
    public void run()
    {
      try
      {
        _powerQueue.add(new Power());
        _deviceQueue.add(new Device());
        Thread.sleep(100);
        _executor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        ex_.printStackTrace();
      }
    }
  }
}
