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

package net.spinetrak.rpitft.ui.center;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import static net.spinetrak.rpitft.ui.center.Charts.MAX_DATA_POINTS;

class SingleLineChart
{
  private XYChart.Series<Number, Number> _dataSeries;
  private LineChart<Number, Number> _lineChart;
  private NumberAxis _xAxis;
  private int _xSeriesData = 0;
  private NumberAxis _yAxis;

  SingleLineChart()
  {
    _dataSeries = new XYChart.Series<>();

    _xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    _xAxis.setForceZeroInRange(false);
    _xAxis.setTickLabelsVisible(false);
    _xAxis.setAutoRanging(false);

    _yAxis = new NumberAxis();
    _yAxis.setForceZeroInRange(false);
    _yAxis.setAutoRanging(true);

    _lineChart = new LineChart<>(_xAxis, _yAxis);
    Charts.setPreferences(_lineChart);
    //noinspection unchecked
    _lineChart.getData().add(_dataSeries);
  }

  void addData(final float data_)
  {
    _dataSeries.getData().add(new XYChart.Data<>(_xSeriesData++, data_));

    if (_dataSeries.getData().size() > MAX_DATA_POINTS)
    {
      _dataSeries.getData().remove(0, _dataSeries.getData().size() - MAX_DATA_POINTS);
    }
    _xAxis.setLowerBound(_xSeriesData - MAX_DATA_POINTS);
    _xAxis.setUpperBound(_xSeriesData - 1);
  }

  Node getChart()
  {
    return _lineChart;
  }
}
