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

package net.spinetrak.rpitft.data;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class GPSTest
{
  @org.junit.Test
  public void parse() throws Exception
  {
    final GPS gps = new GPS();

    final float north = gps.parseCoordinates("3333.5825N");
    assertEquals(String.format("%.4f", 33.5597), String.format("%.4f", north));

    final float east = gps.parseCoordinates("00758.2052E");
    assertEquals(String.format("%.4f", 7.9701), String.format("%.4f", east));

    final float south = gps.parseCoordinates("3333.5825S");
    assertEquals(String.format("%.4f", -33.5597), String.format("%.4f", south));

    final float west = gps.parseCoordinates("00758.2052W");
    assertEquals(String.format("%.4f", -7.9701), String.format("%.4f", west));
  }

  @org.junit.Test
  public void fromString()
  {
    final GPS gps1 = GPS.fromString("130001/0053.1111N/00009.2222E/1/10.1");
    assertFalse(gps1.isHasError());
    final GPS gps2 = GPS.fromString("130002/0053.2222N/0009.3333E/2/11.2");
    assertFalse(gps2.isHasError());
    final GPS gps3 = GPS.fromString("130003/0053.3333N/0009.4444E/3/12.3");
    assertFalse(gps3.isHasError());
  }

}