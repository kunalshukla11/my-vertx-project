package io.vertx.example;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future future) {


        vertx.deployVerticle(new HelloVerticle());
        Router router = Router.router(vertx);
        router.get("/api/v1/hello").handler(this::getNormalRouter);
        router.get("/api/v1/hello/:name").handler(this::getName);


        vertx.createHttpServer().requestHandler(router).listen(8085, result -> {
            if (result.succeeded()) {
                future.complete();

                System.out.println("Sysout deployed");
            } else {
                future.fail(result.cause());
            }
        });

    }

    private void getNormalRouter(RoutingContext routingContext) {

        vertx.eventBus().request("hello.vertx.addr", "", reply->{
            routingContext.response().end((String) reply.result().body());
        });


    }



    private void getName(RoutingContext routingContext) {
        String name = routingContext.pathParam("name");
        vertx.eventBus().request("hello.named.addr", name, reply->{
            routingContext.response().end((String) reply.result().body());
        });

    }



}
