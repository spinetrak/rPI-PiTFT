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
import net.spinetrak.rpitft.data.streams.SingleLineStream;

public class Power
{
  public final static String BATTERY = "B";
  public final static String PRIMARY = "P";
  private float _capacity;
  private float _power;
  private String _source;
  private float _voltage;

  Power()
  {
    try
    {
      parse(Command.Commands.POWER_STATUS.execute(new SingleLineStream()).resultAsString());
    }
    catch (final Exception ex_)
    {
      ex_.printStackTrace();
    }
  }

  public float getCapacity()
  {
    return _capacity;
  }

  public float getPower()
  {
    return _power;
  }

  public String getSource()
  {
    return _source;
  }

  public float getVoltage()
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
    if ((null != token_) && !token_.isEmpty() && (token_.contains("%")))
    {
      _capacity = Float.parseFloat(token_.substring(0, token_.indexOf('%')));
    }
  }

  private void parsePower(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.contains("mA")))
    {
      _power = Float.parseFloat(token_.substring(0, token_.indexOf("mA")));
    }
  }

  private void parseSource(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.contains("P") || token_.contains("B")))
    {
      _source = token_.contains(PRIMARY) ? PRIMARY : BATTERY;
    }
  }

  private void parseVoltage(final String token_)
  {
    if (null != token_ && !token_.isEmpty() && token_.contains("V"))
    {
      _voltage = Float.parseFloat(token_.substring(0, token_.indexOf('V')));
    }
  }
}


