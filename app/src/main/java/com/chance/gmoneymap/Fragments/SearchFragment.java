package com.chance.gmoneymap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.chance.gmoneymap.MapActivity;
import com.chance.gmoneymap.R;
import com.chance.gmoneymap.ResultActivity;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private EditText et_search;
    private ImageButton btn_search;

    private Spinner spn_city, spn_category, spn_searchWhat;

    private String city, category, searchWhat;

    private ArrayAdapter<CharSequence> adapter_city, adapter_category, adapter_searchWhat;

    // search from me
    private MapView mMapView;
    private ImageButton btn_searchFromMe;
    private LocationManager locationManager;
    private String currentCity;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        //initialize
        spn_city = v.findViewById(R.id.spn_city);
        //spn_category = v.findViewById(R.id.spn_category);
        spn_searchWhat = v.findViewById(R.id.spn_searchWhat);
        et_search = v.findViewById(R.id.et_search);
        btn_search = v.findViewById(R.id.btn_search);
//        btn_searchFromMe = v.findViewById(R.id.btn_searchFromMe);

        adapter_city = ArrayAdapter.createFromResource(getActivity(), R.array.cityName, android.R.layout.simple_spinner_item);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_city.setAdapter(adapter_city);
        spn_city.setOnItemSelectedListener(cityItemSelectedListener);

        adapter_searchWhat = ArrayAdapter.createFromResource(getActivity(), R.array.searchWhat, android.R.layout.simple_spinner_item);
        adapter_searchWhat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_searchWhat.setAdapter(adapter_searchWhat);
        spn_searchWhat.setOnItemSelectedListener(searchWhatItemSelectedListener);

        //adapter_category = ArrayAdapter.createFromResource(getActivity(), R.array.categoryName, android.R.layout.simple_spinner_item);
        //adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spn_category.setAdapter(adapter_category);
        //spn_category.setOnItemSelectedListener(categoryItemSelectedListener);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    btn_search.callOnClick();
                }
                return true;
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city == null) {
                    Toast.makeText(getActivity(), "지역을 선택해 주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    String search = et_search.getText().toString().trim();
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra("city", city);
                    //intent.putExtra("category", category);
                    intent.putExtra("searchWhat", searchWhat);
                    intent.putExtra("search", search);
                    startActivity(intent);
                }
            }
        });

