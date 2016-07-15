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
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class Command
{
  private final static String DEVICE_STATUS = "/device.sh";
  private final static String POWER_STATUS = "/power.sh";
  private final static String GPS_STATUS = "/gps.sh";
  private final static String NMEA_STATUS = "/nmea.sh";
  private final static String GPX_NEW = "/gpx.sh";
  private final static String NMEA_BACKUP = "/nmea_backup.sh";
  
  public enum Commands
  {
    DEVICE_STATUS() 
    {
      @Override
      public Result execute(final Stream stream_) 
      { 
        return new Command(stream_).execute(Command.init(DEVICE_STATUS));
      }
    },
    POWER_STATUS() 
    {
      @Override
      public Result execute(final Stream stream_) 
      { 
        return new Command(stream_).execute(Command.init(POWER_STATUS));
      }
    },
    GPS_STATUS() 
    {
      @Override
      public Result execute(final Stream stream_) 
      { 
        return new Command(stream_).execute(Command.init(GPS_STATUS));
      }
    },
    NMEA_STATUS() 
    {
      @Override
      public Result execute(final Stream stream_) 
      { 
        return new Command(stream_).execute(Command.init(NMEA_STATUS));
      }
    },
    GPX_NEW() 
    {
      @Override
      public Result execute(final Stream stream_) 
      { 
        return new Command(stream_).execute(Command.init(GPX_NEW));
      }
    },
    NMEA_BACKUP() 
    {
      @Override
      public Result execute(final Stream stream_) 
      { 
        return new Command(stream_).execute(Command.init(NMEA_BACKUP));
      }
    },;
     
    public abstract Result execute(final Stream stream_);
  }
  private final Stream _stream;

  private Command(final Stream stream_)
  {
    _stream = stream_;
  }

  Stream getStream()
  {
    return _stream;
  }

  private static String init(final String script_)
  {
    final String VAR_TMP = "/var/tmp";
    InputStream scriptIn = null;
    OutputStream scriptOut = null;
    final String script = VAR_TMP + script_;
    try
    {
      scriptIn = Command.class.getResourceAsStream(script_);
      scriptOut = new FileOutputStream(script);

      IOUtils.copy(scriptIn, scriptOut);

      final Set<PosixFilePermission> perms = new HashSet<>();
      perms.add(PosixFilePermission.OWNER_EXECUTE);
      perms.add(PosixFilePermission.GROUP_EXECUTE);
      perms.add(PosixFilePermission.OWNER_READ);
      perms.add(PosixFilePermission.GROUP_READ);
      perms.add(PosixFilePermission.OWNER_WRITE);
      perms.add(PosixFilePermission.GROUP_WRITE);

      Files.setPosixFilePermissions(Paths.get(script), perms);
    }
    catch (final IOException ex_)
    {
      System.err.println(ex_.getMessage());
      ex_.printStackTrace();
    }
    finally
    {
      if (scriptIn != null)
      {
        try
        {
          scriptIn.close();
        }
        catch (final IOException ex_)
        {
          ex_.printStackTrace();
        }
      }
      if (scriptOut != null)
      {
        try
        {
          scriptOut.close();
        }
        catch (final IOException ex_)
        {
          ex_.printStackTrace();
        }
      }
    }
    return script;
  }

  private Result execute(final String command_)
  {
    final CommandLine commandline = CommandLine.parse(command_);
    final DefaultExecutor exec = new DefaultExecutor();
    final ExecuteWatchdog watchdog = new ExecuteWatchdog(500);
    exec.setWatchdog(watchdog);
    final PumpStreamHandler streamHandler = new PumpStreamHandler(_stream.getStream());
    exec.setStreamHandler(streamHandler);
    int result = -1;
    try
    {
      result = exec.execute(commandline);
    }
    catch (final IOException ex_)
    {
      ex_.printStackTrace();
    }
    return new Result(this, result);
  }
}
