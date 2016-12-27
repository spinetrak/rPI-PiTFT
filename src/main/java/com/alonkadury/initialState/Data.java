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

public class Data<T> implements Events
{
  private final static String DATA_API_URL = API_BASEURL + "events";
  private String iso8601;
  private String key;
  private T value;

  public Data(String key, T value, String iso8601Time)
  {
    this.key = key;
    this.value = value;
    this.iso8601 = iso8601Time;
  }

  public Data(String key, T value)
  {
    this(key, value, null);
  }

  public String getEndpoint()
  {
    return DATA_API_URL;
  }

  @Override
  public String toString()
  {
    return "Data{" +
      "key='" + key + '\'' +
      ", value=" + value +
      ", iso8601='" + iso8601 + '\'' +
      "}\n";
  }
}
