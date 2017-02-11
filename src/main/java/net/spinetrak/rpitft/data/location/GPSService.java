/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 spinetrak
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

package net.spinetrak.rpitft.data.location;

import java.util.ArrayList;
import java.util.List;

public class GPSService
{
  private static final int MAX_LENGTH = 60 * 60; //1 hour

  private final List<GPS> _gpsCoordinates = new ArrayList<>();

  public void addGPS(final GPS gps_)
  {
    if (gps_.isValidLocation())
    {
      _gpsCoordinates.add(gps_);
      if (_gpsCoordinates.size() >= MAX_LENGTH)
      {
        _gpsCoordinates.remove(0);
      }
    }
  }

  public GPS getFinish()
  {
    return _gpsCoordinates.get(_gpsCoordinates.size() - 1);
  }

  public List<GPS> getGPS()
  {
    return _gpsCoordinates;
  }

  public GPS getStart()
  {
    return _gpsCoordinates.get(0);
  }

}
