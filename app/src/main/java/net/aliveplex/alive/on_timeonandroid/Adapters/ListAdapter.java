/*package net.aliveplex.alive.on_timeonandroid;

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
/*
public class ListAdapter extends ArrayAdapter<Subject>{
    private final Context context;
    private final ArrayList<Subject> itemsArrayList;
    final Realm realm = Realm.getDefaultInstance();
    public ListAdapter(Context context, ArrayList<Subject> itemsArrayList) {
        super(context, R.layout.listtextview, R.id.classList);
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
*/
package net.aliveplex.alive.on_timeonandroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.aliveplex.alive.on_timeonandroid.R;

public class ListAdapter extends BaseAdapter {
    Context mContext;
    String[] subID;
    int[] sec;

    public ListAdapter(Context context, String[] subID, int[] sec) {
        this.mContext= context;
        this.subID = subID;
        this.sec = sec;
    }

    public int getCount() {
        return subID.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public String[] getSubID(){
        return subID;
    }

    public int[] getSec(){
        return sec;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.listtextview, parent, false);

        TextView SubID = (TextView)view.findViewById(R.id.subject_id_textview);
        SubID.setText(subID[position]);
        TextView Sec = (TextView)view.findViewById(R.id.section_textview);
        Sec.setText(Integer.toString(sec[position]));

        return view;
    }
}