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

package net.spinetrak.rpitft.data.location;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import net.sf.marineapi.nmea.event.AbstractSentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NmeaLogger
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.location.NmeaLogger");
  private static final String OUTFILE = "/home/pi/tracks/nmea.txt";
  private static int COUNTER;
  private final ConcurrentLinkedQueue<GPS> _queue = new ConcurrentLinkedQueue<>();
  private InputStream _inputStream;
  private PrintStream _printStream;
  private SentenceReader _sentenceReader;

  public NmeaLogger()
  {
    initOutput(OUTFILE);
    initInput(true);
  }

  public ConcurrentLinkedQueue<GPS> getQueue()
  {
    return _queue;
  }

  private SerialPort getSerialPort() throws IOException, PortInUseException, UnsupportedCommOperationException
  {
    System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
    final Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
    SerialPort sp = null;
    while (e.hasMoreElements())
    {
      final CommPortIdentifier id = (CommPortIdentifier) e.nextElement();
      if (id.getPortType() == CommPortIdentifier.PORT_SERIAL)
      {
        sp = (SerialPort) id.open("SerialExample", 30);
        sp.setSerialPortParams(9600, SerialPort.DATABITS_8,
                               SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        if (isValid(sp.getInputStream()))
        {
          break;
        }
      }
    }
    return sp;
  }

  private void initInput(final boolean mock_)
  {
    try
    {
      final SerialPort sp = getSerialPort();

      if (sp != null)
      {
        if (mock_)
        {
          _inputStream = new FileInputStream(new File("/home/pi/tracks/nmea_test.txt"));
        }
        else
        {
          _inputStream = sp.getInputStream();
        }
        _sentenceReader = new SentenceReader(_inputStream);
        _sentenceReader.addSentenceListener(new GGAListener());
        _sentenceReader.addSentenceListener(new RMCListener());
        _sentenceReader.start();
      }
    }
    catch (final IOException | PortInUseException | UnsupportedCommOperationException ex_)
    {
      ex_.printStackTrace();
      LOGGER.error(ex_.getMessage());
      IOUtils.closeQuietly(_inputStream);
      _inputStream = null;
    }
    finally
    {
      if (_sentenceReader != null)
      {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            _sentenceReader.stop();
        }));
      }
      if (_inputStream != null)
      {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          IOUtils.closeQuietly(_inputStream);
        }));
      }
    }
  }

  private void initOutput(final String outputFile_)
  {
    try
    {
      _printStream = new PrintStream(
        new BufferedOutputStream(new FileOutputStream(outputFile_, true)),
        false,
        "US-ASCII");
    }
    catch (final UnsupportedEncodingException | FileNotFoundException ex_)
    {
      ex_.printStackTrace();
      LOGGER.error(ex_.getMessage());
      IOUtils.closeQuietly(_printStream);
      _printStream = null;
    }
    finally
    {
      if (_printStream != null)
      {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          IOUtils.closeQuietly(_printStream);
        }));
      }
    }
  }

  private boolean isValid(final InputStream is_) throws IOException
  {
    final InputStreamReader isr = new InputStreamReader(is_);
    final BufferedReader buf = new BufferedReader(isr);

    boolean valid = false;
    // try each port few times before giving up
    for (int i = 0; i < 5; i++)
    {
      try
      {
        final String data = buf.readLine();
        if (SentenceValidator.isValid(data))
        {
          LOGGER.info("NMEA data found!");
          valid = true;
          break;
        }
      }
      catch (final Exception ex_)
      {
        ex_.printStackTrace();
        LOGGER.error(ex_.getMessage());
      }
    }
    isr.close();
    buf.close();
    return valid;
  }

  private class GGAListener extends AbstractSentenceListener<GGASentence>
  {
    @Override
    public void sentenceRead(final GGASentence gga_)
    {
      final GpsFixQuality fix = gga_.getFixQuality();
      if (GpsFixQuality.INVALID != fix)
      {
        COUNTER++;
        _printStream.println(gga_.toSentence());
        _printStream.flush();
        _queue.add(GPS.fromGGASentence(gga_, COUNTER));
      }
    }
  }

  private class RMCListener extends AbstractSentenceListener<RMCSentence>
  {
    @Override
    public void sentenceRead(final RMCSentence rmc_)
    {
      final FaaMode mode = rmc_.getMode();
      if (FaaMode.NONE != mode)
      {
        _printStream.println(rmc_.toSentence());
        _printStream.flush();
        _queue.add(GPS.fromRMCSentence(rmc_));
      }
    }
  }
}
