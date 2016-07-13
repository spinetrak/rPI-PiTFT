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

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main extends Application
{
  private boolean _added = false;
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
    if (gpsQueue_.isEmpty() && !_added)
    {
      _added = true;
      int count = 0;
      final List<GPS> data = GPS.getNMEAData();
      for (final GPS gps : data)
      {
        count++;
        _textPanel.addData(gps);
        _chart.addData(gps);
        if (count >= 480)
        {
          break;
        }
      }
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
    border.setPrefSize(480, 320);
    border.setMaxHeight(320);
    border.setMinHeight(320);
    border.setMaxWidth(480);
    border.setMinWidth(480);

    final VBox center = new VBox();
    center.setSpacing(1);
    center.setPadding(new Insets(1));
    center.getChildren().add(_chart.getPowerLineChart());
    center.getChildren().add(_chart.getGPSLineChart());
    center.setFillWidth(true);
    center.setPrefSize(480, 200);
    center.setMaxHeight(200);
    border.setCenter(center);

    border.setTop(_textPanel.getTop());

    final ButtonPanel buttonPanel = new ButtonPanel();
    border.setBottom(buttonPanel.getBottom());

    final Scene scene = new Scene(border, 480, 320);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }
}
