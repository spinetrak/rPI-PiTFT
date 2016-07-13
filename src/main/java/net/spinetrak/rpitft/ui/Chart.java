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
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.Power;

class Chart
{
  private static final String FX_STROKE_GREEN = "-fx-stroke: green;";
  private static final String FX_STROKE_RED = "-fx-stroke: red;";
  private static final int MAX_DATA_POINTS = 320;
  private final LineChart<Number, Number> _gpsLineChart;
  private final XYChart.Series<Number, Number> _gpsSeries;
  private final XYChart.Series<Number, Number> _lowerVoltageSeries;
  private final XYChart.Series<Number, Number> _mainVoltageSeries;
  private final XYChart.Series<Number, Number> _middleVoltageSeries;
  private final LineChart<Number, Number> _powerLineChart;
  private final XYChart.Series<Number, Number> _upperVoltageSeries;
  private final NumberAxis _xPowerAxis;
  @SuppressWarnings("FieldCanBeLocal")
  private final NumberAxis _yPowerAxis;
  private boolean _batteryAlert = false;
  private int _xSeriesData = 0;

  Chart()
  {
    _mainVoltageSeries = new XYChart.Series<>();
    _upperVoltageSeries = new XYChart.Series<>();
    _middleVoltageSeries = new XYChart.Series<>();
    _lowerVoltageSeries = new XYChart.Series<>();

    _xPowerAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    _xPowerAxis.setTickLabelsVisible(false);
    _xPowerAxis.setForceZeroInRange(false);
    _xPowerAxis.setAutoRanging(false);

    _yPowerAxis = new NumberAxis();
    _yPowerAxis.setLowerBound(4.7);
    _yPowerAxis.setUpperBound(5.3);
    _yPowerAxis.setTickUnit(0.3);
    _yPowerAxis.setForceZeroInRange(false);
    _yPowerAxis.setAutoRanging(false);

    _powerLineChart = new LineChart<>(_xPowerAxis, _yPowerAxis);
    _powerLineChart.setCreateSymbols(false);
    _powerLineChart.setLegendVisible(false);
    _powerLineChart.setAnimated(false);
    _powerLineChart.setHorizontalGridLinesVisible(true);
    _powerLineChart.setMinWidth(320);
    _powerLineChart.setPrefSize(320, 80);
    _powerLineChart.setMaxHeight(80);
    _powerLineChart.setPadding(new Insets(0));
    //noinspection unchecked
    _powerLineChart.getData().addAll(_mainVoltageSeries, _upperVoltageSeries, _middleVoltageSeries,
                                     _lowerVoltageSeries);


    _gpsSeries = new XYChart.Series<>();

    final NumberAxis lonGPSAxis = new NumberAxis();
    final NumberAxis latGPSAxis = new NumberAxis();
    lonGPSAxis.setForceZeroInRange(false);
    latGPSAxis.setForceZeroInRange(false);
    lonGPSAxis.setAutoRanging(true);
    latGPSAxis.setAutoRanging(true);


    _gpsLineChart = new LineChart<>(lonGPSAxis, latGPSAxis);
    _gpsLineChart.setCreateSymbols(false);
    _gpsLineChart.setLegendVisible(false);
    _gpsLineChart.setAnimated(false);
    _gpsLineChart.setVerticalGridLinesVisible(true);
    _gpsLineChart.setHorizontalGridLinesVisible(true);
    _gpsLineChart.setMinWidth(320);
    _gpsLineChart.setPrefSize(320, 80);
    _gpsLineChart.setMaxHeight(80);
    _gpsLineChart.setPadding(new Insets(0));
    //noinspection unchecked
    _gpsLineChart.getData().addAll(_gpsSeries);
  }

  void addData(final Power power_)
  {
    _mainVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData++, power_.getVoltage()));
    _upperVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData, 5.25));
    _middleVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData, 5.00));
    _lowerVoltageSeries.getData().add(new XYChart.Data<>(_xSeriesData, 4.75));

    if (Power.BATTERY.equals(power_.getSource()) && !_batteryAlert)
    {
      _batteryAlert = true;
      _mainVoltageSeries.nodeProperty().get().setStyle(FX_STROKE_RED);
    }
    else if (Power.PRIMARY.equals(power_.getSource()) && _batteryAlert)
    {
      _batteryAlert = false;
      _mainVoltageSeries.nodeProperty().get().setStyle(FX_STROKE_GREEN);
    }

    if (_mainVoltageSeries.getData().size() > MAX_DATA_POINTS)
    {
      _mainVoltageSeries.getData().remove(0, _mainVoltageSeries.getData().size() - MAX_DATA_POINTS);
    }
    _xPowerAxis.setLowerBound(_xSeriesData - MAX_DATA_POINTS);
    _xPowerAxis.setUpperBound(_xSeriesData - 1);
  }

  void addData(final GPS gps_)
  {
    _gpsSeries.getData().add(new XYChart.Data<>(gps_.getLongitude(), gps_.getLatitude()));
    if (_gpsSeries.getData().size() > MAX_DATA_POINTS)
    {
      _gpsSeries.getData().remove(0, _gpsSeries.getData().size() - MAX_DATA_POINTS);
    }
  }

  LineChart<Number, Number> getGPSLineChart()
  {
    return _gpsLineChart;
  }

  LineChart<Number, Number> getPowerLineChart()
  {
    return _powerLineChart;
  }
}
