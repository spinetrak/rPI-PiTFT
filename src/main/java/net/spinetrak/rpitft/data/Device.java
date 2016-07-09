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
  private final static String CPU_STATUS = "/cpu.sh";
  private final static String DISK_STATUS = "/disk.sh";
  private final static String TEMPERATURE_STATUS = "/temperature.sh";
  private static final String VAR_TMP = "/var/tmp";
  private float _cpu;
  private int _disk;
  private float _temperature;
  public Device()
  {
    try
    {
      _disk = Integer.parseInt(Command.execute(VAR_TMP + DISK_STATUS));
      _cpu = Float.parseFloat(Command.execute(VAR_TMP + CPU_STATUS));
      _temperature = Float.parseFloat(Command.execute(VAR_TMP + TEMPERATURE_STATUS));
    }
    catch (final Exception ex_)
    {
      //ex_.printStackTrace();
    }
  }

  public float getCpu()
  {
    return _cpu;
  }

  public int getDisk()
  {
    return _disk;
  }

  public float getTemperature()
  {
    return _temperature;
  }

  static
  {
    try
    {
      final InputStream cpuIn = Command.class.getResourceAsStream(CPU_STATUS);
      final OutputStream cpuOut = new FileOutputStream(VAR_TMP + CPU_STATUS);
      final InputStream diskIn = Command.class.getResourceAsStream(DISK_STATUS);
      final OutputStream diskOut = new FileOutputStream(VAR_TMP + DISK_STATUS);
      final InputStream temperatureIn = Command.class.getResourceAsStream(TEMPERATURE_STATUS);
      final OutputStream temperatureOut = new FileOutputStream(VAR_TMP + TEMPERATURE_STATUS);

      IOUtils.copy(cpuIn, cpuOut);
      IOUtils.copy(diskIn, diskOut);
      IOUtils.copy(temperatureIn, temperatureOut);

      final Set<PosixFilePermission> perms = new HashSet<>();
      perms.add(PosixFilePermission.OWNER_EXECUTE);
      perms.add(PosixFilePermission.GROUP_EXECUTE);
      perms.add(PosixFilePermission.OWNER_READ);
      perms.add(PosixFilePermission.GROUP_READ);
      perms.add(PosixFilePermission.OWNER_WRITE);
      perms.add(PosixFilePermission.GROUP_WRITE);

      Files.setPosixFilePermissions(Paths.get(VAR_TMP + CPU_STATUS), perms);
      Files.setPosixFilePermissions(Paths.get(VAR_TMP + DISK_STATUS), perms);
      Files.setPosixFilePermissions(Paths.get(VAR_TMP + TEMPERATURE_STATUS), perms);
    }
    catch (final IOException ex_)
    {
      System.err.println(ex_.getMessage());
      ex_.printStackTrace();
    }
  }
}
