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
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.location.GPSService;
import net.spinetrak.rpitft.data.location.MapService;

import static net.spinetrak.rpitft.ui.Charts.MIN_HEIGHT;
import static net.spinetrak.rpitft.ui.Charts.MIN_WIDTH;

class GPSMapView
{
  private final Text _distance = new Text(200, 15, "distance");
  private final GPSService _gpsService = new GPSService();
  private final Line _latYAxis = new Line(2, 2, 2, MIN_HEIGHT - 2);
  private final Line _lonXAxis = new Line(2, 2, MIN_WIDTH - 2, 2);
  private final Text _lowerLeft = new Text(4, MIN_HEIGHT - 125, "[-90.0000]\n[-180.0000]");
  private final Text _lowerRight = new Text(MIN_WIDTH - 60, MIN_HEIGHT - 125, "[-90.0000]\n[180.0000]");
  private final Pane _pane = new Pane();
  private final Text _upperLeft = new Text(4, 15, "[90.0000]\n[-180.0000]");
  private final Text _upperRight = new Text(MIN_WIDTH - 60, 15, "[90.0000]\n[180.0000]");

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
    _upperRight.setFont(Font.font(10.0));
    _lowerLeft.setFont(Font.font(10.0));
    _upperLeft.setFont(Font.font(10.0));
    _lowerRight.setFont(Font.font(10.0));
    _distance.setFont(Font.font(12.0));
  }

  void addData(final GPS gps_)
  {
    _gpsService.addGPS(gps_);
    final MapService mapService = new MapService(_gpsService);
    mapService.makeMap();
    final String minLatY = Formatter.formatLatitude(mapService.getMinLatY());
    final String maxLatY = Formatter.formatLatitude(mapService.getMaxLatY());
    final String minLonX = Formatter.formatLongitude(mapService.getMinLonX());
    final String maxLonX = Formatter.formatLongitude(mapService.getMaxLonX());
    _lowerLeft.setText(minLatY + "\n" + minLonX);
    _lowerRight.setText(minLatY + "\n" + maxLonX);
    _upperLeft.setText(maxLatY + "\n" + minLonX);
    _upperRight.setText(maxLatY + "\n" + maxLonX);
    _distance.setText(Formatter.formatDistance(mapService.getDistance()));
    final Polyline track = mapService.getPolyline();
    track.setStroke(Color.RED);
    track.setStrokeWidth(2.0);
    _pane.getChildren().clear();
    _pane.getChildren().addAll(_lonXAxis, _latYAxis, _lowerLeft, _upperRight, _lowerRight, _upperLeft, _distance,
                               track);
  }

  Pane getPane()
  {
    return _pane;
  }

}
