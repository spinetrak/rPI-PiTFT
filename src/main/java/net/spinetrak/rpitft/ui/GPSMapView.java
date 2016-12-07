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

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.spinetrak.rpitft.data.Formatter;
import net.spinetrak.rpitft.data.GPS;
import net.spinetrak.rpitft.data.GPSService;
import net.spinetrak.rpitft.data.MapService;

import static net.spinetrak.rpitft.ui.Charts.MIN_HEIGHT;
import static net.spinetrak.rpitft.ui.Charts.MIN_WIDTH;

class GPSMapView
{
  private final GPSService _gpsService = new GPSService();
  private final Line _latYAxis = new Line(2, 2, 2, MIN_HEIGHT - 2);
  private final Line _lonXAxis = new Line(2, 2, MIN_WIDTH - 2, 2);
  private final Text _maxLatY = new Text(4, 30, "[90.0000]");
  private final Text _maxLonX = new Text(MIN_WIDTH - 60, 15, "[180.0000]");
  private final Text _minLatY = new Text(4, MIN_HEIGHT - 110, "[-90.0000]");
  private final Text _minLonX = new Text(4, 15, "[-180.0000]");
  private final Pane _pane = new Pane();

  GPSMapView()
  {
    _pane.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
    _pane.setMinWidth(MIN_WIDTH);
    _pane.setMinHeight(MIN_HEIGHT);
    _pane.getChildren().addAll(_lonXAxis, _latYAxis);
    _lonXAxis.setStroke(Color.BLUE);
    _latYAxis.setStroke(Color.BLUE);
    _lonXAxis.setStrokeWidth(0.5);
    _latYAxis.setStrokeWidth(0.5);
    _maxLonX.setFont(Font.font(10.0));
    _minLonX.setFont(Font.font(10.0));
    _maxLatY.setFont(Font.font(10.0));
    _minLatY.setFont(Font.font(10.0));
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
    _minLatY.setText(Formatter.formatLatitude(mapService.getMinLatY()));
    _maxLatY.setText(Formatter.formatLatitude(mapService.getMaxLatY()));
    _minLonX.setText(Formatter.formatLongitude(mapService.getMinLonX()));
    _maxLonX.setText(Formatter.formatLongitude(mapService.getMaxLonX()));
    final Polyline track = mapService.getPolyline();
    track.setStroke(Color.RED);
    track.setStrokeWidth(2.0);
    _pane.getChildren().clear();
    _pane.getChildren().addAll(_lonXAxis, _latYAxis, _minLonX, _maxLonX, _minLatY, _maxLatY, track);
  }

  Pane getPane()
  {
    return _pane;
  }

}
