    vertx.eventBus().consumer("hello.vertx.addr",function(msg) {
             msg.reply("Hello vertx.wolrd from java script");
         });