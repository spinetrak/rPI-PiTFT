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

package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.command.Result;
import net.spinetrak.rpitft.data.streams.SingleLineStream;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.spinetrak.rpitft.command.Commands.GPS_STATUS;

public class GPS
{
  public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("HH:mm:ss");
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.GPS");
  private float _altitude;
  private boolean _hasError = false;
  private float _latitude;
  private float _longitude;
  private DateTime _time;
  private int _trackpoints;

  GPS()
  {

  }

  static GPS fromString(final String data_)
  {
    final GPS gps = new GPS();
    gps._hasError = !gps.parseGPS(data_);
    return gps;
  }

  boolean isHasError()
  {
    return _hasError;
  }

  float parseCoordinates(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.length() == 10 || token_.length() == 11) && token_.contains(
      "."))
    {
      final String eastwestnorthsouth = token_.substring(token_.length() - 1);
      final String nmeaParts = token_.substring(0, token_.length() - 1);
      final int decimal = nmeaParts.indexOf(".");

      final int degrees = Integer.parseInt(nmeaParts.substring(0, decimal - 2));
      final float minutes = Float.parseFloat(nmeaParts.substring(decimal - 2)) / 60;
      final float result = degrees + minutes;
      if ("S".equals(eastwestnorthsouth) || "W".equals(eastwestnorthsouth))
      {
        return result * -1;
      }
      return result;
    }
    return -1000;
  }

  void query()
  {
    try
    {
      final Result result = GPS_STATUS.execute(new SingleLineStream());
      _hasError = 0 != result.getResult() || !parseGPS(result.resultAsString());
    }
    catch (final Exception ex_)
    {
      _hasError = true;
      LOGGER.error(ex_.getMessage());
    }
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

  private boolean parseAltitude(final String token_)
  {
    if ((null != token_) && !token_.isEmpty())
    {
      _altitude = Float.parseFloat(token_.trim());
      return true;
    }
    return false;
  }

  /*
  echo "${time}/${latitude}${northsouth}/${longitude}${eastwest}/${points}/${altitude}"
   */
  private boolean parseGPS(final String data_)
  {
    final String tokens[] = data_.split("/");
    if (tokens.length == 5)
    {
      boolean parsedTime = parseTime(tokens[0]);
      boolean parsedTrackpoints = parseTrackpoints(tokens[3]);
      boolean parsedAltitude = parseAltitude(tokens[4]);
      _latitude = parseCoordinates(tokens[1]);
      _longitude = parseCoordinates(tokens[2]);

      return parsedTime && parsedAltitude && parsedTrackpoints && _latitude != -1000 && _longitude != -1000;
    }
    return false;
  }

  private boolean parseNmea(final String line_)
  {
    if (null != line_ && !line_.isEmpty())
    {
      final String[] tokens = line_.split(",");
      if (tokens.length > 10)
      {
        final String[] time = tokens[1].split("\\.");
        boolean parsedTime = parseTime(time[0]);
        boolean parsedAltitude = parseAltitude(tokens[9]);
        _latitude = parseCoordinates(tokens[2] + tokens[3]);
        _longitude = parseCoordinates(tokens[4] + tokens[5]);

        return parsedTime && parsedAltitude && _latitude != -1000 && _longitude != -1000;
      }
    }
    return false;
  }

  private boolean parseTime(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.length() == 6))
    {
      final String[] tokens = token_.split("(?<=\\G..)");
      _time = DTF.parseDateTime(tokens[0] + ":" + tokens[1] + ":" + tokens[2]);
      return true;
    }
    return false;
  }

  private boolean parseTrackpoints(final String token_)
  {
    if ((null != token_) && !token_.isEmpty())
    {
      _trackpoints = Integer.parseInt(token_.replaceAll("[\\D]", ""));
      if (_trackpoints > 0)
      {
        return true;
      }
    }
    return false;
  }
}
