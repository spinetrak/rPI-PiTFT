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

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import net.spinetrak.rpitft.data.Formatter;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.location.GPSService;
import net.spinetrak.rpitft.data.location.MapService;

import static net.spinetrak.rpitft.ui.center.Charts.MIN_WIDTH;

class GPSLocationView
{
  private final static int HEIGHT = 200;
  private final CompassView _compass = new CompassView();
  private final Text _distance = new Text(200, 15, "distance");
  private final GPSService _gpsService = new GPSService();
  private final Text _lowerLeft = new Text(4, HEIGHT - 10, "[-90.0000]\n[-180.0000]");
  private final Text _lowerRight = new Text(MIN_WIDTH - 65, HEIGHT - 10, "[-90.0000]\n[180.0000]");
  private final Pane _pane = new Pane();
  private final Text _upperLeft = new Text(4, 15, "[90.0000]\n[-180.0000]");
  private final Text _upperRight = new Text(MIN_WIDTH - 65, 15, "[90.0000]\n[180.0000]");

  GPSLocationView()
  {
    _pane.setPrefSize(MIN_WIDTH, HEIGHT);
    _pane.setMinWidth(MIN_WIDTH);
    _pane.setMinHeight(HEIGHT);
    _upperRight.setFont(Font.font(10.0));
    _upperRight.setTextAlignment(TextAlignment.RIGHT);
    _lowerLeft.setFont(Font.font(10.0));
    _upperLeft.setFont(Font.font(10.0));
    _lowerRight.setFont(Font.font(10.0));
    _lowerRight.setTextAlignment(TextAlignment.RIGHT);
    _distance.setFont(Font.font(12.0));
    _pane.getChildren().add(_compass.getPane());
  }

  void addData(final GPS gps_)
  {
    if (gps_.isValidMovement())
    {
      _compass.addData(gps_);
    }
    if (gps_.isValidLocation())
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
      _pane.getChildren().addAll(_lowerLeft, _upperRight, _lowerRight, _upperLeft, _distance,
                                 track, _compass.getPane());
    }
  }

  Pane getPane()
  {
    return _pane;
  }

}
