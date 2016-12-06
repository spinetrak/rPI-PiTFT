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

import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Polyline;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.GPSService;
import net.spinetrak.rpitft.data.MapService;

import static net.spinetrak.rpitft.ui.Charts.MIN_HEIGHT;
import static net.spinetrak.rpitft.ui.Charts.MIN_WIDTH;

class GPSMapView
{
  private final GPSService _gpsService = new GPSService();
  private final BorderPane _pane = new BorderPane();

  GPSMapView()
  {
    _pane.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
    _pane.setMinWidth(MIN_WIDTH);
    _pane.setMinHeight(MIN_HEIGHT);
    _pane.setCenter(new Polyline());
  }

  void addData(final GPS gps_, final boolean mock_)
  {
    if (mock_)
    {
      _gpsService.addGPS(_gpsService.getNextMock());
    }
    else
    {
      _gpsService.addGPS(gps_);
    }
    final MapService mapService = new MapService(_gpsService);
    mapService.makeMap();
    _pane.setCenter(mapService.getPolyline());
  }

  BorderPane getPane()
  {
    return _pane;
  }

}
