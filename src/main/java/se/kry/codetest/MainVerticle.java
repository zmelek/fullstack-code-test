package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.kry.codetest.model.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

    private HashMap<String, String> services = new HashMap<>();
    //TODO use this
    private DBConnector connector;
    private BackgroundPoller poller = new BackgroundPoller();

    @Override
    public void start(Future<Void> startFuture) {
        connector = new DBConnector(vertx);
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        Future<ResultSet> resultSet = connector.query("SELECT id, url, name FROM service;");
        resultSet.setHandler(new Handler<AsyncResult<ResultSet>>() {
            @Override
            public void handle(AsyncResult<ResultSet> event) {
                List<Service> serviceList =  event.result().getRows().stream().map(Service::new).collect(Collectors.toList());
                for (Service service : serviceList) {
                    services.put(service.getUrl(), service.getName());
                }
            }
        });
        vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(services));
        setRoutes(router);
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        System.out.println("KRY code test service started");
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }

    private void setRoutes(Router router) {
        router.route("/*").handler(StaticHandler.create());
        router.get("/service").handler(req -> {
            List<JsonObject> jsonServices = services
                    .entrySet()
                    .stream()
                    .map(service ->
                            new JsonObject()
                                    .put("name", service.getKey())
                                    .put("status", service.getValue()))
                    .collect(Collectors.toList());
            req.response()
                    .putHeader("content-type", "application/json")
                    .end(new JsonArray(jsonServices).encode());
        });
        router.post("/service").handler(req -> {
            JsonObject jsonBody = req.getBodyAsJson();
            services.put(jsonBody.getString("url"), jsonBody.getString("name"));
            connector.insert(new Service(jsonBody.getString("url"),jsonBody.getString("name")));
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("OK");
        });
        router.delete("/service").handler(req -> {
            JsonObject jsonBody = req.getBodyAsJson();
            connector.delete(new Service(jsonBody.getString("url"),jsonBody.getString("name")));
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Service "+jsonBody.getString("url")+" is deleted");
        });
    }

}



