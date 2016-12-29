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

import com.alonkadury.initialState.Data;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.spinetrak.rpitft.data.events.Event;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GPS implements Event
{
  public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("HH:mm:ss");
  private float _altitude;
  private double _course;
  private Data[] _data;
  private GpsFixQuality _fix;
  private float _latitude;
  private float _longitude;
  private double _speed;
  private DateTime _time;
  private int _trackpoints;
  private boolean _validLocation;
  private boolean _validMovement;

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
    gps._trackpoints = counter_;
    gps._fix = gga_.getFixQuality();
    gps._validLocation = true;
    gps._validMovement = false;
    return gps;
  }

  static GPS fromRMCSentence(final RMCSentence rmc_)
  {
    final GPS gps = new GPS();
    gps._speed = rmc_.getSpeed() * 1.852;      //knots -> km/h
    gps._course = rmc_.getCourse();
    gps._validMovement = true;
    gps._validLocation = false;
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
    if (Double.compare(gps._course, _course) != 0)
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
    if (Double.compare(gps._speed, _speed) != 0)
    {
      return false;
    }
    if (_trackpoints != gps._trackpoints)
    {
      return false;
    }
    if (_validLocation != gps._validLocation)
    {
      return false;
    }
    if (_validMovement != gps._validMovement)
    {
      return false;
    }
    if (_fix != gps._fix)
    {
      return false;
    }
    return _time.equals(gps._time);

  }

  public float getAltitude()
  {
    return _altitude;
  }

  public double getCourse()
  {
    return _course;
  }

  public GpsFixQuality getFix()
  {
    return _fix;
  }

  public float getLatitude()
  {
    return _latitude;
  }

  public float getLongitude()
  {
    return _longitude;
  }

  public double getSpeed()
  {
    return _speed;
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
    int result;
    long temp;
    result = (_altitude != +0.0f ? Float.floatToIntBits(_altitude) : 0);
    temp = Double.doubleToLongBits(_course);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + _fix.hashCode();
    result = 31 * result + (_latitude != +0.0f ? Float.floatToIntBits(_latitude) : 0);
    result = 31 * result + (_longitude != +0.0f ? Float.floatToIntBits(_longitude) : 0);
    temp = Double.doubleToLongBits(_speed);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + _time.hashCode();
    result = 31 * result + _trackpoints;
    result = 31 * result + (_validLocation ? 1 : 0);
    result = 31 * result + (_validMovement ? 1 : 0);
    return result;
  }

  public boolean isValidLocation()
  {
    return _validLocation;
  }

  public boolean isValidMovement()
  {
    return _validMovement;
  }

  @Override
  public String toString()
  {
    return "GPS{" +
      "_altitude=" + _altitude +
      ", _course=" + _course +
      ", _fix=" + _fix +
      ", _latitude=" + _latitude +
      ", _longitude=" + _longitude +
      ", _speed=" + _speed +
      ", _time=" + _time +
      ", _trackpoints=" + _trackpoints +
      ", _validLocation=" + _validLocation +
      ", _validMovement=" + _validMovement +
      '}';
  }
}
