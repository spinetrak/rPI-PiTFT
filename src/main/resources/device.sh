#!/bin/bash

cpu=$(/bin/cat <(grep 'cpu ' /proc/stat) | awk -v RS="" '{print ($13-$2+$15-$4)*100/($13-$2+$15-$4+$16-$5)}')
disk=$(/bin/df -lh | awk '{if ($6 == "/") { print $5 }}' | head -1 | cut -d'%' -f1)
temperature=$(/opt/vc/bin/vcgencmd measure_temp | awk -F "=" '{print $2}' | awk -F "'" '{print $1}')

echo "${cpu}/${disk}/${temperature}"