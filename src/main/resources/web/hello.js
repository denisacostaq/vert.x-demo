vertx.eventBus().consumer("hello.vertx.addr", function(message) {
    message.reply("Hello World!");
});