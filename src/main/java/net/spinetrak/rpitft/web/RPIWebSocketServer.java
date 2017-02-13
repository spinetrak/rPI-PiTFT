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

package net.spinetrak.rpitft.web;

import com.google.gson.Gson;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.listeners.DeviceListener;
import net.spinetrak.rpitft.data.listeners.GPSListener;
import net.spinetrak.rpitft.data.listeners.HotspotListener;
import net.spinetrak.rpitft.data.listeners.NetworkListener;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.network.Network;
import net.spinetrak.rpitft.data.network.hotspot.Hotspot;
import net.spinetrak.rpitft.data.raspberry.Device;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public class RPIWebSocketServer extends WebSocketServer implements DeviceListener, GPSListener, HotspotListener,
                                                                   NetworkListener
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.websocket.RPIWebSocketServer");
  private static RPIWebSocketServer _instance = null;
  private final Gson _gson = new Gson();

  private RPIWebSocketServer(int port) throws UnknownHostException
  {
    super(new InetSocketAddress(port));
    Dispatcher.getInstance().addListener(this);
  }

  public static RPIWebSocketServer getInstance() throws InterruptedException, IOException
  {
    WebSocketImpl.DEBUG = true;
    final int port = 8887;

    if (_instance == null)
    {
      _instance = new RPIWebSocketServer(port);
      _instance.start();
      LOGGER.info("WebSocketServer started on port: " + _instance.getPort());
    }
    return _instance;
  }

  @Override
  public void handleDeviceData(final Device device_)
  {
    sendToAll(_gson.toJson(device_));
  }

  @Override
  public void handleGPSData(final GPS gps_)
  {
    sendToAll(_gson.toJson(gps_));
  }

  @Override
  public void handleHotspotData(final Hotspot hotspot_)
  {
    sendToAll(_gson.toJson(hotspot_));
  }

  @Override
  public void handleNetworkData(final Network network_)
  {
    sendToAll(_gson.toJson(network_));
  }

  @Override
  public void onClose(final WebSocket conn_, int code_, final String reason_, final boolean remote_)
  {
    LOGGER.info(
      "Closed connection: " + conn_.getRemoteSocketAddress().getAddress().getHostAddress() + " has disconnected with code: " + code_ + "; reason: " + reason_ + "; remote: " + remote_);
  }

  @Override
  public void onError(final WebSocket conn_, final Exception ex_)
  {
    LOGGER.error(ex_.getMessage());
    if (conn_ != null)
    {
      LOGGER.info("Exception occurred on conn " + conn_);
    }
  }

  @Override
  public void onMessage(final WebSocket conn_, final String message_)
  {
    LOGGER.info(
      "Received message from " + conn_.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message_);
  }

  @Override
  public void onOpen(final WebSocket conn_, final ClientHandshake handshake_)
  {
    LOGGER.info(
      "New connection: " + handshake_.getResourceDescriptor() + " from " + conn_.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  private void sendToAll(final String text_)
  {
    final Collection<WebSocket> cons = connections();
    for (final WebSocket con : cons)
    {
      con.send(text_);
    }
  }
}
