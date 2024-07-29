# Nuki Sesami App

Android application to Open (Hold) and Close an Electric Door controlled by the Nuki Sesami service.

## Overview

Using this app you can open (and hold open) an electric door equipped with an _Nuki 3.0 Pro_ smart lock. The _Nuki Sesami_ service will perform the logic for you when using an _ERREKA Smart Evolution Pro_ electric door controller in combination with an _Nuki 3.0 Pro_ smart lock.

![nuki-sesami-wiring](https://raw.githubusercontent.com/michelm/nuki-sesami/master/nuki-sesami-overview.png)

When connected to the same _WiFi_ network commands to, and status from, the _Nuki Sesami_ service can be exchanged using the _mqtt_ protocol. Otherwise when paired the same can be achieved using Bluetooth.

A full description of the _Nuki Sesami_ service, its requirements and setup can be found on [https://github.com/michelm/nuki-sesami](https://github.com/michelm/nuki-sesami).

## Requirements

Following requirements apply for the **nuki-sesami-app**:

- Android version **v7** (_Nougat_) or higher
- Bluetooth enabled and permissions granted
- Wifi enabled and permissions granted

## Installation and setup

The app can be downloaded from the release page using the (chrome) browser on your Android device. Please note that the app is not available in the _Google Play Store_.

Installation procedure:

1. Download the APK file from the [release page](https://github.com/michelm/nuki-sesami-app/releases),
2. Select the release for download and install,
3. Once download has completed select **open** to start the installation,
4. A dialog will appear asking for permission to install the app, select **install** to proceed,
5. When asked to scan the app for security threats select **continue**,
6. Once completed a dialog will appear asking to open the app, select **open** to start the app.
7. When asked _Allow Nuki Sesami to find, connect to, and determine the relative position of nearby devices?_ select **allow** to proceed.

The app has now been installed and is ready for setup and is showing the main view which should be looking like the image below.

![nuki-sesami-app-main-view-not-connected](https://raw.githubusercontent.com/michelm/nuki-sesami/master/nuki-sesami-app-main-view-not-connected.png)

Setup procedure:

1. In the app select the **Settings** menu by pressing the _cog_ icon in the top right corner,
2. In the _Settings_ view select the connection type; **mqtt** or **bluetooth**,
3. Fill in the required fields for the selected connection type;
    - **mqtt**:
      - _Nuki Device Identifier_: the hexadecimal identifier of the _Nuki 3.0 Pro_ smart lock,
      - _Host_: the IP address or of the **mqtt** broker,
      - _Port_: the port number of the **mqtt** broker,
      - _Username_: the **mqtt** client username for this app,
      - _Password_: the **mqtt** client password for this app,
    - **bluetooth**:
      - _Nuki Device Identifier_: the hexadecimal identifier of the _Nuki 3.0 Pro_ smart lock,
      - _Bluetooth Device_: the MAC address / Bluetooth device name of _Raspberry PI_ running the _Nuki Sesami_ service,
      - _Bluetooth Channel_: the channel number of the _Nuki Sesami_ service,
4. When done select the _Home_ icon in the top left corner to return to the main view; the app is now ready for use.

![nuki-sesami-app-settings-view](https://raw.githubusercontent.com/michelm/nuki-sesami/master/nuki-sesami-app-settings-view.png)

## Usage

The main view of the app can be used to open, hold open and close the electric door using a single button. When open-hold is desired select hold before pressing the _open_ button.
While the door open(ing) in normal operation the button will be disabled. When the door is closed the button will be enabled again.

![nuki-sesami-app-main-view](https://raw.githubusercontent.com/michelm/nuki-sesami/master/nuki-sesami-app-main-view.png)

The door, lock and connection status can be viewed in the bottom section of the main view. In case of an connection error or problem the status will be displayed using a red warning sign otherwise it will be displated using a grayed check mark. Also all other states will have an _Undefined_ / _Unknown_ value and the _Open_/_Close_ button, as well the _Hold_ selection will be disabled.

![nuki-sesami-app-status-view](https://raw.githubusercontent.com/michelm/nuki-sesami/master/nuki-sesami-app-status-view.png)

The status view can be used to obtain more information on the current state of the door, lock
server version and connection status. Also in case of connection problems additional error information will be displayed in the bottom section of this view.
