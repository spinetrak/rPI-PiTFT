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

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class LocalFileStream implements SentenceListener
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    "net.spinetrak.rpitft.data.streams.logger.LocalFileStream");
  private PrintStream _printStream;

  public LocalFileStream(final String outputFile_)
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
