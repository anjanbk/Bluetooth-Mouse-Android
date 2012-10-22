Bluetooth-Mouse-Android
=======================

Bluetooth Mouse that turns Android phone into movable mouse using the accelerometer.

The client is an Android application that feeds the phone's current velocity through a Bluetooth Socket.

The server is a Java application that is a Bluetooth Listener. Using the Robot API, the velocity from the socket stream is used to alter the mouse position. 
