package io.vertx.example;

import io.vertx.core.AbstractVerticle;

import java.util.UUID;

public class HelloVerticle extends AbstractVerticle {

    String veticleId = UUID.randomUUID().toString();

    @Override
    public void start(){
         vertx.eventBus().consumer("hello.vertx.addr", msg-> {
             msg.reply("Hello vertx.wolrd");
         });

         vertx.eventBus().consumer("hello.named.addr",msg->{
             String name=(String) msg.body();
             msg.reply(String.format("Hello %s from %s another verticle", name ,veticleId));

        });
    }
}
