package com.chance.gmoneymap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chance.gmoneymap.Adapters.DataAdapter;
import com.chance.gmoneymap.Models.DataModel;
import com.chance.gmoneymap.Models.Head;
import com.chance.gmoneymap.Models.RESULT;
import com.chance.gmoneymap.Models.RegionMnyFacltStu;
import com.chance.gmoneymap.Models.Row;
import com.chance.gmoneymap.Retrofit.ApiInterface;
import com.chance.gmoneymap.Retrofit.GmoneyClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultActivity extends AppCompatActivity {

    //private int pIndex = 1;
    //private int pSize = 10;
    int indexEnd = 2;

    private TextView tv_resultCount, tv_loading;
    private RecyclerView rv_searchResult;
    //private TextView tv_prev, tv_first, tv_second, tv_third, tv_fourth, tv_fifth, tv_next;

    //private int page = 0;
    //boolean first, second, third, fourth, fifth, pageUP, pageDown;
    private String city, category, searchResult, searchWhat;
    private int rowCount;

    private List<DataModel> dataModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("검색 결과");
        actionBar.setDisplayHomeAsUpEnabled(true);

        StrictMode.enableDefaults();

        tv_resultCount = findViewById(R.id.tv_resultCount);
        tv_loading = findViewById(R.id.tv_loading);
        rv_searchResult = findViewById(R.id.rv_searchResult);
/*
        tv_prev = findViewById(R.id.tv_prev);
        tv_first = findViewById(R.id.tv_first);
        tv_second = findViewById(R.id.tv_second);
        tv_third = findViewById(R.id.tv_third);
        tv_fourth = findViewById(R.id.tv_fourth);
        tv_fifth = findViewById(R.id.tv_fifth);
        tv_next = findViewById(R.id.tv_next);
*/

        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        //category = intent.getStringExtra("category");
        searchWhat = intent.getStringExtra("searchWhat");
        searchResult = intent.getStringExtra("search");
        rv_searchResult.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("cityData", MODE_PRIVATE);
        String cityData = sharedPreferences.getString(city, null);
        if (cityData == null) {
            try {
                requestSearchLocal();
            } catch (Exception e) {
                Toast.makeText(ResultActivity.this, "불러오기 오류입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            searchLocalFromDB(cityData);
        }
    }

    private void searchLocalFromDB(String cityData) {
        dataModels = new ArrayList<>();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();

        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<DataModel>>() {
        }.getType();
        List<DataModel> mDataModels = gson.fromJson(cityData, listType);

        for (DataModel dataModel : mDataModels) {
            if (searchWhat.equals("상호명")) {
                String shopName = dataModel.getCMPNM_NM();
                if (searchResult.equals("")) {
                    String category = dataModel.getINDUTYPE_NM();
                    String roadAddress = dataModel.getREFINE_ROADNM_ADDR();
                    String locationAddress = dataModel.getREFINE_LOTNO_ADDR();
                    String telNumber = dataModel.getTELNO();
                    String latitude = dataModel.getREFINE_WGS84_LAT();
                    String longitude = dataModel.getREFINE_WGS84_LOGT();
                    DataModel data = new DataModel(shopName, category, roadAddress,
                            locationAddress, telNumber, latitude, longitude);
                    dataModels.add(data);
                } else if (shopName.contains(searchResult)) {
                    String category = dataModel.getINDUTYPE_NM();
                    String roadAddress = dataModel.getREFINE_ROADNM_ADDR();
                    String locationAddress = dataModel.getREFINE_LOTNO_ADDR();
                    String telNumber = dataModel.getTELNO();
                    String latitude = dataModel.getREFINE_WGS84_LAT();
                    String longitude = dataModel.getREFINE_WGS84_LOGT();
                    DataModel data = new DataModel(shopName, category, roadAddress,
                            locationAddress, telNumber, latitude, longitude);
                    dataModels.add(data);
                }
            } else if (searchWhat.equals("전화번호")) {
                String telNumber = dataModel.getTELNO();
                if (searchResult.equals("")) {
                    String shopName = dataModel.getCMPNM_NM();
                    String category = dataModel.getINDUTYPE_NM();
                    String roadAddress = dataModel.getREFINE_ROADNM_ADDR();
                    String locationAddress = dataModel.getREFINE_LOTNO_ADDR();
                    String latitude = dataModel.getREFINE_WGS84_LAT();
                    String longitude = dataModel.getREFINE_WGS84_LOGT();
                    DataModel data = new DataModel(shopName, category, roadAddress,
                            locationAddress, telNumber, latitude, longitude);
                    dataModels.add(data);
                } else if (telNumber != null && telNumber.contains(searchResult)) {
                    String shopName = dataModel.getCMPNM_NM();
                    String category = dataModel.getINDUTYPE_NM();
                    String roadAddress = dataModel.getREFINE_ROADNM_ADDR();
                    String locationAddress = dataModel.getREFINE_LOTNO_ADDR();
                    String latitude = dataModel.getREFINE_WGS84_LAT();
                    String longitude = dataModel.getREFINE_WGS84_LOGT();
                    DataModel data = new DataModel(shopName, category, roadAddress,
                            locationAddress, telNumber, latitude, longitude);
                    dataModels.add(data);
                }
            } else if (searchWhat.equals("주소")) {
                String roadAddress = dataModel.getREFINE_ROADNM_ADDR();
                String locationAddress = dataModel.getREFINE_LOTNO_ADDR();
                String address;
                if (roadAddress != null) {
                    address = roadAddress;
                } else address = locationAddress;
                if (searchResult.equals("")) {
                    String shopName = dataModel.getCMPNM_NM();
                    String category = dataModel.getINDUTYPE_NM();
                    String telNumber = dataModel.getTELNO();
                    String latitude = dataModel.getREFINE_WGS84_LAT();
                    String longitude = dataModel.getREFINE_WGS84_LOGT();
                    DataModel data = new DataModel(shopName, category, roadAddress,
                            locationAddress, telNumber, latitude, longitude);
                    dataModels.add(data);
                } else if (address != null && address.contains(searchResult)) {
                    String shopName = dataModel.getCMPNM_NM();
                    String category = dataModel.getINDUTYPE_NM();
                    String telNumber = dataModel.getTELNO();
                    String latitude = dataModel.getREFINE_WGS84_LAT();
                    String longitude = dataModel.getREFINE_WGS84_LOGT();
                    DataModel data = new DataModel(shopName, category, roadAddress,
                            locationAddress, telNumber, latitude, longitude);
                    dataModels.add(data);
                }
            }
            if (dataModels.size() == 0) {
                rv_searchResult.setBackgroundResource(android.R.drawable.stat_notify_error);
            } else {
                DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), dataModels);
                rv_searchResult.setAdapter(dataAdapter);
                tv_resultCount.setText(dataModels.size() + "개의 검색결과");
            }
        }
        Toast.makeText(ResultActivity.this, "검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }


    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestSearchLocal() {
        dataModels = new ArrayList<>();
        rowCount = 0;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();

        String base_uri = "https://openapi.gg.go.kr/";
        Retrofit client = new Retrofit.Builder().baseUrl(base_uri).addConverterFactory(GsonConverterFactory.create()).build();
        final ApiInterface service = client.create(ApiInterface.class);

        if (city != null) {
            Call<GmoneyClass> call = service.getSearchCity(getString(R.string.api_key), "json", 1, city);
            call.enqueue(new Callback<GmoneyClass>() {
                @Override
                public void onResponse(@NotNull Call<GmoneyClass> call, @NotNull Response<GmoneyClass> response) {
                    if (response.isSuccessful()) {
                        GmoneyClass result = response.body();
                        List<RegionMnyFacltStu> regionMnyFacltStus = result.getRegionMnyFacltStus();
                        if(regionMnyFacltStus != null) {
                            List<Head> heads = regionMnyFacltStus.get(0).getHead();
                            final Integer listTotalCount = heads.get(0).getListTotalCount();
                            int indexEnd = listTotalCount / 100 + 1;

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
                                                    if (searchWhat.equals("상호명")) {
                                                        String shopName = row.getShopName();
                                                        if (searchResult.equals("")) {
                                                            String category = row.getCategoryName();
                                                            String roadAddress = row.getRoadAddress();
                                                            String locationAddress = row.getLocationAddress();
                                                            String telNumber = row.getTelNumber();
                                                            String latitude = row.getLatitude();
                                                            String longitude = row.getLongitude();
                                                            DataModel data = new DataModel(shopName, category, roadAddress,
                                                                    locationAddress, telNumber, latitude, longitude);
                                                            dataModels.add(data);
                                                        } else if (shopName.contains(searchResult)) {
                                                            String category = row.getCategoryName();
                                                            String roadAddress = row.getRoadAddress();
                                                            String locationAddress = row.getLocationAddress();
                                                            String telNumber = row.getTelNumber();
                                                            String latitude = row.getLatitude();
                                                            String longitude = row.getLongitude();
                                                            DataModel data = new DataModel(shopName, category, roadAddress,
                                                                    locationAddress, telNumber, latitude, longitude);
                                                            dataModels.add(data);
                                                        }
                                                    } else if (searchWhat.equals("전화번호")) {
                                                        String telNumber = row.getTelNumber();
                                                        if (searchResult.equals("")) {
                                                            String shopName = row.getShopName();
                                                            String category = row.getCategoryName();
                                                            String roadAddress = row.getRoadAddress();
                                                            String locationAddress = row.getLocationAddress();
                                                            String latitude = row.getLatitude();
                                                            String longitude = row.getLongitude();
                                                            DataModel data = new DataModel(shopName, category, roadAddress,
                                                                    locationAddress, telNumber, latitude, longitude);
                                                            dataModels.add(data);
                                                        } else if (telNumber != null && telNumber.contains(searchResult)) {
                                                            String shopName = row.getShopName();
                                                            String category = row.getCategoryName();
                                                            String roadAddress = row.getRoadAddress();
                                                            String locationAddress = row.getLocationAddress();
                                                            String latitude = row.getLatitude();
                                                            String longitude = row.getLongitude();
                                                            DataModel data = new DataModel(shopName, category, roadAddress,
                                                                    locationAddress, telNumber, latitude, longitude);
                                                            dataModels.add(data);
                                                        }
                                                    } else if (searchWhat.equals("주소")) {
                                                        String roadAddress = row.getRoadAddress();
                                                        String locationAddress = row.getLocationAddress();
                                                        String address;
                                                        if (roadAddress != null) {
                                                            address = roadAddress;
                                                        } else address = locationAddress;
                                                        if (searchResult.equals("")) {
                                                            String shopName = row.getShopName();
                                                            String category = row.getCategoryName();
                                                            String telNumber = row.getTelNumber();
                                                            String latitude = row.getLatitude();
                                                            String longitude = row.getLongitude();
                                                            DataModel data = new DataModel(shopName, category, roadAddress,
                                                                    locationAddress, telNumber, latitude, longitude);
                                                            dataModels.add(data);
                                                        } else if (address != null && address.contains(searchResult)) {
                                                            String shopName = row.getShopName();
                                                            String category = row.getCategoryName();
                                                            String telNumber = row.getTelNumber();
                                                            String latitude = row.getLatitude();
                                                            String longitude = row.getLongitude();
                                                            DataModel data = new DataModel(shopName, category, roadAddress,
                                                                    locationAddress, telNumber, latitude, longitude);
                                                            dataModels.add(data);
                                                        }
                                                    }
                                                    tv_resultCount.setText(dataModels.size() + "개의 검색결과");
                                                    if (dataModels.size() != 0) {
                                                        DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), dataModels);
                                                        rv_searchResult.setAdapter(dataAdapter);
                                                    }
                                                    if (rowCount == listTotalCount) {
                                                        Toast.makeText(ResultActivity.this, "검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
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
                                        Log.e("Chance", "" + t.getMessage());
                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                            builder.setMessage(city+"는 더 이상 데이터를 제공하지 않습니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }


                @Override
                public void onFailure(@NotNull Call<GmoneyClass> call, @NotNull Throwable t) {
                    Log.e("Chance", "" + t.getMessage());
                }
            });
        }
    }
}
