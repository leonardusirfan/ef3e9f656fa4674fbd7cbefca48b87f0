package id.net.gmedia.selby.Artis.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.net.gmedia.selby.Barang.BarangDetailActivity;
import id.net.gmedia.selby.Barang.MerchandiseDetailActivity;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;
import id.net.gmedia.selby.Util.DialogFactory;

public class BarangArtisAdapter extends RecyclerView.Adapter<BarangArtisAdapter.BarangViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;

    public BarangArtisAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BarangViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_artis_barang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BarangViewHolder barangViewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        barangViewHolder.txt_nama.setText(barang.getNama());
        barangViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));

        Glide.with(activity).load(barang.getUrl()).transition(DrawableTransitionOptions.withCrossFade()).into(barangViewHolder.img_barang);
        //Indikator favorit
        /*if(barang.isFavorit()){
            barangViewHolder.img_favorit.setImageResource(R.drawable.lovepink);
        }
        else{
            barangViewHolder.img_favorit.setImageResource(R.drawable.loveblack);
        }*/

        //pelapak
        barangViewHolder.txt_nama_pelapak.setText(barang.getPenjual().getNama());
        Glide.with(activity).load(barang.getPenjual().getImage()).apply(new RequestOptions().circleCrop()).into(barangViewHolder.img_pelapak);
        barangViewHolder.rate_pelapak.setRating(barang.getPenjual().getRating());

        barangViewHolder.btn_keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_keranjang_tambah, 70, 50);

                Button btn_tambah = dialog.findViewById(R.id.btn_tambah);
                TextView txt_kurang, txt_tambah;
                txt_kurang = dialog.findViewById(R.id.txt_kurang);
                txt_tambah = dialog.findViewById(R.id.txt_tambah);
                final TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
                txt_jumlah.setText("1");
                //final ProgressBar bar_loading = dialog.findViewById(R.id.bar_loading);

                txt_kurang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                        if(jumlah > 1){
                            jumlah--;
                        }
                        txt_jumlah.setText(String.valueOf(jumlah));
                    }
                });

                txt_tambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                        jumlah++;
                        txt_jumlah.setText(String.valueOf(jumlah));
                    }
                });

                btn_tambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //bar_loading.setVisibility(View.VISIBLE);

                        try{
                            JSONObject body = new JSONObject();
                            body.put("id_barang", barang.getId());
                            body.put("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));

                            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject jsonresult = new JSONObject(result);
                                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                                        String message = jsonresult.getJSONObject("metadata").getString("message");

                                        if(status == 200){
                                            Toast.makeText(activity, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                        else if(status == 401){
                                            activity.startActivity(new Intent(activity, LoginActivity.class));
                                        }
                                        else{
                                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (JSONException e){
                                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                                        Log.e("Tambah Keranjang", e.toString());
                                    }
                                }

                                @Override
                                public void onError(String result) {
                                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                                    Log.e("Tambah Keranjang",result);
                                }
                            });
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Tambah Keranjang", e.toString());
                        }

                        //bar_loading.setVisibility(View.INVISIBLE);
                    }
                });
                dialog.show();
            }
        });

        //Tambah favorit
        /*barangViewHolder.img_favorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!barang.isFavorit()){
                    try{
                        JSONObject body = new JSONObject();
                        body.put("id_barang", barang.getId());

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_FAVORIT, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        //Toast.makeText(activity, "Barang berhasil ditambah", Toast.LENGTH_SHORT).show();
                                        final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_message, 65, 30);
                                        TextView txt_pesan = dialog.findViewById(R.id.txt_pesan);
                                        dialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        barang.setFavorit(true);
                                        barangViewHolder.img_favorit.setImageResource(R.drawable.lovepink);
                                        txt_pesan.setText(R.string.tambah_favorit);
                                        dialog.show();
                                    }
                                    else if(status == 401){
                                        activity.startActivity(new Intent(activity, LoginActivity.class));
                                    }
                                    else{
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    Log.e("Tambah Favorit", e.toString());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                                Log.e("Tambah Favorit",result);
                            }
                        });
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Tambah Favorit", e.toString());
                    }
                }
                else{
                    try{
                        JSONObject body = new JSONObject();
                        List<String> listId = new ArrayList<>();
                        listId.add(barang.getId());
                        body.put("id_barang", new JSONArray(listId));

                        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_HAPUS_FAVORIT, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonresult = new JSONObject(result);
                                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                                    String message = jsonresult.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        Toast.makeText(activity, "Barang berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        barang.setFavorit(false);
                                        barangViewHolder.img_favorit.setImageResource(R.drawable.loveblack);
                                    }
                                    else{
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    Log.e("Hapus Favorit", e.toString());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                                Log.e("Hapus Favorit",result);
                            }
                        });
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Hapus Favorit", e.toString());
                    }
                }
            }
        });*/

        barangViewHolder.btn_beli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject body = new JSONObject();
                    body.put("id_barang", barang.getId());
                    body.put("jumlah", 1);

                    ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonresult = new JSONObject(result);
                                int status = jsonresult.getJSONObject("metadata").getInt("status");
                                String message = jsonresult.getJSONObject("metadata").getString("message");

                                if(status == 200){
                                    Intent i = new Intent(activity, HomeActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.putExtra("start", 3);
                                    activity.startActivity(i);
                                }
                                else if(status == 401){
                                    activity.startActivity(new Intent(activity, LoginActivity.class));
                                }
                                else{
                                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e){
                                Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                                Log.e("Tambah Keranjang", e.toString());
                            }
                        }

                        @Override
                        public void onError(String result) {
                            Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                            Log.e("Tambah Keranjang",result);
                        }
                    });
                }
                catch (JSONException e){
                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Tambah Keranjang", e.toString());
                }
            }
        });

        barangViewHolder.btn_lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(barang.getJenis().equals("Preloved")){
                    Intent i = new Intent(activity, BarangDetailActivity.class);
                    i.putExtra("barang", barang.getId());
                    activity.startActivity(i);
                }
                else if(barang.getJenis().equals("Merchandise")){
                    Intent i = new Intent(activity, MerchandiseDetailActivity.class);
                    i.putExtra("merchandise", barang.getId());
                    activity.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class BarangViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_harga;
        private ImageView img_barang, btn_keranjang;
        private LinearLayout btn_lihat;

        //Pelapak
        TextView txt_nama_pelapak;
        ImageView img_pelapak;
        RatingBar rate_pelapak;

        Button btn_beli;

        BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            img_barang = itemView.findViewById(R.id.img_barang);

            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            btn_lihat = itemView.findViewById(R.id.layout_barang);
            btn_keranjang = itemView.findViewById(R.id.img_keranjang);

            txt_nama_pelapak = itemView.findViewById(R.id.txt_nama_pelapak);
            img_pelapak = itemView.findViewById(R.id.img_pelapak);
            rate_pelapak = itemView.findViewById(R.id.rate_pelapak);

            btn_beli = itemView.findViewById(R.id.btn_beli);
        }
    }
}