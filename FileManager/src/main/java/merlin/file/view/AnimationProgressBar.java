package merlin.file.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class AnimationProgressBar extends ProgressBar {

    public AnimationProgressBar(Context context) {
        super(context);
    }

    public AnimationProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public synchronized void setProgress(int progress) {
        setProgress(progress,true);
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        super.setProgress(progress, animate);
    }
}
