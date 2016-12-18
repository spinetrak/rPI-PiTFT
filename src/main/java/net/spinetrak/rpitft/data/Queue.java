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

import javafx.animation.AnimationTimer;
import net.spinetrak.rpitft.data.location.NmeaLogger;
import net.spinetrak.rpitft.data.raspberry.DeviceClient;
import net.spinetrak.rpitft.ui.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Queue
{
  private final DeviceClient _deviceClient;
  private final ExecutorService _executor;
  private final NmeaLogger _nmeaLogger;


  public Queue()
  {
    //_powerQueue = new ConcurrentLinkedQueue<>();

    _executor = Executors.newCachedThreadPool(runnable_ -> {
      final Thread thread = new Thread(runnable_);
      thread.setDaemon(true);
      return thread;
    });

    _deviceClient = new DeviceClient(_executor);
    _executor.execute(_deviceClient);

    _nmeaLogger = new NmeaLogger();

    //final AddToGPSQueue addToGPSQueue = new AddToGPSQueue();
    //_executor.execute(addToGPSQueue);

    //final AddToPowerQueue addToPowerQueue = new AddToPowerQueue();
    //_executor.execute(addToPowerQueue);

  }

  public void start(final Main main_)
  {
    new AnimationTimer()
    {
      @Override
      public void handle(final long now_)
      {
        main_.addDataToSeries(_deviceClient.getQueue(), _nmeaLogger.getQueue());
      }
    }.start();
  }

  /*
  private class AddToGPSQueue implements Runnable
  {
    public void run()
    {
      try
      {
        final GPS gps = new GPS();
        gps.query();
        if (!gps.isHasError())
        {
          _gpsQueue.add(gps);
        }
        Thread.sleep(500);
        _executor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        LOGGER.error(ex_.getMessage());
      }
    }
  }

  private class AddToPowerQueue implements Runnable
  {
    public void run()
    {
      try
      {
        final Power power = new Power()
        power.query();
        if(!power.haError())
        {
          _powerQueue.add(new Power());
        }
        Thread.sleep(500);
        _executor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        LOGGER.error(ex_.getMessage());
      }
    }
  }
  */
}
