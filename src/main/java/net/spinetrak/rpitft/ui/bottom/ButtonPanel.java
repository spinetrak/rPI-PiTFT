/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 spinetrak
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

package net.spinetrak.rpitft.ui.bottom;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.Formatter;
import net.spinetrak.rpitft.data.listeners.HotspotListener;
import net.spinetrak.rpitft.data.listeners.NetworkListener;
import net.spinetrak.rpitft.data.network.Network;
import net.spinetrak.rpitft.data.network.hotspot.Hotspot;
import net.spinetrak.rpitft.data.streams.command.SingleLineStream;
import net.spinetrak.rpitft.data.streams.logger.InitialStateStream;

import static javafx.application.Platform.exit;
import static net.spinetrak.rpitft.command.Commands.*;

public class ButtonPanel implements NetworkListener, HotspotListener
{
  private final HBox _bottom;
  private final Text _hotspotText;
  private final Text _networkText;

  public ButtonPanel()
  {
    _bottom = new HBox();
    _bottom.setPadding(new Insets(1));
    _bottom.setSpacing(1);
    _bottom.setAlignment(Pos.CENTER_RIGHT);

    final HBox statusPanel = new HBox();
    statusPanel.setAlignment(Pos.CENTER_LEFT);

    _hotspotText = new Text("");
    statusPanel.getChildren().add(_hotspotText);

    _networkText = new Text("");
    statusPanel.getChildren().add(_networkText);

    _bottom.getChildren().add(statusPanel);

    final Button stream = getStreamToggleButton();
    stream.setPrefSize(15, 15);
    stream.setMaxHeight(15);
    _bottom.getChildren().add(stream);

    final Button exit = getExitButton();
    exit.setPrefSize(15, 15);
    exit.setMaxHeight(15);
    _bottom.getChildren().add(exit);

    final Button gpx = getGPXButton();
    gpx.setPrefSize(15, 15);
    gpx.setMaxHeight(15);
    _bottom.getChildren().add(gpx);

    final Button nmea = getNMEAButton();
    nmea.setPrefSize(15, 15);
    nmea.setMaxHeight(15);
    _bottom.getChildren().add(nmea);

    final Button restart = getRestartButton();
    restart.setPrefSize(15, 15);
    restart.setMaxHeight(15);
    _bottom.getChildren().add(restart);

    final Button shutdown = getShutdownButton();
    shutdown.setPrefSize(15, 15);
    shutdown.setMaxHeight(15);
    _bottom.getChildren().add(shutdown);

    Platform.runLater(gpx::requestFocus);
    Dispatcher.getInstance().addListener(this);
  }

  public HBox getPanel()
  {
    return _bottom;
  }

  @Override
  public void handleHotspotData(final Hotspot hotspot_)
  {
    if (hotspot_.isConnected())
    {
      _hotspotText.setText(
        Formatter.formatHotspot(hotspot_.getStatus().getBatteryPercent(), hotspot_.getTraffic().getTotalDataVolume()));
    }
  }

  @Override
  public void handleNetworkData(final Network network_)
  {
    final StringBuilder statusText = new StringBuilder();

    final String status = network_.getMessage();
    final boolean isUp = network_.isUp();
    if (status.length() > 30)
    {
      //_networkText.setText(Formatter.formatNetwork(status.substring(0, 28).concat("..."), isUp));
      statusText.append(Formatter.formatNetwork(status.substring(0, 28).concat("..."), isUp));
    }
    else
    {
      //_networkText.setText(Formatter.formatNetwork(status, isUp));
      statusText.append(Formatter.formatNetwork(status, isUp));
    }

    _networkText.setText(statusText.toString());
  }

  private Button getExitButton()
  {
    final Image exitImg = new Image(getClass().getResourceAsStream("/exit.png"));
    final Button exit = new Button();
    exit.setGraphic(new ImageView(exitImg));

    exit.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        exit();
        System.exit(0);
      }
    });
    return exit;
  }

  private Button getGPXButton()
  {
    final Image gpxImg = new Image(getClass().getResourceAsStream("/gpx.png"));
    final Button gpx = new Button();
    gpx.setGraphic(new ImageView(gpxImg));

    gpx.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        GPX_NEW.execute(new SingleLineStream());
      }
    });
    return gpx;
  }

  private Button getNMEAButton()
  {
    final Image nmeaImg = new Image(getClass().getResourceAsStream("/nmea.png"));
    final Button nmea = new Button();
    nmea.setGraphic(new ImageView(nmeaImg));

    nmea.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        GPX_NEW.execute(new SingleLineStream());
        NMEA_BACKUP.execute(new SingleLineStream());
        RESTART.execute(new SingleLineStream());
        System.exit(0);
      }
    });
    return nmea;
  }

  private Button getRestartButton()
  {
    final Image restartImg = new Image(getClass().getResourceAsStream("/restart.png"));
    final Button restart = new Button();
    restart.setGraphic(new ImageView(restartImg));
    restart.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        RESTART.execute(new SingleLineStream());
        System.exit(0);
      }
    });
    return restart;
  }

  private Button getShutdownButton()
  {
    final Image shutdownImg = new Image(getClass().getResourceAsStream("/shutdown.png"));
    final Button shutdown = new Button();
    shutdown.setGraphic(new ImageView(shutdownImg));
    shutdown.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        SHUTDOWN.execute(new SingleLineStream());
        System.exit(0);
      }
    });
    return shutdown;
  }

  private Button getStreamToggleButton()
  {
    final Image streamOnImg = new Image(getClass().getResourceAsStream("/streamOn.png"));
    final Image streamOffImg = new Image(getClass().getResourceAsStream("/streamOff.png"));
    final Button stream = new Button();

    final boolean onStart = InitialStateStream.getInstance().isStreamingEnabled();
    stream.setGraphic(new ImageView((onStart) ? streamOnImg : streamOffImg));

    stream.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        final boolean onNow = InitialStateStream.getInstance().isStreamingEnabled();
        InitialStateStream.getInstance().setStreamingEnabled(!onNow);
        final boolean onThen = InitialStateStream.getInstance().isStreamingEnabled();
        stream.setGraphic(new ImageView((onThen) ? streamOnImg : streamOffImg));
        stream.getGraphic();
      }
    });
    return stream;
  }
}
