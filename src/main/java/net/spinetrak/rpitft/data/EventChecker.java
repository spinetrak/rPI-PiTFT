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

package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.data.network.NetworkChecker;
import net.spinetrak.rpitft.data.network.hotspot.HotspotChecker;
import net.spinetrak.rpitft.data.raspberry.DeviceChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventChecker
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.EventChecker");
  private static EventChecker _instance = null;
  private boolean _started = false;

  private EventChecker()
  {

  }

  public static EventChecker getInstance()
  {
    if (_instance == null)
    {
      _instance = new EventChecker();
    }
    return _instance;
  }

  public void start()
  {
    if (!_started)
    {
      final NetworkChecker networkChecker = new NetworkChecker();
      final Thread networkCheckerThread = new Thread(networkChecker);
      networkCheckerThread.start();
      LOGGER.info("Network checker started.");

      final HotspotChecker hotspotChecker = new HotspotChecker();
      final Thread hotspotCheckerThread = new Thread(hotspotChecker);
      hotspotCheckerThread.start();
      LOGGER.info("Hotspot checker started.");

      final DeviceChecker deviceChecker = new DeviceChecker();
      final Thread deviceCheckerThread = new Thread(deviceChecker);
      deviceCheckerThread.start();
      LOGGER.info("Device checker started.");

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        networkChecker.stop();
        LOGGER.info("Network checker stopped.");

        hotspotChecker.stop();
        LOGGER.info("Hotspot checker stopped.");

        deviceChecker.stop();
        LOGGER.info("Device checker stopped.");
      }));
    }
    _started = true;
  }
}
