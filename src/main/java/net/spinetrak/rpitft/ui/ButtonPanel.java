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
 *
 */

package net.spinetrak.rpitft.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.io.IOException;

import static javafx.application.Platform.exit;

class ButtonPanel
{
  private final HBox _bottom;

  ButtonPanel()
  {
    _bottom = new HBox();
    _bottom.setPadding(new Insets(5));
    _bottom.setSpacing(5);
    _bottom.setAlignment(Pos.CENTER_RIGHT);

    final Button exit = getExitButton();
    _bottom.getChildren().add(exit);
    final Button restart = getRestartButton();
    _bottom.getChildren().add(restart);

    final Button shutdown = getShutdownButton();
    _bottom.getChildren().add(shutdown);
  }

  HBox getBottom()
  {
    return _bottom;
  }

  private Button getExitButton()
  {
    final Image exitImg = new Image(getClass().getResourceAsStream("/exit.png"));
    final Button exit = new Button();
    exit.setGraphic(new ImageView(exitImg));

    exit.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        exit.setText("exiting...");
        exit();
        exit.setText("done...");
        System.exit(0);
      }
    });
    return exit;
  }

  private Button getRestartButton()
  {
    final Image restartImg = new Image(getClass().getResourceAsStream("/restart.png"));
    final Button restart = new Button();
    restart.setGraphic(new ImageView(restartImg));
    restart.setOnKeyPressed(event_ -> {
      if (event_.getCode().equals(KeyCode.ENTER))
      {
        final Runtime runtime = Runtime.getRuntime();
        try
        {
          final Process proc = runtime.exec("sudo shutdown -r now");
        }
        catch (final IOException ex_)
        {
          ex_.printStackTrace();
        }
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
        final Runtime runtime = Runtime.getRuntime();
        try
        {
          final Process proc = runtime.exec("sudo shutdown now");
        }
        catch (final IOException ex_)
        {
          ex_.printStackTrace();
        }
        System.exit(0);
      }
    });
    return shutdown;
  }
}
