#!/bin/bash

cd /home/pi/java/rPI-PiTFT

if [ ! -e /dev/ttyS80 ]
then
    sudo ln -s /dev/ttyACM0 /dev/ttyS80
fi

mvn install > /var/log/rpitft.log 2>&1
sudo java -cp target/rPI-PiTFT-1.0-SNAPSHOT-jar-with-dependencies.jar -Djava.library.path=/usr/lib/jni -Djava.ext.dirs=/usr/lib/jvm/openjfx-8u60-sdk-overlay-linux-armv6hf/jre/lib/ext/ -Djavafx.platform=monocle -Dmonocle.screen.fb=/dev/fb1  net.spinetrak.rpitft.ui.Main  >> /var/log/rpitft.log 2>&1


