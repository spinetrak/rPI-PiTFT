package net.spinetrak.rpitft.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import net.spinetrak.rpitft.data.Queue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.application.Platform.exit;

public class Main extends Application
{
  private static final String FX_FILL_BLACK = "-fx-fill: black;";
  private static final String FX_FILL_RED = "-fx-fill: red;";

  private Text _altitude;

  private Text _batteryCapacity;
  private Text _batteryPower;
  private Chart _chart;
  private Text _cpu;
  private boolean _cpuAlert = false;
  private Text _disk;
  private boolean _diskAlert = false;
  private Text _latitude;
  private Text _longitude;
  private Text _temperature;
  private boolean _temperatureAlert = false;
  private Text _time;
  private Text _trackPoints;


  public static void main(String[] args_)
  {
    launch(args_);
  }

  public void addDataToSeries(final ConcurrentLinkedQueue<Power> powerQueue_,
                              final ConcurrentLinkedQueue<Device> deviceQueue_)
  {
    while (!powerQueue_.isEmpty())
    {
      final Power power = powerQueue_.remove();

      _batteryCapacity.setText(String.format("[%.2f%% bat]", power.getCapacity()));
      _batteryPower.setText(String.format("[%.2f mA]", power.getPower()));
      _chart.addData(power);

    }
    while (!deviceQueue_.isEmpty())
    {
      final Device device = deviceQueue_.remove();
      final float cpu = device.getCpu();
      final float disk = device.getDisk();
      final float temperature = device.getTemperature();

      _cpu.setText(String.format("[%.2f%% cpu]", cpu));
      if (cpu >= 90 && !_cpuAlert)
      {
        _cpuAlert = true;
        _cpu.setStyle(FX_FILL_RED);
      }
      else if (cpu < 90 && _cpuAlert)
      {
        _cpuAlert = false;
        _cpu.setStyle(FX_FILL_BLACK);
      }

      _disk.setText(String.format("[%.2f%% hd]", disk));
      if (disk >= 90 && !_diskAlert)
      {
        _diskAlert = true;
        _disk.setStyle(FX_FILL_RED);
      }
      else if (disk < 90 && _diskAlert)
      {
        _diskAlert = false;
        _disk.setStyle(FX_FILL_BLACK);
      }

      _temperature.setText(String.format("[%.2f C°]", temperature));
      if (temperature >= 75 && !_temperatureAlert)
      {
        _temperatureAlert = true;
        _temperature.setStyle(FX_FILL_RED);
      }
      else if (temperature < 75 && _temperatureAlert)
      {
        _temperatureAlert = false;
        _temperature.setStyle(FX_FILL_BLACK);
      }
    }

  }

  public void setPowerAlarm(final boolean on_)
  {
    if (on_)
    {
      _batteryPower.setStyle(FX_FILL_RED);
      _batteryCapacity.setStyle(FX_FILL_RED);
    }
    else
    {
      _batteryPower.setStyle(FX_FILL_BLACK);
      _batteryCapacity.setStyle(FX_FILL_BLACK);
    }
  }

  @Override
  public void start(final Stage stage_)
  {
    init(stage_);
    stage_.show();

    final Queue queue = new Queue();
    queue.start(this);
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
    _chart = new Chart(this);

    final BorderPane border = new BorderPane();
    border.setTop(setTop());
    border.setBottom(setBottom());
    border.setCenter(_chart.getPowerLineChart());

    final Scene scene = new Scene(border, 320, 240);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
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
}
