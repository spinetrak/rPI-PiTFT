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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.spinetrak.rpitft.data.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.util.Map;

public class HotspotChecker implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "net.spinetrak.rpitft.data.netweok.NetworkChecker");
  private final String _login = "http://192.168.8.1/html/home.html";
  private final String _status = "http://192.168.8.1/api/monitoring/status";
  private final String _traffic = "http://192.168.8.1/api/monitoring/traffic-statistics";
  private boolean _stopped = false;

  @Override
  public void run()
  {
    while (!_stopped)
    {
      checkHotspot();
    }
  }

  public void stop()
  {
    _stopped = true;
  }

  private void checkHotspot()
  {
    try
    {
      final Map<String, NewCookie> cookies = ClientBuilder.newClient().target(_login).request().get().getCookies();

      final ObjectMapper xmlMapper = new XmlMapper();

      final Hotspot hotspot = new Hotspot();
      final HotspotTraffic traffic = xmlMapper.readValue(
        ClientBuilder.newClient().target(_traffic).request(MediaType.TEXT_PLAIN).cookie(cookies.get("SessionID")).get(
          String.class), HotspotTraffic.class);
      hotspot.setTraffic(traffic);

      final HotspotStatus status = xmlMapper.readValue(
        ClientBuilder.newClient().target(_status).request(MediaType.TEXT_PLAIN).cookie(cookies.get("SessionID")).get(
          String.class), HotspotStatus.class);
      hotspot.setStatus(status);

      Dispatcher.getInstance().getQueue().add(hotspot);
      Thread.sleep(5000);
    }
    catch (final InterruptedException | IOException | ProcessingException ex_)
    {
      LOGGER.error(ex_.getMessage());
    }
  }
}
