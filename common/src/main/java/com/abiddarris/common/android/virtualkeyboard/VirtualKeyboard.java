/***********************************************************************************
 * Copyright 2024 Abiddarris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************************/
package com.abiddarris.common.android.virtualkeyboard;

import static android.view.MotionEvent.ACTION_UP;

import static com.abiddarris.common.android.virtualkeyboard.JSONKeys.KEYS;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import com.abiddarris.common.R;
import com.abiddarris.common.android.view.MoveableViewsGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VirtualKeyboard extends MoveableViewsGroup {
    
    private ImageButton editButton;
    private Key focus;
    private List<Key> keys = new ArrayList<>();
    private KeyListener listener;
    
    public VirtualKeyboard(Context context) {
        super(context);
        
        init(null);
    }
    
    public VirtualKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init(attrs);
    }

    public VirtualKeyboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setClickable(true);
        
        editButton = new ImageButton(getContext());
        editButton.setVisibility(GONE);
        editButton.setImageResource(R.drawable.ic_setting);
        editButton.setOnClickListener(v -> {
            var dialog = EditButtonDialog.newInstance(this);
            dialog.showForResult(((FragmentActivity)getContext()).getSupportFragmentManager(), (res) -> {
                if(focus != null)
                    onFocusMoved();            
            });
        });
        
        addView(editButton);
    }
    
    @Override
    protected boolean onChildTouch(View view, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                onLostFocus();
                
                break;
            case MotionEvent.ACTION_UP :
                focus = keys.stream()
                    .filter(key -> key.getButton() == view)
                    .findFirst()
                    .get();
            
                onFocusMoved();
        }
        return super.onChildTouch(view, event);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(focus == null) return false;
        switch(event.getAction()) {
            case ACTION_UP :
                onLostFocus();
                return true;
        }
        return super.onTouchEvent(event);
    }
    
    
    @Override
    public void setEdit(boolean edit) {
        super.setEdit(edit);
        
        if(!edit) {
            editButton.setVisibility(GONE);
        }
    }
    
    protected Key getFocus() {
        return focus;
    }
    
    protected void sendKeyEvent(Event event, int keycode) {
        var listener = getKeyListener();
        if(listener != null) {
            listener.onKey(event, keycode);
        }
    }
    
    public Key addButton() {
        Key key = new Key();
        key.init(getContext());
        
        keys.add(key);
        
        Button button = key.getButton();
        button.setText(R.string.button);
        
        addMoveableView(
            button, 
            new LayoutParams(100, 100),
            new TouchListener(this, key)
        );
        
        return key;
    }
    
    public void removeButton(Key key) {
        removeView(key.getButton());
      
        if(key == focus) {
            onLostFocus();
        }
        
        keys.remove(key);
    }
    
    public KeyListener getKeyListener() {
        return this.listener;
    }
    
    public void setKeyListener(KeyListener listener) {
        this.listener = listener;
    }
    
    public JSONObject save() throws JSONException {
        JSONObject header = new JSONObject();
        JSONArray array = new JSONArray();
        
        for(var key : keys) {
        	array.put(key.save());
        }
        
        header.put(KEYS, array);
        
        return header;
    }
    
    public void clearKeys() {
        for(Key key : new ArrayList<>(keys)) {
        	removeButton(key);
        }
    }
    
    public void load(JSONObject keyboard) throws JSONException {
        clearKeys(); 
        
        try {
            JSONArray keys = keyboard.getJSONArray(KEYS);
            for(int i = 0; i < keys.length(); ++i) {
                Key key = addButton();
                JSONObject keyJSON = keys.getJSONObject(i);
            
                key.load(keyJSON);
            }
        } catch (Throwable e) {
            clearKeys();
            throw e;
        }
    }
    
    private void onLostFocus() {
        focus = null;
            
        editButton.setVisibility(GONE);
    }
    
    private void onFocusMoved() { 
        View view = focus.getButton();
        
        LayoutParams params = (LayoutParams)editButton.getLayoutParams();
        params.height = view.getHeight();
                
        updateViewLayout(editButton, params);
            
        float x = view.getX() + view.getWidth();
            
        editButton.setVisibility(VISIBLE);
        editButton.setX(x <= getWidth() - editButton.getWidth() ? x : view.getX() - editButton.getWidth());
        editButton.setY(view.getY());
        editButton.bringToFront();
    }
}
