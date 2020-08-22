package com.chance.gmoneymap.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chance.gmoneymap.Models.NoticeModel;
import com.chance.gmoneymap.R;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.mViewHolder> {

    List<NoticeModel> list;

    public NoticeAdapter(List<NoticeModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewHolder holder, int position) {
        holder.item_tv_title.setText(list.get(position).getTitle());
        if (list.get(position).getContent() != null) {
            holder.item_tv_content.setText(list.get(position).getContent());
        } else {
            for (int i = 0; i < list.get(position).getNoticeArray().length; i++) {
                holder.item_tv_content.setText(holder.item_tv_content.getText() + list.get(position).getNoticeArray()[i] + "\n");
            }
        }
        holder.item_ll_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.item_ll_content.getVisibility() == View.VISIBLE) {
                    holder.item_iv_arrow.setImageResource(R.drawable.ic_expand);
                    holder.item_ll_content.setVisibility(View.GONE);
                } else {
                    holder.item_iv_arrow.setImageResource(R.drawable.ic_expand_less);
                    holder.item_ll_content.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_ll_title, item_ll_content;
        TextView item_tv_title, item_tv_content;
        ImageView item_iv_arrow;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            item_ll_title = itemView.findViewById(R.id.item_ll_title);
            item_ll_content = itemView.findViewById(R.id.item_ll_content);
            item_tv_title = itemView.findViewById(R.id.item_tv_title);
            item_tv_content = itemView.findViewById(R.id.item_tv_content);
            item_iv_arrow = itemView.findViewById(R.id.item_iv_arrow);
        }
    }
}
