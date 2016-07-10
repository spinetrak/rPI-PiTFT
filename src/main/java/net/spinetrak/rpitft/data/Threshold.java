package net.spinetrak.rpitft.data;

import javafx.scene.text.Text;

public class Threshold
{
  private static final String FX_FILL_AMBER = "-fx-fill: orange;";
  private static final String FX_FILL_GREEN = "-fx-fill: green;";
  private static final String FX_FILL_RED = "-fx-fill: red;";
  private final float _amber;
  private final float _red;
  private final Text _text;
  private boolean _inverse;
  private State _state;

  public Threshold(final Text text_, final float red_, final float amber_)
  {
    _text = text_;
    _red = red_;
    _amber = amber_;
    _state = State.GREEN;
    _inverse = red_ < amber_;
  }

  public void setColor(final float data_)
  {
    if (!_inverse)
    {
      if (data_ >= _red && _state != State.RED)
      {
        _state = State.RED;
        _text.setStyle(FX_FILL_RED);
      }
      else if (data_ < _red && data_ >= _amber && _state != State.AMBER)
      {
        _state = State.AMBER;
        _text.setStyle(FX_FILL_AMBER);
      }
      else if (data_ > _amber && _state != State.GREEN)
      {
        _state = State.GREEN;
        _text.setStyle(FX_FILL_GREEN);
      }
    }
    else
    {
      if (data_ <= _red && _state != State.RED)
      {
        _state = State.RED;
        _text.setStyle(FX_FILL_RED);
      }
      else if (data_ > _red && data_ <= _amber && _state != State.AMBER)
      {
        _state = State.AMBER;
        _text.setStyle(FX_FILL_AMBER);
      }
      else if (data_ < _amber && _state != State.GREEN)
      {
        _state = State.GREEN;
        _text.setStyle(FX_FILL_GREEN);
      }
    }
  }

  public enum State
  {
    RED,
    AMBER,
    GREEN
  }
}
