package merlin.file.adapter;

import com.merlin.file.Label;

import luckmerlin.core.json.Json;

public final class Query  extends Json {

    public final Query setPath(String path){
        return putJsonValueSafe(this,Label.LABEL_PATH,path);
    }

    public String getPath(){
        return getText(Label.LABEL_PATH,null);
    }


}
