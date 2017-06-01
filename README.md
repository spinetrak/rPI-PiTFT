# rPI-PiTFT

A simple read-only JavaFX user interface for using an RPi3 as a GPS data logger (in NMEA format) in the car.
Now also logs to [Initial State](https://initialstate.com/).

Hardware Components:
- [Raspberry Pi 3 Model B](https://www.raspberrypi.org/products/raspberry-pi-3-model-b/)
- [3.5" PiTFT](https://learn.adafruit.com/running-opengl-based-games-and-emulators-on-adafruit-pitft-displays/3-dot-5-pitft)
- [Huawei E5770 4G Hotspot](http://consumer.huawei.com/en/mobile-broadband/mobile-wifi/features/e5770-en.htm)
- [GPS](http://www.navilock.de/produkte/G_62523/merkmale.html?setLanguage=en)
- [Powerbank RAVPower 26800mAh](https://www.amazon.de/dp/B012V88B90)

Software Components:
- [OpenJFX](http://chriswhocodes.com/)
- [Java Marine API](http://ktuukkan.github.io/marine-api/)
- [rxtx](https://github.com/rxtx/rxtx)
- [Pi4J](http://pi4j.com/)
- [Medusa](https://github.com/HanSolo/Medusa)
- [initialState-java](https://github.com/kadualon/initialState-java)
- [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
- [smoothie.js](http://smoothiecharts.org/)
- [gmaps.js](https://hpneo.github.io/gmaps/)

What does it do?
- JavaFX Mode:
  - displays various JavaFX linecharts and gauges in a tab panel for various data points and series
    - RPi3 cpu / disk / memory / temperature
    - Hotspot battery status and data volume
    - GPS location (latitude, longitude, altitude)
    - GPS movement (speed, direction)
  - shows the current track in a polyline
  - creates local GPX file from current NMEA log on command
  - backs up local NMEA log on command
- Headless Mode:
  - displays a few linecharts updated via websocket
    - RPi3 cpu / disk / memory / temperature
    - GPS location (latitude, longitude, altitude)
    - GPS movement (speed)
  - shows the current track on a Google Map
- Any Mode:
  - if there is an internet connection, sends all data averaged every few seconds to Initial State for data visualization in the cloud
  - writes local NMEA log of GGA and RMC sentences


 


