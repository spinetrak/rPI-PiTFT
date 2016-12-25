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

package net.spinetrak.rpitft.ui.center;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.raspberry.Device;

class GaugesView implements GPSListener, DeviceListener
{
  private final int SIZE = 82;
  private final Gauge _altitude;
  private final Gauge _cpu;
  private final Gauge _direction;
  private final Gauge _disk;
  private final Gauge _mem;
  private final TilePane _panel;
  private final Gauge _speed;
  private final Gauge _temp;
  private final Gauge _trackpoints;

  GaugesView()
  {
    final GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.DASHBOARD).prefSize(SIZE, SIZE)
      .thresholdColor(Color.RED)
      .thresholdVisible(true).decimals(2).maxValue(100).unit("%").threshold(90);

    _cpu = builder.build();
    _mem = builder.build();
    _disk = builder.build();
    _temp = builder.threshold(80).build();
    _speed = builder.decimals(1).maxValue(200).unit("km/h").threshold(140).build();
    _altitude = builder.maxValue(1500).unit("m").thresholdVisible(false).build();
    _direction = builder.maxValue(360).unit("deg").build();
    _trackpoints = builder.decimals(0).maxValue(15000).unit("#").build();

    final VBox tempBox = getTopicBox("TEMPERATURE", Color.GREEN, _temp);
    final VBox cpuBox = getTopicBox("CPU", Color.GREEN, _cpu);
    final VBox memBox = getTopicBox("MEMORY", Color.GREEN, _mem);
    final VBox diskBox = getTopicBox("DISKSPACE", Color.GREEN, _disk);
    final VBox altitudeBox = getTopicBox("ALTUTUDE", Color.GREEN, _altitude);
    final VBox directionBox = getTopicBox("DIRECTION", Color.GREEN, _direction);
    final VBox speedBox = getTopicBox("SPEED", Color.GREEN, _speed);
    final VBox trackpointsBox = getTopicBox("TRACKPOINTS", Color.GREEN, _trackpoints);

    _panel = new TilePane();
    _panel.setAlignment(Pos.CENTER);
    _panel.setPadding(new Insets(1, 0, 1, 0));
    _panel.setVgap(1);
    _panel.setHgap(1);
    _panel.setPrefRows(2);
    _panel.getChildren().add(tempBox);
    _panel.getChildren().add(cpuBox);
    _panel.getChildren().add(memBox);
    _panel.getChildren().add(diskBox);
    _panel.getChildren().add(altitudeBox);
    _panel.getChildren().add(directionBox);
    _panel.getChildren().add(speedBox);
    _panel.getChildren().add(trackpointsBox);
    Dispatcher.getInstance().addListener((GPSListener) this);
    Dispatcher.getInstance().addListener((DeviceListener) this);
  }


  Node getPanel()
  {
    return _panel;
  }

  @Override
  public void handleData(final Device device_)
  {
    _cpu.setValue(device_.getCpu());
    _disk.setValue(device_.getDisk());
    _mem.setValue(device_.getMemory());
    _temp.setValue(device_.getTemperature());
  }

  @Override
  public void handleData(final GPS gps_)
  {
    if (gps_.isValidLocation())
    {
      _altitude.setValue(gps_.getAltitude());
      _trackpoints.setValue(gps_.getTrackpoints());
    }
    if (gps_.isValidMovement())
    {
      _speed.setValue(gps_.getSpeed());
      _direction.setValue(gps_.getCourse());
    }
  }

  private VBox getTopicBox(final String text_, final Color color_, final Gauge gauge_)
  {
    final Rectangle bar = new Rectangle(SIZE, 3);
    bar.setArcWidth(6);
    bar.setArcHeight(6);
    bar.setFill(color_);

    final Label label = new Label(text_);
    label.setTextFill(color_);
    label.setAlignment(Pos.CENTER);
    label.setPadding(new Insets(0, 0, 1, 0));

    gauge_.setBarColor(Color.ORANGE);
    gauge_.setBarBackgroundColor(Color.GREEN);
    gauge_.setAnimated(true);

    final VBox vBox = new VBox(bar, label, gauge_);
    vBox.setSpacing(1);
    vBox.setAlignment(Pos.CENTER);
    return vBox;
  }

}
