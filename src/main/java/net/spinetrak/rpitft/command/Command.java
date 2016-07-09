package net.spinetrak.rpitft.command;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Command
{
  public static String execute(final String command_)
  {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final CommandLine commandline = CommandLine.parse(command_);
    final DefaultExecutor exec = new DefaultExecutor();
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
}
