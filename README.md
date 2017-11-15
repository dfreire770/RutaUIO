# RutaUIO

This Android App was created to solve a common problem for commuters in Quito, Ecuador.

As a commuter, sometimes I had trobule to travel from one place to another using Public Transportantion in the City,
and people who was just visiting the city had the same problem.

While developing this application, Google Maps Transit Feature  was disabled, so it was the perfect time for this App to be live.

This App was made with Java in Android Studio, using the following APIs and Libraries:

* Google Maps API
* QR Scanner (Requires another App to work)

Features

* Map
* List of Main Bus Route in the City.
* Bus Route Details, schedule, fares, and stops.
* QR Scanner to access Bus Route Details. 

# How it Works

## V1
The application connecets to a Server, through a simple API made with PHP.
The API, is used to access a MYSQL database with all the required data for the App, like bus routes, details, and more.

## V2
The application connecets to a Server, through a Web API made with ASP .NET.
The Web API, is used to access a SQL Server database with all the required data for the App, like bus routes, details, and more.

The API and Database Schema are not included. You'll have to design and create your own API according to your needs 
and modify the App to make it work.

V2 was made for Android Lolipop, you'll need to modify Gradle and some classes to make it work.

