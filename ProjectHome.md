This [demo](http://gwt-earth-visitors.appspot.com/) makes use of the [Google Analytics Java API](http://code.google.com/apis/analytics/docs/gdata/1.0/gdataJava.html) and the [gwt-earth-3 API](http://code.google.com/p/gwt-earth-3/) to render visitor statistics on Google Earth.

In order to use this project to render statistics for your own Google Analytics account, you will need to:

  1. Create a file named config.properties in the following location: src/com/nitrous/gwtearth/visitors/server/config/config.properties
  1. Configure the following properties within the file:
```
   # The google analytics account user id 
   anayltics.account.id=

   # The google analytics user account password
   anayltics.account.password=
```


See the demo live [here](http://gwt-earth-visitors.appspot.com/).
