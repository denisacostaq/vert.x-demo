package com.github.denisacostaq.demo;

import io.vertx.core.AbstractVerticle;

import java.util.UUID;

public class HelloVerticle extends AbstractVerticle {

    String verticleId = UUID.randomUUID().toString();

        @Override
        public void start() {
            vertx.eventBus().consumer("hello.vertx.addr", message -> {
                message.reply("Hello World!");
            });

            vertx.eventBus().consumer("hello.name.addr", message -> {
                String name = (String) message.body();
                message.reply(String.format("Hello %s from %s!", name, verticleId));
            });
        }
}
