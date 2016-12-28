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

public class Bucket implements Events
{
  private final static String BUCKET_API_URL = API_BASEURL + "buckets";
  private final String _bucketKey;
  private final String _bucketName;

  public Bucket(final String bucketKey_)
  {
    this(bucketKey_, null);
  }

  public Bucket(final String bucketKey_, final String friendlyName_)
  {
    _bucketKey = bucketKey_;
    _bucketName = friendlyName_;
  }

  String getFriendlyName()
  {
    return _bucketName;
  }

  String getKey()
  {
    return _bucketKey;
  }

  public String getEndpoint()
  {
    return BUCKET_API_URL;
  }
}
