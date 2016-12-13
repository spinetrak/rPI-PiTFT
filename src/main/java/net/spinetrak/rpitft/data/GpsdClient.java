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

import de.taimos.gpsd4java.api.ObjectListener;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import de.taimos.gpsd4java.types.DeviceObject;
import de.taimos.gpsd4java.types.DevicesObject;
import de.taimos.gpsd4java.types.ENMEAMode;
import de.taimos.gpsd4java.types.TPVObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

class GPSdClient implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.GpsdClient");
  private static int COUNTER = 0;
  private final ConcurrentLinkedQueue<GPS> _queue = new ConcurrentLinkedQueue<>();

  ConcurrentLinkedQueue<GPS> getQueue()
  {
    return _queue;
  }

  public void run()
  {
    try
    {
      final GPSdEndpoint ep = new GPSdEndpoint("localhost", 2947, new ResultParser());

      ep.addListener(new ObjectListener()
      {
        @Override
        public void handleDevice(final DeviceObject device)
        {
          LOGGER.info("Device: {}", device);
        }

        @Override
        public void handleDevices(final DevicesObject devices)
        {
          for (final DeviceObject d : devices.getDevices())
          {
            LOGGER.info("Device: {}", d);
          }
        }

        @Override
        public void handleTPV(final TPVObject tpv_)
        {
          LOGGER.info("TPV: {}", tpv_);
          if (tpv_.getMode() != ENMEAMode.NotSeen && tpv_.getMode() != ENMEAMode.NoFix)
          {
            COUNTER++;
            _queue.add(GPS.fromTPVObject(tpv_, COUNTER));
          }
        }
      });

      ep.start();

      LOGGER.info("Version: {}", ep.version());

      LOGGER.info("Watch: {}", ep.watch(true, true));

      LOGGER.info("Poll: {}", ep.poll());
      LOGGER.info("gpsd client started: " + ep.version());
      Thread.sleep(60000);
    }
    catch (final IOException | JSONException | InterruptedException ex_)
    {
      LOGGER.error("Error starting gpsd client: " + ex_.getMessage());
    }

  }
}
