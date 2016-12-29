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
import net.spinetrak.rpitft.data.events.Event;
import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.EventListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.listeners.NetworkListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.location.NmeaLogger;
import net.spinetrak.rpitft.data.network.Network;
import net.spinetrak.rpitft.data.raspberry.Device;
import net.spinetrak.rpitft.data.raspberry.DeviceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher
{
  private final static Dispatcher _instance = new Dispatcher();
  private final ConcurrentLinkedQueue<Event> _queue = new ConcurrentLinkedQueue<>();
  private List<EventListener> _listeners = new ArrayList<>();

  private Dispatcher()
  {
    final DispatcherQueue queue = new DispatcherQueue();
    queue.start();
  }

  public static Dispatcher getInstance()
  {
    return _instance;
  }

  public void addListener(final EventListener listener_)
  {
    _listeners.add(listener_);
  }

  public Queue getQueue()
  {
    return _queue;
  }

  private void dispatchEvent(final Event event_)
  {
    for (final EventListener listener : _listeners)
    {
      if (event_ instanceof Device && listener instanceof DeviceListener)
      {
        ((DeviceListener) listener).handleDeviceData((Device) event_);
      }
      if (event_ instanceof GPS && listener instanceof GPSListener)
      {
        ((GPSListener) listener).handleGPSData((GPS) event_);
      }
      if (event_ instanceof Network && listener instanceof NetworkListener)
      {
        ((NetworkListener) listener).handleNetworkData((Network) event_);
      }
    }
  }

  private void processQueue()
  {
    while (!_queue.isEmpty())
    {
      final Event event = _queue.remove();
      dispatchEvent(event);
    }
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
      _nmeaLogger.start();
    }

    void start()
    {
      new AnimationTimer()
      {
        @Override
        public void handle(final long now_)
        {
          Dispatcher.getInstance().processQueue();
        }
      }.start();
    }
  }
}
