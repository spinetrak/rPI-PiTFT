package net.spinetrak.rpitft.data;

import javafx.animation.AnimationTimer;
import net.spinetrak.rpitft.ui.Main;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Queue
{
  private ConcurrentLinkedQueue<Device> _deviceQueue;
  private ExecutorService _executor;
  private ConcurrentLinkedQueue<Power> _powerQueue;


  public Queue()
  {
    _deviceQueue = new ConcurrentLinkedQueue<>();
    _powerQueue = new ConcurrentLinkedQueue<>();
    _executor = Executors.newCachedThreadPool(new ThreadFactory()
    {
      @Override
      public Thread newThread(final Runnable runnable_)
      {
        final Thread thread = new Thread(runnable_);
        thread.setDaemon(true);
        return thread;
      }
    });

    final AddToQueue addToQueue = new AddToQueue();
    _executor.execute(addToQueue);
  }

  public void start(final Main main_)
  {
    new AnimationTimer()
    {
      @Override
      public void handle(final long now_)
      {
        main_.addDataToSeries(_powerQueue, _deviceQueue);
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
        Thread.sleep(100);
        _executor.execute(this);
      }
      catch (final InterruptedException ex_)
      {
        ex_.printStackTrace();
      }
    }
  }
}
