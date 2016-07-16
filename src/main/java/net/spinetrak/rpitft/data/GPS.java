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
  public static final int MAX_POINTS = 480;
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.GPS");
  private float _altitude;
  private float _latitude;
  private float _longitude;
  private DateTime _time;
  private int _trackpoints;

  GPS()
  {
    try
    {
      parseGPS(GPS_STATUS.execute(new SingleLineStream()).resultAsString());
    }
    catch (final Exception ex_)
    {
      LOGGER.error(ex_.getMessage());
    }
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
    return 0;
  }


  @Override
  public boolean equals(final Object obj_)
  {
    if (this == obj_)
    {
      return true;
    }
    if (!(obj_ instanceof GPS))
    {
      return false;
    }

    final GPS gps = (GPS) obj_;

    return !((Float.compare(gps._altitude, _altitude) != 0) || (Float.compare(gps._latitude,
                                                                              _latitude) != 0) || Float.compare(
      gps._longitude, _longitude) != 0);
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

  private void parseAltitude(final String token_)
  {
    if ((null != token_) && !token_.isEmpty())
    {
      _altitude = Float.parseFloat(token_.trim());
    }
  }

  private void parseGPS(final String data_)
  {
    final String tokens[] = data_.split("/");
    if (tokens.length == 5)
    {
      parseTime(tokens[0]);

      _latitude = parseCoordinates(tokens[1]);

      _longitude = parseCoordinates(tokens[2]);

      parseTrackpoints(tokens[3]);
      parseAltitude(tokens[4]);
    }
  }

  private void parseNmea(final String line_)
  {
    if(null != line_ && !line_.isEmpty())
    {
      final String[] tokens = line_.split(",");
      if (tokens.length > 10)
      {
        final String[] time = tokens[1].split("\\.");
        parseTime(time[0]);
        _latitude = parseCoordinates(tokens[2] + tokens[3]);
        _longitude = parseCoordinates(tokens[4] + tokens[5]);
        parseAltitude(tokens[9]);
      }
    }
  }

  private void parseTime(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.length() == 6))
    {
      final String[] tokens = token_.split("(?<=\\G..)");
      _time = DTF.parseDateTime(tokens[0] + ":" + tokens[1] + ":" + tokens[2]);
    }
  }

  private void parseTrackpoints(final String token_)
  {
    if ((null != token_) && !token_.isEmpty())
    {
      _trackpoints = Integer.parseInt(token_.replaceAll("[\\D]", ""));
    }
  }
}
