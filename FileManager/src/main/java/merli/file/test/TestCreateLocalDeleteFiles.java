package merli.file.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import luckmerlin.core.debug.Debug;

public class TestCreateLocalDeleteFiles {

    public void create(File file,int cycle,int size){
        if (null==file||(file.exists()&&!file.isDirectory())){
            return;
        }else if (!file.exists()){
            createFile(file.getParentFile(),true);
        }
        if (!file.exists()){
            return;
        }
        File chileFile;
        int count=cycle;
        final List<File> dirs=new ArrayList<>();
        File parent=file;
        while (count>0){
            for (int i = 0; i < size; i++) {
                boolean dir=new Random().nextBoolean();
                parent=dirs.size()>0?dirs.get(new Random().nextInt(dirs.size())):file;
                parent=null!=parent?parent:file;
                chileFile=new File(parent,"test_"+count+"_"+i+(dir?"":".lin"));
                if (createFile(chileFile,dir)&&dir) {
                    dirs.add(chileFile);
                }
            }
            count-=1;
        }
        return;
    }

    private boolean createFile(File file,boolean directory){
        if (null==file||file.exists()){
            return false;
        }
        File parent=file.getParentFile();
        if (null!=parent&&!parent.exists()){
            parent.mkdirs();
        }
        try {
            if (null!=parent&&parent.exists()?directory?file.mkdir():file.createNewFile():false){
                Debug.D("Create test file."+file.exists()+" "+file );
                return true;
            }
            Debug.D("File create test file."+file);
            return  false;
        } catch (IOException e) {
            Debug.E("Exception Create test file."+file+" "+e);
            e.printStackTrace();
        }
        return false;
    }
}
