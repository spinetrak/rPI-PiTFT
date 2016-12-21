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

package net.spinetrak.rpitft.ui.top;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import net.spinetrak.rpitft.data.Formatter;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.raspberry.Device;
import net.spinetrak.rpitft.data.raspberry.Power;
import net.spinetrak.rpitft.ui.Threshold;
import org.joda.time.DateTime;

import static net.spinetrak.rpitft.data.Formatter.formatLatitude;
import static net.spinetrak.rpitft.data.Formatter.formatLongitude;
import static net.spinetrak.rpitft.ui.center.Charts.MIN_TOP_HEIGHT;
import static net.spinetrak.rpitft.ui.center.Charts.MIN_WIDTH;
import static org.joda.time.DateTimeZone.UTC;

public class TextPanel
{
  /*
  private final Text _batteryCapacity;
  private final Threshold _batteryCapacityThreshold;
  private final Text _batteryPower;
  private final Threshold _batteryPowerThreshold;
  */
  private final Text _altitude;
  private final Text _cpu;
  private final Threshold _cpuThreshold;
  private final Text _disk;
  private final Threshold _diskThreshold;
  private final Text _latitude;
  private final Text _longitude;
  private final Text _memory;
  private final Threshold _memoryThreshold;
  private final Text _temperature;
  private final Threshold _temperatureThreshold;
  private final Text _time;
  private final Threshold _timeThreshold;
  private final FlowPane _top;
  private final Text _trackPoints;
  private final Threshold _trackpointsThreshold;

  public TextPanel()
  {
    _top = new FlowPane(Orientation.HORIZONTAL);
    _top.setMinHeight(MIN_TOP_HEIGHT);
    _top.setPrefSize(MIN_WIDTH, MIN_TOP_HEIGHT);
    _top.setPadding(new Insets(1));

    _time = new Text("[hh:mm:ss]");
    _top.getChildren().add(_time);
    _timeThreshold = new Threshold(_time, 60, 30);

    _latitude = new Text("[xxxx.xxx N]");
    _top.getChildren().add(_latitude);

    _longitude = new Text("[xxxx.xxx E]");
    _top.getChildren().add(_longitude);

    _altitude = new Text("[xxxx.x M]");
    _top.getChildren().add(_altitude);

    _trackPoints = new Text("[xxxxxxx]");
    _top.getChildren().add(_trackPoints);
    _trackpointsThreshold = new Threshold(_trackPoints, 40000, 30000);

    _temperature = new Text("[xx.x C°]");
    _top.getChildren().add(_temperature);
    _temperatureThreshold = new Threshold(_temperature, 85, 75);

    _cpu = new Text("[xx.xx% cpu]");
    _top.getChildren().add(_cpu);
    _cpuThreshold = new Threshold(_cpu, 90, 80);

    _disk = new Text("[xx.xx% hd]");
    _top.getChildren().add(_disk);
    _diskThreshold = new Threshold(_disk, 90, 80);

    _memory = new Text("[xx.xx% mem]");
    _top.getChildren().add(_memory);
    _memoryThreshold = new Threshold(_memory, 90, 80);

    /*
    _batteryCapacity = new Text("[xxx.xx% bat]");
    _top.getChildren().add(_batteryCapacity);
    _batteryCapacityThreshold = new Threshold(_batteryCapacity, 25, 50);

    _batteryPower = new Text("[xxx.xxx mA]");
    _top.getChildren().add(_batteryPower);
    _batteryPowerThreshold = new Threshold(_batteryPower, 400, 500);
    */
  }

  void addData(final Power power_)
  {
    /*
    final float capacity = power_.getCapacity();
    final float power = power_.getPower();

    _batteryCapacity.setText(String.format("[%.2f%% bat]", capacity));
    _batteryCapacityThreshold.setColor(capacity);

    _batteryPower.setText(String.format("[%.2f mA]", power));
    _batteryPowerThreshold.setColor(power);
    */
  }

  public void addData(final Device device_)
  {
    final float cpu = device_.getCpu();
    final float disk = device_.getDisk();
    final float temperature = device_.getTemperature();
    final float memory = device_.getMemory();

    _cpu.setText(Formatter.formatCPU(cpu));
    _cpuThreshold.setColor(cpu);

    _disk.setText(Formatter.formatHD(disk));
    _diskThreshold.setColor(disk);

    _memory.setText(Formatter.formatMEM(memory));
    _memoryThreshold.setColor(memory);

    _temperature.setText(Formatter.formatTemperature(temperature));
    _temperatureThreshold.setColor(temperature);
  }

  public void addData(final GPS gps_)
  {
    final DateTime time = gps_.getTime();
    final float latitude = gps_.getLatitude();
    final float longitude = gps_.getLongitude();
    final float altitude = gps_.getAltitude();
    final int trackpoints = gps_.getTrackpoints();

    if (time != null)
    {
      _time.setText(String.format("[%s]", time.toString(GPS.DTF)));
    }
    _timeThreshold.setColor(getTimeDifferenceInSeconds(time));
    _latitude.setText(formatLatitude(latitude));
    _longitude.setText(formatLongitude(longitude));
    _altitude.setText(Formatter.formatAltitude(altitude));
    _trackPoints.setText(Formatter.formatTrackpoints(trackpoints));
    _trackpointsThreshold.setColor(trackpoints);
  }

  public int getTimeDifferenceInSeconds(final DateTime time_)
  {
    if (time_ == null)
    {
      return 60;
    }
    final int nowSeconds = (int) ((new DateTime(UTC).getMillis() % (24 * 60 * 60 * 1000L)) / 1000);
    final int timeSeconds = (int) ((time_.getMillis() % (24 * 60 * 60 * 1000L)) / 1000);
    if (nowSeconds < timeSeconds)
    {
      return 60;
    }
    return nowSeconds - timeSeconds;
  }

  public FlowPane getTop()
  {
    return _top;
  }


}