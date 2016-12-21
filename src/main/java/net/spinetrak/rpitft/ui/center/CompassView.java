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

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.spinetrak.rpitft.data.location.GPS;

import java.util.Locale;

class CompassView
{
  private final Gauge _compass;
  private final VBox _compassBox;

  CompassView()
  {
    final Label compassValue = new Label("0\u00B0");
    _compass = initCompass(compassValue);
    _compassBox = new VBox(_compass, compassValue);
    _compassBox.setSpacing(2);
    _compassBox.setPadding(new Insets(1));

  }

  void addData(final GPS gps_)
  {
    if (gps_.isValidMovement())
    {
      _compass.setValue(gps_.getCourse());
    }
  }

  Node getPane()
  {
    return _compassBox;
  }

  private Gauge initCompass(final Label compassValue_)
  {
    final Gauge gauge = GaugeBuilder.create()
      .prefSize(200, 200)
      .borderPaint(Gauge.DARK_COLOR)
      .minValue(0)
      .maxValue(359)
      .autoScale(false)
      .startAngle(180)
      .angleRange(360)
      .minorTickMarksVisible(false)
      .mediumTickMarksVisible(false)
      .majorTickMarksVisible(false)
      .customTickLabelsEnabled(true)
      .customTickLabels("N", "", "", "", "", "", "", "", "",
                        "E", "", "", "", "", "", "", "", "",
                        "S", "", "", "", "", "", "", "", "",
                        "W", "", "", "", "", "", "", "", "")
      .customTickLabelFontSize(48)
      .knobType(Gauge.KnobType.FLAT)
      .knobColor(Gauge.DARK_COLOR)
      .needleShape(Gauge.NeedleShape.FLAT)
      .needleType(Gauge.NeedleType.FAT)
      .needleBehavior(Gauge.NeedleBehavior.OPTIMIZED)
      .tickLabelColor(Gauge.DARK_COLOR)
      .animated(true)
      .animationDuration(500)
      .valueVisible(false)
      .build();

    gauge.valueProperty().addListener(o -> {
      compassValue_.setText(String.format(Locale.US, "%.0f\u00B0", gauge.getValue()));
    });


    compassValue_.setFont(Fonts.latoBold(72));
    compassValue_.setAlignment(Pos.CENTER);
    compassValue_.setPrefWidth(400);

    return gauge;
  }
}
