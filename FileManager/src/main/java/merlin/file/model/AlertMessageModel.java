package merlin.file.model;

import android.view.View;
import androidx.databinding.ObservableField;
import luckmerlin.databinding.model.Model;
import luckmerlin.databinding.touch.OnViewClick;

public class AlertMessageModel extends Model implements OnViewClick {
    private final ObservableField<String> mTitle=new ObservableField<>();
    private final ObservableField<String> mMessage=new ObservableField<>();
    private final ObservableField<Integer> mLeftText=new ObservableField<>();
    private final ObservableField<Integer> mRightText=new ObservableField<>();
    private final ObservableField<Integer> mCenterText=new ObservableField<>();
    private final ObservableField<String> mInputText=new ObservableField<>();
    private final ObservableField<String> mInputHintText=new ObservableField<>();

    @Override
    public boolean onClicked(View view, int id, int count, Object tag) {
        return false;
    }

    public final AlertMessageModel setTitle(String title){
        mTitle.set(title);
        return this;
    }

    public final String getInputText(){
        return mInputText.get();
    }

    public final AlertMessageModel setInputHit(String inputText){
        mInputHintText.set(inputText);
        return this;
    }

    public final AlertMessageModel setInput(String inputText){
        mInputText.set(inputText);
        return this;
    }

    public final AlertMessageModel setMessage(String message){
        mMessage.set(message);
        return this;
    }

    public final AlertMessageModel setLeft(Integer leftText){
        mLeftText.set(leftText);
        return this;
    }

    public final AlertMessageModel setCenter(Integer center){
        mCenterText.set(center);
        return this;
    }

    public final AlertMessageModel setRight(Integer right){
        mRightText.set(right);
        return this;
    }

    public final ObservableField<String> getMessage() {
        return mMessage;
    }

    public final ObservableField<Integer> getLeft() {
        return mLeftText;
    }

    public final ObservableField<Integer> getCenter() {
        return mCenterText;
    }

    public final ObservableField<Integer> getRight() {
        return mRightText;
    }

    public final ObservableField<String> getTitle() {
        return mTitle;
    }

    public final ObservableField<String> getInput() {
        return mInputText;
    }

    public ObservableField<String> getInputHint() {
        return mInputHintText;
    }
}
