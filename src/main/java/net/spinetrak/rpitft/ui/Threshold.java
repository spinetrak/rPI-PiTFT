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

package net.spinetrak.rpitft.ui;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.text.Text;

import static net.spinetrak.rpitft.ui.Threshold.State.*;

class Threshold
{
  private static final String FX_FILL_AMBER = "-fx-fill: orange;";
  private static final String FX_FILL_GREEN = "-fx-fill: green;";
  private static final String FX_FILL_RED = "-fx-fill: red;";
  private static final String FX_STROKE_AMBER = "-fx-stroke: orange;";
  private static final String FX_STROKE_GREEN = "-fx-stroke: green;";
  private static final String FX_STROKE_RED = "-fx-stroke: red;";
  private final float _amber;
  private final boolean _inverse;
  private final Node _node;
  private final float _red;
  private State _state = null;

  Threshold(final Node node_, final float red_, final float amber_)
  {
    _node = node_;
    _red = red_;
    _amber = amber_;
    _inverse = red_ < amber_;
  }

  void setColor(final float data_)
  {
    if (!_inverse)
    {
      if (data_ >= _red && _state != RED)
      {
        _state = RED;
        RED.setStyle(_node);
      }
      else if (data_ < _red && data_ >= _amber && _state != State.AMBER)
      {
        _state = State.AMBER;
        AMBER.setStyle(_node);
      }
      else if (data_ < _amber && _state != GREEN)
      {
        _state = GREEN;
        GREEN.setStyle(_node);
      }
    }
    else
    {
      if (data_ <= _red && _state != RED)
      {
        _state = RED;
        RED.setStyle(_node);
      }
      else if (data_ > _red && data_ <= _amber && _state != State.AMBER)
      {
        _state = State.AMBER;
        AMBER.setStyle(_node);
      }
      else if (data_ > _amber && _state != GREEN)
      {
        _state = GREEN;
        GREEN.setStyle(_node);
      }
    }
  }

  enum State
  {
    RED
      {
        @Override
        void setStyle(final Node node_)
        {
          if (node_ instanceof Text)
          {
            node_.setStyle(FX_FILL_RED);
          }
          else if (node_ instanceof LineChart)
          {
            node_.setStyle(FX_STROKE_RED);
          }
        }
      },
    AMBER
      {
        @Override
        void setStyle(final Node node_)
        {
          if (node_ instanceof Text)
          {
            node_.setStyle(FX_FILL_AMBER);
          }
          else if (node_ instanceof LineChart)
          {
            node_.setStyle(FX_STROKE_AMBER);
          }
        }
      },
    GREEN
      {
        @Override
        void setStyle(final Node node_)
        {
          if (node_ instanceof Text)
          {
            node_.setStyle(FX_FILL_GREEN);
          }
          else if (node_ instanceof LineChart)
          {
            node_.setStyle(FX_STROKE_GREEN);
          }
        }
      };

    abstract void setStyle(final Node node_);
  }
}
