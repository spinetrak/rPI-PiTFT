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

import net.spinetrak.rpitft.command.Result;
import net.spinetrak.rpitft.data.streams.SingleLineStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.spinetrak.rpitft.command.Commands.POWER_STATUS;

public class Power
{
  private final static String BATTERY = "B";
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.Power");
  private final static String PRIMARY = "P";
  private float _capacity;
  private boolean _hasError = false;
  private float _power;
  private String _source;
  private float _voltage;

  Power()
  {
    try
    {
      final Result result = POWER_STATUS.execute(new SingleLineStream());
      _hasError = 0 != result.getResult() || !parse(result.resultAsString());
    }
    catch (final Exception ex_)
    {
      _hasError = true;
      LOGGER.error(ex_.getMessage());
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

  public boolean isHasError()
  {
    return _hasError;
  }

  private boolean parse(final String data_)
  {
    final String tokens[] = data_.split("/");
    if (tokens.length == 4)
    {
      final boolean parsedSource = parseSource(tokens[0]);
      final boolean parsedCapacity = parseCapacity(tokens[1]);
      final boolean parsedVoltage = parseVoltage(tokens[2]);
      final boolean parsedPower = parsePower(tokens[3]);
      return parsedSource && parsedCapacity && parsedVoltage && parsedPower;
    }
    return false;
  }

  private boolean parseCapacity(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.contains("%")))
    {
      _capacity = Float.parseFloat(token_.substring(0, token_.indexOf('%')));
      return true;
    }
    return false;
  }

  private boolean parsePower(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.contains("mA")))
    {
      _power = Float.parseFloat(token_.substring(0, token_.indexOf("mA")));
      return true;
    }
    return false;
  }

  private boolean parseSource(final String token_)
  {
    if ((null != token_) && !token_.isEmpty() && (token_.contains("P") || token_.contains("B")))
    {
      _source = token_.contains(PRIMARY) ? PRIMARY : BATTERY;
      return true;
    }
    return false;
  }

  private boolean parseVoltage(final String token_)
  {
    if (null != token_ && !token_.isEmpty() && token_.contains("V"))
    {
      _voltage = Float.parseFloat(token_.substring(0, token_.indexOf('V')));
      return true;
    }
    return false;
  }
}


