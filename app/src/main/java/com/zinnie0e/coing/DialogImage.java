package com.zinnie0e.coing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

import com.zinnie0e.coing.fragment.SaveFragment;

public class DialogImage extends DialogFragment implements View.OnClickListener {
    OnMyDialogResult mDialogResult;
    SaveFragment fragment;

    View view;
    LinearLayout btn_lyCamera, btn_lyGallery;
    Button btn_cancle;

    public DialogImage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_image, container, false);

        findId();

        Bundle args = getArguments();
        String value = args.getString("key");


        //fragment = (AddringFragment) getActivity().getSupportFragmentManager().findFragmentByTag("tag");


        return view;
    }

    private void findId() {
        btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
        btn_lyCamera = (LinearLayout) view.findViewById(R.id.btn_lyCamera);
        btn_lyGallery = (LinearLayout) view.findViewById(R.id.btn_lyGallery);

        btn_cancle.setOnClickListener(this);
        btn_lyCamera.setOnClickListener(this);
        btn_lyGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_cancle){
            finishDialog();
        }else if(v == btn_lyCamera){
            if( mDialogResult != null ){
                mDialogResult.finish("camera");
            }
            finishDialog();
        }else if(v == btn_lyGallery){
            if( mDialogResult != null ){
                mDialogResult.finish("gallery");
            }
            finishDialog();
        }
    }

    private void finishDialog() {
        DialogImage.this.dismiss();
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }


}