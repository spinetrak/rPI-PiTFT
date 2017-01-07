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

package net.spinetrak.rpitft.data.network.hotspot;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
public class HotspotStatus
{
  @JacksonXmlProperty
  private int BatteryLevel;
  @JacksonXmlProperty
  private int BatteryPercent;
  @JacksonXmlProperty
  private int BatteryStatus;
  @JacksonXmlProperty
  private long ConnectionStatus;
  @JacksonXmlProperty
  private int CurrentNetworkType;
  @JacksonXmlProperty
  private int CurrentNetworkTypeEx;
  @JacksonXmlProperty
  private int CurrentServiceDomain;
  @JacksonXmlProperty
  private int CurrentWifiUser;
  @JacksonXmlProperty
  private String PrimaryDns;
  @JacksonXmlProperty
  private String PrimaryIPv6Dns;
  @JacksonXmlProperty
  private int RoamingStatus;
  @JacksonXmlProperty
  private String SecondaryDns;
  @JacksonXmlProperty
  private String SecondaryIPv6Dns;
  @JacksonXmlProperty
  private int ServiceStatus;
  @JacksonXmlProperty
  private int SignalIcon;
  @JacksonXmlProperty
  private int SignalStrength;
  @JacksonXmlProperty
  private int SimStatus;
  @JacksonXmlProperty
  private int TotalWifiUser;
  @JacksonXmlProperty
  private String WanIPAddress;
  @JacksonXmlProperty
  private String WanIPv6Address;
  @JacksonXmlProperty
  private int WanPolicy;
  @JacksonXmlProperty
  private int WifiConnectionStatus;
  @JacksonXmlProperty
  private int WifiStatus;
  @JacksonXmlProperty
  private int cellroam;
  @JacksonXmlProperty
  private String classify;
  @JacksonXmlProperty
  private int currenttotalwifiuser;
  @JacksonXmlProperty
  private int flymode;
  @JacksonXmlProperty
  private int maxsignal;
  @JacksonXmlProperty
  private int simlockStatus;
  @JacksonXmlProperty
  private int wififrequence;
  @JacksonXmlProperty
  private int wifiindooronly;

  public int getBatteryPercent()
  {
    return BatteryPercent;
  }

}
