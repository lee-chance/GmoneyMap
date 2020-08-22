package com.chance.gmoneymap.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.chance.gmoneymap.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends Fragment {

    private TextView textView, tv_dDay, tv_aboutDate;
    private CalendarView calendarView;
    private Button button;

    private int selectedYear, selectedMonth, selectedDay;
    private SharedPreferences sharedPreferences;

    public NotifyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        String selectedDate = sharedPreferences.getString("selectedDate", null);
        if (selectedDate == null) {
            tv_dDay.setVisibility(View.GONE);
            calendarView.setVisibility(View.VISIBLE);
            textView.setText("지급일 설정");
            button.setText("확인");

            tv_aboutDate.setVisibility(View.GONE);
/*
            tv_aboutDate.setText("지급일은 어떻게 알 수 있나요?");
            tv_aboutDate.setClickable(true);
            tv_aboutDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new Dialog
                    Toast.makeText(getActivity(), "확인하기", Toast.LENGTH_SHORT).show();
                }
            });
*/
        } else {
            tv_dDay.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.GONE);
            textView.setText("지원금 소멸까지");
            button.setText("다시 설정하기");

            tv_aboutDate.setText("( ~ " + selectedDate + ")");
            //tv_aboutDate.setClickable(false);
            tv_aboutDate.setVisibility(View.VISIBLE);

            setDDayText(calDate(selectedDate));
        }
    }

    private void setDDayText(int calDate) {
        if (calDate < 0) {
            tv_dDay.setText("유효기간이 지났습니다");
        } else if (calDate == 0) {
            tv_dDay.setText("D - Day");
        } else {
            tv_dDay.setText("D - " + calDate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notify, container, false);

        textView = v.findViewById(R.id.textView);
        tv_dDay = v.findViewById(R.id.tv_dDay);
        tv_aboutDate = v.findViewById(R.id.tv_aboutDate);
        calendarView = v.findViewById(R.id.calendarView);
        button = v.findViewById(R.id.button);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (calendarView.getVisibility() == View.GONE) {
                    editor.putString("selectedDate", null);
                } else {
                    editor.putString("selectedDate", selectedYear + "-" + (selectedMonth + 4) + "-" + selectedDay);
                }
                editor.apply();
                onStart();
            }
        });

        return v;
    }

    private int calDate(String criteriaDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // criteriaDate, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date criteriaTime = format.parse(criteriaDate);

            Calendar c = Calendar.getInstance(); // 비교할 시간
            c.setTime(criteriaTime);
            c.clear(Calendar.HOUR);
            c.clear(Calendar.MINUTE);
            c.clear(Calendar.SECOND);
            c.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

            Calendar c2 = Calendar.getInstance(); // 현재 시간
            c2.clear(Calendar.HOUR);
            c2.clear(Calendar.MINUTE);
            c2.clear(Calendar.SECOND);
            c2.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화
            long dDayDiff = c.getTimeInMillis() - c2.getTimeInMillis();
            int result = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));

            return result + 1;

        } catch (ParseException e) {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }
}
