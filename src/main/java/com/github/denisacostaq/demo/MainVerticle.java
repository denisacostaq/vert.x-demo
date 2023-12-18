package com.github.denisacostaq.demo;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.deployVerticle(new HelloVerticle());
        Router router = Router.router(vertx);
        router.route().handler(ctx -> {
            String authToken = ctx.request().getHeader("Authorization");
            String token = authToken == null ? null : authToken.substring("Bearer ".length());
            if ("supper_secret".equals(token)) {
                ctx.next();
            } else {
                ctx.response().setStatusCode(401).setStatusMessage("non authorized").end();
            }
        });
        router.get("/api/v1/hello").handler(this::helloVertx);
        router.get("/api/v1/hello/:name").handler(this::helloName);
        router.route().handler(StaticHandler.create("web").setIndexPage("index.html"));
        router.route().handler(ctx -> {
            ctx.response().setStatusCode(404).setStatusMessage("not found").end();
        });
        doConfig(startPromise, router);
    }

    void doConfig(Promise<Void> startPromise, Router router) {
        ConfigStoreOptions defaultConfig = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "conf/config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(defaultConfig);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        // Handler<AsyncResult<JsonObject>> handler = ar -> handleConfigResults(startPromise, router, ar);
        retriever.getConfig(/*handler*/ar -> handleConfigResults(startPromise, router, ar));
    }

    void handleConfigResults(Promise<Void> startPromise, Router router, AsyncResult<JsonObject> ar) {
        if (ar.failed()) {
            System.out.println("Failed to retrieve the configuration.");
            startPromise.fail(ar.cause());
        } else {
            JsonObject config = ar.result();
            System.out.println("Retrieved configuration: " + config.encodePrettily());
            int httpPort = config.getInteger("http.port", 8080);
            vertx.createHttpServer().requestHandler(router).listen(httpPort);
            startPromise.complete();
        }
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
