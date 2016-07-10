package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.command.Command;

public class Device
{
  private final static String DEVICE_STATUS = "/device.sh";
  private final static String SCRIPT = Command.init(DEVICE_STATUS);
  private float _cpu;
  private float _disk;
  private float _temperature;

  public Device()
  {
    String result = "";
    try
    {
      result = Command.execute(SCRIPT);
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

}
