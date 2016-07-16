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

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;

class Charts
{
  static final int MAX_DATA_POINTS = 480;
  static final int MIN_BOTTOM_HEIGHT = 20;
  static final int MIN_HEIGHT = 320;
  static final int MIN_TOP_HEIGHT = 40;
  static final int MIN_WIDTH = 480;
  private static final int MIN_CENTER_HEIGHT = 255;

  static void setPreferences(final LineChart gpsLineChart_)
  {
    gpsLineChart_.setCreateSymbols(false);
    gpsLineChart_.setLegendVisible(false);
    gpsLineChart_.setAnimated(false);
    gpsLineChart_.setHorizontalGridLinesVisible(true);
    gpsLineChart_.setMinWidth(MIN_WIDTH);
    gpsLineChart_.setPrefSize(MIN_WIDTH, MIN_CENTER_HEIGHT);
    gpsLineChart_.setMaxHeight(MIN_CENTER_HEIGHT);
    gpsLineChart_.setPadding(new Insets(0));
  }
}
