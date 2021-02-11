package com.chance.gmoneymap.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.chance.gmoneymap.DownloadActivity;
import com.chance.gmoneymap.Models.DataModel;
import com.chance.gmoneymap.Models.Head;
import com.chance.gmoneymap.Models.RESULT;
import com.chance.gmoneymap.Models.RegionMnyFacltStu;
import com.chance.gmoneymap.Models.Row;
import com.chance.gmoneymap.R;
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

import static android.content.Context.MODE_PRIVATE;

public class DownloadFragment extends Fragment {

    private Spinner spinner;
    private Button btn_download;
    private ListView lv_download;

    private String city;
    private String[] cityArray = {"가평군", "고양시", "과천시", "광명시", "광주시", "구리시", "군포시",
            "김포시", "남양주시", "동두천시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시",
            "안양시", "양주시", "양평군", "여주시", "연천군", "오산시", "용인시", "의왕시", "의정부시",
            "이천시", "파주시", "평택시", "포천시", "하남시", "화성시"};

    private ArrayAdapter<CharSequence> arrayAdapter;

    private ProgressDialog progressDialog;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_download, container, false);

        spinner = view.findViewById(R.id.spinner);
        btn_download = view.findViewById(R.id.btn_download);
        lv_download = view.findViewById(R.id.lv_download);

        arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityName, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(cityItemSelectedListener);


        ArrayList<String> list = new ArrayList<>();
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cityData", MODE_PRIVATE);
        for (int i = 0; i < cityArray.length; i++) {
            String cityData = sharedPreferences.getString(cityArray[i], null);
            if (cityData != null) {
                list.add(cityArray[i]);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        lv_download.setAdapter(adapter);

        lv_download.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String city = (String) parent.getItemAtPosition(position);
                builder.setTitle("데이터 삭제")
                        .setMessage("정말로 " + city + "데이터를 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(city, null);
                                editor.apply();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(DownloadFragment.this).attach(DownloadFragment.this).commit();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city == null) {
                    Toast.makeText(getActivity(), "지역을 선택해 주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("데이터 다운로드")
                            .setMessage(city + " 데이터를 다운로드 할까요?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        requestSearchLocal();
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "불러오기 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void requestSearchLocal() {
        final List<DataModel> dataModels = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("불러오는 중...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String base_uri = "https://openapi.gg.go.kr/";
        Retrofit client = new Retrofit.Builder().baseUrl(base_uri).addConverterFactory(GsonConverterFactory.create()).build();
        final ApiInterface service = client.create(ApiInterface.class);

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
                        final int indexEnd = listTotalCount / 100 + 1;

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
                                                String shopName = row.getShopName();
                                                String category = row.getCategoryName();
                                                String roadAddress = row.getRoadAddress();
                                                String locationAddress = row.getLocationAddress();
                                                String telNumber = row.getTelNumber();
                                                String latitude = row.getLatitude();
                                                String longitude = row.getLongitude();
                                                DataModel data = new DataModel(shopName, category, roadAddress,
                                                        locationAddress, telNumber, latitude, longitude);
                                                dataModels.add(data);
                                                if (dataModels.size() != 0) {
                                                    progressDialog.setMessage((dataModels.size()*100/listTotalCount)+"%\n"+dataModels.size() + "개의 데이터 저장중...");
                                                }
                                                if (dataModels.size() == listTotalCount) {
                                                    Toast.makeText(getActivity(), "다운로드를 완료했습니다.", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();

                                                    Gson gson = new GsonBuilder().create();
                                                    Type listType = new TypeToken<ArrayList<DataModel>>() {
                                                    }.getType();
                                                    String strDataModels = gson.toJson(dataModels, listType);

                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cityData", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString(city, strDataModels);
                                                    editor.apply();

                                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                    ft.detach(DownloadFragment.this).attach(DownloadFragment.this).commit();
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
                                    Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(city+"는 더 이상 데이터를 제공하지 않습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        onBackPressed();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }


            @Override
            public void onFailure(@NotNull Call<GmoneyClass> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemSelectedListener cityItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                city = null;
            }
            for (int i = 0; i < cityArray.length; i++) {
                if (position - 1 == i) {
                    city = cityArray[i];
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            city = null;
        }
    };
}