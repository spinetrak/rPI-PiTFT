#!/bin/bash

cd /home/pi/java/rPI-PiTFT

if [ ! -e /dev/ttyS80 ]
then
    sudo ln -s /dev/ttyACM0 /dev/ttyS80
fi

java -cp target/rPI-PiTFT-1.0-SNAPSHOT-jar-with-dependencies.jar -Djava.library.path=/usr/lib/jni -Djava.util.logging.config.file=logging.properties -DPROD_MODE=true -Dinitialstatekey=your_initialstate_key net.spinetrak.rpitft.HeadlessMain  >> /dev/null 2>&1


