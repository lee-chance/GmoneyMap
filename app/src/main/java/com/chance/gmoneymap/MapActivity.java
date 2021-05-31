package com.chance.gmoneymap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import com.chance.gmoneymap.Adapters.ViewPagerAdapter;
import com.chance.gmoneymap.Models.DataModel;
import com.chance.gmoneymap.Models.Head;
import com.chance.gmoneymap.Models.RESULT;
import com.chance.gmoneymap.Models.RegionMnyFacltStu;
import com.chance.gmoneymap.Models.Row;
import com.chance.gmoneymap.Retrofit.ApiInterface;
import com.chance.gmoneymap.Retrofit.GmoneyClass;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends BaseActivity {

    private MapView mMapView;

    //location global variable
    private double latitude, longitude;
    private MapPoint currentMapPoint;
    private LocationManager locationManager;

    int tagNum;
    int overlapCount;
    int rowCount;
    private static String city;
    private List<String> categoryList;
    private List<String> stringList;
    private List<Row> rowList;
    private List<Row> innerRowList;
    private List<Row> searchRowList;
    private Map<String, Integer> map;
    private int[] tv_category = {R.id.tv_food1, R.id.tv_food2, R.id.tv_sales1, R.id.tv_sales2,
            R.id.tv_business1, R.id.tv_business2, R.id.tv_cafe, R.id.tv_hospital, R.id.tv_accommodation,
            R.id.tv_leisure, R.id.tv_culture, R.id.tv_home, R.id.tv_academy, R.id.tv_service,
            R.id.tv_manufacturing, R.id.tv_oil, R.id.tv_flower, R.id.tv_construction, R.id.tv_etc};

    private LinearLayout ll_top;
    private FloatingActionButton fab_gps;
    private NestedScrollView nestedScrollView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tv_address;
    private TextView tv_myArea;
    private TextView tv_mapArea;

    private BottomSheetBehavior bottomSheetBehavior;

    private boolean fullScreen = true;

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        MapView.clearMapTilePersistentCache();
//    }
//
//    @Override
//    protected void onStart() {
//        MapView.setMapTilePersistentCacheEnabled(true);
//        super.onStart();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // binding & initialize
        initialize();

        initView();
        initMap();

        // 검색
        final Row newRow = getIntent().getParcelableExtra("newRow");
        if (newRow != null) {
            mMapView.removeAllCircles();
            mMapView.removeAllPOIItems();
            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(newRow.getLatitude()), Double.parseDouble(newRow.getLongitude())), true);
            nestedScrollView.fling(convertPixelsToDp(60));
            MapPOIItem marker = new MapPOIItem();
            marker.setTag(50000);
            double pointX = Double.parseDouble(newRow.getLatitude());
            double pointY = Double.parseDouble(newRow.getLongitude());
            //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(pointX, pointY);
            marker.setItemName(newRow.getShopName());
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            marker.setMapPoint(mapPoint);
            marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
            mMapView.addPOIItem(marker);
            searchRowList = new ArrayList<>();
            searchRowList.add(newRow);
        } else {
            getCurrentLocation();
        }

        //gps click listener
        fab_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

//        TextView tv_warning = findViewById(R.id.tv_warning);
//        tv_warning.setSelected(true);
//        tv_warning.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
//                startActivity(intent);
//            }
//        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout.Tab newTab = tabLayout.getTabAt(1);
                newTab.select();
            }
        });
    }

    private void initialize() {
        // binding
        ll_top = findViewById(R.id.ll_top);
        fab_gps = findViewById(R.id.fab_gps);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tv_address = findViewById(R.id.tv_address);
        tv_myArea = findViewById(R.id.tv_myArea);
        tv_mapArea = findViewById(R.id.tv_mapArea);

        // default
        tv_myArea.setSelected(true);
        tv_mapArea.setSelected(false);
    }

    private void initView() {
        setBottomTabLayout();
        setBottomViewPager();
    }

    private void setBottomTabLayout() {
        //init nested scroll view
        nestedScrollView.setFillViewport(true);
        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);

        //init tab layout
        tabLayout.addTab(tabLayout.newTab().setText("데이터").setIcon(R.drawable.ic_baseline_cloud_download_24));
