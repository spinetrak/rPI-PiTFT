#!/bin/bash

GGA=$(tail -n 25 /Users/renzop/nmea.txt | grep -a GGA)
time=$(echo ${GGA} | awk -F',' '{ print $2 }' | awk -F'.' '{ print $1 }')
latitude=$(echo ${GGA} | awk -F',' '{ print $3 }')
northsouth=$(echo ${GGA} | awk -F',' '{ print $4 }')
longitude=$(echo ${GGA} | awk -F',' '{ print $5 }')
eastwest=$(echo ${GGA} | awk -F',' '{ print $6 }')
altitude=$(echo ${GGA} | awk -F',' '{ print $10 }')
points=$(cat /Users/renzop/nmea.txt | grep -a GGA | wc -l)

echo "${time}/${latitude}${northsouth}/${longitude}${eastwest}/${points}/${altitude}"
