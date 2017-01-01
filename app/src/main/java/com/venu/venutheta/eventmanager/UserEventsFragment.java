package com.venu.venutheta.eventmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.venu.venutheta.R;

public class UserEventsFragment extends Fragment  {
    PopupMenu popupMenu;



    public UserEventsFragment() {
    }

    public static UserEventsFragment newInstance() {
        return new UserEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
              return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void showPopup(View view,int pos) {
        popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_discover);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() ==R.id.action_editt){

                // what to do


            }else if (item.getItemId() == R.id.action_delete){

            }
            return false;
        });
        popupMenu.show();
    }


}
