package net.aliveplex.alive.on_timeonandroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by snail on 2/1/2017.
 */

public class ListAdapter extends ArrayAdapter<Subject>{
    private final Context context;
    private final ArrayList<Subject> itemsArrayList;
    final Realm realm = Realm.getDefaultInstance();
    public ListAdapter(Context context, ArrayList<Subject> itemsArrayList) {
        super(context, R.layout.listtextview, R.id.lv);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowview = inflater.inflate(R.layout.listtextview,parent,false);

        TextView subject = (TextView) rowview.findViewById(R.id.text1);
        TextView section = (TextView) rowview.findViewById(R.id.text2);

        String readdata = itemsArrayList.get(position).getID();
        String[] splitLine = readdata.split(",");
        subject.setText(splitLine[0]);
        section.setText(splitLine[1]);

        return rowview;

    }

}
