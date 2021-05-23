package ua.kpi.comsys.iv8121.pms.ui.lab1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class classViewModel extends androidx.lifecycle.ViewModel {

    private MutableLiveData<String> mText;

    public classViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}