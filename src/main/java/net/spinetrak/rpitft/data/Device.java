package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.command.Command;
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

public class Device
{
  private final static String DEVICE_STATUS = "/device.sh";
  private static final String VAR_TMP = "/var/tmp";
  private float _cpu;
  private float _disk;
  private float _temperature;
  public Device()
  {
    String result = "";
    try
    {
      result = Command.execute(VAR_TMP + DEVICE_STATUS);
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
    }
    parse(result);
  }

  public float getCpu()
  {
    return _cpu;
  }

  public float getDisk()
  {
    return _disk;
  }

  public float getTemperature()
  {
    return _temperature;
  }

  private void parse(final String data_)
  {
    final String[] tokens = data_.split("/");
    if (tokens.length == 3)
    {
      _cpu = Float.parseFloat(tokens[0]);
      _disk = Float.parseFloat(tokens[1]);
      _temperature = Float.parseFloat(tokens[2]);
    }
  }

  static
  {
    InputStream deviceIn = null;
    OutputStream deviceOut = null;

    try
    {
      deviceIn = Command.class.getResourceAsStream(DEVICE_STATUS);
      deviceOut = new FileOutputStream(VAR_TMP + DEVICE_STATUS);

      IOUtils.copy(deviceIn, deviceOut);

      final Set<PosixFilePermission> perms = new HashSet<>();
      perms.add(PosixFilePermission.OWNER_EXECUTE);
      perms.add(PosixFilePermission.GROUP_EXECUTE);
      perms.add(PosixFilePermission.OWNER_READ);
      perms.add(PosixFilePermission.GROUP_READ);
      perms.add(PosixFilePermission.OWNER_WRITE);
      perms.add(PosixFilePermission.GROUP_WRITE);

      Files.setPosixFilePermissions(Paths.get(VAR_TMP + DEVICE_STATUS), perms);
    }
    catch (final IOException ex_)
    {
      System.err.println(ex_.getMessage());
      ex_.printStackTrace();
    }
    finally
    {
      if (deviceIn != null)
      {
        try
        {
          deviceIn.close();
        }
        catch (final IOException ex_)
        {
          ex_.printStackTrace();
        }
      }
      if (deviceOut != null)
      {
        try
        {
          deviceOut.close();
        }
        catch (final IOException ex_)
        {
          ex_.printStackTrace();
        }
      }
    }
  }
}
