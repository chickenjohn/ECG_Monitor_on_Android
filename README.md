#A Simple ECG Monitor for Android Platform

##What is this?

ECG Monitor is a simple ECG Wave displaying APP. After connecting to a Bluetooth ECG device, 
it allows your Android device to show and analysing the ECG data. 

##How is it like?

After launching the app, you should see this:

![](./figures/fig1.png)

Sliding from the left, a menu should be shown:

![](./figures/fig2.png)

Changing the device into landscape and details of the waveform should be shown:

![](./figures/fig3.png)

##How to use it?

To compile the project, please install the Android Studio 2.1 first. Also you should install Android API 5.0 or higher version.  

To use it, **HC-05 bluetooth serial port named as "hc-bluetooth"** is needed. Connect it on your PC by using the serial-port-to-USB converter, check the serial port number on your PC and modify the port ID in SendWaveTool/sendwave.py. 

Then launch the ECG Monitor App. The app should  begin searching and connecting the bluetooth by itself. After it is connected, execute the sendwave.py program. The app should run normally now. 

If you changing your device into landscape, touch your device to measure the wave.

##For more info

> Email: [chickenjohn93@outlook.com](mailto:chickenjohn93@outlook.com "email")

Contact me if you have more questions. 