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

package com.alonkadury.initialState;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class API
{
  private final static String BUCKET_KEY = "X-IS-BucketKey";
  private String accessKey;
  private ExecutorService executor;
  private Gson gson; // thread safe according to docs

  public API(String accessKey)
  {
    this(accessKey, 0);
  }

  public API(String accessKey, int threadExecuterSize)
  {
    this.accessKey = accessKey;
    if (threadExecuterSize > 0)
    {
      executor = Executors.newFixedThreadPool(5);
    }
    else
    {
      executor = new SameThreadExecuter();
    }

    gson = new Gson();
  }

  public void createBucket(Bucket bucket)
  {
    executor.execute(new EventTask(accessKey, bucket.getEndpoint(), null, gson.toJson(bucket)));
  }

  public void createBulkData(Bucket bucket, Data[] bulkData)
  {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put(BUCKET_KEY, bucket.getKey());
    executor.execute(new EventTask(accessKey, bulkData[0].getEndpoint(), hash, gson.toJson(bulkData)));
  }

  public void createData(Bucket bucket, Data data)
  {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put(BUCKET_KEY, bucket.getKey());
    executor.execute(new EventTask(accessKey, data.getEndpoint(), hash, gson.toJson(data)));
  }

  public void terminate()
  {
    executor.shutdown();
  }

  private class SameThreadExecuter implements ExecutorService
  {

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
      return false;
    }

    @Override
    public void execute(Runnable command)
    {
      command.run();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
      return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws
                                                                                                               InterruptedException
    {
      return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
      return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws
                                                                                                 InterruptedException,
                                                                                                 ExecutionException,
                                                                                                 TimeoutException
    {
      return null;
    }

    @Override
    public boolean isShutdown()
    {
      return false;
    }

    @Override
    public boolean isTerminated()
    {
      return false;
    }

    @Override
    public void shutdown()
    {
    }

    @Override
    public List<Runnable> shutdownNow()
    {
      return null;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
      return null;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result)
    {
      return null;
    }

    @Override
    public Future<?> submit(Runnable task)
    {
      return null;
    }
  }
}
