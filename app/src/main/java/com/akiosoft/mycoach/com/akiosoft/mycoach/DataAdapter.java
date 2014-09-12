package com.akiosoft.mycoach.com.akiosoft.mycoach;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.akiosoft.mycoach.Application;
import com.google.api.client.json.GenericJson;

import java.util.List;

/**
 * Created by nvoskeritchian on 9/12/14.
 */
public abstract class DataAdapter<T extends GenericJson> extends ArrayAdapter {

    public DataAdapter(Application application) {
        super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                application.greetings);
    }

    public void replaceData(List<T> greetings) {
        clear();
        for (T greeting : greetings) {
            add(greeting);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextColor(Color.BLACK);

        T model = (T) this.getItem(position);

        StringBuilder sb = new StringBuilder();


        view.setText((String)model.get("label"));
        return view;
    }
}

