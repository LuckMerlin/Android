package merlin.file.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;

import luckmerlin.core.Convertor;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.io.Closer;
import luckmerlin.core.io.StreamTextConvertor;
import luckmerlin.core.json.JsonParser;

public class Http extends Closer {
    private final StreamTextConvertor mConvertor=new StreamTextConvertor(1024*1024);
    private final JsonParser mJsonParser=new JsonParser();

    public final HttpURLConnection openHttpConnection(String urlPath, String method) {
        return openHttpConnection(urlPath,method,null);
    }

    public final HttpURLConnection openHttpConnection(String urlPath,String method,String routeName) {
        URL url= null;
        try {
            url = null!=urlPath&&urlPath.length()>1?new URL(null!=routeName&&routeName.length()>0?
                    urlPath+routeName :urlPath):null;
            HttpURLConnection conn = null!=url?(HttpURLConnection) url.openConnection():null;
            if (null!=conn){
                conn.setRequestMethod(null!=method&&method.length()>0?method:"GET");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setUseCaches(false);
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(5000);
                return conn;
            }
        } catch (Exception e) {
            Debug.E("Exception open http connect.e="+e,e);
            e.printStackTrace();
        }
        return null;
    }

    public final Http inflateHeader(HttpURLConnection connection,String key,String value){
        if (null!=connection&&null!=key&&key.length()>0&&null!=value){
            try {
                connection.setRequestProperty(key, URLEncoder.encode(value,"utf-8"));
            } catch (UnsupportedEncodingException e) {
                Debug.E("Exception set value to header."+e,e);
                e.printStackTrace();
            }
        }
        return this;
    }

    public final String convertStreamText(InputStream stream){
        return null!=stream?mConvertor.convert(stream):null;
    }

    public final<F,T> T parseJson(Convertor<F,T> parser, F json){
        return mJsonParser.object(parser,json);
    }

    public final <T,M extends Collection<T>> M list(Convertor<Object,T> parser, Object json){
        return mJsonParser.list(parser,json);
    }

    public final <T,M extends Collection<T>> M list(Convertor<Object,T> parser,Object json, M collection){
        return mJsonParser.list(parser,json,collection);
    }
}
