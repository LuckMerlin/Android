package com.merlin.file;

import org.json.JSONObject;
import luckmerlin.core.json.Json;

public abstract class JsonPath extends Json implements Path {

    public JsonPath(){
        this(null);
    }

    public JsonPath(JSONObject jsonObject){
        super(jsonObject);
    }
}
