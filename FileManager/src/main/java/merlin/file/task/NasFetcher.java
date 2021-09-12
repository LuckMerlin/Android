package merlin.file.task;

import com.merlin.file.Label;
import com.merlin.file.NasPath;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.io.Closer;
import luckmerlin.core.json.Json;
import luckmerlin.task.InputStreamWrapper;
import luckmerlin.task.OutputStreamWrapper;

final class NasFetcher extends Closer{

    public final Reply<InputStream> delete(String host, String filePath) throws Exception {
        HttpURLConnection connection=openHttpConnection(host,"DELETE");
        if (null==connection){
            Debug.W("Fail delete nas file while open connect NULL.");
            return new Reply<>(Code.CODE_FAIL,"Open connect NULL",null);
        }
        inflateHeader(connection,Label.LABEL_PATH,filePath);
        inflateHeader(connection,"Content-Type", "binary/octet-stream");
        connection.setDoInput(true);
        connection.setChunkedStreamingMode(0);
        connection.connect();
        return new Reply<>(Code.CODE_SUCCEED,null,new NasDeleteStream(connection));
    }

    public final Reply<OutputStream> openCloudOutput(String host, String toFilePath,
                                                     long fromLength,long totalLength) throws Exception {
        HttpURLConnection connection=openHttpConnection(host,"POST");
        if (null==connection){
            Debug.W("Fail open cloud output while open connect NULL.");
            return new Reply<>(Code.CODE_FAIL,"Open connect NULL",null);
        }
        Debug.D("Opening cloud output stream."+host+" "+fromLength+" "+toFilePath);
        inflateHeader(connection,Label.LABEL_PATH,toFilePath);
        inflateHeader(connection,Label.LABEL_FROM,""+fromLength);
        inflateHeader(connection,Label.LABEL_TOTAL,""+totalLength);
        inflateHeader(connection,"Content-Type", "binary/octet-stream");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.connect();
        return new Reply<>(Code.CODE_SUCCEED,null,new NasOutputStream(connection,fromLength));
    }

    public final Reply<InputStream> openCloudInput(String host, String fromFilePath, long fromLength) throws Exception {
        HttpURLConnection connection=openHttpConnection(host,"GET");
        if (null==connection){
            Debug.W("Fail open cloud input while open connect NULL.");
            return new Reply<>(Code.CODE_FAIL,"Open connect NULL",null);
        }
        inflateHeader(connection,Label.LABEL_PATH,fromFilePath);
        inflateHeader(connection,Label.LABEL_FROM,""+fromLength);
        inflateHeader(connection,"Content-Type", "binary/octet-stream");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.connect();
        return new Reply<>(Code.CODE_SUCCEED,null,new NasInputStream(connection,fromLength));
    }

    public final Reply<NasPath> fetchNasFile(String host, String path){
        if (null==host||host.length()<=0){
            Debug.W("Fail fetch nas path while host invalid.");
            return null;
        }else if (null==path||path.length()<=0){
            Debug.W("Fail fetch nas path while path invalid.");
            return null;
        }
        final HttpURLConnection connection=openHttpConnection(host,"GET");
        if (null==connection){
            Debug.W("Fail fetch nas path while connect invalid.");
            return null;
        }
        InputStreamReader streamReader=null;BufferedReader inputStreamReader=null;
        try {
            inflateHeader(connection, Label.LABEL_PATH,path);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(false);
            connection.setUseCaches(false);
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            String encoding=connection.getContentEncoding();
            streamReader=null!=inputStream?new InputStreamReader(inputStream,
                    null!=encoding&&encoding.length()>0?encoding:"UTF-8"):null;
            inputStreamReader = null!=streamReader?new BufferedReader(streamReader):null;
            String contentText=null;
            if (null!=inputStreamReader){
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = inputStreamReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                contentText=stringBuffer.length()>0?stringBuffer.toString():null;
            }
            Json jsonObject=null!=contentText&&contentText.length()>0? Json.create(contentText):null;
            if (null==jsonObject){
                Debug.TD("Can't parse nas path response while NONE json response.",contentText);
                return new Reply<>(Code.CODE_FAIL,"None json response.",null);
            }
            JSONObject dataJson=jsonObject.optJSONObject(Label.LABEL_DATA);
            return new Reply(jsonObject.optInt(Label.LABEL_CODE,Code.CODE_FAIL),
                    jsonObject.optString(Label.LABEL_MESSAGE),null!=dataJson?new NasPath(dataJson):null);
        }catch (Exception e){
            Debug.E("Exception parse nas path response.e="+e,e);
            e.printStackTrace();
        }finally {
            close(true,inputStreamReader,streamReader);
            connection.disconnect();
        }
        Debug.D("Can't parse nas path response.");
        return new Reply<>(Code.CODE_FAIL,"Fail.",null);
    }

    public final HttpURLConnection openHttpConnection(String urlPath, String method) {
        URL url= null;
        try {
            url = null!=urlPath&&urlPath.length()>0?new URL(urlPath):null;
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

    public final boolean inflateHeader(HttpURLConnection connection,String key,String value){
        if (null!=connection&&null!=key&&key.length()>0&&null!=value){
            try {
                connection.setRequestProperty(key, URLEncoder.encode(value,"utf-8"));
                return true;
            } catch (UnsupportedEncodingException e) {
                Debug.E("Exception set value to header."+e,e);
                e.printStackTrace();
            }
        }
        return false;
    }

    private static class NasDeleteStream extends InputStreamWrapper implements Closeable {
        private final HttpURLConnection mConnection;

        public NasDeleteStream(HttpURLConnection connection) throws IOException {
            super(null!=connection?connection.getInputStream():null);
            mConnection=connection;
        }

        @Override
        public void close() throws IOException {
            super.close();
            HttpURLConnection connection=mConnection;
            if (null!=connection){
                connection.disconnect();
            }
        }
    }

    private static class NasInputStream extends InputStreamWrapper {
        private final HttpURLConnection mConnection;

        public NasInputStream(HttpURLConnection connection,long length) throws IOException {
            super(null!=connection?connection.getInputStream():null);
            mConnection=connection;
        }

        @Override
        public void close() throws IOException {
            super.close();
            HttpURLConnection connection=mConnection;
            if (null!=connection){
                connection.disconnect();
            }
        }
    }

    private static class NasOutputStream extends OutputStreamWrapper {
        private final HttpURLConnection mConnection;

        public NasOutputStream(HttpURLConnection connection,long fromLength) throws IOException {
            super(null!=connection?connection.getOutputStream():null);
            mConnection=connection;
        }

        @Override
        public void close() throws IOException {
            super.close();
            HttpURLConnection connection=mConnection;
            if (null!=connection){
                connection.disconnect();
            }
        }
    }
}
