#!/bin/bash

now=$(date +%F_%H-%M-%S)
/usr/bin/gpsbabel -D 3 -i nmea -f /home/pi/tracks/nmea.txt -x simplify,count=1500 -o gpx,gpxver=1.1 -F /home/pi/tracks/gpx/$now.gpx
