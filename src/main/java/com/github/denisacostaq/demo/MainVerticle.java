package com.github.denisacostaq.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/api/v1/hello").handler(this::helloVertx);
        router.get("/api/v1/hello/:name").handler(this::helloName);
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    void helloVertx(RoutingContext ctx) {
        ctx.request().response().end("Hello World!");
    }

    void helloName(RoutingContext ctx) {
        String name = ctx.request().getParam("name");
        ctx.request().response().end(String.format("Hello %s!", name));
    }

}
