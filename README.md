# What is DriveKit?

**DriveKit** is a mobile telematics technology developed by [DriveQuant](https://www.drivequant.com/) based on smartphone sensors to analyze vehicle trips and evaluate the influence of driving style on safety and fuel consumption.

The **DriveKit** is a flexible software suite for creating mobile applications to engage, coach and improve drivers.

To integrate the **DriveKit SDK** in your application, follow the installation guidelines: [docs.drivequant.com](https://docs.drivequant.com)

# Why use the demo application?

The demo application includes all the **DriveKit** components. It has been developed to guide mobile developers to understand how DriveQuant's telematics solution works.

The available components are:
* **Trip Analysis** does not have a graphical user interface. It is the heart of the reactor that allows access to the phone's sensors and contains autostart mechanisms to analyse trips while your app runs in background.
* **Permissions Utils** is used to get user's permissions that allow the SDK to access the smartphone's sensors while running in background.
* **Driver Data** manages the driver trips display (list and details) and ensures the synchronisation of driver data.
* **Vehicle** is made to select one or more vehicles and to choose the autostart mode for each ot them.
* **Driver Achievement** contains gaming features that measure driver performance and stimulate driver improvement.

Before installing the **DriveKit SDK** in your mobile application we recommend that you do some tests with the demo application. It's fast and it will save you time. 

This is why you need to use the application before you start coding:
* To get the code as an example for integrating the **DriveKit SDK** into your application.
* To test the open-source graphic components in order to choose the ones you wish to integrate in your application.
* To validate that the behaviour of the **DriveKit SDK** in your application is perfectly similar to that of the demo application.

# How to use the demo application?

You can follow the steps below to install, run and experiment the DriveKit sample app.

### Install and run the demo app

* The DriveKit SDK can only be used if you have a valid **DriveKit API Key**. To get an **DriveKit API key**, contact us at: <contact@drivequant.com>.
* In `configureDriveKit` of the `DriveKitDemoApplication.kt`, set your **DriveKit API Key** where the method `DriveKit.setApiKey(<DRIVEKIT_API_KEY>)` is called.
* Build and run the demo app on your phone.

### Configure the demo app

* Click on the **gear icon** to open the app settings.
* In the app settings, set a unique user identifier. The **User id** can be an email or any strings.
* Make sure that the **Auto start** is enabled.
* Click on **LAUNCH PERMISSIONS**, to grant the required permissions to allow the application to access the smartphone's sensors.
* Click on **ADD A VEHICLE** to add a vehicle to your account.

![Android Sample App Screenshots](https://github.com/DriveQuantPublic/drivekit-ui-android/blob/master/Android%20Sample%20App.png)


### Test the demo app

The application is now configured and ready to run! 

The best way to become familiar with smartphone telematics is to try the app in real conditions when travelling by car or public transport.

Alternatively, you can perform some tests in the office using the trip simulator which allows you to reproduce pre-recorded data from real trips.



