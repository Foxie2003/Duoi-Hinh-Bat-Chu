package com.example.dhbc;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SanPham_avt_Adapter extends  RecyclerView.Adapter<SanPham_avt_Adapter.ViewHolder> {

    private Context context;
    ArrayList<SanPham> list;
    ItemClick_dapan mClick;

    public SanPham_avt_Adapter(Context context, ArrayList<SanPham> list,ItemClick_dapan mClick) {
        this.context = context;
        this.list = list;
        this.mClick=mClick;
    }

    @NonNull
    @Override
    public SanPham_avt_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.dongsanpham,parent,false);

        return new SanPham_avt_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SanPham_avt_Adapter.ViewHolder holder, int position) {
        CSDL csdl=new CSDL(context);
        ThongTinNguoiChoi tt=csdl.HienThongTinNhanVat();
        String fileName = list.get(position).getHinhAnh().toString(); // Lấy tên tệp ảnh từ đối tượng baiHat
        int resId = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName()); // Tìm ID tài nguyên dựa trên tên tệp ảnh
        if (resId != 0) {
            holder.avt.setImageResource(resId);
            holder.avt.setBackgroundColor(Color.WHITE); // Thiết lập hình ảnh cho ImageView
        } else {
            // Xử lý trường hợp không tìm thấy tệp ảnh
        }
        if(list.get(position).getTinhtrang()==0){
            holder.btnmua.setBackgroundResource(R.drawable.btnmua);
            holder.price.setText(String.valueOf(list.get(position).getPrice()));
        }
        else if(list.get(position).getId()!=tt.getAvt_id()){
            holder.btnmua.setBackgroundResource(R.drawable.btndung);
            holder.linmua.setVisibility(View.GONE);
        }
        else {
            holder.btnmua.setVisibility(View.GONE);
            holder.linmua.setVisibility(View.GONE);
        }
        holder.btnmua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avt;
        ImageButton btnmua;
        TextView price;
        LinearLayout linmua;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avt=itemView.findViewById(R.id.avt);
            btnmua=itemView.findViewById(R.id.btnmua);
            price=itemView.findViewById(R.id.ruby);
            linmua=itemView.findViewById(R.id.linmua);
        }
    }
}
