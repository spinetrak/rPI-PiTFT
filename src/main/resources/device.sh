#!/bin/bash

cpu=$(/bin/cat <(grep 'cpu ' /proc/stat) | awk -v RS="" '{print ($13-$2+$15-$4)*100/($13-$2+$15-$4+$16-$5)}')
disk=$(/bin/df -l /home/pi | awk '{if ($6 == "/") { print $3/$2*100 }}')
temperature=$(/opt/vc/bin/vcgencmd measure_temp | awk -F "=" '{print $2}' | awk -F "'" '{print $1}')
memory=$(cat /proc/meminfo)
total=$(echo "${memory}" | grep -i "memtotal" | awk -F ':' '{print $2}' | tr -d ' kB')
free=$(echo "${memory}" | grep -i "memavailable" | awk -F ':' '{print $2}' | tr -d ' kB')
used=$(echo "scale=0; ${total}-${free}" | bc -l)
memorypercent=$(echo "scale=2; (${used}*100)/${total}" | bc -l)

echo "${cpu}/${disk}/${memorypercent}/${temperature}"