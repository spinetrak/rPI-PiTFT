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

package net.spinetrak.rpitft.data;

import javafx.scene.shape.Polyline;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

public class MapService
{
  // CHANGE THIS: image height in pixel
  private static final int IMAGE_HEIGHT_IN_PX = 320;
  // CHANGE THIS: image width in pixel
  private static final int IMAGE_WIDTH_IN_PX = 480;
  // CHANGE THIS: minimum padding in pixel
  private static final int MINIMUM_IMAGE_PADDING_IN_PX = 1;

  // formula for quarter PI
  private final static double QUARTERPI = Math.PI / 4.0;
  // some service that provides GPS data in longitude and latitude
  private final GPSService _gpsService;
  private final Polyline _polyline = new Polyline();
  private float _maxLatY = 200.0f;
  private float _maxLonX = 200.0f;
  private float _minLatY = -200.0f;
  private float _minLonX = -200.0f;

  public MapService(final GPSService gpsService_)
  {
    _gpsService = gpsService_;
  }

  public double getDistance()
  {
    final GPS start = _gpsService.getStart();
    final GPS finish = _gpsService.getFinish();
    double d2r = Math.PI / 180;

    double dlong = (finish.getLongitude() - start.getLongitude()) * d2r;
    double dlat = (finish.getLatitude() - start.getLatitude()) * d2r;
    double a =
      Math.pow(Math.sin(dlat / 2.0), 2)
        + Math.cos(start.getLatitude() * d2r)
        * Math.cos(finish.getLatitude() * d2r)
        * Math.pow(Math.sin(dlong / 2.0), 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return 6367 * c;
  }

  public float getMaxLatY()
  {
    return _maxLatY;
  }

  public float getMaxLonX()
  {
    return _maxLonX;
  }

  public float getMinLatY()
  {
    return _minLatY;
  }

  public float getMinLonX()
  {
    return _minLonX;
  }

  public Polyline getPolyline()
  {
    return _polyline;
  }

  public void makeMap()
  {
    // min and max coordinates, used in the computation below
    final Point2D.Double minXY = new Point2D.Double(-1, -1);
    final Point2D.Double maxXY = new Point2D.Double(-1, -1);


    final Collection<Point2D.Double> lonLat = new ArrayList<>();

    for (final GPS gps : _gpsService.getGPS())
    {
      // convert to radian
      float lon = gps.getLongitude();
      float lat = gps.getLatitude();
      double longitude = lon * Math.PI / 180;
      double latitude = lat * Math.PI / 180;

      _minLatY = (_minLatY == -200.0f) ? lat : Math.min(_minLatY, lat);
      _maxLatY = (_maxLatY == 200.0f) ? lat : Math.max(_maxLatY, lat);
      _minLonX = (_minLonX == -200.0f) ? lon : Math.min(_minLonX, lon);
      _maxLonX = (_maxLonX == 200.0f) ? lon : Math.max(_maxLonX, lon);

      final Point2D.Double xy = new Point2D.Double();
      xy.x = longitude;
      xy.y = Math.log(Math.tan(QUARTERPI + 0.5 * latitude));

      // The reason we need to determine the min X and Y values is because in order to draw the map,
      // we need to offset the position so that there will be no negative X and Y values
      minXY.x = (minXY.x == -1) ? xy.x : Math.min(minXY.x, xy.x);
      minXY.y = (minXY.y == -1) ? xy.y : Math.min(minXY.y, xy.y);

      lonLat.add(xy);
    }


    for (final Point2D.Double point : lonLat)
    {
      point.x = point.x - minXY.x;
      point.y = point.y - minXY.y;

      // now, we need to keep track the max X and Y values
      maxXY.x = (maxXY.x == -1) ? point.x : Math.max(maxXY.x, point.x);
      maxXY.y = (maxXY.y == -1) ? point.y : Math.max(maxXY.y, point.y);
    }


    int paddingBothSides = MINIMUM_IMAGE_PADDING_IN_PX * 2;

    // the actual drawing space for the map on the image
    int mapWidth = IMAGE_WIDTH_IN_PX - paddingBothSides;
    int mapHeight = IMAGE_HEIGHT_IN_PX - paddingBothSides;

    // determine the width and height ratio because we need to magnify the map to fit into the given image dimension
    double mapWidthRatio = mapWidth / maxXY.x;
    double mapHeightRatio = mapHeight / maxXY.y;

    // using different ratios for width and height will cause the map to be stretched. So, we have to determine
    // the global ratio that will perfectly fit into the given image dimension
    double globalRatio = Math.min(mapWidthRatio, mapHeightRatio);

    // now we need to readjust the padding to ensure the map is always drawn on the center of the given image dimension
    double heightPadding = (IMAGE_HEIGHT_IN_PX - (globalRatio * maxXY.y)) / 2;
    double widthPadding = (IMAGE_WIDTH_IN_PX - (globalRatio * maxXY.x)) / 2;


    for (final Point2D.Double point : lonLat)
    {
      double adjustedX = (widthPadding + (point.getX() * globalRatio));

      // need to invert the Y since 0,0 starts at top left
      double adjustedY = (IMAGE_HEIGHT_IN_PX - heightPadding - (point.getY() * globalRatio));

      _polyline.getPoints().addAll(adjustedX, adjustedY);
    }

  }

}
