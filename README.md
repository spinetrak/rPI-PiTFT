# rPI-PiTFT

A simple read-only JavaFX user interface for using an RPi3 as a GPS data logger (in NMEA format) in the car.

Components:
- [Raspberry Pi 3 Model B] (https://www.raspberrypi.org/products/raspberry-pi-3-model-b/)
- [3.5" PiTFT] (https://learn.adafruit.com/running-opengl-based-games-and-emulators-on-adafruit-pitft-displays/3-dot-5-pitft)
- [OpenJFX] (http://chriswhocodes.com/)
- [S.USV] (http://www.s-usv.de/index_en.php)
- [GPS] (http://www.navilock.de/produkte/G_62523/merkmale.html?setLanguage=en)

What does it do?
- monitors S.USV backup battery status in a JavaFX linechart
- monitors RPi3 cpu / disk / temperature
- monitors GPS tracking status
- monitors altitude in a JavFX linechart
- creates GPX file from current NMEA log
 


