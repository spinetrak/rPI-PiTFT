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

import com.pi4j.system.NetworkInfo;
import net.spinetrak.rpitft.data.events.Event;

import java.io.IOException;

public class Network implements Event
{
  private final String _message;
  private boolean _isUp;

  Network()
  {
    _isUp = isInternetReachable();
    _message = getLocalNetworkInfo();
  }

  public Network(final String message_)
  {
    _isUp = isInternetReachable();
    _message = message_;
  }

  public String getMessage()
  {
    return _message;
  }

  public boolean isUp()
  {
    return _isUp;
  }


  private String getLocalNetworkInfo()
  {
    String status;
    try
    {
      final String[] addresses = NetworkInfo.getIPAddresses();
      status = addresses != null ? addresses[0] : NetworkInfo.getIPAddress();
    }
    catch (final IOException | InterruptedException ex_)
    {
      status = ex_.getMessage();
    }
    return status;
  }

  private boolean isInternetReachable()
  {
    try
    {
      final Process process = Runtime.getRuntime().exec("nc -w 1 -z google.com 80");
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
