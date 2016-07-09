#!/bin/bash

cpu=$(/bin/cat <(grep 'cpu ' /proc/stat) | awk -v RS="" '{print ($13-$2+$15-$4)*100/($13-$2+$15-$4+$16-$5)}')
disk=$(/bin/df -l /home/pi | awk '{if ($6 == "/") { print $3/$2*100 }}')
temperature=$(/opt/vc/bin/vcgencmd measure_temp | awk -F "=" '{print $2}' | awk -F "'" '{print $1}')

echo "${cpu}/${disk}/${temperature}"