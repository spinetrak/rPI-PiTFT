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

package net.spinetrak.rpitft.data.raspberry;

import com.pi4j.system.SystemInfo;
import net.spinetrak.rpitft.data.events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Device implements Event
{
  private final static Logger LOGGER = LoggerFactory.getLogger("net.spinetrak.rpitft.data.raspberry.Device");
  private float _cpu = Float.NaN;
  private float _disk = Float.NaN;
  private boolean _hasError = false;
  private float _memory = Float.NaN;
  private float _temperature = Float.NaN;

  Device()
  {
    try
    {
      _memory = calculateMemory();
      _cpu = calculateCPU();
      _temperature = SystemInfo.getCpuTemperature();
      _disk = calculateFreeDiskSpace();
    }
    catch (final IOException | InterruptedException | MalformedObjectNameException | InstanceNotFoundException | ReflectionException ex_)
    {
      LOGGER.error("Error calculating data: " + ex_.getMessage());
      _hasError = true;
    }
  }

  boolean isHasError()
  {
    return _hasError;
  }

  public float getCpu()
  {
    return _cpu;
  }

  public float getDisk()
  {
    return _disk;
  }

  public float getMemory()
  {
    return _memory;
  }

  public float getTemperature()
  {
    return _temperature;
  }

  private float calculateCPU() throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException
  {
    final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    final ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
    final AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

    if (list.isEmpty())
    {
      return Float.NaN;
    }

    final Attribute att = (Attribute) list.get(0);
    final float value = (float) ((double) att.getValue());

    if (value == -1.0)
    {
      return Float.NaN;
    }
    return (value * 1000) / 10.0f;
  }

  private float calculateFreeDiskSpace() throws IOException
  {
    float freeTotal = 0.0f;
    float totalTotal = 0.0f;
    float totalUsed;
    for (final Path root : FileSystems.getDefault().getRootDirectories())
    {
      final FileStore store = Files.getFileStore(root);
      freeTotal += store.getUsableSpace();
      totalTotal += store.getTotalSpace();
    }
    totalUsed = totalTotal - freeTotal;
    return totalUsed * 100 / totalTotal;
  }

  private float calculateMemory() throws IOException, InterruptedException
  {
    final long total = SystemInfo.getMemoryTotal();
    final long free = SystemInfo.getMemoryFree();
    final long used = total - free;
    return (float) used * 100 / total;
  }
}
