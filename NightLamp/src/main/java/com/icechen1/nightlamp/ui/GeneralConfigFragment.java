package com.icechen1.nightlamp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.icechen1.nightlamp.AppPreferenceManager;
import com.icechen1.nightlamp.R;
import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.OpacityBar;
import com.larswerkman.colorpicker.SVBar;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class GeneralConfigFragment extends Fragment {
    private final static int CODE_COLOR_BKG = 1;
    private final static int CODE_COLOR_CLOCK = -1;
    private final static int CODE_COLOR_CLOCK_DIM = -2;

    private AppPreferenceManager pref;
    private FrameLayout bkgColor;
    private FrameLayout clockColor;
    private FrameLayout dimClockColor;

    public GeneralConfigFragment() {
            // Empty constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_general_options, container, false);
            pref = new AppPreferenceManager(getActivity());



            //UI References
            CheckBox fadeOutCheckbox = (CheckBox) view.findViewById(R.id.autoFadeCheckBox);
            final EditText fadeOutTime = (EditText) view.findViewById(R.id.autoFadeeditText);

            CheckBox flipCheckBox = (CheckBox) view.findViewById(R.id.flipCheckBox);

            //Preset values
            //FADE OUT TIMER
            fadeOutCheckbox.setChecked(pref.getFadeOutEnabled());
            fadeOutCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    pref.setFadeOutEnabled(b);
                    fadeOutTime.setVisibility(b?View.VISIBLE :View.GONE);
                }
            });
            fadeOutTime.setVisibility(pref.getFadeOutEnabled()?View.VISIBLE :View.GONE);
            fadeOutTime.setHint(pref.getFadeOutTime() + " mins");
            fadeOutTime.addTextChangedListener(new TextWatcher(){

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().equals(""))return;
                    try{
                      pref.setFadeOutTime(Integer.parseInt(editable.toString()));
                    }catch(Exception e){
                        pref.setFadeOutTime(30);
                    }
                }
            });

            //Phone flip
            //Check if the feature is on the device
            boolean flip = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if(flip){
                flipCheckBox.setChecked(pref.getFlip());
                flipCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        pref.setFlip(b);
                    }
                });
            }else{
                //No flash
                pref.setFlip(false);
            }

            //COLORS
            LinearLayout bkgColorContainer = (LinearLayout) view.findViewById(R.id.bkgcolorContainer);
            bkgColorContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorPickerDialog(CODE_COLOR_BKG);
                }
            });
            bkgColor = (FrameLayout) view.findViewById(R.id.bkgcolorView);
            bkgColor.setBackgroundColor(pref.getBkgColor());

            //Bright Clock color
            LinearLayout clockColorContainer = (LinearLayout) view.findViewById(R.id.clockColorContainer);
            clockColorContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorPickerDialog(CODE_COLOR_CLOCK);
                }
            });

            clockColor = (FrameLayout) view.findViewById(R.id.clockColorView);
            clockColor.setBackgroundColor(pref.getClockColorLight());

            //Dim clock color
            LinearLayout dimColorContainer = (LinearLayout) view.findViewById(R.id.dimClockColorContainer);
            dimColorContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorPickerDialog(CODE_COLOR_CLOCK_DIM);
                }
            });

            dimClockColor = (FrameLayout) view.findViewById(R.id.dimClockColorView);
            dimClockColor.setBackgroundColor(pref.getClockColorDim());

            final String[] clockSizeArray = getResources().getStringArray(R.array.clocksize);
            final Spinner clockSizeSpinner = (Spinner) view.findViewById(R.id.clockSizeSpinner);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, clockSizeArray);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            clockSizeSpinner.setAdapter(dataAdapter);
            clockSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    if(clockSizeSpinner.getSelectedItem().equals(clockSizeArray[0])){
                        pref.setClockSize("big");
                    }
                    if(clockSizeSpinner.getSelectedItem().equals(clockSizeArray[1])){
                        pref.setClockSize("normal");
                    }
                    if(clockSizeSpinner.getSelectedItem().equals(clockSizeArray[2])){
                        pref.setClockSize("small");
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //Load settings for clock
            if(pref.getClockSize().equals("big")){
                clockSizeSpinner.setSelection(0);
            }
            if(pref.getClockSize().equals("normal")){
                clockSizeSpinner.setSelection(1);
            }
            if(pref.getClockSize().equals("small")){
                clockSizeSpinner.setSelection(2);
            }


                return view;
        }

    public void colorPickerDialog(final int TYPE){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View f1 = inflater.inflate(R.layout.colorpicker_layout, null);

        final ColorPicker picker = (ColorPicker) f1.findViewById(R.id.picker);
        SVBar svBar = (SVBar) f1.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) f1.findViewById(R.id.opacitybar);
        Button button = (Button) f1.findViewById(R.id.button1);
        final TextView text = (TextView) f1.findViewById(R.id.textView1);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener(){

            @Override
            public void onColorChanged(int color) {
                if(TYPE == CODE_COLOR_BKG){
                    pref.setBkgColor(color);
                    bkgColor.setBackgroundColor(pref.getBkgColor());
                }
                if(TYPE == CODE_COLOR_CLOCK){
                    pref.setClockColorLight(color);
                    clockColor.setBackgroundColor(pref.getClockColorLight());
                }
                if(TYPE == CODE_COLOR_CLOCK_DIM){
                    pref.setClockColorDim(color);
                    dimClockColor.setBackgroundColor(pref.getClockColorDim());
                }
            }

        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                text.setTextColor(picker.getColor());
                picker.setOldCenterColor(picker.getColor());
            }
        });

        //Show dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Set a color");
        builder.setView(f1);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
