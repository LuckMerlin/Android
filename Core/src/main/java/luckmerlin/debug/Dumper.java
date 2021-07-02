package luckmerlin.debug;

import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Set;

public final class Dumper {

    public JSONObject dump(Bundle bundle){
        Set<String> set=null!=bundle?bundle.keySet():null;
        if (null!=set){
            JSONObject object=new JSONObject();
            for (String child:set){
                Object value=bundle.get(child);
                try {
                    object.put(child,null!=value?value:"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return object;
        }
        return null;
    }
}
