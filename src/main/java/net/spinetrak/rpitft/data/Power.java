package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.command.Command;

public class Power
{
  public final static String BATTERY = "B";
  public final static String PRIMARY = "P";
  private static final String POWER_STATUS = "/usr/share/i3blocks/battery_raspi";
  double _capacity;
  double _power;
  String _source;
  double _voltage;

  public Power()
  {
    String result = "";
    try
    {
      result = Command.execute(POWER_STATUS);
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
    }
    parse(result);
  }

  public double getCapacity()
  {
    return _capacity;
  }

  public double getPower()
  {
    return _power;
  }

  public String getSource()
  {
    return _source;
  }

  public double getVoltage()
  {
    return _voltage;
  }

  private void parse(final String data_)
  {
    final String tokens[] = data_.split("/");
    if (tokens.length == 4)
    {
      parseSource(tokens[0]);

      parseCapacity(tokens[1]);

      parseVoltage(tokens[2]);

      parsePower(tokens[3]);
    }
  }

  private void parseCapacity(final String token_)
  {
    final String capacity = token_;
    if ((null != capacity) && !capacity.isEmpty() && (capacity.contains("%")))
    {
      _capacity = Float.parseFloat(capacity.substring(0, capacity.indexOf('%')));
    }
  }

  private void parsePower(final String token_)
  {
    final String power = token_;
    if ((null != power) && !power.isEmpty() && (power.contains("mA")))
    {
      _power = Float.parseFloat(power.substring(0, power.indexOf("mA")));
    }
  }

  private void parseSource(final String token_)
  {
    final String source = token_;
    if ((null != source) && !source.isEmpty() && (source.contains("P") || source.contains("B")))
    {
      _source = source.contains(PRIMARY) ? PRIMARY : BATTERY;
    }
  }

  private void parseVoltage(final String token_)
  {
    final String voltage = token_;
    if (null != voltage && !voltage.isEmpty() && voltage.contains("V"))
    {
      _voltage = Float.parseFloat(voltage.substring(0, voltage.indexOf('V')));
    }
  }
}

