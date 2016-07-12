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

import net.spinetrak.rpitft.command.Command;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GPS
{
  public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("HH:mm:ss");
  private final static String GPS_STATUS = "/gps.sh";
  private final static String SCRIPT = Command.init(GPS_STATUS);
  private float _altitude;
  private float _latitude;
  private float _longitude;
  private DateTime _time;
  private int _trackpoints;

  GPS()
  {
    String result = "";
    try
    {
      result = Command.execute(SCRIPT);
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
    }
    parse(result);
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
      return degrees + minutes;
    }
    return 0;
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

  private void parse(final String data_)
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

  private void parseAltitude(final String token_)
  {
    if ((null != token_) && !token_.isEmpty())
    {
      _altitude = Float.parseFloat(token_.trim());
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
