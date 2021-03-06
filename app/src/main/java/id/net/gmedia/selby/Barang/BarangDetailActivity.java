package id.net.gmedia.selby.Barang;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.ImageSlider.ImageSlider;
import com.leonardus.irfan.ImageSlider.ImageSliderAdapter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Barang.Adapter.DetailBarangViewPagerAdapter;
import id.net.gmedia.selby.Barang.Fragment.FragmentDetailBarang;
import id.net.gmedia.selby.Barang.Fragment.FragmentDiskusiBarang;
import id.net.gmedia.selby.Barang.Fragment.FragmentUlasan;
import id.net.gmedia.selby.Chat.ChatDetailActivity;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.LoginActivity;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.TopCropCircularImageView;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;

public class BarangDetailActivity extends AppCompatActivity {
    /*
        Activity yang menampilkan informasi detail dari barang
     */

    //Variabel penampung menu action bar
    private Menu menu;

    //Variabel atribut barang
    private String nama_barang = "";
    private String id = "";
    private double harga_barang = 0;
    private String main_image = "";
    private String deskripsi = "";
    private String kategori = "";
    private String berat = "";
    private String merk = "";
    private float rating = 0;
    private boolean favorit = false;
    private boolean follow = false;

    //Variabel atribut penjual
    private String penjual_uid;
    private ArtisModel penjual;

    //Variabel UI
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageSlider slider;
    private FloatingMenuButton fab_tambah;
    private Toolbar toolbar;
    private Button btn_follow;
    private CollapsingToolbarLayout collapsingToolbar;
    //private ImageView btn_chat;
    private TextView txt_title, txt_nama, txt_harga,
            txt_kondisi, txt_dilihat, txt_terkirim;
    private LinearLayout layout_pelapak;

