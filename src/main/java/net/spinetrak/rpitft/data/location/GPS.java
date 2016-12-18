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

package net.spinetrak.rpitft.data.location;

import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GPS
{
  public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("HH:mm:ss");
  private float _altitude;
  private boolean _hasError = false;
  private float _latitude;
  private float _longitude;
  private DateTime _time;
  private int _trackpoints;

  private GPS()
  {

  }


  static GPS fromGGASentence(final GGASentence gga_, final int counter_)
  {
    final GPS gps = new GPS();
    gps._time = new DateTime(gga_.getTime().getMilliseconds());
    gps._longitude = (float) gga_.getPosition().getLongitude();
    gps._altitude = (float) gga_.getPosition().getAltitude();
    gps._latitude = (float) gga_.getPosition().getLatitude();
    final GpsFixQuality fix = gga_.getFixQuality();
    gps._hasError = GpsFixQuality.INVALID == fix;
    gps._trackpoints = counter_;
    return gps;
  }

  @Override
  public boolean equals(final Object o_)
  {
    if (this == o_)
    {
      return true;
    }
    if (!(o_ instanceof GPS))
    {
      return false;
    }

    final GPS gps = (GPS) o_;

    if (Float.compare(gps._altitude, _altitude) != 0)
    {
      return false;
    }
    if (_hasError != gps._hasError)
    {
      return false;
    }
    if (Float.compare(gps._latitude, _latitude) != 0)
    {
      return false;
    }
    if (Float.compare(gps._longitude, _longitude) != 0)
    {
      return false;
    }
    if (_trackpoints != gps._trackpoints)
    {
      return false;
    }
    return _time != null ? _time.equals(gps._time) : gps._time == null;

  }

  public float getAltitude()
  {
    return _altitude;
  }

  public float getLatitude()
  {
    return _latitude;
  }

  public float getLongitude()
  {
    return _longitude;
  }

  public DateTime getTime()
  {
    return _time;
  }

  public int getTrackpoints()
  {
    return _trackpoints;
  }

  @Override
  public int hashCode()
  {
    int result = (_altitude != +0.0f ? Float.floatToIntBits(_altitude) : 0);
    result = 31 * result + (_latitude != +0.0f ? Float.floatToIntBits(_latitude) : 0);
    result = 31 * result + (_longitude != +0.0f ? Float.floatToIntBits(_longitude) : 0);
    return result;
  }

  @Override
  public String toString()
  {
    return "GPS{" +
      "_altitude=" + _altitude +
      ", _latitude=" + _latitude +
      ", _longitude=" + _longitude +
      ", _time=" + _time +
      ", _trackpoints=" + _trackpoints +
      '}';
  }
}
