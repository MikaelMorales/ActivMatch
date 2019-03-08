package ch.unil.eda.activmatch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewId {

    public static ViewCreator of(int resId) {
        return (parent) -> LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
    }

    public interface ViewCreator {
        View createView(ViewGroup parent);
    }

}