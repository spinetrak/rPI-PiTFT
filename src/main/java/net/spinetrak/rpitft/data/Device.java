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

package net.spinetrak.rpitft.data;

import net.spinetrak.rpitft.command.Command;

public class Device
{
  private final static String DEVICE_STATUS = "/device.sh";
  private final static String SCRIPT = Command.init(DEVICE_STATUS);
  private float _cpu;
  private float _disk;
  private float _memory;
  private float _temperature;
  Device()
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

  public float getMemory()
  {
    return _memory;
  }

  public float getTemperature()
  {
    return _temperature;
  }

  private void parse(final String data_)
  {
    final String[] tokens = data_.split("/");
    if (tokens.length == 4)
    {
      _cpu = Float.parseFloat(tokens[0]);
      _disk = Float.parseFloat(tokens[1]);
      _memory = Float.parseFloat(tokens[2]);
      _temperature = Float.parseFloat(tokens[3]);
    }
  }

}
