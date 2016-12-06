/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 spinetrak
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

package net.spinetrak.rpitft.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import static net.spinetrak.rpitft.ui.Charts.MIN_HEIGHT;
import static net.spinetrak.rpitft.ui.Charts.MIN_WIDTH;

public class Main extends Application
{
  private TabPanel _tabPanel;
  private TextPanel _textPanel;

  public static void main(String[] args_)
  {
    launch(args_);
  }

  public void addDataToSeries(//final ConcurrentLinkedQueue<Power> powerQueue_,
                              final ConcurrentLinkedQueue<Device> deviceQueue_,
                              final ConcurrentLinkedQueue<GPS> gpsQueue_)
  {
    /*
    while (!powerQueue_.isEmpty())
    {
      final Power power = powerQueue_.remove();
      _textPanel.addData(power);
    }
    */
    while (!deviceQueue_.isEmpty())
    {
      final Device device = deviceQueue_.remove();
      _textPanel.addData(device);
      _tabPanel.addData(device);
    }
    while (!gpsQueue_.isEmpty())
    {
      final GPS gps = gpsQueue_.remove();
      _textPanel.addData(gps);
      _tabPanel.addData(gps);
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

  private void init(final Stage stage_)
  {
    _tabPanel = new TabPanel();
    _textPanel = new TextPanel();

    final BorderPane pane = new BorderPane();
    pane.setPadding(new Insets(1));
    pane.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
    pane.setMaxHeight(MIN_HEIGHT);
    pane.setMinHeight(MIN_HEIGHT);
    pane.setMaxWidth(MIN_WIDTH);
    pane.setMinWidth(MIN_WIDTH);

    pane.setCenter(_tabPanel.getCenter());

    pane.setTop(_textPanel.getTop());

    final ButtonPanel buttonPanel = new ButtonPanel();
    pane.setBottom(buttonPanel.getBottom());

    final Scene scene = new Scene(pane, MIN_WIDTH, MIN_HEIGHT);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }
}
