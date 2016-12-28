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

package net.spinetrak.rpitft.data.streams.logger;

import com.alonkadury.initialState.API;
import com.alonkadury.initialState.Bucket;
import com.alonkadury.initialState.Data;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.Formatter;
import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.raspberry.Device;
import net.spinetrak.rpitft.data.raspberry.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class InitialStateStream implements GPSListener, DeviceListener
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "net.spinetrak.rpitft.data.streams.logger.InitialStateStream");
  final private API _account;
  final private Bucket _bucket;
  private final LinkedBlockingQueue<Device> _deviceQueue = new LinkedBlockingQueue<>();
  private final LinkedBlockingQueue<GPS> _gpsQueue = new LinkedBlockingQueue<>();

  public InitialStateStream()
  {
    _bucket = new Bucket("spinetrak", "spinetrak");
    _account = new API(System.getProperty("initialstatekey"), 5);
    _account.createBucket(_bucket);

    Dispatcher.getInstance().addListener((GPSListener) this);
    Dispatcher.getInstance().addListener((DeviceListener) this);

    Runtime.getRuntime().addShutdownHook(new Thread(_account::terminate));

    final Publisher publisher = new Publisher();
    final Thread publisherThread = new Thread(publisher);
    publisherThread.start();

    final NetworkChecker networkChecker = new NetworkChecker();
    final Thread networkCheckerThread = new Thread(networkChecker);
    networkCheckerThread.start();
  }

  @Override
  public void handleData(final Device device_)
  {
    _deviceQueue.add(device_);
  }

  @Override
  public void handleData(final GPS gps_)
  {
    _gpsQueue.add(gps_);
  }


  @Override
  public String toString()
  {
    return "InitialStateStream{" +
      "_account=" + _account +
      ", _bucket=" + _bucket +
      '}';
  }

  private class NetworkChecker implements Runnable
  {
    @Override
    public void run()
    {
      try
      {
        final Network network = new Network();
        Dispatcher.getInstance().dispatchEvent(network);
        Thread.sleep(30000);
      }
      catch (final InterruptedException ex_)
      {
        LOGGER.error(ex_.getMessage());
      }
    }
  }

  private class Publisher implements Runnable
  {
    @Override
    public void run()
    {
      while (true)
      {
        collectData();
      }
    }

    private void collectData()
    {
      final List<GPS> gpsData = new ArrayList<>();
      final List<Device> deviceData = new ArrayList<>();

      while (!_gpsQueue.isEmpty())
      {
        final GPS gps = _gpsQueue.remove();
        gpsData.add(gps);
      }
      while (!_deviceQueue.isEmpty())
      {
        final Device device = _deviceQueue.remove();
        deviceData.add(device);
      }

      final Data[] avgData = getAverage(gpsData, deviceData);
      if (avgData != null)
      {
        publish(avgData);
      }
    }

    private Data[] getAverage(final List<GPS> gpsData_, final List<Device> deviceData_)
    {
      final List<Data<Serializable>> data = new ArrayList<>();
      final String iso8601 = Formatter.formatISO8601Timestamp(new Date());
      getAverageGPSData(gpsData_, data, iso8601);

      getAverageDeviceData(deviceData_, data, iso8601);

      if (data.size() > 0)
      {
        return data.toArray(new Data[data.size()]);
      }
      return null;
    }

    private void getAverageDeviceData(final List<Device> deviceData_, final List<Data<java.io.Serializable>> data_,
                                      final String timestamp_)
    {
      float cpu = 0.0f;
      float disk = 0.0f;
      float mem = 0.0f;
      float temp = 0.0f;
      int devices = 0;

      for (final Device device : deviceData_)
      {
        cpu += device.getCpu();
        disk += device.getDisk();
        mem += device.getMemory();
        temp += device.getTemperature();
        devices++;
      }
      if (devices > 0)
      {
        data_.add(new Data<>("CPU", Formatter.round(cpu / devices, 2), timestamp_));
        data_.add(new Data<>("Disk", Formatter.round(disk / devices, 2), timestamp_));
        data_.add(new Data<>("Memory", Formatter.round(mem / devices, 2), timestamp_));
        data_.add(new Data<>("Temperature", Formatter.round(temp / devices, 2), timestamp_));
      }
    }

    private void getAverageGPSData(final List<GPS> gpsData_, final List<Data<Serializable>> data_,
                                   final String timestamp_)
    {
      float lat = 0.0f;
      float lon = 0.0f;
      float alt = 0.0f;
      float speed = 0.0f;
      int locations = 0;
      int movements = 0;

      for (final GPS gps : gpsData_)
      {
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

      if (locations > 0)
      {
        data_.add(new Data<>("Altitude", Formatter.round(alt / locations, 2), timestamp_));
        data_.add(new Data<>("Location", lat / locations + "," + lon / locations, timestamp_));
      }
      if (movements > 0)
      {
        data_.add(new Data<>("Speed", Formatter.round(speed / movements, 2), timestamp_));
      }
    }

    private void publish(final Data[] data_)
    {
      _account.createBulkData(_bucket, data_);

      try
      {
        synchronized (_account)
        {
          _account.wait(10000);
        }
      }
      catch (final InterruptedException ex_)
      {
        LOGGER.error(ex_.getMessage());
      }

      /*
      for (final Data data : data_)
      {
        _account.createData(_bucket, data);

        try
        {
          synchronized (_account)
          {
            _account.wait(1300);
          }
        }
        catch (final InterruptedException ex_)
        {
          LOGGER.error(ex_.getMessage());
        }
      }
      */
    }
  }

}
