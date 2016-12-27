# rPI-PiTFT

A simple read-only JavaFX user interface for using an RPi3 as a GPS data logger (in NMEA format) in the car.
Now also logs to [Initial State] (https://initialstate.com/).

Hardware Components:
- [Raspberry Pi 3 Model B] (https://www.raspberrypi.org/products/raspberry-pi-3-model-b/)
- [3.5" PiTFT] (https://learn.adafruit.com/running-opengl-based-games-and-emulators-on-adafruit-pitft-displays/3-dot-5-pitft)
- ~~[S.USV] (http://www.s-usv.de/index_en.php)~~ (currently broken, temporarly disabled)
- [GPS] (http://www.navilock.de/produkte/G_62523/merkmale.html?setLanguage=en)

Software Components:
- [OpenJFX] (http://chriswhocodes.com/)
- [Java Marine API] (http://ktuukkan.github.io/marine-api/)
- [rxtx] (https://github.com/rxtx/rxtx)
- [Pi4J] (http://pi4j.com/)
- [Medusa] (https://github.com/HanSolo/Medusa)
- [initialState-java] (https://github.com/kadualon/initialState-java)

What does it do?
- displays various JavaFX linecharts and gauges in a tab panel for various data points and series
  - ~~S.USV backup battery status in a JavaFX linechart~~ 
  - RPi3 cpu / disk / memory / temperature
  - GPS location (latitude, longitude, altitude)
  - GPS movement (speed, direction)
- writes local NMEA log of GGA and RMC sentences
- creates local GPX file from current NMEA log on command
- backs up local NMEA log on command
- if there is an internet connection, sends all data averaged every few seconds to Initial State for data visualization in the cloud
 


