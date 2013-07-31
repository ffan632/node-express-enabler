exports.HTTP_PORT=#LISTEN_PORT#;
exports.HTTPS_PORT=#LISTEN_PORT_SSL#;
exports.SSL_KEY_FILE='#SSL_KEY_FILE#';
exports.SSL_TRUSTED_CA_FILE='#SSL_TRUSTED_CA_FILE#';
exports.SSL_KEY_PASSWORD='#SSL_KEY_PASSWORD#';
exports.HTTP_ENABLED=#HTTP_ENABLED#;
exports.HTTPS_ENABLED=#HTTPS_ENABLED#;
exports.SERVER_STATS_ENABLED=#SERVER_STATS_ENABLED#;
exports.SERVER_STATS_PATH='#SERVER_STATS_PATH#';

var counter = 0;
exports.stats = function(req, res, next) {
    if ( req.url == exports.SERVER_STATS_PATH ){
      res.writeHead(200, {'Content-Type': 'text/plain'});
      res.end(counter + "");
    }  else {
      counter++;
      next();
    }
  };
