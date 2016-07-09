package net.spinetrak.rpitft.command;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;

public class Command
{
  public static String execute(final String command_) throws Exception
  {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final CommandLine commandline = CommandLine.parse(command_);
    final DefaultExecutor exec = new DefaultExecutor();
    final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
    exec.setStreamHandler(streamHandler);
    exec.execute(commandline);
    return (outputStream.toString());
  }
}
