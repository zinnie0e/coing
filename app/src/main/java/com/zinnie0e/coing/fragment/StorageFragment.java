package com.zinnie0e.coing.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.R;
import com.zinnie0e.coing.adapter.AdapterEdit;
import com.zinnie0e.coing.adapter.MyAdapter;
import com.zinnie0e.coing.data.ConvData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StorageFragment extends Fragment implements View.OnClickListener {
    static Context mContext;
    Resources res;

    View view;
    LinearLayout ly_status, ly_status_list, ly_status_new, ly_status_learning, ly_status_done, ly_status_nothing, ly_topInfo;
    ImageView img_status;
    TextView txt_status, txt_title;
    ImageButton btn_prev, btn_edit;
    static ListView lst_conv;

    boolean isCheckStatus = false;
    boolean isChkEdit = false;

    static MyAdapter myAdapter;
    static AdapterEdit editAdapter;
    static ArrayList<ConvData> convDataList;
    static ArrayList<ConvData> arraylist;
    private int mLastResourceId;

    public StorageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_storage, container, false);
        mContext = getActivity();
        res = mContext.getResources();
        this.mLastResourceId = R.drawable.ic_expand_left;

        findId();
        testttest();

        ly_status_list.setVisibility(View.GONE);
        return view;
    }

    private void testttest() {
        convDataList = new ArrayList<ConvData>();
        JSONArray jsondata = new JSONArray();

        try {
            for(int i = 0; i < 5; i ++){
                JSONObject obj = new JSONObject();
                obj.put("en", "test" + i);
                obj.put("ko", "테스트" + i);
                obj.put("sta", res.getString(R.string.status_done));
                jsondata.put(obj);
            }
            for(int i = 5; i < 15; i ++){
                JSONObject obj = new JSONObject();
                obj.put("en", "test" + i);
                obj.put("ko", "테스트" + i);
                obj.put("sta", res.getString(R.string.status_learning));
                jsondata.put(obj);
            }

            for(int i = 0; i < jsondata.length() ; i ++){
                convDataList.add(new ConvData(jsondata.getJSONObject(i).getString("en"), jsondata.getJSONObject(i).getString("ko"), jsondata.getJSONObject(i).getString("sta")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        myAdapter = new MyAdapter(getActivity(), convDataList);
        lst_conv.setAdapter(myAdapter);

        lst_conv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                //v.btn_itemSel.setImageResource(R.drawable.ic_radio_off);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == ly_status){
            if(!isCheckStatus) ly_status_list.setVisibility(View.VISIBLE);
            else ly_status_list.setVisibility(View.GONE);
            isCheckStatus = !isCheckStatus;
        }else if(v == ly_status_new){
            initStatusList(0);
        }else if(v == ly_status_learning){
            initStatusList(1);
        }else if(v == ly_status_done){
            initStatusList(2);
        }else if(v == ly_status_nothing){
            initStatusList(3);
        }else if(v == btn_edit){
            showEdit();
        }else if(v == btn_prev){
            //findNavController(this).navigate(R.id.action_to_homeFragment);
            if (this.mLastResourceId == R.drawable.ic_expand_left) {
                ((MainActivity) mContext).bottom_menu.findViewById(R.id.tab_home).performClick();
            }
        }else if(v == ((MainActivity)mContext).btn_bottom){
            Log.i("!!!!" , "과연 여기로?");
        }
    }

    private void showEdit() {
        float px;
        if(isChkEdit){
            ly_topInfo.setVisibility(View.VISIBLE);
            ly_status.setVisibility(View.VISIBLE);
            ((MainActivity)mContext).bottom_menu.setVisibility(View.VISIBLE);
            ((MainActivity)mContext).btn_bottom.setVisibility(View.GONE);
            txt_title.setText("저장함");
            btn_edit.setImageResource(R.drawable.ic_edit);
            btn_prev.setImageResource(R.drawable.ic_expand_left);
            this.mLastResourceId = R.drawable.ic_expand_left;

            px = 20 * ((float) res.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            myAdapter = new MyAdapter(getActivity(), convDataList);
            lst_conv.setAdapter(myAdapter);
        }else{
            ly_topInfo.setVisibility(View.GONE);
            ly_status.setVisibility(View.GONE);
            ((MainActivity)mContext).bottom_menu.setVisibility(View.GONE);
            ((MainActivity)mContext).btn_bottom.setVisibility(View.VISIBLE);
            txt_title.setText("편집");
            btn_edit.setImageResource(R.drawable.ic_close);
            btn_prev.setImageResource(R.color.claen);
            this.mLastResourceId = R.color.claen;


            px = 42 * ((float) res.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            editAdapter = new AdapterEdit(getActivity(), convDataList);
            lst_conv.setAdapter(editAdapter);
        }

        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) lst_conv.getLayoutParams();
        param.topMargin = (int) px;
        lst_conv.setLayoutParams(param);

        isChkEdit = !isChkEdit;

    }

    public void initStatusList(int val){
        /** 0: new / 1: learning / 2: done / 3: nothing*/
        int[] img_arr = {R.drawable.ic_new, R.drawable.ic_learning, R.drawable.ic_done, R.color.claen};
        int[] string_arr = {R.string.status_new, R.string.status_learning, R.string.status_done, R.string.status_nothing};
        img_status.setImageResource(img_arr[val]);
        txt_status.setText(string_arr[val]);
        ly_status_list.setVisibility(View.GONE);
        isCheckStatus = !isCheckStatus;
    }

    private void findId() {
        ly_status = (LinearLayout) view.findViewById(R.id.ly_status);
        ly_status_list = (LinearLayout) view.findViewById(R.id.ly_status_list);
        ly_status_new = (LinearLayout) view.findViewById(R.id.ly_status_new);
        ly_status_learning = (LinearLayout) view.findViewById(R.id.ly_status_learning);
        ly_status_done = (LinearLayout) view.findViewById(R.id.ly_status_done);
        ly_status_nothing = (LinearLayout) view.findViewById(R.id.ly_status_nothing);
        ly_topInfo = (LinearLayout) view.findViewById(R.id.ly_topInfo);

        txt_status = (TextView) view.findViewById(R.id.txt_status);
        txt_title = (TextView) view.findViewById(R.id.txt_title);

        lst_conv = (ListView)view.findViewById(R.id.lst_conv);

        img_status = (ImageView) view.findViewById(R.id.img_status);

        btn_prev = (ImageButton) view.findViewById(R.id.btn_prev);
        btn_edit = (ImageButton) view.findViewById(R.id.btn_edit);

        ly_status.setOnClickListener(this);
        ly_status_new.setOnClickListener(this);
        ly_status_learning.setOnClickListener(this);
        ly_status_done.setOnClickListener(this);
        ly_status_nothing.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_prev.setOnClickListener(this);

        ((MainActivity)mContext).btn_bottom.setOnClickListener(this);
    }
}