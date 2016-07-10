package net.spinetrak.rpitft.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import net.spinetrak.rpitft.data.Device;
import net.spinetrak.rpitft.data.Power;

public class TextPanel
{
  private static final String FX_FILL_BLACK = "-fx-fill: black;";
  private static final String FX_FILL_RED = "-fx-fill: red;";
  private final Text _altitude;
  private final Text _batteryCapacity;
  private final Text _batteryPower;
  private final Text _cpu;
  private final Text _disk;
  private final Text _latitude;
  private final Text _longitude;
  private final Text _temperature;
  private final Text _time;
  private final FlowPane _top;
  private final Text _trackPoints;
  private boolean _cpuAlert = false;
  private boolean _diskAlert = false;
  private boolean _temperatureAlert = false;

  public TextPanel()
  {
    _top = new FlowPane(Orientation.HORIZONTAL);
    _top.setPadding(new Insets(5));

    _time = new Text("[hh:mm:ss]");
    _top.getChildren().add(_time);

    _latitude = new Text("[xxxx.xxx N]");
    _top.getChildren().add(_latitude);

    _longitude = new Text("[xxxx.xxx E]");
    _top.getChildren().add(_longitude);

    _altitude = new Text("[xxxx.x M]");
    _top.getChildren().add(_altitude);

    _trackPoints = new Text("[xxxxxxx]");
    _top.getChildren().add(_trackPoints);

    _temperature = new Text("[xx.x C°]");
    _top.getChildren().add(_temperature);

    _cpu = new Text("[xx.x% cpu]");
    _top.getChildren().add(_cpu);

    _disk = new Text("[xx% hd]");
    _top.getChildren().add(_disk);

    _batteryCapacity = new Text("[xxx.xx% bat]");
    _top.getChildren().add(_batteryCapacity);

    _batteryPower = new Text("[xxx.xxx mA]");
    _top.getChildren().add(_batteryPower);
  }

  public void addData(final Power power_)
  {
    _batteryCapacity.setText(String.format("[%.2f%% bat]", power_.getCapacity()));
    _batteryPower.setText(String.format("[%.2f mA]", power_.getPower()));
  }

  public void addData(final Device device_)
  {

    final float cpu = device_.getCpu();
    final float disk = device_.getDisk();
    final float temperature = device_.getTemperature();

    _cpu.setText(String.format("[%.2f%% cpu]", cpu));
    if (cpu >= 90 && !_cpuAlert)
    {
      _cpuAlert = true;
      _cpu.setStyle(FX_FILL_RED);
    }
    else if (cpu < 90 && _cpuAlert)
    {
      _cpuAlert = false;
      _cpu.setStyle(FX_FILL_BLACK);
    }

    _disk.setText(String.format("[%.2f%% hd]", disk));
    if (disk >= 90 && !_diskAlert)
    {
      _diskAlert = true;
      _disk.setStyle(FX_FILL_RED);
    }
    else if (disk < 90 && _diskAlert)
    {
      _diskAlert = false;
      _disk.setStyle(FX_FILL_BLACK);
    }

    _temperature.setText(String.format("[%.2f C°]", temperature));
    if (temperature >= 75 && !_temperatureAlert)
    {
      _temperatureAlert = true;
      _temperature.setStyle(FX_FILL_RED);
    }
    else if (temperature < 75 && _temperatureAlert)
    {
      _temperatureAlert = false;
      _temperature.setStyle(FX_FILL_BLACK);
    }

  }

  public FlowPane getTop()
  {
    return _top;
  }

  public void setPowerAlarm(final boolean on_)
  {
    if (on_)
    {
      _batteryPower.setStyle(FX_FILL_RED);
      _batteryCapacity.setStyle(FX_FILL_RED);
    }
    else
    {
      _batteryPower.setStyle(FX_FILL_BLACK);
      _batteryCapacity.setStyle(FX_FILL_BLACK);
    }
  }
}
