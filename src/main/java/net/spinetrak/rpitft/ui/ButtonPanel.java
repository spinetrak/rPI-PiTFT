package net.spinetrak.rpitft.ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

import static javafx.application.Platform.exit;

public class ButtonPanel
{
  final HBox _bottom;

  public ButtonPanel()
  {
    _bottom = new HBox();
    _bottom.setPadding(new Insets(5));
    _bottom.setSpacing(5);
    _bottom.setAlignment(Pos.CENTER_RIGHT);

    final Button exit = getExitButton();
    _bottom.getChildren().add(exit);
    final Button restart = getRestartButton();
    _bottom.getChildren().add(restart);

    final Button shutdown = getShutdownButton();
    _bottom.getChildren().add(shutdown);
  }

  public HBox getBottom()
  {
    return _bottom;
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
}
