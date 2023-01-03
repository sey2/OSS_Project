package org.techtown.osshango.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.techtown.osshango.Data.Travel;
import org.techtown.osshango.Data.UserInfoData;

import java.util.ArrayList;

public class TravelViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Travel>> travelItems;
    private MutableLiveData<UserInfoData> userInfoItem;
    private ArrayList<Travel> items = new ArrayList<>();

    public LiveData<ArrayList<Travel>> getTravelLiveItems(){
        if(travelItems == null)
            travelItems = new MutableLiveData<ArrayList<Travel>>();

        return travelItems;
    }

    public ArrayList<Travel> getList (){
        return items;
    }

    public void add(Travel item){
        items.add(item);

        if(travelItems == null) getTravelLiveItems();

        travelItems.setValue(items);
    }

    public void deleteList(){items = new ArrayList<>();}


    public MutableLiveData<UserInfoData> getUserinfo(){
        if(userInfoItem == null)
            userInfoItem = new MutableLiveData<UserInfoData>();

        return userInfoItem;
    }

    public void setLiveItems(UserInfoData item){

        if(userInfoItem == null) getUserinfo();

        userInfoItem.setValue(item);

    }

}
