package com.example.cilo.codevated;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cilo on 4/23/17.
 */

public class FragmentMyCircles extends Fragment {
    GridView gridView;
    LinearLayout noDataLinear;
    LocalUserStorage localUserStorage;
    User user;
    String url;
    HandleJsonDataFromServer handleJsonDataFromServer;
    ArrayList<HashMap<String,String>> dataFromServerArraylist;
    HashMap<String,String> dataSelectedHashMap,requestDataFromServer,dataFromServerhashmap;

    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_circles,null);

        gridView = (GridView) view.findViewById(R.id.gridView);
        noDataLinear = (LinearLayout) view.findViewById(R.id.no_data);

        localUserStorage = new LocalUserStorage(getActivity());
        user = localUserStorage.getSignedinUser();

        url = "/getUserCircles.php";
        requestDataFromServer = new HashMap<>();
        requestDataFromServer.put("user_id",""+user.userId);

        new GetDataFromServer(requestDataFromServer, url, new UrlCallBack() {
            @Override
            public void done(String response) {
                if(response == null){

                }else{
                    handleJsonDataFromServer=new HandleJsonDataFromServer(response);
                    dataFromServerArraylist = handleJsonDataFromServer.getCircles();

                    if(dataFromServerArraylist == null){

                    }else{
                        dataFromServerhashmap = dataFromServerArraylist.get(0);
                        int state = Integer.parseInt(dataFromServerhashmap.get("state"));

                        Log.d("cilo123",""+state);

                        if(state == 0){
                            noDataLinear.setVisibility(View.VISIBLE);
                        }else {
                            CustomGridviewAllCircles customGridviewAllCircles =
                                    new CustomGridviewAllCircles(getActivity(), dataFromServerArraylist);
                            gridView.setAdapter(customGridviewAllCircles);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    dataSelectedHashMap = new HashMap<String, String>();

                                    dataSelectedHashMap = dataFromServerArraylist.get(position);
                                    intent = new Intent(getActivity(), ActivityCircleSelected.class);
                                    intent.putExtra("dataSelectedHashmap", dataSelectedHashMap);
                                    getActivity().startActivity(intent);
                                }
                            });
                        }
                    }
                }
            }
        }).execute();

        return view;
    }
}
