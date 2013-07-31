var express = require('express');
var app = express();
var cntr_env = require('../container_env.js');

// enable STATS
if ( cntr_env.SERVER_STATS_ENABLED ){
  app.use(cntr_env.stats);
}

// simple respond
app.use(function(req, res, next){
  res.send('Hello World');
});

// enable HTTP port
if ( cntr_env.HTTP_ENABLED ){
  var http = require ('http');
  http.createServer(app).listen(cntr_env.HTTP_PORT);
}

// enable HTTPS port
if ( cntr_env.HTTPS_ENABLED ){
  var https = require ('https');
  var fs = require ('fs');
  var options = {
    key: fs.readFileSync(cntr_env.SSL_KEY_FILE),
    cert: fs.readFileSync(cntr_env.SSL_TRUSTED_CA_FILE),
    passphrase: cntr_env.SSL_KEY_PASSWORD 
  };
  https.createServer(options, app).listen(cntr_env.HTTPS_PORT);
}
