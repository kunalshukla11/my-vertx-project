vertx.eventBus().consumer("hello.named.addr").handler({
    msg -> msg.reply("Hello !from groovy")
})