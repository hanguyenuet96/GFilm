package vnu.uet.hanguyen.gfilm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DetailMoviesActivity extends AppCompatActivity implements Constant{

    private ImageView imgPhim,imgPhim2;
    private TextView txtTenPhim;
    private Button btnXem,btnTaixuong,btnBaoCao;
    private Dialog dialog_loading;
    private ProgressDialog progressDialog;

    private TextView txtNamThoiluongphim,txtQuocgia,txtDophangiai,txtDaodien,txtDienvien,txtNoidung;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movies);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải, xin vui lòng đợi ...");
        progressDialog.setCancelable(true); // Check as required
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Intent intent = getIntent();
        final int id_phim = intent.getIntExtra("id_phim",0);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new JSON().execute(IP_SERVER+"/OnFilm/services/getItemPhim.php?id_film_request="+id_phim);
            }
        });

    }

    private void initView(final PhimDetails p) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgPhim = findViewById(R.id.imgPhim);
        imgPhim2 = findViewById(R.id.imgPhim2);
        txtTenPhim = findViewById(R.id.txtTenPhim);
        btnXem = findViewById(R.id.btnXem);
        btnTaixuong = findViewById(R.id.btnTaixuong);
        btnBaoCao = findViewById(R.id.btnBaoCao);

        txtNamThoiluongphim = findViewById(R.id.txtNamThoiluongphim);
        txtQuocgia = findViewById(R.id.txtQuocgia);
        txtDophangiai = findViewById(R.id.txtDophangiai);
        txtDaodien = findViewById(R.id.txtDaodien);
        txtDienvien = findViewById(R.id.txtDienvien);
        txtNoidung = findViewById(R.id.txtNoidung);


        Picasso.with(this).load(p.get$iframe()).into(imgPhim);
        Picasso.with(this).load(p.get$iframe()).into(imgPhim2);
        txtTenPhim.setText(p.get$tenphim());
        txtNamThoiluongphim.setText("Năm : "+p.get$namsx()+", Thời gian : "+p.get$thoiluongphim());
        txtQuocgia.setText("Quốc gia : "+p.get$quocgia());
        txtDophangiai.setText("Độ phân giải : "+p.get$dophangiai());
        txtDaodien.setText(p.get$daodien());
        txtDienvien.setText(p.get$dienvien());
        txtNoidung.setText(p.get$noidung());

        btnXem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailMoviesActivity.this,WatchMoviesActivity.class);
                intent.putExtra("link_phim",p.get$link());
                String link_comments = "http://35.187.247.104/OnFilm/comments/"+p.get$id();
                intent.putExtra("link_comments",link_comments);
                startActivity(intent);
            }
        });
    }

    class JSON extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_Tu_URL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                JSONArray mang = new JSONArray(s);
                JSONObject phims = mang.getJSONObject(0);
                PhimDetails p = new PhimDetails(phims.getInt("id"),phims.getString("theloai"),phims.getString("link"),phims.getString("tenphim"),phims.getString("iframe"),phims.getInt("namsx"),phims.getString("thoiluongphim"),phims.getString("quocgia"),phims.getString("dophangiai"),phims.getString("daodien"),phims.getString("dienvien"),phims.getString("noidung"));
                initView(p);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class PhimDetails{
        int $id;
        String $theloai;
        String $link;
        String $tenphim;
        String $iframe;
        int $namsx;
        String $thoiluongphim;
        String $quocgia;
        String $dophangiai;
        String $daodien;
        String $dienvien;
        String $noidung;

        public PhimDetails(int $id, String $theloai, String $link, String $tenphim, String $iframe, int $namsx, String $thoiluongphim, String $quocgia, String $dophangiai, String $daodien, String $dienvien, String $noidung) {
            this.$id = $id;
            this.$theloai = $theloai;
            this.$link = $link;
            this.$tenphim = $tenphim;
            this.$iframe = $iframe;
            this.$namsx = $namsx;
            this.$thoiluongphim = $thoiluongphim;
            this.$quocgia = $quocgia;
            this.$dophangiai = $dophangiai;
            this.$daodien = $daodien;
            this.$dienvien = $dienvien;
            this.$noidung = $noidung;
        }

        public int get$id() {
            return $id;
        }

        public String get$theloai() {
            return $theloai;
        }

        public String get$link() {
            return $link;
        }

        public String get$tenphim() {
            return $tenphim;
        }

        public String get$iframe() {
            return $iframe;
        }

        public int get$namsx() {
            return $namsx;
        }

        public String get$thoiluongphim() {
            return $thoiluongphim;
        }

        public String get$quocgia() {
            return $quocgia;
        }

        public String get$dophangiai() {
            return $dophangiai;
        }

        public String get$daodien() {
            return $daodien;
        }

        public String get$dienvien() {
            return $dienvien;
        }

        public String get$noidung() {
            return $noidung;
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





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
