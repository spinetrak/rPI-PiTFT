#!/bin/bash
cd /opt/susvd
source=$(./susv -status | grep Source | awk '{ print $4 }')
capacity=$(./susv -capbat  0 | head -n 1)
capInt=${capacity%.*}
voltage=$(./susv -vin 0)
pwrbat=$(./susv -pwrbat 0)

echo "${source:0:1}/${capacity}%/${voltage}V/${pwrbat}mA"