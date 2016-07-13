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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GPS
{
  public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("HH:mm:ss");
  private final static String GPS_STATUS = "/gps.sh";
  private static final int MAX_POINTS = 320;
  private final static String NMEA_FILE = "/home/pi/tracks/nmea.txt";
  private final static String SCRIPT = Command.init(GPS_STATUS);
  private float _altitude;
  private float _latitude;
  private float _longitude;
  private DateTime _time;
  private int _trackpoints;

  static GPS fromCommand()
  {
    GPS gps = null;
    try
    {
      gps = new GPS();
      gps.parse(new Command(new ByteArrayOutputStream()).execute(SCRIPT).resultAsString());
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
    }
    return gps;
  }
  
  static GPS fromNMEA(final String nmea_)
  {
    final GPS gps = new GPS();
    gps.parseNmea(line_);
    return gps;
  }

  private GPS(final String line_)
  {
    parseNmea(line_);
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

  class OutputCollectingStream extends LogOutputStream 
  {
    private final List<GPS> _gps = new LinkedList<GPS>();
  
    @Override protected void processLine(String line_, int level_) 
    {
      _gps.add(GPS.fromNMEA(line_));
    }   
    public List<GPS> getGPS() 
    {
      return _gps;
    }
  
  public static List<GPS> getHistoricalData()
  {
    final List<GPS> list = new ArrayList<>();

    final GPS gps = GPS.fromCommand();
    final int points = gps.getTrackpoints();
    final int steps = points / MAX_POINTS;
    if (points == 0 || steps == 0)
    {
      return list;
    }

    try
    {
      final List<GPS> results = new Command(new OutputCollectingStream()).execute(SCRIPT).resultAsList();
      for(final String line : lines)
      {
        list.addAll(results);
      }
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
    }

/*
    try (final BufferedReader reader = new BufferedReader(new FileReader(NMEA_FILE)))
    {
      int count = 0;
      String line;
      while ((line = reader.readLine()) != null)
      {
        if (line.contains("GGA"))
        {
          if ((count % steps) == 0 && list.size() <= MAX_POINTS)
          {
            final GPS gps = new GPS(line);
            if (!list.contains(gps))
            {
              list.add(gps);
            }
          }
          count++;
        }
      }
    }
    catch (final IOException ex_)
    {
      ex_.printStackTrace();
    }
    */
    return list;
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

    if (Float.compare(gps._altitude, _altitude) != 0)
    {
      return false;
    }
    if (Float.compare(gps._latitude, _latitude) != 0)
    {
      return false;
    }
    return Float.compare(gps._longitude, _longitude) == 0;
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

  private void parseNmea(final String line_)
  {
    final String[] tokens = line_.split(",");
    final String[] time = tokens[1].split("\\.");
    parseTime(time[0]);
    _latitude = parseCoordinates(tokens[2] + "N");
    _longitude = parseCoordinates(tokens[4] + "E");
    parseAltitude(tokens[9]);

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
