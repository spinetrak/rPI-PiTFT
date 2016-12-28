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

package net.spinetrak.rpitft.data;

import javafx.animation.AnimationTimer;
import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.listeners.NetworkListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.location.NmeaLogger;
import net.spinetrak.rpitft.data.raspberry.Device;
import net.spinetrak.rpitft.data.raspberry.DeviceClient;
import net.spinetrak.rpitft.data.raspberry.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher
{
  private final static Dispatcher _instance = new Dispatcher();
  private List<DeviceListener> _deviceListeners = new ArrayList<>();
  private List<GPSListener> _gpsListeners = new ArrayList<>();
  private List<NetworkListener> _networkListeners = new ArrayList<>();

  private Dispatcher()
  {
    final DispatcherQueue queue = new DispatcherQueue();
    queue.start();
  }

  public static Dispatcher getInstance()
  {
    return _instance;
  }

  public void addListener(final DeviceListener listener_)
  {
    _deviceListeners.add(listener_);
  }

  public void addListener(final NetworkListener listener_)
  {
    _networkListeners.add(listener_);
  }

  public void addListener(final GPSListener listener_)
  {
    _gpsListeners.add(listener_);
  }

  public void dispatchEvent(final GPS gps_)
  {
    for (final GPSListener listener : _gpsListeners)
    {
      listener.handleData(gps_);
    }
  }

  public void dispatchEvent(final Device device_)
  {
    for (final DeviceListener listener : _deviceListeners)
    {
      listener.handleData(device_);
    }
  }

  public void dispatchEvent(final Network network_)
  {
    for (final NetworkListener listener : _networkListeners)
    {
      listener.handleData(network_);
    }
  }

  private void addDataToSeries(
                       final ConcurrentLinkedQueue<Device> deviceQueue_,
                       final ConcurrentLinkedQueue<GPS> gpsQueue_)
  {
    while (!deviceQueue_.isEmpty())
    {
      final Device device = deviceQueue_.remove();
      dispatchEvent(device);
    }
    while (!gpsQueue_.isEmpty())
    {
      final GPS gps = gpsQueue_.remove();
      dispatchEvent(gps);
    }

    final Network network = new Network();
    dispatchEvent(network);
  }

  private static class DispatcherQueue
  {
    private final DeviceClient _deviceClient;
    private final ExecutorService _executor;
    private final NmeaLogger _nmeaLogger;

    DispatcherQueue()
    {
      _executor = Executors.newCachedThreadPool(runnable_ -> {
        final Thread thread = new Thread(runnable_);
        thread.setDaemon(true);
        return thread;
      });

      _deviceClient = new DeviceClient(_executor);
      _executor.execute(_deviceClient);

      _nmeaLogger = new NmeaLogger();

    }

    void start()
    {
      new AnimationTimer()
      {
        @Override
        public void handle(final long now_)
        {
          Dispatcher.getInstance().addDataToSeries(_deviceClient.getQueue(), _nmeaLogger.getQueue());
        }
      }.start();
    }
  }
}
