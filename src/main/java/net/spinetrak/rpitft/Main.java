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

package net.spinetrak.rpitft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.Main");

  public static void main(final String[] args_)
  {
    if (1 == args_.length && "-headless".equals(args_[0].toLowerCase()))
    {
      LOGGER.info("Starting headless.");
      HeadlessMain.main(args_);
    }
    else
    {
      LOGGER.info("Starting headless.");
      HeadlessMain.main(args_);
      LOGGER.info("Starting JavaFX.");
      JavaFXMain.main(args_);
    }
  }
}
