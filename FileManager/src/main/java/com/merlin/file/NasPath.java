package com.merlin.file;

import org.json.JSONObject;
import luckmerlin.core.json.Json;

public class NasPath extends Json implements Path {

    public NasPath(JSONObject jsonObject){
        super(jsonObject);
    }

    @Override
    public String getHost() {
        return optString(Label.LABEL_HOST,"");
    }

    @Override
    public long getSize() {
        return optLong(Label.LABEL_SIZE,0);
    }

    @Override
    public long getLength() {
        return optLong(Label.LABEL_LENGTH,0);
    }

    @Override
    public long getTotalSpace() {
        return optLong(Label.LABEL_TOTAL,0);
    }

    @Override
    public long getFreeSpace() {
        return optLong(Label.LABEL_FREE,0);
    }

    @Override
    public String getMimeType() {
        return optString(Label.LABEL_MIME_TYPE,null);
    }

    @Override
    public long getModifyTime() {
        return optLong(Label.LABEL_MODIFY_TIME,0);
    }

    @Override
    public String getParent() {
        return optString(Label.LABEL_PARENT,null);
    }

    @Override
    public String getSep() {
        return optString(Label.LABEL_SEP,null);
    }

    @Override
    public String getName() {
        return optString(Label.LABEL_NAME,null);
    }
}
