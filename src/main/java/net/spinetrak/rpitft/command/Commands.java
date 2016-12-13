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

package net.spinetrak.rpitft.command;

import net.spinetrak.rpitft.data.streams.Stream;

public enum Commands
{
  POWER_STATUS()
    {
      private final Command _command = new Command("/power.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    },
  GPS_STATUS()
    {
      private final Command _command = new Command("/gps.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    },
  NMEA_STATUS()
    {
      private final Command _command = new Command("/nmea.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    },
  GPX_NEW()
    {
      private final Command _command = new Command("/gpx.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    },
  NMEA_BACKUP()
    {
      private final Command _command = new Command("/nmea_backup.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    },
  SHUTDOWN()
    {
      private final Command _command = new Command("/shutdown.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    },
  RESTART()
    {
      private final Command _command = new Command("/restart.sh");

      @Override
      public Result execute(final Stream stream_)
      {
        return _command.execute(stream_);
      }
    };

  public abstract Result execute(final Stream stream_);
}
