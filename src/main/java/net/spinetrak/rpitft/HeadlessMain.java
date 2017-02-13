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

package net.spinetrak.rpitft;/*
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

import net.spinetrak.rpitft.data.EventChecker;
import net.spinetrak.rpitft.data.streams.logger.InitialStateStreamLogger;
import net.spinetrak.rpitft.data.streams.logger.NmeaFileLogger;
import net.spinetrak.rpitft.web.RPIWebHttpServer;
import net.spinetrak.rpitft.web.RPIWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HeadlessMain
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.HeadlessMain");

  public static void main(final String[] args_)
  {
    LOGGER.info("Starting headless.");
    try
    {
      RPIWebHttpServer.start();
      LOGGER.info("RPIWebHttpServer started.");

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        RPIWebHttpServer.stop();
        LOGGER.info("RPIWebHttpServer stopped.");
      }));

      final RPIWebSocketServer rpiWebSocketServer = RPIWebSocketServer.getInstance();
      LOGGER.info("RPIWebSocketServer started: " + rpiWebSocketServer);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try
        {
          rpiWebSocketServer.stop();
          LOGGER.info("RPI WebSocketServer stopped.");
        }
        catch (final IOException | InterruptedException ex_)
        {
          LOGGER.error(ex_.getMessage());
        }
      }));
    }
    catch (final IOException | InterruptedException ex_)
    {
      LOGGER.error(ex_.getMessage());
    }

    final HeadlessTimer headlessTimer = new HeadlessTimer();
    final Thread headlessTimerThread = new Thread(headlessTimer);
    headlessTimerThread.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      headlessTimer.stop();
      LOGGER.info("Headless timer stopped.");
    }));

    final EventChecker eventChecker = EventChecker.getInstance();
    eventChecker.start();
    LOGGER.info("Event checker started.");

    final InitialStateStreamLogger iss = InitialStateStreamLogger.getInstance();
    LOGGER.info("Started logging to " + iss.toString());

    final NmeaFileLogger nmeaLogger = NmeaFileLogger.getInstance();
    nmeaLogger.start();
    LOGGER.info("Started logging to file.");
  }
}
