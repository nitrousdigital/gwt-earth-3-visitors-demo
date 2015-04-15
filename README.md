# gwt-earth-3-visitors-demo
A demo application that plots the locations of gwt-earth visitors (using google analytics) on a google earth map.
This demo makes use of the Google Analytics Java API and the [gwt-earth-3 API](https://github.com/nitrousdigital/gwt-earth-3) to render visitor statistics on a Google Earth map.

In order to use this project to render statistics for your own Google Analytics account, you will need to:

Create a file named config.properties in the following location: src/com/nitrous/gwtearth/visitors/server/config/config.properties

Configure the following properties within the file:
```
   # The google analytics account user id 
   anayltics.account.id=

   # The google analytics user account password
   anayltics.account.password=
```
