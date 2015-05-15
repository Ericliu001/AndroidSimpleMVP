package com.ericliudeveloper.withmvp;

import android.app.Fragment;
import android.os.Bundle;

public class CacheModelFragment extends Fragment {

    private Bundle cache; // As Fragment is the View in MVP pattern, it should have no knowledge of the data, so a Bundle is idea for this job


    public void setDataToBeCached(Bundle data){
        cache = data;
    }

    public Bundle getCachedData(){
        return cache;
    }

    public CacheModelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


}