//        btn_searchFromMe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getCurrentLocation();
//                Intent intent = new Intent(getActivity(), ResultActivity.class);
//                if(currentCity==null){
//                    Toast.makeText(getActivity(), "현 위치는 경기도가 아닙니다", Toast.LENGTH_SHORT).show();
//                } else if(currentCity.equals("error")){
//                    Toast.makeText(getActivity(), "다시 시도해주세요", Toast.LENGTH_SHORT).show();
//                } else {
//                    intent.putExtra("city", currentCity);
//                    intent.putExtra("searchFromMe", true);
//                }
//            }
//        });

        return v;
    }

    private void getCurrentLocation() {
        // GPS체크
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            Toast.makeText(getActivity(), "GPS를 켜주세요!", Toast.LENGTH_LONG).show();
        }

        mMapView = new MapView(getActivity());
        mMapView.setCurrentLocationEventListener(currentLocationEventListener);
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        //tv_address.setText("현재위치 검색중...");
    }

    public String getCurrentCity(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                // "대한민국 " 글자 지워버림
                strAdd = strAdd.substring(5);

                if (strAdd.contains("경기도")) {
                    // "경기도" 글자 지우기
                    strAdd = strAdd.substring(4);
                    // city 값 찾기
                    if (strAdd.contains("시") && strAdd.indexOf("시") > 1) {
                        currentCity = strAdd.substring(0, strAdd.indexOf("시") + 1);
                    } else if (strAdd.contains("군") && strAdd.indexOf("군") > 1) {
                        currentCity = strAdd.substring(0, strAdd.indexOf("군") + 1);
                    }
                } else {
                    strAdd = "현 위치는 경기도가 아닙니다";
                    currentCity = null;
                }
            } else {
                currentCity = "error";
                strAdd = "위치를 찾을 수 없습니다.";
                Log.w("Chance", "No Address returned!");
            }
        } catch (Exception e) {
            currentCity = "error";
            strAdd = "위치를 찾을 수 없습니다.";
            Log.e("Chance", "getAddressError : " + e.getMessage());
        }

        return strAdd;
    }

    private MapView.CurrentLocationEventListener currentLocationEventListener = new MapView.CurrentLocationEventListener() {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            //전역변수로 현재 좌표 저장
            double latitude = mapPointGeo.latitude;
            double longitude = mapPointGeo.longitude;
        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {
            Toast.makeText(getActivity(), "현재 위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {
        }
    };

    AdapterView.OnItemSelectedListener cityItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    city = null;
                    break;
                case 1:
                    city = "가평군";
                    break;
                case 2:
                    city = "고양시";
                    break;
                case 3:
                    city = "과천시";
                    break;
                case 4:
                    city = "광명시";
                    break;
                case 5:
                    city = "광주시";
                    break;
                case 6:
                    city = "구리시";
                    break;
                case 7:
                    city = "군포시";
                    break;
                case 8:
                    city = "김포시";
                    break;
                case 9:
                    city = "남양주시";
                    break;
                case 10:
                    city = "동두천시";
                    break;
                case 11:
                    city = "부천시";
                    break;
                case 12:
                    city = "성남시";
                    break;
                case 13:
                    city = "수원시";
                    break;
                case 14:
                    city = "시흥시";
                    break;
                case 15:
                    city = "안산시";
                    break;
                case 16:
                    city = "안성시";
                    break;
                case 17:
                    city = "안양시";
                    break;
                case 18:
                    city = "양주시";
                    break;
                case 19:
                    city = "양평군";
                    break;
                case 20:
                    city = "여주시";
                    break;
                case 21:
                    city = "연천군";
                    break;
                case 22:
                    city = "오산시";
                    break;
                case 23:
                    city = "용인시";
                    break;
                case 24:
                    city = "의왕시";
                    break;
                case 25:
                    city = "의정부시";
                    break;
                case 26:
                    city = "이천시";
                    break;
                case 27:
                    city = "파주시";
                    break;
                case 28:
                    city = "평택시";
                    break;
                case 29:
                    city = "포천시";
                    break;
                case 30:
                    city = "하남시";
                    break;
                case 31:
                    city = "화성시";
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            city = null;
        }
    };

    AdapterView.OnItemSelectedListener categoryItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    category = null;
                    break;
                case 1:
                    category = "음식1";
                    break;
                case 2:
                    category = "음식2";
                    break;
                case 3:
                    category = "상가1";
                    break;
                case 4:
                    category = "상가2";
                    break;
                case 5:
                    category = "가게1";
                    break;
                case 6:
                    category = "가게2";
                    break;
                case 7:
                    category = "카페/마트/편의점";
                    break;
                case 8:
                    category = "병원/약국/기타의료";
                    break;
                case 9:
                    category = "숙박/여행";
                    break;
                case 10:
                    category = "레저";
                    break;
                case 11:
                    category = "도서/미용/문화";
                    break;
                case 12:
                    category = "가전/가구/의류";
                    break;
                case 13:
                    category = "학원/교육";
                    break;
                case 14:
                    category = "서비스";
                    break;
                case 15:
                    category = "제조업";
                    break;
                case 16:
                    category = "주유소";
                    break;
                case 17:
                    category = "꽃/과일/떡/농업";
                    break;
                case 18:
                    category = "건축/건설";
                    break;
                case 19:
                    category = "기타";
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            category = null;
        }
    };

    AdapterView.OnItemSelectedListener searchWhatItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    searchWhat = "상호명";
                    break;
                case 1:
                    searchWhat = "주소";
                    break;
                case 2:
                    searchWhat = "전화번호";
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            searchWhat = null;
        }
    };

}
