package luckmerlin.database;

import android.os.CancellationSignal;
import java.io.IOException;

public abstract class AbstractDatabase implements Database {

    @Override
    public int execSQL(String sql, Object[] bindArgs) {

        return 0;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

}
