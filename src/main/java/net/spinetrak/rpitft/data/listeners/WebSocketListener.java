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

package net.spinetrak.rpitft.data.listeners;

import com.google.gson.Gson;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.network.Network;
import net.spinetrak.rpitft.data.network.hotspot.Hotspot;
import net.spinetrak.rpitft.data.raspberry.Device;

public class WebSocketListener implements DeviceListener, GPSListener, HotspotListener, NetworkListener
{
  private final Gson _gson = new Gson();

  public WebSocketListener()
  {

  }

  @Override
  public void handleDeviceData(final Device device_)
  {
    System.out.println(_gson.toJson(device_));
  }

  @Override
  public void handleGPSData(final GPS gps_)
  {
    System.out.println(_gson.toJson(gps_));
  }

  @Override
  public void handleHotspotData(final Hotspot hotspot_)
  {
    System.out.println(_gson.toJson(hotspot_));
  }

  @Override
  public void handleNetworkData(final Network network_)
  {
    System.out.println(_gson.toJson(network_));
  }

  public void start()
  {
    Dispatcher.getInstance().addListener(this);
  }
}
