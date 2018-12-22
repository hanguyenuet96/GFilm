package vnu.uet.hanguyen.gfilm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,Constant, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recycler_view1,recycler_view2,recycler_view3,recycler_view4,recycler_view5,recycler_view6,recycler_view7,recycler_view8,recycler_view9,recycler_view10,recycler_view11,recycler_view12;
    private TextView[] txtTenTheLoai;
    private SwipeRefreshLayout srlRefreshLayout;

    private ArrayList<Phim>[] arrListPhim;

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private SearchView searchView;
    private Dialog dialog_internet_connection =null;
    private ProgressDialog progressDialog;

    private PhimAdapter phimAdapter0,phimAdapter1,phimAdapter2,phimAdapter3,phimAdapter4,phimAdapter5,phimAdapter6,phimAdapter7,phimAdapter8,phimAdapter9,phimAdapter10,phimAdapter11;

    private BroadcastReceiver wifi_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo() !=null){
                if(dialog_internet_connection != null){
                    dialog_internet_connection.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            }else{
                if(dialog_internet_connection == null){
                    progressDialog.dismiss();
                    dialog_notify_internetconnection();
                }else{
                    progressDialog.dismiss();
                    dialog_internet_connection.show();
                }
            }
        }
    };


    public void dialog_notify_internetconnection(){
        dialog_internet_connection = new Dialog(this);
        dialog_internet_connection.setContentView(R.layout.dialog_checking_internet);
        dialog_internet_connection.setCanceledOnTouchOutside(false);

        Button btn_thoat    = dialog_internet_connection.findViewById(R.id.btn_thoat);


        btn_thoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog_internet_connection.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifi_receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(wifi_receiver != null){
            unregisterReceiver(wifi_receiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải, xin vui lòng đợi ...");
        progressDialog.setCancelable(true); // Check as required
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerSlideAnimationEnabled(true);
        toggle.syncState();

        arrListPhim = new ArrayList[12];
        txtTenTheLoai = new TextView[12];

        for(int i=0;i<12;i++){
            arrListPhim[i] = new ArrayList<>();
        }

        initData();
        initView();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            Toast.makeText(MainActivity.this,"opened",Toast.LENGTH_SHORT).show();
        }
       return true;
    }

    private void initData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new docJSON().execute(IP_SERVER+"/OnFilm/services/getData.php");
            }
        });
    }

    private void initView() {
        initRecyclerView();
        srlRefreshLayout = findViewById(R.id.srl_refresh_layout);
        srlRefreshLayout.setColorSchemeColors(Color.rgb(12,12,44));
        srlRefreshLayout.setOnRefreshListener(this);
        initTextView();

        {
            recycler_view1.setHasFixedSize(true);
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view1.setLayoutManager(layoutManager1);


            recycler_view2.setHasFixedSize(true);
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view2.setLayoutManager(layoutManager2);


            recycler_view3.setHasFixedSize(true);
            LinearLayoutManager layoutManager3 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view3.setLayoutManager(layoutManager3);


            recycler_view4.setHasFixedSize(true);
            LinearLayoutManager layoutManager4 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view4.setLayoutManager(layoutManager4);

            recycler_view5.setHasFixedSize(true);
            LinearLayoutManager layoutManager5 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view5.setLayoutManager(layoutManager5);

            recycler_view6.setHasFixedSize(true);
            LinearLayoutManager layoutManager6 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view6.setLayoutManager(layoutManager6);

            recycler_view7.setHasFixedSize(true);
            LinearLayoutManager layoutManager7 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view7.setLayoutManager(layoutManager7);

            recycler_view8.setHasFixedSize(true);
            LinearLayoutManager layoutManager8 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view8.setLayoutManager(layoutManager8);

            recycler_view9.setHasFixedSize(true);
            LinearLayoutManager layoutManager9 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view9.setLayoutManager(layoutManager9);

            recycler_view10.setHasFixedSize(true);
            LinearLayoutManager layoutManager10 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view10.setLayoutManager(layoutManager10);

            recycler_view11.setHasFixedSize(true);
            LinearLayoutManager layoutManager11 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view11.setLayoutManager(layoutManager11);

            recycler_view12.setHasFixedSize(true);
            LinearLayoutManager layoutManager12 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recycler_view12.setLayoutManager(layoutManager12);

        }

    }

    private void initTextView() {
        txtTenTheLoai[0] = findViewById(R.id.txtTenTheLoai1);
        txtTenTheLoai[1] = findViewById(R.id.txtTenTheLoai2);
        txtTenTheLoai[2] = findViewById(R.id.txtTenTheLoai3);
        txtTenTheLoai[3] = findViewById(R.id.txtTenTheLoai4);
        txtTenTheLoai[4] = findViewById(R.id.txtTenTheLoai5);
        txtTenTheLoai[5] = findViewById(R.id.txtTenTheLoai6);
        txtTenTheLoai[6] = findViewById(R.id.txtTenTheLoai7);
        txtTenTheLoai[7] = findViewById(R.id.txtTenTheLoai8);
        txtTenTheLoai[8] = findViewById(R.id.txtTenTheLoai9);
        txtTenTheLoai[9] = findViewById(R.id.txtTenTheLoai10);
        txtTenTheLoai[10] = findViewById(R.id.txtTenTheLoai11);
        txtTenTheLoai[11] = findViewById(R.id.txtTenTheLoai12);
    }

    private void initRecyclerView() {
        recycler_view1 = findViewById(R.id.recycler_view1);
        recycler_view2 = findViewById(R.id.recycler_view2);
        recycler_view3 = findViewById(R.id.recycler_view3);
        recycler_view4 = findViewById(R.id.recycler_view4);
        recycler_view5 = findViewById(R.id.recycler_view5);
        recycler_view6 = findViewById(R.id.recycler_view6);
        recycler_view7 = findViewById(R.id.recycler_view7);
        recycler_view8 = findViewById(R.id.recycler_view8);
        recycler_view9 = findViewById(R.id.recycler_view9);
        recycler_view10 = findViewById(R.id.recycler_view10);
        recycler_view11 = findViewById(R.id.recycler_view11);
        recycler_view12 = findViewById(R.id.recycler_view12);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(getApplicationContext(),SearchFilmActivity.class);
        intent.putExtra("query",query);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new docJSON().execute(IP_SERVER+"/OnFilm/services/getData.php");
            }
        });
