package com.chance.gmoneymap.Fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.chance.gmoneymap.Adapters.MenuAdapter;
import com.chance.gmoneymap.Models.MenuModel;
import com.chance.gmoneymap.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private List<MenuModel> list;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        RecyclerView rv_menu = v.findViewById(R.id.rv_menu);

        list = new ArrayList<>();
        setListData("공지사항", getResources().getDrawable(R.drawable.ic_notice));
        //setListData("설정", getResources().getDrawable(R.drawable.ic_settings));
        setListData("데이터 다운로드", getResources().getDrawable(R.drawable.ic_download));
        setListData("오류제보", getResources().getDrawable(R.drawable.ic_report));
        setListData("평점주기", getResources().getDrawable(R.drawable.ic_grade));
        setListData("공유하기", getResources().getDrawable(R.drawable.ic_share));
        setListData("정보", getResources().getDrawable(R.drawable.ic_info));
        //setListData("좋아요리스트", getResources().getDrawable(R.drawable.ic_info));

        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), list);
        rv_menu.setAdapter(menuAdapter);

        return v;
    }

    private void setListData(String content, Drawable icon) {
        MenuModel menuModel = new MenuModel(content, icon);
        list.add(menuModel);
    }

}
