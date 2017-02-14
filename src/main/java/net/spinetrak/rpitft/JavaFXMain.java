/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 spinetrak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.spinetrak.rpitft;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.EventChecker;
import net.spinetrak.rpitft.data.streams.logger.InitialStateStreamLogger;
import net.spinetrak.rpitft.data.streams.logger.NmeaFileLogger;
import net.spinetrak.rpitft.ui.bottom.ButtonPanel;
import net.spinetrak.rpitft.ui.center.TabPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.spinetrak.rpitft.ui.center.Charts.MIN_HEIGHT;
import static net.spinetrak.rpitft.ui.center.Charts.MIN_WIDTH;

public class JavaFXMain extends Application
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.JavaFXMain");
  private ButtonPanel _buttonPanel;
  private TabPanel _tabPanel;

  public static void exit()
  {
    LOGGER.info("Exiting...");
    Platform.exit();
    LOGGER.info("Platform exited.");
    Runtime.getRuntime().exit(0);
  }

  public static void main(final String[] args_)
  {
    LOGGER.info("Starting JavaFX.");
    launch(args_);
  }

  @Override
  public void start(final Stage stage_)
  {
    init(stage_);
    stage_.show();

    final EventChecker eventChecker = EventChecker.getInstance();
    eventChecker.start();
    LOGGER.info("Event checker started.");

    final InitialStateStreamLogger iss = InitialStateStreamLogger.getInstance();
    LOGGER.info("Started logging to " + iss.toString());

    final NmeaFileLogger nmeaLogger = NmeaFileLogger.getInstance();
    nmeaLogger.start();
    LOGGER.info("Started logging to file.");

    final AnimationTimer animationTimer = new AnimationTimer()
    {
      @Override
      public void handle(final long now_)
      {
        Dispatcher.getInstance().processQueue();
      }
    };
    animationTimer.start();
    LOGGER.info("Animation timer started.");
  }

  private void init(final Stage stage_)
  {
    _tabPanel = new TabPanel();
    _buttonPanel = new ButtonPanel();

    final VBox pane = new VBox(_tabPanel.getPanel(), _buttonPanel.getPanel());
    pane.setPadding(new Insets(1));
    pane.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
    pane.setMaxHeight(MIN_HEIGHT);
    pane.setMinHeight(MIN_HEIGHT);
    pane.setMaxWidth(MIN_WIDTH);
    pane.setMinWidth(MIN_WIDTH);

    final Scene scene = new Scene(pane, MIN_WIDTH, MIN_HEIGHT);
    scene.getStylesheets().add("/css/stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }
}
