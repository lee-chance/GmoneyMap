package com.chance.gmoneymap.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chance.gmoneymap.MapActivity;
import com.chance.gmoneymap.Models.DataModel;
import com.chance.gmoneymap.Models.Row;
import com.chance.gmoneymap.R;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.mViewHolder> {

    Context context;
    List<DataModel> dataModels;

    public DataAdapter(Context context, List<DataModel> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_data, parent, false);
        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, final int position) {
        holder.shopName.setText(dataModels.get(position).getCMPNM_NM());
        holder.category.setText(dataModels.get(position).getINDUTYPE_NM());
        holder.telNumber.setText(dataModels.get(position).getTELNO());
        if (dataModels.get(position).getREFINE_ROADNM_ADDR() != null) {
            holder.address.setText(dataModels.get(position).getREFINE_ROADNM_ADDR());
        } else if (dataModels.get(position).getREFINE_LOTNO_ADDR() != null) {
            holder.address.setText(dataModels.get(position).getREFINE_LOTNO_ADDR());
        }

        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataModels.get(position).getTELNO() == null) {
                    Toast.makeText(context, "전화번호를 제공하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String tel = "tel:" + dataModels.get(position).getTELNO();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        holder.iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataModels.get(position).getREFINE_WGS84_LAT() == null
                        && dataModels.get(position).getREFINE_WGS84_LOGT() == null) {
                    Toast.makeText(context, "지도검색을 제공하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent locationIntent = new Intent(context, MapActivity.class);

                    Row newRow = new Row(dataModels.get(position).getCMPNM_NM(),
                            dataModels.get(position).getINDUTYPE_NM(),
                            dataModels.get(position).getTELNO(),
                            dataModels.get(position).getREFINE_ROADNM_ADDR(),
                            dataModels.get(position).getREFINE_LOTNO_ADDR(),
                            dataModels.get(position).getREFINE_WGS84_LOGT(),
                            dataModels.get(position).getREFINE_WGS84_LAT());
                    locationIntent.putExtra("newRow", newRow);

                    locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //locationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(locationIntent);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        private TextView shopName, category, address, telNumber;
        private ImageView iv_call, iv_location;
        private CardView cardView;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);

            shopName = itemView.findViewById(R.id.shopName);
            category = itemView.findViewById(R.id.category);
            address = itemView.findViewById(R.id.address);
            telNumber = itemView.findViewById(R.id.telNumber);
            iv_call = itemView.findViewById(R.id.iv_call);
            iv_location = itemView.findViewById(R.id.iv_location);

            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