//        tabLayout.addTab(tabLayout.newTab().setText("알리미").setIcon(R.drawable.ic_notifications));
        tabLayout.addTab(tabLayout.newTab().setText("검색").setIcon(R.drawable.ic_search));
        tabLayout.addTab(tabLayout.newTab().setText("메뉴").setIcon(R.drawable.ic_menu));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                setTabIconColor(tab, R.color.colorPrimaryLighter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabIconColor(tab, android.R.color.black);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setBottomViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setTabIconColor(TabLayout.Tab tab, @ColorRes int id) {
        int tabIconColor = ContextCompat.getColor(getApplicationContext(), id);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
    }

    private void initMap() {
        mMapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.mMapView);
        mapViewContainer.addView(mMapView);

//        MapView.setMapTilePersistentCacheEnabled(true);
        mMapView.setPOIItemEventListener(poiItemEventListener);
        mMapView.setMapViewEventListener(mapViewEventListener);
    }

    private void getCurrentLocation() {
        // GPS체크
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            Toast.makeText(this, "GPS를 켜주세요!", Toast.LENGTH_LONG).show();
        }

        mMapView.setCurrentLocationEventListener(currentLocationEventListener);
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        tv_address.setText("현재위치 검색중...");
    }

    private void showTabs() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        ll_top.setVisibility(View.VISIBLE);
        fab_gps.setVisibility(View.VISIBLE);
        fullScreen = true;
    }

    private void hideTabs() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ll_top.setVisibility(View.GONE);
        fab_gps.setVisibility(View.GONE);
        fullScreen = false;
    }

    public void toggleSearchWhere(View view) {
        boolean isSelectedMyArea = view.getId() == R.id.tv_myArea; // or tv_mapArea
        tv_myArea.setSelected(isSelectedMyArea);
        tv_mapArea.setSelected(!isSelectedMyArea);
        setCategoryDeselected();
    }

    private void setCategoryDeselected() {
        findViewById(R.id.tv_all).setSelected(false);
        for (int value : tv_category) {
            findViewById(value).setSelected(false);
        }
    }

    // 검색시작
    public void categoryClick(View v) {
        setCategoryDeselected();

        // 저장되어 있는 현재도시 불러오기
        SharedPreferences sharedPreferences = getSharedPreferences("cityData", MODE_PRIVATE);
        String cityData = sharedPreferences.getString(city, null);

        mMapView.removeAllPOIItems();
        mMapView.removeAllCircles();

        if (tv_myArea.isSelected()) {
            getCurrentLocation();

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actionSearch(v, cityData);
            }
        } else {
            MapPoint centerPoint = mMapView.getMapCenterPoint();
            latitude = centerPoint.getMapPointGeoCoord().latitude;
            longitude = centerPoint.getMapPointGeoCoord().longitude;

            actionSearch(v, cityData);
        }
    }

    private void actionSearch(View v, String cityData) {
        if (v.getId() == R.id.tv_all) {
            findViewById(R.id.tv_all).setSelected(true);
            search(cityData, false);
        } else {
            for (int i = 0; i < tv_category.length; i++) {
                if (v.getId() == tv_category[i]) {
                    findViewById(tv_category[i]).setSelected(true);
                    categoryExcel(i);
                    search(cityData, true);
                    return;
                }
            }
        }
    }

    private void search(String data, boolean isSelectedCategory) {
        if (data == null) {
            try {
                requestSearchLocal(isSelectedCategory);
            } catch (Exception e) {
                Toast.makeText(this, "불러오기 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            searchLocalFromDB(data, isSelectedCategory);
        }
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
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
                        city = strAdd.substring(0, strAdd.indexOf("시") + 1);
                    } else if (strAdd.contains("군") && strAdd.indexOf("군") > 1) {
                        city = strAdd.substring(0, strAdd.indexOf("군") + 1);
                    }
                } else {
                    strAdd = "현 위치는 경기도가 아닙니다";
                    city = null;
                }
            } else {
                city = "error";
                strAdd = "위치를 찾을 수 없습니다.";
                Log.w("Chance", "No Address returned!");
            }
        } catch (Exception e) {
            city = "error";
            strAdd = "위치를 찾을 수 없습니다.";
            Log.e("Chance", "getAddressError : " + e.getMessage());
        }

        return strAdd;
    }

    private void overlapDialog(MapPOIItem mapPOIItem) {
        innerRowList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("검색결과");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        String xString = "" + mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        String yString = "" + mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
        for (int i = 0; i < rowList.size(); i++) {
            String xOverlap = "" + mMapView.findPOIItemByTag(i).getMapPoint().getMapPointGeoCoord().latitude;
            String yOverlap = "" + mMapView.findPOIItemByTag(i).getMapPoint().getMapPointGeoCoord().longitude;
            if (xString.equals(xOverlap) && yString.equals(yOverlap)) {
                innerRowList.add((Row) mMapView.findPOIItemByTag(i).getUserObject());
                adapter.add(rowList.get(i).getShopName());
            }

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = adapter.getItem(which);
                    for (int i = 0; i < innerRowList.size(); i++) {
                        if (selectedItem.equals(innerRowList.get(i).getShopName())) {
                            detailDialog(i, innerRowList);
                        }
                    }
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void detailDialog(final int tag, final List<Row> rows) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);

        CardView cardView = new CardView(MapActivity.this);
        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardViewParams.setMargins(convertPixelsToDp(30), 0, convertPixelsToDp(12), 0);
        cardView.setLayoutParams(cardViewParams);
        cardView.setCardElevation(convertPixelsToDp(8));
        cardView.setRadius(convertPixelsToDp(20));
        cardView.setUseCompatPadding(true);
        cardView.setPreventCornerOverlap(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        LinearLayout ll_parent = new LinearLayout(MapActivity.this);
        ll_parent.setLayoutParams(params);
        ll_parent.setPadding(convertPixelsToDp(20), convertPixelsToDp(20), convertPixelsToDp(20), convertPixelsToDp(20));
        ll_parent.setOrientation(LinearLayout.VERTICAL);

        TextView dialog_shopName = new TextView(MapActivity.this);
        dialog_shopName.setLayoutParams(params);
        dialog_shopName.setGravity(Gravity.CENTER);
        dialog_shopName.setPadding(0, 0, 0, convertPixelsToDp(12));
        dialog_shopName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        dialog_shopName.setTextColor(getResources().getColor(android.R.color.black));
        dialog_shopName.setText(rows.get(tag).getShopName());

        LinearLayout ll_child = new LinearLayout(MapActivity.this);
        ll_child.setLayoutParams(params);
        ll_child.setPadding(convertPixelsToDp(12), convertPixelsToDp(12), convertPixelsToDp(12), convertPixelsToDp(12));
        ll_child.setBackground(getDrawable(R.drawable.shape_detail_dialog));

        LinearLayout ll_content = new LinearLayout(MapActivity.this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentParams.weight = 1;
        ll_content.setLayoutParams(contentParams);
        ll_content.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0, convertPixelsToDp(8), 0, convertPixelsToDp(8));
        textViewParams.gravity = Gravity.START;

        TextView dialog_categoryName = new TextView(MapActivity.this);
        dialog_categoryName.setLayoutParams(textViewParams);
        dialog_categoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        dialog_categoryName.setText(rows.get(tag).getCategoryName());

        TextView dialog_telNumber = new TextView(MapActivity.this);
        dialog_telNumber.setLayoutParams(textViewParams);
        dialog_telNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if (rows.get(tag).getTelNumber() != null) {
            dialog_telNumber.setText(rows.get(tag).getTelNumber());
        } else {
            dialog_telNumber.setText("-");
        }

        final TextView dialog_address = new TextView(MapActivity.this);
        dialog_address.setLayoutParams(textViewParams);
        dialog_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if (rows.get(tag).getRoadAddress() != null) {
            dialog_address.setText(rows.get(tag).getRoadAddress());
        } else if (rows.get(tag).getLocationAddress() != null) {
            dialog_address.setText(rows.get(tag).getLocationAddress());
        } else {
            dialog_address.setText("-");
        }

        ll_content.addView(dialog_categoryName);
        ll_content.addView(dialog_telNumber);
        ll_content.addView(dialog_address);

        LinearLayout ll_image = new LinearLayout(MapActivity.this);
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewParams.gravity = Gravity.CENTER;
        ll_image.setLayoutParams(imageViewParams);
        ll_image.setOrientation(LinearLayout.VERTICAL);

        ImageView iv_call = new ImageView(MapActivity.this);
        iv_call.setLayoutParams(imageViewParams);
        iv_call.setImageResource(R.drawable.ic_call_big);
        iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rows.get(tag).getTelNumber() == null) {
                    Toast.makeText(getApplicationContext(), "전화번호를 제공하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String tel = "tel:" + rows.get(tag).getTelNumber();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            }
        });

        ll_image.addView(iv_call);

        ll_child.addView(ll_content);
        ll_child.addView(ll_image);

        ll_parent.addView(dialog_shopName);
        ll_parent.addView(ll_child);

        cardView.addView(ll_parent);

        builder.setView(cardView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    public int convertPixelsToDp(float px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    private void setNewMarker(Row row) {
        rowList.add(row);
        MapPOIItem marker = new MapPOIItem();
        marker.setUserObject(row);
        marker.setTag(tagNum);
        double pointX = Double.parseDouble(row.getLatitude());
        double pointY = Double.parseDouble(row.getLongitude());
        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(pointX, pointY);
        String xyString = pointX + "-" + pointY;
        if (stringList.contains(xyString)) {
            // 2개 이상의 검색결과는 블루핀으로 찍음
            if (map.get(xyString) == null) {
                overlapCount = 1;
            } else {
                overlapCount = map.get(xyString);
            }
            overlapCount++;
            map.put(xyString, overlapCount);
            marker.setItemName(overlapCount + "개의 검색결과");
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        } else {
            // 단일 검색결과는 레드핀으로 찍음
            stringList.add(xyString);
            marker.setItemName(row.getShopName());
            marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        }
        marker.setMapPoint(mapPoint);
        marker.setCustomImageAutoscale(false);
        marker.setCustomImageAnchor(0.5f, 1.0f);
        mMapView.addPOIItem(marker);
        tagNum++;
    }

    public void categoryExcel(int i) {
        categoryList = new ArrayList<>();
        Workbook workbook;
        Sheet sheet;
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("shoplist.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);

            int
                    RowStart = 1,
                    RowEnd = 613 - 1;

            for (int row = RowStart; row <= RowEnd; row++) {
                String excelload = sheet.getCell(i, row).getContents();
                if (excelload.equals("")) {
                    break;
                }
                categoryList.add(excelload);
            }
            workbook.close();

        } catch (IOException e) {
            Log.e("Chance", "1 : " + e.getMessage());
        } catch (BiffException e) {
            Log.e("Chance", "2 : " + e.getMessage());
        }
    }

    // Override Part
    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    // Listener Part
    private final MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
            fab_gps.setImageResource(R.drawable.ic_gps_off);
        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
            if (fullScreen) {
                hideTabs();
            } else {
                showTabs();
            }
        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
            double lat = mapPoint.getMapPointGeoCoord().latitude;
            double lon = mapPoint.getMapPointGeoCoord().longitude;
            tv_address.setText(getCompleteAddressString(getApplicationContext(), lat, lon));
        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        }
    };

    private final MapView.CurrentLocationEventListener currentLocationEventListener = new MapView.CurrentLocationEventListener() {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
//            currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
            currentMapPoint = mapPoint;
            //이 좌표로 지도 중심 이동
            mapView.setMapCenterPoint(currentMapPoint, true);
            //전역변수로 현재 좌표 저장
            latitude = mapPointGeo.latitude;
            longitude = mapPointGeo.longitude;
            tv_address.setText(getCompleteAddressString(getApplicationContext(), latitude, longitude));
            if (city != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("city", city);
                editor.apply();
            }
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setZoomLevel(2, true);
            fab_gps.setImageResource(R.drawable.ic_gps_on);
        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {
            Toast.makeText(MapActivity.this, "현재 위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {
        }
    };

    private final MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            switch (mapPOIItem.getMarkerType()) {
                case YellowPin:
                    detailDialog(0, searchRowList);
                    break;
                case BluePin:
                    overlapDialog(mapPOIItem);
                    break;
                case RedPin:
                    if (rowList == null) {
                        overlapDialog(mapPOIItem);
                    } else {
                        detailDialog(mapPOIItem.getTag(), rowList);
                    }
                    break;
            }
//            if (mapPOIItem.getMarkerType() == MapPOIItem.MarkerType.YellowPin) {
//                detailDialog(0, searchRowList);
//            } else if (mapPOIItem.getMarkerType() == MapPOIItem.MarkerType.BluePin) {
//                overlapDialog(mapPOIItem);
//            } else if (mapPOIItem.getMarkerType() == MapPOIItem.MarkerType.RedPin) {
//                if (rowList == null) {
//                    overlapDialog(mapPOIItem);
//                } else {
//                    detailDialog(mapPOIItem.getTag(), rowList);
//                }
//            }
        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        }
    };

    private void searchLocalFromDB(String cityData, boolean isSelectedCategory) {
        stringList = new ArrayList<>();
        rowList = new ArrayList<>();
        map = new HashMap<>();
        overlapCount = 0;
        tagNum = 0;

        int radius = 300;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();

        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                radius, // radius
                Color.argb(70, 1, 104, 179), // strokeColor
                Color.argb(30, 1, 104, 179) // fillColor
        );
        circle.setTag(5678);
        mMapView.addCircle(circle);

        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<DataModel>>() {
        }.getType();
        List<DataModel> mDataModels = gson.fromJson(cityData, listType);

        for (DataModel dataModel : mDataModels) {
            String category = dataModel.getINDUTYPE_NM();
            if (isSelectedCategory) {
                if (category != null && categoryList.contains(category)) {
                    setNewMarkerIntoCircle(dataModel, radius);
                }
            } else {
                setNewMarkerIntoCircle(dataModel, radius);
            }
        }
        Toast.makeText(MapActivity.this, "검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    private void setNewMarkerIntoCircle(DataModel dataModel, int radius) {
        if (dataModel.getREFINE_WGS84_LAT() != null && dataModel.getREFINE_WGS84_LOGT() != null) {
            String targetX = dataModel.getREFINE_WGS84_LAT();
            String targetY = dataModel.getREFINE_WGS84_LOGT();
            int distance = (int) (Math.sqrt(Math.pow(Double.parseDouble(targetX) - latitude, 2) + Math.pow(Double.parseDouble(targetY) - longitude, 2)) * 100000);
            if (distance < radius) {
                Row row = new Row(dataModel.getCMPNM_NM(), dataModel.getINDUTYPE_NM(), dataModel.getTELNO(), dataModel.getREFINE_ROADNM_ADDR(),
                        dataModel.getREFINE_LOTNO_ADDR(), dataModel.getREFINE_WGS84_LOGT(), dataModel.getREFINE_WGS84_LAT());
                setNewMarker(row);
            }
        }
    }

    // openapi에서 데이터 가져오기
    private void requestSearchLocal(final boolean isSelectedCategory) {
        // 초기화
        stringList = new ArrayList<>();
        rowList = new ArrayList<>();
        map = new HashMap<>();
        overlapCount = 0;
        tagNum = 0;
        rowCount = 0;

        // 프로그래스바
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();

        // Retrofit 연결
        final int radius = 300;
        String base_uri = "https://openapi.gg.go.kr/";
        Retrofit client = new Retrofit.Builder().baseUrl(base_uri).addConverterFactory(GsonConverterFactory.create()).build();
        final ApiInterface service = client.create(ApiInterface.class);

        if (city == null) {
            // 경기도가 아닐 때
            Toast.makeText(this, "경기도가 아닙니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (city.equals("error")) {
            // 에러
            Toast.makeText(this, "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            // 검색실행
            Call<GmoneyClass> call = service.getSearchCity(getString(R.string.api_key), "json", 1, city);
            call.enqueue(new Callback<GmoneyClass>() {
                @Override
                public void onResponse(@NotNull Call<GmoneyClass> call, @NotNull Response<GmoneyClass> response) {
                    if (response.isSuccessful()) {
                        GmoneyClass result = response.body();
                        List<RegionMnyFacltStu> regionMnyFacltStus = result.getRegionMnyFacltStus();
                        if (regionMnyFacltStus != null) {
                            List<Head> heads = regionMnyFacltStus.get(0).getHead();
                            final Integer listTotalCount = heads.get(0).getListTotalCount();
                            int indexEnd = listTotalCount / 100 + 1;

                            //circle 생성
                            MapCircle circle = new MapCircle(
                                    MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                                    radius, // radius
                                    Color.argb(70, 1, 104, 179), // strokeColor
                                    Color.argb(30, 1, 104, 179) // fillColor
                            );
                            circle.setTag(5678);
                            mMapView.addCircle(circle);

                            for (int pIndex = 1; pIndex <= indexEnd; pIndex++) {
                                call = service.getSearchCity(getString(R.string.api_key), "json", pIndex, city);
                                call.enqueue(new Callback<GmoneyClass>() {
                                    @Override
                                    public void onResponse(@NotNull Call<GmoneyClass> call, @NotNull Response<GmoneyClass> response) {
                                        if (response.isSuccessful()) {
                                            GmoneyClass result = response.body();
                                            List<RegionMnyFacltStu> regionMnyFacltStus = result.getRegionMnyFacltStus();
                                            List<Head> heads = regionMnyFacltStus.get(0).getHead();
                                            RESULT results = heads.get(1).getRESULT();
                                            if (results.getCODE().equals("INFO-000")) {
                                                List<Row> rows = regionMnyFacltStus.get(1).getRow();
                                                for (Row row : rows) {
                                                    rowCount++;
                                                    String category = row.getCategoryName();
                                                    if (isSelectedCategory) {
                                                        if (category != null && categoryList.contains(category)) {
                                                            if (row.getLatitude() != null && row.getLongitude() != null) {
                                                                String targetX = row.getLatitude();
                                                                String targetY = row.getLongitude();
                                                                int distance = (int) (Math.sqrt(Math.pow(Double.parseDouble(targetX) - latitude, 2) + Math.pow(Double.parseDouble(targetY) - longitude, 2)) * 100000);
                                                                if (distance < radius) {
                                                                    setNewMarker(row);
                                                                }
                                                            }
                                                        }
                                                        if (rowCount == listTotalCount) {
                                                            progressDialog.dismiss();
                                                            stringList.clear();
                                                            categoryList.clear();
                                                            Toast.makeText(MapActivity.this, "검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (row.getLatitude() != null && row.getLongitude() != null) {
                                                            String targetX = row.getLatitude();
                                                            String targetY = row.getLongitude();
                                                            int distance = (int) (Math.sqrt(Math.pow(Double.parseDouble(targetX) - latitude, 2) + Math.pow(Double.parseDouble(targetY) - longitude, 2)) * 100000);
                                                            if (distance < radius) {
                                                                // 거리 300이내에만 표시
                                                                setNewMarker(row);
                                                            }
                                                        }
                                                        if (rowCount == listTotalCount) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(MapActivity.this, "검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            } else {
                                                progressDialog.setMessage(results.getCODE());
                                                progressDialog.show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<GmoneyClass> call, @NotNull Throwable t) {
                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                            builder.setMessage(city + "는 더 이상 데이터를 제공하지 않습니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            onBackPressed();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }
                }


                @Override
                public void onFailure(@NotNull Call<GmoneyClass> call, @NotNull Throwable t) {
                }
            });
        }
    }
}
