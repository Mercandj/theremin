
## Theremin on Android things

<p align="center">
	<a margin="20px 0" href="http://mercandalli.com/">
		<img  src="https://raw.github.com/Mercandj/theremin/master/config/screenshot/screen-things.jpg" width="560" />
	</a>
</p>


<p align="center">
	<a margin="20px 0" href="http://mercandalli.com/">
		<img  src="https://raw.github.com/Mercandj/theremin/master/config/screenshot/hcsr04.png" width="560" />
	</a>
</p>

* HC-SR04
* Raspberry pi 3
* Oboe (Google CPP library to play sound) 

## Requirements

- Install Android Things on your raspberry pi 3: https://developer.android.com/things/hardware/raspberrypi
- Generate an apk from the module "things" (to install only on Android Things operating system)
   - Run `./gradlew things:assembleDebug`
   - The apk will be in `./things/build/output/apk/debug/things-debug.apk`
- Install the apk on the raspberry. The app installed will become the raspberry default "home" / "app launcher"
- This "app launcher" is displaying the local ip in order to connect to it via `adb`

## DEVELOPER

* Mercandalli Jonathan

