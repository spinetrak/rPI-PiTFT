package net.spinetrak.rpitft.ui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import net.spinetrak.rpitft.data.Power;

public class Chart
{
  public static final String FX_STROKE_GREEN = "-fx-stroke: green;";
  public static final String FX_STROKE_RED = "-fx-stroke: red;";
  private static final int MAX_DATA_POINTS = 320;
  private final XYChart.Series<Number, Number> _lowerVoltageSeries;
  private final Main _main;
  private final XYChart.Series<Number, Number> _mainVoltageSeries;
  private final XYChart.Series<Number, Number> _middleVoltageSeries;
  private final LineChart<Number, Number> _powerLineChart;
  private final XYChart.Series<Number, Number> _upperVoltageSeries;
  private final NumberAxis _xPowerAxis;
  private final NumberAxis _yPowerAxis;
  private boolean _batteryAlert = false;
  private int _xSeriesData = 0;

  public Chart(final Main main_)
  {
    _main = main_;
    _mainVoltageSeries = new XYChart.Series<>();
    _upperVoltageSeries = new XYChart.Series<>();
    _middleVoltageSeries = new XYChart.Series<>();
    _lowerVoltageSeries = new XYChart.Series<>();

    _xPowerAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    _xPowerAxis.setTickLabelsVisible(false);
    _xPowerAxis.setTickUnit(10);
    _xPowerAxis.setForceZeroInRange(false);
    _xPowerAxis.setAutoRanging(false);

    _yPowerAxis = new NumberAxis();
    _yPowerAxis.setLowerBound(4.6);
    _yPowerAxis.setUpperBound(5.4);
    _yPowerAxis.setTickUnit(0.05);
    _yPowerAxis.setForceZeroInRange(false);
    _yPowerAxis.setAutoRanging(false);

    _powerLineChart = new LineChart<>(_xPowerAxis, _yPowerAxis);
    _powerLineChart.setCreateSymbols(false);
    _powerLineChart.setLegendVisible(false);
    _powerLineChart.setAnimated(false);
    _powerLineChart.setHorizontalGridLinesVisible(true);
    _powerLineChart.getData().addAll(_mainVoltageSeries, _upperVoltageSeries, _middleVoltageSeries,
                                     _lowerVoltageSeries);
  }

  public void addData(final Power power_)
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

  public LineChart<Number, Number> getPowerLineChart()
  {
    return _powerLineChart;
  }
}
