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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.HashMap;

class EventTask implements Runnable
{

  private final static String ACCEPT_VERSION_KEY = "Accept-Version";
  private final static String ACCEPT_VERSION_VALUE = "~0";
  private final static String CONTENT_TYPE_KEY = "Content-Type";
  private final static String CONTENT_TYPE_VALUE = "application/json";
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "com.alonkadury.initialState.EventTask");
  private final static String METHOD_TYPE = "POST";
  private final static String X_ACCESS_KEY = "X-IS-AccessKey";
  private String accessKey;
  private String body;
  private HashMap<String, String> customHeaders;
  private String endpoint;

  public EventTask(String accessKey, String endpoint, HashMap<String, String> customHeaders, String body)
  {
    this.accessKey = accessKey;
    this.endpoint = endpoint;
    this.customHeaders = customHeaders;
    this.body = body;
  }

  @Override
  public void run()
  {
    try
    {
      sendRequest();
    }
    catch (Exception ex)
    {
      Thread t = Thread.currentThread();
      t.getUncaughtExceptionHandler().uncaughtException(t, ex);
    }
  }

  private void sendRequest() throws Exception
  {
    URL url = new URL(endpoint);
    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

    //add reuqest headers
    con.setRequestMethod(METHOD_TYPE);
    con.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
    con.setRequestProperty(X_ACCESS_KEY, accessKey);
    con.setRequestProperty(ACCEPT_VERSION_KEY, ACCEPT_VERSION_VALUE);
    if (customHeaders != null)
    {
      customHeaders.forEach((k, v) -> con.setRequestProperty(k, v));
    }

    con.setDoOutput(true);
    con.setDoInput(true);

    DataOutputStream writer = new DataOutputStream(con.getOutputStream());

    writer.writeBytes(body);
    writer.flush();
    writer.close();


    int responseCode = con.getResponseCode();
    con.disconnect();

    if (responseCode < 200 || responseCode >= 300)
    {
      throw new Exception(String.format("InitialState call returned: %d", responseCode));
    }

    //      InputStream is = con.getInputStream();
    //      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    //      String line;
    //      StringBuffer response = new StringBuffer();
    //      while((line = reader.readLine()) != null) {
    //        response.append(line);
    //        response.append('\r');
    //      }
    //      reader.close();
  }
}
