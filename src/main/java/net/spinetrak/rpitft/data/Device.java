package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.command.Command;

public class Device
{
  private final static String CPU_STATUS = "cat <(grep 'cpu ' /proc/stat) | awk -v RS=\"\" '{print ($13-$2+$15-$4)*100/($13-$2+$15-$4+$16-$5)}'";
  private final static String DISK_STATUS = "df -lh | awk '{if ($6 == \"/\") { print $5 }}' | head -1 | cut -d'%' -f1";
  private final static String TEMPERATURE_STATUS = "/opt/vc/bin/vcgencmd measure_temp | awk -F \"=\" '{print $2}' | awk -F \"'\" '{print $1}'";
  private float _cpu;
  private int _disk;
  private float _temperature;

  public Device()
  {
    try
    {
      _disk = Integer.parseInt(Command.execute(DISK_STATUS));
      _cpu = Float.parseFloat(Command.execute(CPU_STATUS));
      _temperature = Float.parseFloat(Command.execute(TEMPERATURE_STATUS));
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
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
}
