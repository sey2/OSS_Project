package org.techtown.osshango.Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.osshango.Adapter.TravelAdapter;
import org.techtown.osshango.BuildConfig;
import org.techtown.osshango.Data.Travel;
import org.techtown.osshango.Data.UserInfoData;
import org.techtown.osshango.R;
import org.techtown.osshango.ViewModel.TravelViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment {

    protected RecyclerView todayRecycler,hotelRecycler, famousRecycler;
    protected TravelAdapter todayAdapter, hotelAdapter, famousAdapter;
    private TravelViewModel model;
    private EditText searchText;
    private ImageView searchButton;
    private ImageView profile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        model = new ViewModelProvider(this.getActivity()).get(TravelViewModel.class);

        todayAdapter = new TravelAdapter();
        todayRecycler = rootView.findViewById(R.id.todayRecycler);
        setAdapter(todayAdapter, todayRecycler);
        loadData(container,todayAdapter, "areaBasedList","","1");

        hotelAdapter = new TravelAdapter();
        hotelRecycler = rootView.findViewById(R.id.hotRecycler);
        setAdapter(hotelAdapter, hotelRecycler);
        loadData(container, hotelAdapter, "searchStay","","1");

        LocalDate current_date = LocalDate.now();
        String date[] = current_date.toString().split("-");

        famousAdapter = new TravelAdapter();
        famousRecycler = rootView.findViewById(R.id.famousRecycler);
        setAdapter(famousAdapter, famousRecycler);
        loadData(container, famousAdapter, "searchFestival", date[0]+date[1]+date[2],"1");

        searchText = rootView.findViewById(R.id.searchEditText);
        searchButton = rootView.findViewById(R.id.serachButton);
        profile = rootView.findViewById(R.id.home_profile);

        attachListener(container, date);

        Log.d("Activity2", "HomeFragment");

        if(model.getUserinfo().getValue() != null) {
            model.getUserinfo().observe(this.getActivity(), new Observer<UserInfoData>() {
                @Override
                public void onChanged(UserInfoData userInfoData) {
                    Log.d("callUrl", "change: " + userInfoData.getProfileUri());
                    Glide.with(getContext()).load(userInfoData.getProfileUri()).into(profile);
                }
            });
        }

        return rootView;
    }

    // 앱 첫 실행순서 HomeFragment -> MainActivity -> HomeFragment (onResume)
    @Override
    public void onResume() {
        super.onResume();

        if(model.getUserinfo().getValue().getProfileUri() == null) setProfileFromCloud();

    }


    private void attachListener(ViewGroup container, String date[]){
        // 검색어 입력 후 자판 내리기
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    //자판 감추기
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);    //hide keyboard

                    searchData(container, date);

                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener((v)->{
            //자판 감추기
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);    //hide keyboard

            searchData(container, date);
        });
    }

    private void searchData(ViewGroup container, String date[]){

        String local = convertLocalNum(searchText.getText().toString());
        if(local.equals("-1")){
            searchText.setText("");
            searchText.setHint("올바르지 않은 검색어 입니다.");
            return;
        }

        searchText.setText("");
        searchText.setHint("검색어를 입력해주세요");
        deleteAdapter();
        model.deleteList();
        loadData(container,todayAdapter, "areaBasedList","",local);
        loadData(container, hotelAdapter, "searchStay","",local);
        loadData(container, famousAdapter, "searchFestival", date[0]+date[1]+date[2],local);
    }


    private void setAdapter(TravelAdapter adapter, RecyclerView recyclerView){
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false));
    }

    private void deleteAdapter(){
        todayAdapter.deleteList();
        hotelAdapter.deleteList();
        famousAdapter.deleteList();
    }

    private void loadData(ViewGroup view, TravelAdapter adapter, String search, String eventDate, String local) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ServiceKey", BuildConfig.TRAVEL_API_KEY);
        params.put("numOfRows", "10");
        params.put("pageNo", "1");
        params.put("MobileOS", "ETC");
        params.put("MobileApp", "AppTest");
        params.put("arrange", "P");
        params.put("listYN", "Y");
        params.put("areaCode", local);//1 서울 //39 제주도 //5 광주 // 6 부산
        params.put("_type", "json");

        String url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/" + search;

        url = addParams(url, params);

        if(!eventDate.equals(""))
            url += "&eventStartDate=" + eventDate;

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try{
                            Log.d("Json", "execute");
                            JSONObject response = (JSONObject) jsonObject.get("response");
                            JSONObject body = (JSONObject) response.get("body");
                            JSONObject items = (JSONObject) body.get("items");
                            JSONArray itemArray = items.getJSONArray("item");

                            ArrayList<Travel> arItem = new ArrayList<>();
                            for(int i=0; i<itemArray.length(); i++){
                                JSONObject item = itemArray.getJSONObject(i);
                                Travel travel = new Travel();
                                Log.d("Json",item.getString("addr1") + " " +
                                        item.getString("title") + " " + item.getString("firstimage") + " " +
                                        item.getDouble("mapx") + " " + item.getDouble("mapy"));
                                travel.setSpot(parseAddress(item.getString("addr1")));
                                travel.setAddress(parseTitle(item.getString("title")));
                                travel.setImage(item.getString("firstimage"));

                                if(travel.getImg().equals("")) continue;

                                travel.setMapX(item.getDouble("mapx"));
                                travel.setMapY(item.getDouble("mapy"));
                                arItem.add(travel);
                                model.add(travel);
                            }

                            if (arItem.size() > 0) {
                                adapter.listData = arItem;
                                adapter.notifyDataSetChanged();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.d("Json", "error");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private String addParams(String url, HashMap<String, String> mapParam) {
        StringBuilder stringBuilder = new StringBuilder(url + "?");

        if (mapParam != null) {
            for (String key : mapParam.keySet()) {
                stringBuilder.append(key + "=");
                stringBuilder.append(mapParam.get(key) + "&");
            }
        }
        return stringBuilder.toString();
    }

    private String parseAddress(String addr){
        String str[] = addr.split(" ");
        return (str.length > 1) ? str[0] + str[1]: addr;
    }

    private String parseTitle(String title){
        return (title.length() >= 12 ) ? title.substring(0, 12) + ".." : title;
    }

    private String convertLocalNum (String local){

        if(local.equals("제주") || local.equals("제주도")) return "39";

        String arr1[] = {"서울", "인천", "대전", "대구", "광주", "부산", "울산", "세종"};

        String arr2[] = {"서울특별시", "인천광역시", "대전광역시", "대구광역시", "광주광역시", "부산광역시", "울산광역시", "세종특별자치시"};

        for(int i=0; i<arr1.length; i++) {
            if (local.equals(arr1[i]) || local.equals(arr2[i])) {
                return String.valueOf(i + 1);
            }
        }

            return "-1";
    }

    private void setProfileFromCloud(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("profile/" + model.getUserinfo().getValue().getUserID() +"profile.png");

        if(imgRef != null){
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("callUrl", "sucess" + uri);
                    Glide.with(getContext()).load(uri).error(R.drawable.user_ic).into(profile);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("test", e.toString());
                }
            });
        }
    }


}