//        phimAdapter0.notifyDataSetChanged();
//        phimAdapter1.notifyDataSetChanged();
//        phimAdapter2.notifyDataSetChanged();
//        phimAdapter3.notifyDataSetChanged();
//        phimAdapter4.notifyDataSetChanged();
//        phimAdapter5.notifyDataSetChanged();
//        phimAdapter6.notifyDataSetChanged();
//        phimAdapter7.notifyDataSetChanged();
//        phimAdapter8.notifyDataSetChanged();
//        phimAdapter9.notifyDataSetChanged();
//        phimAdapter10.notifyDataSetChanged();
//        phimAdapter11.notifyDataSetChanged();
    }


    class docJSON extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_Tu_URL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(srlRefreshLayout.isRefreshing()){
                srlRefreshLayout.setRefreshing(false);
            }
            try {
                JSONArray mang = new JSONArray(s);
                if(mang.length()==0)return;
                for(int i=0;i<mang.length();i++){
                    arrListPhim[i] = new ArrayList<>();
                    JSONObject theloai = (JSONObject) mang.get(i);
                    JSONArray list_phim_theo_the_loai = theloai.getJSONArray("data");
                    txtTenTheLoai[i].setText(theloai.getString("category_name"));
                    for(int j=0;j<list_phim_theo_the_loai.length();j++){
                        JSONObject obj_phim = (JSONObject) list_phim_theo_the_loai.get(j);
                        arrListPhim[i].add(new Phim(obj_phim.getInt("id"),obj_phim.getString("theloai"),obj_phim.getString("link"),obj_phim.getString("tenphim"),obj_phim.getString("iframe")));
                    }
                }


                phimAdapter0 = new PhimAdapter(arrListPhim[0],MainActivity.this);
                recycler_view1.setAdapter(phimAdapter0);

                phimAdapter1 = new PhimAdapter(arrListPhim[1],MainActivity.this);
                recycler_view2.setAdapter(phimAdapter1);

                phimAdapter2 = new PhimAdapter(arrListPhim[2],MainActivity.this);
                recycler_view3.setAdapter(phimAdapter2);

                phimAdapter3 = new PhimAdapter(arrListPhim[3],MainActivity.this);
                recycler_view4.setAdapter(phimAdapter3);

                phimAdapter4 = new PhimAdapter(arrListPhim[4],MainActivity.this);
                recycler_view5.setAdapter(phimAdapter4);

                phimAdapter5 = new PhimAdapter(arrListPhim[5],MainActivity.this);
                recycler_view6.setAdapter(phimAdapter5);

                phimAdapter6 = new PhimAdapter(arrListPhim[6],MainActivity.this);
                recycler_view7.setAdapter(phimAdapter6);

                phimAdapter7 = new PhimAdapter(arrListPhim[7],MainActivity.this);
                recycler_view8.setAdapter(phimAdapter7);

                phimAdapter8 = new PhimAdapter(arrListPhim[8],MainActivity.this);
                recycler_view9.setAdapter(phimAdapter8);

                phimAdapter9 = new PhimAdapter(arrListPhim[9],MainActivity.this);
                recycler_view10.setAdapter(phimAdapter9);

                phimAdapter10 = new PhimAdapter(arrListPhim[10],MainActivity.this);
                recycler_view11.setAdapter(phimAdapter10);

                phimAdapter11 = new PhimAdapter(arrListPhim[11],MainActivity.this);
                recycler_view12.setAdapter(phimAdapter11);

            } catch (JSONException e) {
                Log.e("error","error code");
                e.printStackTrace();
            }
        }
    }

    private String docNoiDung_Tu_URL(String theUrl){
        StringBuilder content = new StringBuilder();
        try    {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
        return content.toString();
    }




}
