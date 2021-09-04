package com.merlin.file;

import luckmerlin.core.json.Json;

public class NasPath extends Json implements Path {

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public long getModifyTime() {
        return 0;
    }

    @Override
    public Path getParent() {
        return null;
    }

    @Override
    public String getSep() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
