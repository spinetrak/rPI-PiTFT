# rPI-PiTFT

A simple read-only JavaFX user interface for using an RPi3 as a GPS data logger (in NMEA format) in the car.

Components:
- [Raspberry Pi 3 Model B] (https://www.raspberrypi.org/products/raspberry-pi-3-model-b/)
- [3.5" PiTFT] (https://learn.adafruit.com/running-opengl-based-games-and-emulators-on-adafruit-pitft-displays/3-dot-5-pitft)
- [OpenJFX] (http://chriswhocodes.com/)
- ~~[S.USV] (http://www.s-usv.de/index_en.php)~~ (currently broken, temporarly disabled)
- [GPS] (http://www.navilock.de/produkte/G_62523/merkmale.html?setLanguage=en)

What does it do?
- displays various JavaFX linecharts in a tab panel for various data series
  - ~~S.USV backup battery status in a JavaFX linechart~~ 
  - RPi3 cpu / disk / memory / temperature  (via [Pi4J] (http://pi4j.com/))
  - GPS location (latitude, longitude, altitude) (via [ktuukkan/marine-api] (https://github.com/ktuukkan/marine-api))
- writes NMEA log of GGA sentences
- creates GPX file from current NMEA log on command
- backs up NMEA log on command
 


