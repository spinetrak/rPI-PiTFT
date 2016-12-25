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

import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.listeners.NetworkListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.raspberry.Device;
import net.spinetrak.rpitft.data.raspberry.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Dispatcher
{
  private static Dispatcher _instance = new Dispatcher();
  private List<DeviceListener> _deviceListeners = new ArrayList<>();
  private List<GPSListener> _gpsListeners = new ArrayList<>();
  private List<NetworkListener> _networkListeners = new ArrayList<>();

  private Dispatcher()
  {

  }

  void addDataToSeries(//final ConcurrentLinkedQueue<Power> powerQueue_,
                       final ConcurrentLinkedQueue<Device> deviceQueue_,
                       final ConcurrentLinkedQueue<GPS> gpsQueue_)
  {
    while (!deviceQueue_.isEmpty())
    {
      final Device device = deviceQueue_.remove();
      for (final DeviceListener listener : _deviceListeners)
      {
        listener.handleData(device);
      }
    }
    while (!gpsQueue_.isEmpty())
    {
      final GPS gps = gpsQueue_.remove();
      for (final GPSListener listener : _gpsListeners)
      {
        listener.handleData(gps);
      }
    }

    final Network network = new Network();
    for (final NetworkListener listener : _networkListeners)
    {
      listener.handleData(network);
    }
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
}
