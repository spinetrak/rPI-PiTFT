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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class Command
{
  public static String execute(final String command_)
  {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final CommandLine commandline = CommandLine.parse(command_);
    final DefaultExecutor exec = new DefaultExecutor();
    final ExecuteWatchdog watchdog = new ExecuteWatchdog(500);
    exec.setWatchdog(watchdog);
    final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
    exec.setStreamHandler(streamHandler);
    try
    {
      exec.execute(commandline);
    }
    catch (final IOException ex_)
    {
      ex_.printStackTrace();
      System.err.println("Command failed: [" + command_ + "]");
    }
    return (outputStream.toString());
  }

  public static String init(final String script_)
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
}
