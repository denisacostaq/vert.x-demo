package com.github.denisacostaq.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.deployVerticle(new HelloVerticle());
        Router router = Router.router(vertx);
        router.get("/api/v1/hello").handler(this::helloVertx);
        router.get("/api/v1/hello/:name").handler(this::helloName);
        int httpPort = 8080;
        try {
            httpPort = Integer.parseInt(System.getProperty("http.port", "8080"));
        } catch (NumberFormatException ex) {
            System.err.println("Invalid format for HTTP_PORT: " + System.getProperty("http.port"));
        } catch (Exception ex) {
            System.err.println("HTTP_PORT not set");
        }
        System.out.println("HTTP_PORT: " + httpPort);
        vertx.createHttpServer().requestHandler(router).listen(httpPort);
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
