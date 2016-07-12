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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.Power;
import net.spinetrak.rpitft.data.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main extends Application
{
  private Chart _chart;
  private TextPanel _textPanel;

  public static void main(String[] args_)
  {
    launch(args_);
  }

  public void addDataToSeries(final ConcurrentLinkedQueue<Power> powerQueue_,
                              final ConcurrentLinkedQueue<Device> deviceQueue_,
                              final ConcurrentLinkedQueue<GPS> gpsQueue_)
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
    while (!gpsQueue_.isEmpty())
    {
      final GPS gps = gpsQueue_.remove();

      _textPanel.addData(gps);
      _chart.addData(gps);
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
    _chart = new Chart();
    _textPanel = new TextPanel();

    final BorderPane border = new BorderPane();
    border.setPadding(new Insets(1));
    border.setPrefSize(320, 240);
    border.setMaxHeight(240);
    border.setMinHeight(240);
    border.setMaxWidth(320);
    border.setMinWidth(320);

    final VBox center = new VBox();
    center.setSpacing(1);
    center.setPadding(new Insets(1));
    center.getChildren().add(_chart.getPowerLineChart());
    center.getChildren().add(_chart.getGPSLineChart());
    center.setFillWidth(true);
    center.setPrefSize(320, 160);
    center.setMaxHeight(160);
    border.setCenter(center);

    border.setTop(_textPanel.getTop());

    final ButtonPanel buttonPanel = new ButtonPanel();
    border.setBottom(buttonPanel.getBottom());

    final Scene scene = new Scene(border, 320, 240);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }
}
