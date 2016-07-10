package net.spinetrak.rpitft.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.Power;
import net.spinetrak.rpitft.data.Queue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.application.Platform.exit;

public class Main extends Application
{
  private Chart _chart;
  private TextPanel _textPanel;

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
      _textPanel.addData(power);
      _chart.addData(power);

    }
    while (!deviceQueue_.isEmpty())
    {
      final Device device = deviceQueue_.remove();

      _textPanel.addData(device);
    }
  }

  public TextPanel getTextPanel()
  {
    return _textPanel;
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
    _textPanel = new TextPanel();

    final BorderPane border = new BorderPane();
    border.setTop(_textPanel.getTop());

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
}
