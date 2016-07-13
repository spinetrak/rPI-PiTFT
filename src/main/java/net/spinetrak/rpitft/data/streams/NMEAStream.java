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

package net.spinetrak.rpitft.data.streams;

import net.spinetrak.rpitft.data.GPS;
import org.apache.commons.exec.LogOutputStream;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class NMEAStream extends LogOutputStream implements Stream
{
  private final List<GPS> _nmea = new LinkedList<>();
  private final int _steps;
  private int _count;

  public NMEAStream(final int steps_)
  {
    super();
    _steps = steps_;
  }

  @Override
  public OutputStream getStream()
  {
    return this;
  }

  @Override
  public List toList()
  {
    return _nmea;
  }

  @Override
  protected void processLine(final String line_, int level_)
  {
    if ((_count % _steps) == 0 && _nmea.size() <= GPS.MAX_POINTS)
    {
      final GPS gps = GPS.fromNMEA(line_);
      if (!_nmea.contains(gps))
      {
        _nmea.add(gps);
      }
    }
    _count++;
  }
}

