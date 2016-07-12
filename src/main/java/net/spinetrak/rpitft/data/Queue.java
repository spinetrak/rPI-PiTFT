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
import net.spinetrak.rpitft.ui.Main;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Queue
{
  private final ConcurrentLinkedQueue<Device> _deviceQueue;
  private final ExecutorService _executor;
  private final ExecutorService _gpsExecutor;
  private final ConcurrentLinkedQueue<GPS> _gpsQueue;
  private final ConcurrentLinkedQueue<Power> _powerQueue;


  public Queue()
  {
    _deviceQueue = new ConcurrentLinkedQueue<>();
    _powerQueue = new ConcurrentLinkedQueue<>();
    _gpsQueue = new ConcurrentLinkedQueue<>();
    _executor = Executors.newCachedThreadPool(runnable_ -> {
      final Thread thread = new Thread(runnable_);
      thread.setDaemon(true);
      return thread;
    });
    final AddToQueue addToQueue = new AddToQueue();
    _executor.execute(addToQueue);

    _gpsExecutor = Executors.newCachedThreadPool(runnable_ -> {
      final Thread thread = new Thread(runnable_);
      thread.setDaemon(true);
      return thread;
    });
    final AddToGPSQueue addToGPSQueue = new AddToGPSQueue();
    _executor.execute(addToGPSQueue);

  }

  public void start(final Main main_)
  {
    new AnimationTimer()
    {
      @Override
      public void handle(final long now_)
      {
        main_.addDataToSeries(_powerQueue, _deviceQueue, _gpsQueue);
      }
    }.start();
  }

  private class AddToQueue implements Runnable
  {
    public void run()
    {
      try
      {
        _powerQueue.add(new Power());
        _deviceQueue.add(new Device());
        Thread.sleep(200);
        _executor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        ex_.printStackTrace();
      }
    }
  }

  private class AddToGPSQueue implements Runnable
  {
    public void run()
    {
      try
      {
        final GPS gps = new GPS();
        gps.parseCommand();
        _gpsQueue.add(gps);
        Thread.sleep(60000);
        _gpsExecutor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        ex_.printStackTrace();
      }
    }
  }
}