    private DetailBarangViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang);

        //Inisialisasi UI
        txt_title = findViewById(R.id.txt_title);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_kondisi = findViewById(R.id.txt_kondisi);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        slider = findViewById(R.id.slider);
        fab_tambah = findViewById(R.id.fab_tambah);
        txt_dilihat = findViewById(R.id.txt_dilihat);
        txt_terkirim = findViewById(R.id.txt_terkirim);
        btn_follow = findViewById(R.id.btn_follow);
        layout_pelapak = findViewById(R.id.layout_pelapak);

        //Inisialisasi Toolbar
        initToolbar();

        //Inisialisasi barang
        if (getIntent().hasExtra(Constant.EXTRA_BARANG)) {
            initBarang();
        }

        //Follow/unfollow penjual
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(penjual != null){
                    followPenjual();
                }
            }
        });

        //Menambah barang ke keranjang
        findViewById(R.id.btn_keranjang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSharedPreferences.isLoggedIn(BarangDetailActivity.this)){
                    tambahKeranjang();
                }
                else{
                    startActivity(new Intent(BarangDetailActivity.this, LoginActivity.class));
                }
            }
        });

        //membeli barang
        findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //beli barang -> transaksi
            /*Intent i = new Intent(BarangDetailActivity.this, PembayaranActivity.class);
            i.putExtra("barang", gson.toJson(barang));
            startActivity(i);*/
                /*final Dialog dialog = DialogFactory.getInstance().createDialog
                        (BarangDetailActivity.this, R.layout.popup_keranjang_tambah,
                                70, 50);

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
                        if(!id.equals("")){
                            //bar_loading.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            Intent i = new Intent(BarangDetailActivity.this, PembayaranActivity.class);

                            LinkedHashMap<String, List<BarangJualModel>> listBarangBeli = new LinkedHashMap<>();
                            List<ArtisModel> listPenjual = new ArrayList<>();
                            ArrayList<BarangJualModel> listBarang = new ArrayList<>();

                            listBarang.add(new BarangJualModel(id, nama_barang, main_image, harga_barang,
                                    Integer.parseInt(txt_jumlah.getText().toString())));

                            listBarangBeli.put(penjual.getId(), listBarang);
                            listPenjual.add(penjual);

                            i.putExtra(Constant.EXTRA_LIST_BARANG, gson.toJson(listBarangBeli));
                            i.putExtra(Constant.EXTRA_LIST_PENJUAL, gson.toJson(listPenjual));
                            startActivity(i);
                            //bar_loading.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                dialog.show();*/
                AppLoading.getInstance().showLoading(BarangDetailActivity.this);
                JSONBuilder body = new JSONBuilder();
                body.add("id_barang", id);
                body.add("jumlah", 1);
                body.add("jenis", "barang");

                ApiVolleyManager.getInstance().addRequest(BarangDetailActivity.this,
                        Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST,
                        Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                        new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                            @Override
                            public void onSuccess(String response) {
                                Intent i = new Intent(BarangDetailActivity.this, HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra(Constant.EXTRA_START, 3);
                                startActivity(i);

                                AppLoading.getInstance().stopLoading();
                            }

                            @Override
                            public void onFail(String message) {
                                Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                AppLoading.getInstance().stopLoading();
                            }
                        }));
                    }
                });

        if(AppSharedPreferences.isLoggedIn(this)){
            initFloatingActionButton();
        }

        findViewById(R.id.btn_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(BarangDetailActivity.this, ChatDetailActivity.class);
                i.putExtra(Constant.EXTRA_USER, gson.toJson(penjual));
                i.putExtra(Constant.EXTRA_CHAT_FROM, penjual_uid);
                startActivity(i);
            }
        });
    }

    private void initFloatingActionButton(){
        //Floating Action Button untuk menambah ulasan atau diskusi barang tergantung fragment yang aktif
        fab_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabLayout.getSelectedTabPosition() == 1){
                    //tambah ulasan
                    ((FragmentUlasan)adapter.getItem(1)).bukaDialogReview();
                }
                else if(tabLayout.getSelectedTabPosition() == 2){
                    //tambah diskusi barang
                    ((FragmentDiskusiBarang)adapter.getItem(2)).tambahDiskusi();
                }
            }
        });

        //Mengubah tampilan Floating Action Button tergantung fragment yang aktif
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:fab_tambah.setVisibility(View.INVISIBLE);break;
                    case 1:
                        fab_tambah.setBackgroundResource(R.drawable.ulas);
                        fab_tambah.setVisibility(View.VISIBLE);break;
                    case 2:
                        fab_tambah.setBackgroundResource(R.drawable.diskusi);
                        fab_tambah.setVisibility(View.VISIBLE);break;
                }
                super.onPageSelected(position);
            }
        });
    }

    private void followPenjual(){
        //Mengubah status follow/unfollow terhadap penjual
        if(!penjual.getId().equals("")){
            JSONBuilder body = new JSONBuilder();
            body.add("id_penjual", penjual.getId());

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_FOLLOW_PENJUAL,
                    ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                    body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                @Override
                public void onSuccess(String response) {
                    if(follow){
                        Toast.makeText(BarangDetailActivity.this, "Berhenti Follow berhasil",
                                Toast.LENGTH_SHORT).show();
                        btn_follow.setText(R.string.penjual_follow);
                    }
                    else{
                        Toast.makeText(BarangDetailActivity.this, "Follow berhasil",
                                Toast.LENGTH_SHORT).show();
                        btn_follow.setText(R.string.penjual_unfollow);
                    }

                    follow = !follow;
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }

    private void initToolbar(){
        //Inisialisasi Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        collapsingToolbar.setTitle(" ");
        txt_title.setText("");
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.setExpanded(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(getSupportActionBar() != null){
                        if (appBarLayout.getTotalScrollRange() + verticalOffset <= getActionBarHeight()) {
                            if(nama_barang != null){
                                txt_title.setText(nama_barang);
                            }
                            isShow = true;
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        } else if (isShow) {
                            txt_title.setText("");
                            isShow = false;
                            getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.style_rectangle_gradient_black));
                        }
                    }
                }
            });
        }
    }

    private void initBarang(){
        //Membaca detail barang dari Web Service
        JSONBuilder body = new JSONBuilder();
        id = getIntent().getStringExtra(Constant.EXTRA_BARANG);
        body.add("id", id);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DETAIL_PRODUK,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onEmpty(String message) {
                Toast.makeText(BarangDetailActivity.this,"Barang tidak ditemukan",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String result) {
                try{
                    JSONObject barang = new JSONObject(result);

                    nama_barang = barang.getString("nama");
                    txt_nama.setText(nama_barang);
                    harga_barang = barang.getDouble("harga");
                    txt_harga.setText(Converter.doubleToRupiah(harga_barang));
                    txt_kondisi.setText(barang.getString("kondisi"));
                    txt_terkirim.setText(barang.getString("terjual"));
                    txt_dilihat.setText(barang.getString("dilihat"));

                    deskripsi = barang.getString("deskripsi");
                    kategori = barang.getString("category");
                    berat = barang.getInt("berat") + " " + barang.getString("berat_satuan");
                    merk = barang.getString("brand");
                    rating = (float) barang.getDouble("rating");

                    favorit = barang.getString("is_favorit").equals("1");
                    if(favorit){
                        menu.getItem(1).setIcon(R.drawable.ic_favorit_isi);
                    }
                    else{
                        menu.getItem(1).setIcon(R.drawable.ic_favorit_kosong);
                    }

                    penjual = new ArtisModel(barang.getJSONObject("penjual").getString("id"),
                            barang.getJSONObject("penjual").getString("nama"),
                            barang.getJSONObject("penjual").getString("image"));
                    penjual_uid = barang.getJSONObject("penjual").getString("uid");
                    if(!penjual_uid.equals(FirebaseAuth.getInstance().getUid())){
                        layout_pelapak.setVisibility(View.VISIBLE);
                    }
                    Glide.with(BarangDetailActivity.this).load(penjual.getImage()).
                            apply(new RequestOptions().priority(Priority.LOW)).
                            into((TopCropCircularImageView) findViewById(R.id.img_artis));
                    follow = barang.getJSONObject("penjual").getInt("followed") == 1;
                    if(follow){
                        btn_follow.setText(R.string.penjual_unfollow);
                    }

                    List<String> listImage = new ArrayList<>();
                    main_image = barang.getString("image");
                    listImage.add(main_image);
                    JSONArray galeri = barang.getJSONArray("gallery");
                    for(int i = 0; i < galeri.length(); i++){
                        listImage.add(galeri.getJSONObject(i).getString("image"));
                    }

                    initSlider(listImage);
                }
                catch (JSONException e){
                    Toast.makeText(BarangDetailActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void tambahKeranjang(){
        //Memunculkan dialog dan menambahkan barang ke keranjang
        final Dialog dialog = DialogFactory.getInstance().createDialog
                (BarangDetailActivity.this,
                        R.layout.popup_keranjang_tambah,
                        70, 50);

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
                if(!id.equals("")){
                    JSONBuilder body = new JSONBuilder();
                    body.add("id_barang", id);
                    body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
                    body.add("jenis", "barang");

                    ApiVolleyManager.getInstance().addRequest(BarangDetailActivity.this,
                            Constant.URL_TAMBAH_KERANJANG, ApiVolleyManager.METHOD_POST,
                            Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                            new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(BarangDetailActivity.this,
                                    "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFail(String message) {
                            Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            }
        });
        dialog.show();
    }

    private void setupViewPager(ViewPager viewPager) {
        //Inisialisasi View Pager dan Fragment Detail barang, Ulasan, dan Diskusi Barang
        adapter = new DetailBarangViewPagerAdapter(this, getSupportFragmentManager());

        Bundle bundle;
        bundle = new Bundle();
        FragmentDetailBarang detailBarang = new FragmentDetailBarang();
        bundle.putString("deskripsi", deskripsi);
        bundle.putString("kategori", kategori);
        bundle.putString("berat", berat);
        bundle.putString("merk", merk);
        detailBarang.setArguments(bundle);
        adapter.addFrag(detailBarang);

        bundle = new Bundle();
        FragmentUlasan fragmentUlasan = new FragmentUlasan();
        bundle.putString("id", id);
        bundle.putFloat("rating", rating);
        fragmentUlasan.setArguments(bundle);
        adapter.addFrag(fragmentUlasan);

        bundle = new Bundle();
        FragmentDiskusiBarang fragmentDiskusiBarang = new FragmentDiskusiBarang();
        bundle.putString("id", id);
        fragmentDiskusiBarang.setArguments(bundle);
        adapter.addFrag(fragmentDiskusiBarang);

        viewPager.setAdapter(adapter);
    }

    private void initSlider(List<String> listImage){
        //Inisialisasi Slider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, listImage, true);
        slider.setAdapter(sliderAdapter);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if(getSupportActionBar() != null){
            actionBarHeight = getSupportActionBar().getHeight();
            if (actionBarHeight != 0)
                return actionBarHeight;
            final TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    //FUNGSI MENU ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_barang, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //setCount(String.valueOf(1));
        return super.onPrepareOptionsMenu(menu);
    }

    /*public void setCount(String count) {
        MenuItem menuItem = menu.findItem(R.id.action_keranjang);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(this, R.dimen.text12, R.color.orange);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }*/

    private void ubahFavorit(final MenuItem item){
        if(!favorit){
            JSONBuilder body = new JSONBuilder();
            body.add("id_barang", id);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TAMBAH_FAVORIT,
                    ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                    body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {

                @Override
                public void onSuccess(String response) {
                    final Dialog dialog = DialogFactory.getInstance().createDialog
                            (BarangDetailActivity.this, R.layout.popup_message,
                                    65, 30);
                    TextView txt_pesan = dialog.findViewById(R.id.txt_pesan);
                    dialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    favorit = true;
                    item.setIcon(R.drawable.ic_favorit_isi);
                    txt_pesan.setText(R.string.barang_tambah_favorit);
                    dialog.show();
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }));
        }
        else {
            JSONBuilder body = new JSONBuilder();
            List<String> listId = new ArrayList<>();
            listId.add(id);
            body.add("id_barang", new JSONArray(listId));

            ApiVolleyManager.getInstance().addRequest(BarangDetailActivity.this, Constant.URL_HAPUS_FAVORIT,
                    ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                    body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(BarangDetailActivity.this, "Barang berhasil dihapus", Toast.LENGTH_SHORT).show();
                    favorit = false;
                    menu.getItem(1).setIcon(R.drawable.ic_favorit_kosong);
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(BarangDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_keranjang:
                Intent i = new Intent(this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra(Constant.EXTRA_START, 3);
                startActivity(i);
                return true;
            case R.id.action_favorit:
                if(AppSharedPreferences.isLoggedIn(this)){
                    ubahFavorit(item);
                }
                else {
                   startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

