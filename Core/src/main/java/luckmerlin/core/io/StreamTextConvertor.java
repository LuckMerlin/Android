package luckmerlin.core.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import luckmerlin.core.Convertor;
import luckmerlin.core.debug.Debug;

public class StreamTextConvertor extends Closer implements Convertor<InputStream, String> {
    private final byte[] mBuffer;
    private String mEncoding;
    private boolean mAutoClose=false;
    private Convertor<InputStream, String> mConvertor;

    public StreamTextConvertor(int bufferSize){
        this(bufferSize<=0||bufferSize>Integer.MAX_VALUE>>1?null:new byte[bufferSize]);
    }

    public StreamTextConvertor(byte[] buffer){
        mBuffer=null!=buffer&&buffer.length>0?buffer:new byte[1024*1024];
    }

    public final StreamTextConvertor enableAutoClose(boolean enable){
        mAutoClose=enable;
        return this;
    }

    public final boolean isAutoClose() {
        return mAutoClose;
    }

    public final StreamTextConvertor setEncoding(String encoding) {
        this.mEncoding = encoding;
        return this;
    }

    public final String getEncoding() {
        return mEncoding;
    }

    @Override
    public String convert(InputStream inputStream) {
        if (null==inputStream){
            Debug.W("Can't convert stream text while input stream invalid.");
            return null;
        }
        InputStreamReader streamReader=null;
        BufferedReader bufferedReader=null;
        try {
            byte[] buffer=mBuffer;
            if (null==buffer||buffer.length<=0){
                Debug.W("Can't convert stream text while buffer invalid.");
                return null;
            }
            String encoding=mEncoding;
            streamReader=null!=inputStream?new InputStreamReader(inputStream,
                    null!=encoding&&encoding.length()>0?encoding:"UTF-8"):null;
            bufferedReader=null!=streamReader?new BufferedReader(streamReader):null;
            String contentText=null;
            if (null!=streamReader){
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                contentText=stringBuilder.length()>0?stringBuilder.toString():null;
            }
            return contentText;
        } catch (Exception e) {
            Debug.W("Exception convert stream text."+e);
            e.printStackTrace();
        }finally {
            close(mAutoClose,inputStream);
        }
        return null;
    }
}
