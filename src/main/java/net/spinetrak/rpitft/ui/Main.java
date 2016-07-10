package net.spinetrak.rpitft.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.Power;
import net.spinetrak.rpitft.data.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main extends Application
{
  private ButtonPanel _buttonPanel;
  private Chart _chart;
  private TextPanel _textPanel;

  public static void main(String[] args_)
  {
    launch(args_);
  }

  public void addDataToSeries(final ConcurrentLinkedQueue<Power> powerQueue_,
                              final ConcurrentLinkedQueue<Device> deviceQueue_)
  {
    while (!powerQueue_.isEmpty())
    {
      final Power power = powerQueue_.remove();
      _textPanel.addData(power);
      _chart.addData(power);

    }
    while (!deviceQueue_.isEmpty())
    {
      final Device device = deviceQueue_.remove();

      _textPanel.addData(device);
    }
  }

  public TextPanel getTextPanel()
  {
    return _textPanel;
  }

  @Override
  public void start(final Stage stage_)
  {
    init(stage_);
    stage_.show();

    final Queue queue = new Queue();
    queue.start(this);
  }

  private void init(final Stage stage_)
  {
    _chart = new Chart(this);
    _textPanel = new TextPanel();

    final BorderPane border = new BorderPane();
    border.setTop(_textPanel.getTop());

    _buttonPanel = new ButtonPanel();

    border.setBottom(_buttonPanel.getBottom());
    border.setCenter(_chart.getPowerLineChart());

    final Scene scene = new Scene(border, 320, 240);
    scene.getStylesheets().add("stylesheet.css");
    stage_.setScene(scene);
    stage_.setFullScreen(true);
  }
}
