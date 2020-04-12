package se.kry.codetest.model;

import io.vertx.core.json.JsonObject;

public class Service {
    private int id;
    private String url;
    private String name;
    private String ts;

    public Service() {
    }

    public Service(String url,String name){
        this.url = url;
        this.name = name;
    }

    public Service(JsonObject entry) {
        this.id = entry.getInteger("id");
        this.url = entry.getString("url");
        this.name = entry.getString("name");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
