var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(8080, function(){
  console.log("Server running");
});

io.on('connection', function(socket){
  console.log("Player Connected!");
  socket.emit('socketID', { id: socket.id});
  socket.broadcast.emit('newPlayer', {id: socket.id});

  socket.on('place', function(data){
    data.id = socket.id;
    socket.broadcast.emit('place', data);
    console.log("ID: " + data.id + " place X:" + data.x + " Y: " + data.y)
  });

  socket.on('pass', function(){
    socket.broadcast.emit('pass');
    console.log("Player " + socket.id + " passed");
  });

  socket.on('disconnect', function(){
    console.log("Player Disconnected");
  });

  socket.on('hopeless', function(data){
    socket.broadcast.emit('hopeless', data);
    console.log("Hopeless:\n" + data.hopeless);
  });

});