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

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.raspberry.Device;
import net.spinetrak.rpitft.ui.Threshold;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TabPanel
{
  private final SingleLineChart _altitudeChart;
  private final SingleLineChart _cpuChart;
  private final Threshold _cpuThreshold;
  private final ExecutorService _executor;
  private final Tab _gaugesTab;
  private final GaugesView _gaugesView;
  private final GPSLocationView _gpsLocationView;
  private final VBox _mainPanel;
  private final SingleLineChart _memoryChart;
  private final Threshold _memoryThreshold;
  private final SingleLineChart _speedChart;
  private final TabPane _tabPane;
  private final SingleLineChart _temperatureChart;
  private final Threshold _temperatureThreshold;
  private final TextPanel _textPanel;
  private boolean _inFocus;

  public TabPanel()
  {
    _textPanel = new TextPanel();

    _gaugesTab = new Tab("gauges");
    _gaugesView = new GaugesView();
    _gaugesTab.setContent(_gaugesView.getPanel());

    final Tab altTab = new Tab("altitude");
    _altitudeChart = new SingleLineChart();
    altTab.setContent(_altitudeChart.getChart());

    final Tab cpuTab = new Tab("cpu");
    _cpuChart = new SingleLineChart();
    cpuTab.setContent(_cpuChart.getChart());
    _cpuThreshold = new Threshold(_cpuChart.getChart(), 90, 80);

    final Tab memTab = new Tab("memory");
    _memoryChart = new SingleLineChart();
    memTab.setContent(_memoryChart.getChart());
    _memoryThreshold = new Threshold(_memoryChart.getChart(), 90, 80);

    final Tab temTab = new Tab("temperature");
    _temperatureChart = new SingleLineChart();
    temTab.setContent(_temperatureChart.getChart());
    _temperatureThreshold = new Threshold(_temperatureChart.getChart(), 85, 75);

    final Tab mapTab = new Tab("location");
    _gpsLocationView = new GPSLocationView();
    mapTab.setContent(_gpsLocationView.getPane());

    final Tab speedTab = new Tab("speed");
    _speedChart = new SingleLineChart();
    speedTab.setContent(_speedChart.getChart());


    _tabPane = new TabPane();
    _tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

    _tabPane.getTabs().addAll(_gaugesTab, cpuTab, memTab, temTab, mapTab, altTab, speedTab);

    _executor = Executors.newCachedThreadPool(runnable_ -> {
      final Thread thread = new Thread(runnable_);
      thread.setDaemon(true);
      return thread;
    });


    final TransitionTabs transitionTabs = new TransitionTabs();
    _executor.execute(transitionTabs);

    _tabPane.focusedProperty().addListener(
      (final ObservableValue<? extends Boolean> observableValue_, final Boolean old_, final Boolean new_) -> {
        _inFocus = new_;
      });

    _mainPanel = new VBox(_textPanel.getPanel(), _tabPane);

  }


  public void addData(final GPS gps_)
  {
    _textPanel.addData(gps_);

    //gauges are cpu hogs
    if (_gaugesTab.isSelected())
    {
      _gaugesView.addData(gps_);
    }

    _gpsLocationView.addData(gps_);

    _altitudeChart.addData(gps_.getAltitude());
    _speedChart.addData((float) gps_.getSpeed());
  }

  public void addData(final Device device_)
  {
    _textPanel.addData(device_);

    if (_gaugesTab.isSelected())
    {
      _gaugesView.addData(device_);
    }

    final float cpu = device_.getCpu();
    _cpuChart.addData(cpu);
    _cpuThreshold.setColor(cpu);

    final float memory = device_.getMemory();
    _memoryChart.addData(memory);
    _memoryThreshold.setColor(memory);

    float temperature = device_.getTemperature();
    _temperatureChart.addData(temperature);
    _temperatureThreshold.setColor(temperature);
  }

  public Node getPanel()
  {
    return _mainPanel;
  }

  private class TransitionTabs implements Runnable
  {
    public void run()
    {
      while (true)
      {
        if (!_inFocus)
        {
          final int before = _tabPane.getSelectionModel().getSelectedIndex();
          _tabPane.getSelectionModel().selectNext();
          final int after = _tabPane.getSelectionModel().getSelectedIndex();
          if (before == after)
          {
            _tabPane.getSelectionModel().selectFirst();
          }
        }
        try
        {
          Thread.sleep(5000);
        }
        catch (final InterruptedException ex_)
        {
          //ignore
        }
      }
    }
  }
}
