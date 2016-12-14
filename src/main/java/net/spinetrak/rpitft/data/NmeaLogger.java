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

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;

class NmeaLogger
{
  private final DateTimeFormatter _dateTimeFormatter = DateTimeFormat.forPattern("HHmmss");
  private PrintStream _printStream;

  NmeaLogger(final String path_)
  {
    try
    {
      _printStream = new PrintStream(
        new BufferedOutputStream(new FileOutputStream(path_)),
        false,
        "US-ASCII");
    }
    catch (final UnsupportedEncodingException | FileNotFoundException ex_)
    {
      ex_.printStackTrace();
      IOUtils.closeQuietly(_printStream);
      _printStream = null;
    }
    finally
    {
      if (_printStream != null)
      {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          IOUtils.closeQuietly(_printStream);
        }));
      }
    }
  }

  void writeGGASentence(final GPS gps_)
  {
    if (_printStream != null)
    {
      // Documentation for NMEA Standard 0183
      // taken from http://www.gpsinformation.org/dale/nmea.htm#GGA
      StringBuilder sb = new StringBuilder();
      sb.append("GP");
      // Global Positioning System Fix Data
      sb.append("GGA");
      sb.append(",");
      // Fix taken at "HHMMSS" UTC
      sb.append(formatTime(gps_.getTime()));
      sb.append(",");
      // Latitude "DDMM.MMM,[N|S]"
      sb.append(formatLatitude(gps_.getLatitude()));
      sb.append(",");
      // Longitude "DDDMM.MMM,[E|W]"
      sb.append(formatLongitude(gps_.getLongitude()));
      sb.append(",");
      // Fix quality: 0 = invalid
      //              1 = GPS fix (SPS)
      //              2 = DGPS fix
      //              3 = PPS fix
      //              4 = Real Time Kinematic
      //              5 = Float RTK
      //              6 = estimated (dead reckoning) (2.3 feature)
      //              7 = Manual input mode
      //              8 = Simulation mode
      sb.append("7"); // Intentionally left blank.
      sb.append(",");
      // Number of satellites being tracked
      sb.append("00"); // Intentionally left blank.
      sb.append(",");
      // Horizontal dilution of position
      sb.append(""); // Intentionally left blank
      sb.append(",");
      // Altitude, Meters, above mean sea level
      sb.append(formatElevation(gps_.getAltitude()));
      sb.append(",");
      // Height of geoid (mean sea level) above WGS84 ellipsoid
      sb.append(",M"); // Intentionally left blank.
      // time in seconds since last DGPS update
      sb.append(""); // Intentionally left blank.
      sb.append(",");
      // DGPS station ID number
      sb.append(""); // Intentionally left blank.
      sb.append(",");
      // the checksum data, always begins with *
      int chksum = computeChecksum(sb, 0, sb.length());
      sb.append("*");
      sb.append(formatChecksum(chksum));

      _printStream.print("$");
      _printStream.print(sb);
      _printStream.print("\r\n");
      _printStream.flush();
    }
  }

  private int computeChecksum(final CharSequence sequence_, final int start_, final int end_)
  {
    int chksum = 0;
    for (int i = start_; i < end_; i++)
    {
      int c = 0xFF & (int) sequence_.charAt(i);
      chksum ^= c;
    }
    return chksum;
  }

  private String formatChecksum(final int checksum_)
  {
    return Integer.toHexString(checksum_).toUpperCase();
  }

  private String formatElevation(final double metersElevation_)
  {
    // Format elevation with 1 digit of precision.
    // This provides decimeter resolution.
    return String.format("%.1f,M", metersElevation_);
  }

  private String formatLatitude(final double degrees_)
  {
    int d = (int) Math.floor(Math.abs(degrees_));
    double m = 60 * (Math.abs(degrees_) - d);
    // Format latitude as "DDMM.MMM[N|S]"
    return String.format("%02d%06.3f,%s", d, m, degrees_ < 0 ? "S" : "N");
  }

  private String formatLongitude(final double degrees_)
  {
    int d = (int) Math.floor(Math.abs(degrees_));
    double m = 60 * (Math.abs(degrees_) - d);
    // Format longitude as "DDDMM.MMM[N|S]"
    return String.format("%03d%06.3f,%s", d, m, degrees_ < 0 ? "W" : "E");
  }

  private String formatTime(final DateTime time_)
  {
    // Format time as "HHMMSS"
    return (time_ != null) ? _dateTimeFormatter.print(time_) : "";
  }
}
