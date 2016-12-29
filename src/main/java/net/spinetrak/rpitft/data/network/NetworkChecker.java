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

package net.spinetrak.rpitft.data.network;

import net.spinetrak.rpitft.data.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkChecker implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "net.spinetrak.rpitft.data.netweok.NetworkChecker");

  @Override
  public void run()
  {
    while (true)
    {
      try
      {
        final Network network = new Network();
        Dispatcher.getInstance().getQueue().add(network);
        Thread.sleep(15000);
      }
      catch (final InterruptedException ex_)
      {
        LOGGER.error(ex_.getMessage());
      }
    }
  }
}
