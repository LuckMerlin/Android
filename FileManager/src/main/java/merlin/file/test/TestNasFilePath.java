package merlin.file.test;

import com.merlin.file.Path;

public class TestNasFilePath implements Path {

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public long getTotalSpace() {
        return 0;
    }

    @Override
    public String getHost() {
        return "http://192.168.0.4:5000";
    }

    @Override
    public long getFreeSpace() {
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
    public String getParent() {
        return "/Volumes/Work/Workspace/FileBrowser";
    }

    @Override
    public String getSep() {
        return "/";
    }

    @Override
    public String getName() {
        return "独家记忆.mp3";
    }
}
