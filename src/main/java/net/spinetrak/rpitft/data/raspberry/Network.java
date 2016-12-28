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

package net.spinetrak.rpitft.data.raspberry;

import com.pi4j.system.NetworkInfo;

import java.io.IOException;
import java.net.InetAddress;

public class Network
{
  private final String _message;
  private boolean _isUp;

  public Network()
  {
    _isUp = getInternetAccessInfo();
    _message = getLocalNetworkInfo();
  }

  public String getMessage()
  {
    return _message;
  }

  public boolean isUp()
  {
    return _isUp;
  }

  private boolean getInternetAccessInfo()
  {
    try
    {
      return InetAddress.getByName("8.8.8.8").isReachable(1000);
    }
    catch (final IOException ex_)
    {
      //ignore
    }
    return true;
  }

  private String getLocalNetworkInfo()
  {
    final String isUp = _isUp ? "UP" : "DOWN";
    String status;
    try
    {
      final String[] addresses = NetworkInfo.getIPAddresses();
      final String address = addresses != null ? addresses[0] : NetworkInfo.getIPAddress();
      status = "[" + isUp + "/" + address + "]";
    }
    catch (final IOException | InterruptedException ex_)
    {
      status = "[" + isUp + "/" + ex_.getMessage() + "]";
    }
    return status;
  }
}
