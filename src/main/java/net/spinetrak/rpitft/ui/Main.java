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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.Queue;
import net.spinetrak.rpitft.data.parser.TXTParser;
import net.spinetrak.rpitft.ui.bottom.ButtonPanel;
import net.spinetrak.rpitft.ui.center.TabPanel;

import static net.spinetrak.rpitft.ui.center.Charts.MIN_HEIGHT;
import static net.spinetrak.rpitft.ui.center.Charts.MIN_WIDTH;

public class Main extends Application
{
  private final Dispatcher _dispatcher = Dispatcher.getInstance();
  private ButtonPanel _buttonPanel;
  private TabPanel _tabPanel;

  public static void main(String[] args_)
  {
    launch(args_);
  }


  @Override
  public void start(final Stage stage_)
  {
    init(stage_);
    stage_.show();
    final boolean mockdata = Boolean.valueOf(System.getProperty("mockdata"));
    final Queue queue = new Queue(mockdata);

    queue.start(_dispatcher);
  }

  private void init(final Stage stage_)
  {
    SentenceFactory.getInstance().registerParser("TXT", TXTParser.class);

    _tabPanel = new TabPanel();
    _buttonPanel = new ButtonPanel();

    final VBox pane = new VBox(_tabPanel.getPanel(), _buttonPanel.getPanel());
    pane.setPadding(new Insets(1));
    pane.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
    pane.setMaxHeight(MIN_HEIGHT);
    pane.setMinHeight(MIN_HEIGHT);
    pane.setMaxWidth(MIN_WIDTH);
    pane.setMinWidth(MIN_WIDTH);

    final Scene scene = new Scene(pane, MIN_WIDTH, MIN_HEIGHT);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }
}
