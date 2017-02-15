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

package net.spinetrak.rpitft.data.streams.logger;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import net.sf.marineapi.nmea.event.AbstractSentenceListener;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.spinetrak.rpitft.data.Dispatcher;
import net.spinetrak.rpitft.data.location.GPS;
import net.spinetrak.rpitft.data.network.Network;
import net.spinetrak.rpitft.data.nmeaparser.TXTParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;

public class NmeaFileLogger
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.streams.logger.NmeaLogger");
  private static final String OUTFILE = "/home/pi/tracks/nmea.txt";
  private static int COUNTER;
  private static NmeaFileLogger _instance = null;
  private final LocalFileStream _localFileStream;
  private InputStream _inputStream;
  private SentenceReader _sentenceReader;

  private NmeaFileLogger()
  {
    SentenceFactory.getInstance().registerParser("TXT", TXTParser.class);
    LOGGER.info("TXT parser registered.");

    _localFileStream = new LocalFileStream(OUTFILE);
  }

  public static NmeaFileLogger getInstance()
  {
    if (_instance == null)
    {
      _instance = new NmeaFileLogger();
    }
    return _instance;
  }

  public void start()
  {
    final boolean mock = Boolean.valueOf(System.getProperty("mockdata"));
    try
    {
      final SerialPort sp = getSerialPort();

      if (sp != null)
      {
        if (mock)
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
        _sentenceReader.addSentenceListener(_localFileStream);
        _sentenceReader.start();
      }
    }
    catch (final IOException | PortInUseException | UnsupportedCommOperationException ex_)
    {
      final String msg = ex_.getMessage();
      final Network network = new Network(msg);
      Dispatcher.getInstance().dispatch(network);
      LOGGER.error(msg);
      ex_.printStackTrace();
      IOUtils.closeQuietly(_inputStream);
      _inputStream = null;
    }
    finally
    {
      if (_sentenceReader != null)
      {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            _sentenceReader.stop();
          LOGGER.info("Sentence reader stopped.");
        }));
      }
      if (_inputStream != null)
      {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          IOUtils.closeQuietly(_inputStream);
          LOGGER.info("Input stream closed.");
        }));
      }
    }
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
        final String msg = ex_.getMessage();
        final Network network = new Network(msg);
        Dispatcher.getInstance().dispatch(network);
        ex_.printStackTrace();
        LOGGER.error(msg);
      }
    }
    isr.close();
    buf.close();
    return valid;
  }

  private static class LocalFileStream implements SentenceListener
  {
    private PrintStream _printStream;

    LocalFileStream(final String outputFile_)
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
            LOGGER.info("Print stream closed.");
          }));
        }
      }
    }

    @Override
    public void readingPaused()
    {
      _printStream.flush();
    }

    @Override
    public void readingStarted()
    {
      //ignore
    }

    @Override
    public void readingStopped()
    {
      _printStream.flush();
    }

    @Override
    public void sentenceRead(final SentenceEvent sentenceEvent_)
    {
      final Sentence sentence = sentenceEvent_.getSentence();
      if (sentence instanceof GGASentence)
      {
        final GGASentence gga = (GGASentence) sentence;
        _printStream.println(gga.toSentence());
        _printStream.flush();
      }

      if (sentence instanceof RMCSentence)
      {
        final RMCSentence rmc = (RMCSentence) sentence;
        _printStream.println(rmc.toSentence());
        _printStream.flush();
      }
    }
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
        Dispatcher.getInstance().dispatch(GPS.fromGGASentence(gga_, COUNTER));
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
        Dispatcher.getInstance().dispatch(GPS.fromRMCSentence(rmc_));
      }
    }
  }
}
