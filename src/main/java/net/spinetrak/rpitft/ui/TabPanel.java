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

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.Threshold;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TabPanel
{
  private final SingleLineChart _altitudeChart;
  private final SingleLineChart _cpuChart;
  private final Threshold _cpuThreshold;
  private final ExecutorService _executor;
  private final GPSMapView _gpsMapView;
  //private final SingleLineChart _latitudeChart;
  //private final SingleLineChart _longitudeChart;
  private final SingleLineChart _memoryChart;
  private final Threshold _memoryThreshold;
  private final TabPane _tabPane;
  private final SingleLineChart _temperatureChart;
  private final Threshold _temperatureThreshold;
  private boolean _inFocus;

  TabPanel()
  {
    /*
    final Tab lonTab = new Tab("lon");
    _longitudeChart = new SingleLineChart();
    lonTab.setContent(_longitudeChart.getChart());

    final Tab latTab = new Tab("lat");
    _latitudeChart = new SingleLineChart();
    latTab.setContent(_latitudeChart.getChart());
    */
    final Tab altTab = new Tab("alt");
    _altitudeChart = new SingleLineChart();
    altTab.setContent(_altitudeChart.getChart());


    final Tab cpuTab = new Tab("cpu");
    _cpuChart = new SingleLineChart();
    cpuTab.setContent(_cpuChart.getChart());
    _cpuThreshold = new Threshold(_cpuChart.getChart(), 90, 80);

    final Tab memTab = new Tab("mem");
    _memoryChart = new SingleLineChart();
    memTab.setContent(_memoryChart.getChart());
    _memoryThreshold = new Threshold(_memoryChart.getChart(), 90, 80);

    final Tab temTab = new Tab("temp");
    _temperatureChart = new SingleLineChart();
    temTab.setContent(_temperatureChart.getChart());
    _temperatureThreshold = new Threshold(_temperatureChart.getChart(), 85, 75);


    final Tab mapTab = new Tab("map");
    _gpsMapView = new GPSMapView();
    mapTab.setContent(_gpsMapView.getPane());


    _tabPane = new TabPane();
    _tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    //_tabPane.getTabs().addAll(lonTab, latTab, altTab, cpuTab, memTab, temTab, mapTab);

    _tabPane.getTabs().addAll(cpuTab, memTab, temTab, altTab, mapTab);

    _executor = Executors.newCachedThreadPool(runnable_ -> {
      final Thread thread = new Thread(runnable_);
      thread.setDaemon(true);
      return thread;
    });


    //_tabPane.getSelectionModel().select(mapTab);


    final TransitionTabs transitionTabs = new TransitionTabs();
    _executor.execute(transitionTabs);

    _tabPane.focusedProperty().addListener(
      (final ObservableValue<? extends Boolean> observableValue_, final Boolean old_, final Boolean new_) -> {
        _inFocus = new_;
      });

  }


  void addData(final GPS gps_)
  {
    //_longitudeChart.addData(gps_.getLongitude());
    //_latitudeChart.addData(gps_.getLatitude());
    _altitudeChart.addData(gps_.getAltitude());
    _gpsMapView.addData(gps_, false);
  }

  void addData(final Device device_)
  {

    final float cpu = device_.getCpu();
    _cpuChart.addData(cpu);
    _cpuThreshold.setColor(cpu);

    final float memory = device_.getMemory();
    _memoryChart.addData(memory);
    _memoryThreshold.setColor(memory);

    float temperature = device_.getTemperature();
    _temperatureChart.addData(temperature);
    _temperatureThreshold.setColor(temperature);

    _gpsMapView.addData(null, true);
  }

  Node getCenter()
  {
    return _tabPane;
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
        catch (final InterruptedException e_)
        {
          //ignore
        }
      }
    }
  }
}
