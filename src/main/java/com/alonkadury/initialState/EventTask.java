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

import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

class EventTask implements Runnable
{

  private final static String ACCEPT_VERSION_KEY = "Accept-Version";
  private final static String ACCEPT_VERSION_VALUE = "~0";
  private final static String BUCKET_KEY = "X-IS-BucketKey";
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "com.alonkadury.initialState.EventTask");
  private final static String X_ACCESS_KEY = "X-IS-AccessKey";
  private final String _accessKey;
  private final String _body;
  private final String _bucketKey;
  private final String _endpoint;

  EventTask(final String accessKey_, final String endpoint_, final String bucketKey_, final String body_)
  {
    _accessKey = accessKey_;
    _endpoint = endpoint_;
    _bucketKey = bucketKey_;
    _body = body_;
  }

  @Override
  public void run()
  {
    post();
  }

  private void post()
  {
    final Client client = ClientBuilder.newClient();
    final Entity payload = Entity.json(_body);

    final Invocation.Builder builder = client.target(_endpoint)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .header(X_ACCESS_KEY, _accessKey)
      .header(ACCEPT_VERSION_KEY, ACCEPT_VERSION_VALUE);
    if (_bucketKey != null)
    {
      builder.header(BUCKET_KEY, _bucketKey);
    }

    try
    {
      final Response response = builder.post(payload);

      final int status = response.getStatus();
      if (status < 200 || status >= 300)
      {
        throw new Exception(String.format("InitialState call returned: %d", status));
      }
    }
    catch (final Exception ex_)
    {
      final String msg = ex_.getMessage();
      final Network network = new Network(msg);
      Dispatcher.getInstance().getQueue().add(network);
      LOGGER.error(msg);
    }
  }

}
