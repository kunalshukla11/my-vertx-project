package io.vertx.example;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) {


        vertx.deployVerticle(new HelloVerticle());
        Router router = Router.router(vertx);
        router.route().handler(ctx->{
            String authToken = ctx.request().getHeader("AUTH_TOKEN");
            if( authToken!= null &&"mySuperSecureAuthToken".contentEquals(authToken)){
                ctx.next();
            } else {
                ctx.response().setStatusCode(401).setStatusMessage("un authorise").end();
            }

        });

        router.get("/api/v1/hello").handler(this::getNormalRouter);
        router.get("/api/v1/hello/:name").handler(this::getName);
        router.route().handler(StaticHandler.create("web"));

//using configuratin store
        ConfigStoreOptions deafaultConfig = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "config.json"));

        ConfigRetrieverOptions opts= new ConfigRetrieverOptions()
                .addStore(deafaultConfig);

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, opts);
        configRetriever.getConfig(getAsyncResultHandler(promise, router));




    }

    private Handler<AsyncResult<JsonObject>> getAsyncResultHandler(Promise<Void> promise, Router router) {
        return ar-> {
            configHandler(promise, router, ar);
        };
    }

    private void configHandler(Promise<Void> promise, Router router, AsyncResult<JsonObject> ar) {
        if(ar.succeeded()){

            JsonObject config = ar.result();
            JsonObject http= config.getJsonObject("http");
            int httpPort = http.getInteger("port");
            vertx.createHttpServer().requestHandler(router).listen(httpPort);
            promise.complete();
        }else {

            promise.fail("unable to load the configurations");
        }
    }


    private void getNormalRouter(RoutingContext routingContext) {

        vertx.eventBus().request("hello.vertx.addr", "", reply -> {
            routingContext.response().end((String) reply.result().body());
        });


    }


    private void getName(RoutingContext routingContext) {
        String name = routingContext.pathParam("name");
        vertx.eventBus().request("hello.named.addr", name, reply -> {
            routingContext.response().end((String) reply.result().body());
        });

    }


}
