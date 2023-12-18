package com.github.denisacostaq.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        DeploymentOptions options = new DeploymentOptions()
                .setWorker(true)
                .setInstances(8);
        vertx.deployVerticle(HelloVerticle.class.getName(), options);
        Router router = Router.router(vertx);
        router.get("/api/v1/hello").handler(this::helloVertx);
        router.get("/api/v1/hello/:name").handler(this::helloName);
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    void helloVertx(RoutingContext ctx) {
        vertx.eventBus().request("hello.vertx.addr", "", reply -> {
            ctx.request().response().end((String) reply.result().body());
        });
    }

    void helloName(RoutingContext ctx) {
        String name = ctx.request().getParam("name");
        vertx.eventBus().request("hello.name.addr", name, reply -> {
            ctx.request().response().end((String) reply.result().body());
        });
    }

}
