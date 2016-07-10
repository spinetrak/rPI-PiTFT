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
 *
 */

package net.spinetrak.rpitft.data;

import javafx.scene.text.Text;

public class Threshold
{
  private static final String FX_FILL_AMBER = "-fx-fill: orange;";
  private static final String FX_FILL_GREEN = "-fx-fill: green;";
  private static final String FX_FILL_RED = "-fx-fill: red;";
  private final float _amber;
  private final float _red;
  private final Text _text;
  private boolean _inverse;
  private State _state = null;

  public Threshold(final Text text_, final float red_, final float amber_)
  {
    _text = text_;
    _red = red_;
    _amber = amber_;
    _inverse = red_ < amber_;
  }

  public void setColor(final float data_)
  {
    if (!_inverse)
    {
      if (data_ >= _red && _state != State.RED)
      {
        _state = State.RED;
        _text.setStyle(FX_FILL_RED);
      }
      else if (data_ < _red && data_ >= _amber && _state != State.AMBER)
      {
        _state = State.AMBER;
        _text.setStyle(FX_FILL_AMBER);
      }
      else if (data_ < _amber && _state != State.GREEN)
      {
        _state = State.GREEN;
        _text.setStyle(FX_FILL_GREEN);
      }
    }
    else
    {
      if (data_ <= _red && _state != State.RED)
      {
        _state = State.RED;
        _text.setStyle(FX_FILL_RED);
      }
      else if (data_ > _red && data_ <= _amber && _state != State.AMBER)
      {
        _state = State.AMBER;
        _text.setStyle(FX_FILL_AMBER);
      }
      else if (data_ > _amber && _state != State.GREEN)
      {
        _state = State.GREEN;
        _text.setStyle(FX_FILL_GREEN);
      }
    }
  }

  private enum State
  {
    RED,
    AMBER,
    GREEN
  }
}
