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

package net.spinetrak.rpitft.data.streams.logger;

import com.alonkadury.initialState.API;
import com.alonkadury.initialState.Bucket;
import com.alonkadury.initialState.Data;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.Formatter;
import net.spinetrak.rpitft.data.events.Event;
import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.listeners.HotspotListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.network.NetworkChecker;
import net.spinetrak.rpitft.data.network.hotspot.Hotspot;
import net.spinetrak.rpitft.data.network.hotspot.HotspotChecker;
import net.spinetrak.rpitft.data.raspberry.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class InitialStateStream implements GPSListener, DeviceListener, HotspotListener
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "net.spinetrak.rpitft.data.streams.logger.InitialStateStream");
  private static InitialStateStream _instance;
  final private API _account;
  final private Bucket _bucket;
  private final LinkedBlockingQueue<Event> _queue = new LinkedBlockingQueue<>();
  private boolean _isStreamingEnabled = true;

  private InitialStateStream()
  {
    _bucket = new Bucket("spinetrak-2016-12-28", "spinetrak");
    _account = new API(System.getProperty("initialstatekey"), 5);
    _account.createBucket(_bucket);

    Dispatcher.getInstance().addListener(this);
    Dispatcher.getInstance().addListener(this);

    final Publisher publisher = new Publisher();
    final Thread publisherThread = new Thread(publisher);
    publisherThread.start();

    final NetworkChecker networkChecker = new NetworkChecker();
    final Thread networkCheckerThread = new Thread(networkChecker);
    networkCheckerThread.start();

    final HotspotChecker hotspotChecker = new HotspotChecker();
    final Thread hotspotCheckerThread = new Thread(hotspotChecker);
    hotspotCheckerThread.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      _account.terminate();
      LOGGER.info("Account terminated.");

      publisher.stop();
      LOGGER.info("Publisher stopped.");

      networkChecker.stop();
      LOGGER.info("Network checker stopped.");

      hotspotChecker.stop();
      LOGGER.info("Hotspot checker stopped.");
    }));
  }

  public static InitialStateStream getInstance()
  {
    if (_instance == null)
    {
      _instance = new InitialStateStream();
    }
    return _instance;
  }

  @Override
  public void handleDeviceData(final Device device_)
  {
    _queue.add(device_);
  }

  @Override
  public void handleGPSData(final GPS gps_)
  {
    _queue.add(gps_);
  }

  @Override
  public void handleHotspotData(final Hotspot hotspot_)
  {
    _queue.add(hotspot_);
  }

  public boolean isStreamingEnabled()
  {
    return _isStreamingEnabled;
  }

  public void setStreamingEnabled(final boolean streamingEnabled_)
  {
    _isStreamingEnabled = streamingEnabled_;
  }

  @Override
  public String toString()
  {
    return "InitialStateStream{" +
      "_account=" + _account +
      ", _bucket=" + _bucket +
      '}';
  }

  private class Publisher implements Runnable
  {
    private boolean _stopped = false;

    void stop()
    {
      _stopped = true;
    }

    @Override
    public void run()
    {
      while (!_stopped)
      {
        collectData();
      }
    }

    private void collectData()
    {
      final List<Event> data = new ArrayList<>();
      while (!_queue.isEmpty())
      {
        final Event event = _queue.remove();
        data.add(event);
      }

      if (_isStreamingEnabled)
      {
        final Data[] avgData = getAverage(data);

        if (avgData != null)
        {
          publish(avgData);
        }
      }
      else
      {
        data.clear();
      }
    }

    private Data[] getAverage(final List<Event> data_)
    {
      final List<Data<Serializable>> data = new ArrayList<>();
      final String iso8601 = Formatter.formatISO8601Timestamp(new Date());

      data.addAll(getAverageGPSData(data_, iso8601));
      data.addAll(getAverageDeviceData(data_, iso8601));
      data.addAll(getAverageHotspotData(data_, iso8601));

      if (data.size() > 0)
      {
        return data.toArray(new Data[data.size()]);
      }
      return null;
    }

    private List<Data<java.io.Serializable>> getAverageDeviceData(final List<Event> deviceData_,
                                                                  final String timestamp_)
    {
      final List<Data<java.io.Serializable>> data = new ArrayList<>();
      float cpu = 0.0f;
      float disk = 0.0f;
      float mem = 0.0f;
      float temp = 0.0f;
      int devices = 0;

      for (final Event event : deviceData_)
      {
        if (event instanceof Device)
        {
          final Device device = (Device) event;
          cpu += device.getCpu();
          disk += device.getDisk();
          mem += device.getMemory();
          temp += device.getTemperature();
          devices++;
        }
      }
      if (devices > 0)
      {
        data.add(new Data<>("CPU", Formatter.round(cpu / devices, 2), timestamp_));
        data.add(new Data<>("Disk", Formatter.round(disk / devices, 2), timestamp_));
        data.add(new Data<>("Memory", Formatter.round(mem / devices, 2), timestamp_));
        data.add(new Data<>("Temperature", Formatter.round(temp / devices, 2), timestamp_));
      }
      return data;
    }

    private List<Data<java.io.Serializable>> getAverageGPSData(final List<Event> gpsData_, final String timestamp_)
    {
      final List<Data<java.io.Serializable>> data = new ArrayList<>();
      float lat = 0.0f;
      float lon = 0.0f;
      float alt = 0.0f;
      float speed = 0.0f;
      int locations = 0;
      int movements = 0;

      for (final Event event : gpsData_)
      {
        if (event instanceof GPS)
        {
          final GPS gps = (GPS) event;
          if (gps.isValidLocation())
          {
            lat += gps.getLatitude();
            lon += gps.getLongitude();
            alt += gps.getAltitude();
            locations++;
          }
          if (gps.isValidMovement())
          {
            speed += gps.getSpeed();
            movements++;
          }
        }
      }

      if (locations > 0)
      {
        data.add(new Data<>("Altitude", Formatter.round(alt / locations, 2), timestamp_));
        data.add(new Data<>("Location", lat / locations + "," + lon / locations, timestamp_));
      }
      if (movements > 0)
      {
        data.add(new Data<>("Speed", Formatter.round(speed / movements, 2), timestamp_));
      }
      return data;
    }

    private List<Data<java.io.Serializable>> getAverageHotspotData(final List<Event> hotspotData_,
                                                                   final String timestamp_)
    {
      final List<Data<java.io.Serializable>> data = new ArrayList<>();
      float batteryPercent = 0.0f;
      float dataVolume = 0.0f;
      int hotspots = 0;

      for (final Event event : hotspotData_)
      {
        if (event instanceof Hotspot)
        {
          final Hotspot hotspot = (Hotspot) event;
          batteryPercent += hotspot.getStatus().getBatteryPercent();
          dataVolume += hotspot.getTraffic().getTotalDataVolume();
          hotspots++;
        }
      }
      if (hotspots > 0)
      {
        data.add(new Data<>("HotspotBattery", Formatter.round(batteryPercent / hotspots, 2), timestamp_));
        data.add(new Data<>("HotspotData", Formatter.round(dataVolume / hotspots, 2), timestamp_));
      }
      return data;
    }

    private void publish(final Data[] data_)
    {
      _account.createBulkData(_bucket, data_);

      try
      {
        synchronized (_account)
        {
          _account.wait(6000);
        }
      }
      catch (final InterruptedException ex_)
      {
        LOGGER.error(ex_.getMessage());
      }
    }
  }

}
