#!/bin/bash

now=$(date +%F_%H:%M:%S)
mv /home/pi/tracks/nmea.txt /home/pi/tracks/nmea_$now.txt
