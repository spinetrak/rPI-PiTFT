#!/bin/bash

cd /home/pi/java/rPI-PiTFT

if [ ! -e /dev/ttyS80 ]
then
    sudo ln -s /dev/ttyACM0 /dev/ttyS80
fi

java -cp target/rPI-PiTFT-1.0-SNAPSHOT-jar-with-dependencies.jar -Djava.library.path=/usr/lib/jni -Djava.ext.dirs=/usr/lib/jvm/openjfx-8u60-sdk-overlay-linux-armv6hf/jre/lib/ext/ -Djava.util.logging.config.file=logging.properties -Djavafx.platform=monocle -Dmonocle.screen.fb=/dev/fb1  -Dinitialstatekey=your_initialstate_key net.spinetrak.rpitft.JavaFXMain  >> /var/log/rpiJavaFX.log 2>&1


