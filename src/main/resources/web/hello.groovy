vertx.eventBus().consumer("hello.vertx.addr").handler { message ->
    message.reply("Hello ${message}!")
}