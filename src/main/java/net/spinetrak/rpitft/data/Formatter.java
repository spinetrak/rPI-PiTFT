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

public class Formatter
{


  public static String formatAltitude(final float altitude_)
  {
    return String.format("[%.1f m]", altitude_);
  }

  public static String formatCPU(final float cpu_)
  {
    return String.format("[%.2f%% cpu]", cpu_);
  }

  public static String formatDistance(final double distance_)
  {
    return String.format("[%.4f km]", distance_);
  }

  public static String formatHD(final float disk_)
  {
    return String.format("[%.2f%% hd]", disk_);
  }

  public static String formatLatitude(final float latitude_)
  {
    return String.format("[%.4f %s]", latitude_, latitude_ > 0 ? "N" : "S");
  }

  public static String formatLongitude(final float longitude_)
  {
    return String.format("[%.4f %s]", longitude_, longitude_ > 0 ? "E" : "W");
  }

  public static String formatMEM(final float memory_)
  {
    return String.format("[%.2f%% mem]", memory_);
  }

  public static String formatSpeed(final double speed_)
  {
    return String.format("[%.2f km/h]", speed_);
  }

  public static String formatTemperature(final float temperature_)
  {
    return String.format("[%.2f C°]", temperature_);
  }

  public static String formatTrackpoints(final int trackpoints_)
  {
    return String.format("[%d]", trackpoints_);
  }
}
