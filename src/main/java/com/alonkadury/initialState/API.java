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
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.listeners.NetworkListener;
import net.spinetrak.rpitft.data.raspberry.Network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class API implements NetworkListener
{
  private final String _accessKey;
  private final ExecutorService _executor;
  private final Gson _gson; // thread safe according to docs
  private boolean _networkIsUp = false;

  public API(final String accessKey_)
  {
    this(accessKey_, 2);
  }

  public API(final String accessKey_, int threadExecuterSize_)
  {
    _accessKey = accessKey_;
    _executor = Executors.newFixedThreadPool(threadExecuterSize_);
    _gson = new Gson();
    Dispatcher.getInstance().addListener(this);
  }

  public void createBucket(final Bucket bucket_)
  {
    if (_networkIsUp)
    {
      _executor.execute(new EventTask(_accessKey, bucket_.getEndpoint(), null, _gson.toJson(bucket_)));
    }
  }

  public void createBulkData(final Bucket bucket_, final Data[] bulkData_)
  {
    if (_networkIsUp)
    {
      _executor.execute(
        new EventTask(_accessKey, bulkData_[0].getEndpoint(), bucket_.getKey(), _gson.toJson(bulkData_)));
    }
  }

  public void createData(final Bucket bucket_, final Data data_)
  {
    if (_networkIsUp)
    {
      _executor.execute(new EventTask(_accessKey, data_.getEndpoint(), bucket_.getKey(), _gson.toJson(data_)));
    }
  }

  @Override
  public void handleData(final Network network_)
  {
    _networkIsUp = network_.isUp();
  }

  public void terminate()
  {
    _executor.shutdown();
  }

}
