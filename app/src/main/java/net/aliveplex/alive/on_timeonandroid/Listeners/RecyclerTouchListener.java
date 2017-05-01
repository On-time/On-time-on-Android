package net.aliveplex.alive.on_timeonandroid.Listeners;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import net.aliveplex.alive.on_timeonandroid.Interfaces.ClickListener;

/**
 * Created by Aliveplex on 30/4/2560.
 */

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    if (Build.VERSION.SDK_INT >= 22){
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                    else {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d("RecyclerTouchListener", "onIntercept was called.");
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            Log.d("RecyclerTouchListener", "listener's method will be called");
            if (Build.VERSION.SDK_INT >= 22){
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            else {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
        }
        else {
            Log.d("RecyclerTouchListener", "child not found or listener is null or not touch event");
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}