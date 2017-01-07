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

package net.spinetrak.rpitft.data.network;

import net.spinetrak.rpitft.data.events.Event;

import java.io.IOException;

public class Hotspot implements Event
{
  private final boolean _isConnected;
  private HotspotStatus _status = new HotspotStatus();
  private HotspotTraffic _traffic = new HotspotTraffic();

  Hotspot()
  {
    _isConnected = initReachable();
  }

  void setStatus(final HotspotStatus status_)
  {
    _status = status_;
  }

  void setTraffic(final HotspotTraffic traffic_)
  {
    _traffic = traffic_;
  }

  public HotspotStatus getStatus()
  {
    return _status;
  }

  public HotspotTraffic getTraffic()
  {
    return _traffic;
  }

  public boolean isConnected()
  {
    return _isConnected;
  }

  private boolean initReachable()
  {
    try
    {
      final Process process = Runtime.getRuntime().exec("nc -w 1 -z 192.168.8.1 80");
      int returnVal = process.waitFor();
      return (returnVal == 0);
    }
    catch (final IOException | InterruptedException ex_)
    {
      //ignore
    }
    return false;
  }
}
