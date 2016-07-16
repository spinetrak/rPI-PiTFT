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
import javafx.scene.layout.VBox;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.Power;

class ChartsPanel
{
  private static final String FX_STROKE_GREEN = "-fx-stroke: green;";
  private static final String FX_STROKE_RED = "-fx-stroke: red;";
  private static final int MAX_DATA_POINTS = 480;
  private final VBox _center;
  private boolean _batteryAlert = false;
  private LineChart<Number, Number> _gpsLineChart;
  private XYChart.Series<Number, Number> _gpsSeries;
  private XYChart.Series<Number, Number> _lowerSeries;
  private LineChart<Number, Number> _mainLineChart;
  private XYChart.Series<Number, Number> _mainSeries;
  private XYChart.Series<Number, Number> _middleSeries;
  private boolean _powerError = false;
  private XYChart.Series<Number, Number> _upperSeries;
  private NumberAxis _xGPSAxis;
  private int _xGPSSeriesData = 0;
  private NumberAxis _xMainAxis;
  private int _xMainSeriesData = 0;
  private NumberAxis _yGPSAxis;
  private NumberAxis _yMainAxis;

  ChartsPanel()
  {
    initPowerChart();

    initGPSChart();

    _center = new VBox();
    _center.setSpacing(1);
    _center.setPadding(new Insets(1));
    _center.getChildren().add(_mainLineChart);
    _center.getChildren().add(_gpsLineChart);
    _center.setFillWidth(true);
    _center.setPrefSize(480, 200);
    _center.setMaxHeight(200);

  }

  void addData(final Power power_)
  {
    if (power_.isError())
    {
      if (!_powerError)
      {
        _powerError = true;
        clearPowerData();
        prepYAxisForDevice();
      }
    }
    else
    {
      if (_powerError)
      {
        _powerError = false;
        clearPowerData();
        prepYAxisForPower();
      }
      addMainData(power_.getVoltage(), 5.25, 5.00, 4.75);

      if (Power.BATTERY.equals(power_.getSource()) && !_batteryAlert)
      {
        _batteryAlert = true;
        _mainSeries.nodeProperty().get().setStyle(FX_STROKE_RED);
      }
      else if (Power.PRIMARY.equals(power_.getSource()) && _batteryAlert)
      {
        _batteryAlert = false;
        _mainSeries.nodeProperty().get().setStyle(FX_STROKE_GREEN);
      }
    }
  }

  void addData(final GPS gps_)
  {
    _gpsSeries.getData().add(new XYChart.Data<>(_xGPSSeriesData++, gps_.getAltitude()));

    if (_gpsSeries.getData().size() > MAX_DATA_POINTS)
    {
      _gpsSeries.getData().remove(0, _gpsSeries.getData().size() - MAX_DATA_POINTS);
    }
    _xGPSAxis.setLowerBound(_xGPSSeriesData - MAX_DATA_POINTS);
    _xGPSAxis.setUpperBound(_xGPSSeriesData - 1);

    if (_powerError)
    {
      final float lon = gps_.getLongitude();
      addMainData(lon, lon * 1.01, lon, lon * 0.99);
    }
  }

  VBox getCenter()
  {
    return _center;
  }

  LineChart<Number, Number> getGPSLineChart()
  {
    return _gpsLineChart;
  }

  LineChart<Number, Number> getMainLineChart()
  {
    return _mainLineChart;
  }

  private void addMainData(final float data_, final double upper_, final double middle_, final double lower_)
  {
    _mainSeries.getData().add(new XYChart.Data<>(_xMainSeriesData++, data_));
    _upperSeries.getData().add(new XYChart.Data<>(_xMainSeriesData, upper_));
    _middleSeries.getData().add(new XYChart.Data<>(_xMainSeriesData, middle_));
    _lowerSeries.getData().add(new XYChart.Data<>(_xMainSeriesData, lower_));

    if (_mainSeries.getData().size() > MAX_DATA_POINTS)
    {
      _mainSeries.getData().remove(0, _mainSeries.getData().size() - MAX_DATA_POINTS);
    }
    _xMainAxis.setLowerBound(_xMainSeriesData - MAX_DATA_POINTS);
    _xMainAxis.setUpperBound(_xMainSeriesData - 1);
  }

  private void clearPowerData()
  {
    _xMainSeriesData = 0;
    _mainSeries.getData().remove(0, _mainSeries.getData().size() - 1);
    _upperSeries.getData().remove(0, _upperSeries.getData().size() - 1);
    _middleSeries.getData().remove(0, _middleSeries.getData().size() - 1);
    _lowerSeries.getData().remove(0, _lowerSeries.getData().size() - 1);
  }

  private void initGPSChart()
  {
    _gpsSeries = new XYChart.Series<>();

    _xGPSAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    _xGPSAxis.setForceZeroInRange(false);
    _xGPSAxis.setTickLabelsVisible(false);
    _xGPSAxis.setAutoRanging(false);

    _yGPSAxis = new NumberAxis();
    _yGPSAxis.setForceZeroInRange(false);
    _yGPSAxis.setAutoRanging(true);

    _gpsLineChart = new LineChart<>(_xGPSAxis, _yGPSAxis);
    _gpsLineChart.setCreateSymbols(false);
    _gpsLineChart.setLegendVisible(false);
    _gpsLineChart.setAnimated(false);
    _gpsLineChart.setHorizontalGridLinesVisible(true);
    _gpsLineChart.setMinWidth(480);
    _gpsLineChart.setPrefSize(480, 80);
    _gpsLineChart.setMaxHeight(80);
    _gpsLineChart.setPadding(new Insets(0));
    //noinspection unchecked
    _gpsLineChart.getData().add(_gpsSeries);
  }

  private void initPowerChart()
  {
    _mainSeries = new XYChart.Series<>();
    _upperSeries = new XYChart.Series<>();
    _middleSeries = new XYChart.Series<>();
    _lowerSeries = new XYChart.Series<>();

    _xMainAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    _xMainAxis.setTickLabelsVisible(false);
    _xMainAxis.setForceZeroInRange(false);
    _xMainAxis.setAutoRanging(false);

    _yMainAxis = new NumberAxis();
    prepYAxisForPower();

    _mainLineChart = new LineChart<>(_xMainAxis, _yMainAxis);
    _mainLineChart.setCreateSymbols(false);
    _mainLineChart.setLegendVisible(false);
    _mainLineChart.setAnimated(false);
    _mainLineChart.setHorizontalGridLinesVisible(true);
    _mainLineChart.setMinWidth(480);
    _mainLineChart.setPrefSize(480, 80);
    _mainLineChart.setMaxHeight(80);
    _mainLineChart.setPadding(new Insets(0));
    //noinspection unchecked
    _mainLineChart.getData().addAll(_mainSeries, _upperSeries, _middleSeries,
                                    _lowerSeries);
  }

  private void prepYAxisForDevice()
  {
    _yMainAxis.setAutoRanging(true);
    _yMainAxis.setForceZeroInRange(false);
  }

  private void prepYAxisForPower()
  {
    _yMainAxis.setLowerBound(4.7);
    _yMainAxis.setUpperBound(5.3);
    _yMainAxis.setTickUnit(0.3);
    _yMainAxis.setAutoRanging(false);
    _yMainAxis.setForceZeroInRange(false);
  }
}
