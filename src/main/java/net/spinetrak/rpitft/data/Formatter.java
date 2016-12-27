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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class Formatter
{
  final static private DateTimeFormatter _iso8601Formatter = ISODateTimeFormat.dateTime();

  public static String formatAltitude(final float altitude_, final boolean with_)
  {
    return with_ ? String.format("[%.1f m]", altitude_) : String.format("%.1f", altitude_);
  }

  public static String formatCPU(final float cpu_, final boolean with_)
  {
    return with_ ? String.format("[%.2f%% cpu]", cpu_) : String.format("%.2f", cpu_);
  }

  public static String formatDistance(final double distance_, final boolean with_)
  {
    return with_ ? String.format("[%.4f km]", distance_) : String.format("%.4f", distance_);
  }

  public static String formatHD(final float disk_, final boolean with_)
  {
    return with_ ? String.format("[%.2f%% hd]", disk_) : String.format("%.2f", disk_);
  }

  public static String formatISO8601Timestamp(final Date date_)
  {
    final DateTime date = new DateTime(date_);
    return _iso8601Formatter.print(date);
  }

  public static String formatLatitude(final float latitude_)
  {
    return String.format("[%.4f %s]", latitude_, latitude_ > 0 ? "N" : "S");
  }

  public static String formatLongitude(final float longitude_)
  {
    return String.format("[%.4f %s]", longitude_, longitude_ > 0 ? "E" : "W");
  }

  public static String formatMEM(final float memory_, final boolean with_)
  {
    return with_ ? String.format("[%.2f%% mem]", memory_) : String.format("%.2f", memory_);
  }

  public static String formatSpeed(final double speed_, final boolean with_)
  {
    return with_ ? String.format("[%.2f km/h]", speed_) : String.format("%.2f", speed_);
  }

  public static String formatTemperature(final float temperature_, final boolean with_)
  {
    return with_ ? String.format("[%.2f C°]", temperature_) : String.format("%.2f", temperature_);
  }

  public static String formatTrackpoints(final int trackpoints_)
  {
    return String.format("[%d]", trackpoints_);
  }

  public static float round(float number_, int decimalPlaces_)
  {
    BigDecimal bd = new BigDecimal(Float.toString(number_));
    bd = bd.setScale(decimalPlaces_, BigDecimal.ROUND_HALF_UP);
    return bd.floatValue();
  }
}
