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
 *
 */

package net.spinetrak.rpitft.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.Power;
import net.spinetrak.rpitft.data.Threshold;

class TextPanel
{
  private final Text _altitude;
  private final Text _batteryCapacity;
  private final Threshold _batteryCapacityThreshold;
  private final Text _batteryPower;
  private final Threshold _batteryPowerThreshold;
  private final Text _cpu;
  private final Threshold _cpuThreshold;
  private final Text _disk;
  private final Threshold _diskThreshold;
  private final Text _latitude;
  private final Text _longitude;
  private final Text _temperature;
  private final Threshold _temperatureThreshold;
  private final Text _time;
  private final FlowPane _top;
  private final Text _trackPoints;
  private boolean _cpuAlert = false;
  private boolean _diskAlert = false;
  private boolean _temperatureAlert = false;

  TextPanel()
  {
    _top = new FlowPane(Orientation.HORIZONTAL);
    _top.setPadding(new Insets(5));

    _time = new Text("[hh:mm:ss]");
    _top.getChildren().add(_time);

    _latitude = new Text("[xxxx.xxx N]");
    _top.getChildren().add(_latitude);

    _longitude = new Text("[xxxx.xxx E]");
    _top.getChildren().add(_longitude);

    _altitude = new Text("[xxxx.x M]");
    _top.getChildren().add(_altitude);

    _trackPoints = new Text("[xxxxxxx]");
    _top.getChildren().add(_trackPoints);

    _temperature = new Text("[xx.x C°]");
    _top.getChildren().add(_temperature);
    _temperatureThreshold = new Threshold(_temperature, 80, 75);

    _cpu = new Text("[xx.x% cpu]");
    _top.getChildren().add(_cpu);
    _cpuThreshold = new Threshold(_cpu, 90, 80);

    _disk = new Text("[xx% hd]");
    _top.getChildren().add(_disk);
    _diskThreshold = new Threshold(_disk, 90, 80);

    _batteryCapacity = new Text("[xxx.xx% bat]");
    _top.getChildren().add(_batteryCapacity);
    _batteryCapacityThreshold = new Threshold(_batteryCapacity, 25, 50);

    _batteryPower = new Text("[xxx.xxx mA]");
    _top.getChildren().add(_batteryPower);
    _batteryPowerThreshold = new Threshold(_batteryPower, 400, 500);
  }

  void addData(final Power power_)
  {
    final float capacity = power_.getCapacity();
    final float power = power_.getPower();
    _batteryCapacity.setText(String.format("[%.2f%% bat]", capacity));
    _batteryCapacityThreshold.setColor(capacity);
    _batteryPower.setText(String.format("[%.2f mA]", power));
    _batteryPowerThreshold.setColor(power);
  }

  void addData(final Device device_)
  {
    final float cpu = device_.getCpu();
    final float disk = device_.getDisk();
    final float temperature = device_.getTemperature();

    _cpu.setText(String.format("[%.2f%% cpu]", cpu));
    _cpuThreshold.setColor(cpu);

    _disk.setText(String.format("[%.2f%% hd]", disk));
    _diskThreshold.setColor(disk);

    _temperature.setText(String.format("[%.2f C°]", temperature));
    _temperatureThreshold.setColor(temperature);
  }


  FlowPane getTop()
  {
    return _top;
  }

}
