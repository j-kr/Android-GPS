package com.example.user.zadanie2;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private TextView wyswSz;
    private TextView wyswDl;
    private TextView test;
    private String Adres;

    public String nazwaMiasta;
    public String opisMiasta;
    public String szerMiasta;
    public String dlugMiasta;

    String[] tab = new String[5];
    String[] tabwysla = new String[4];


    private Button button;
    Database myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new Database(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Adres ="http://10.0.2.2/zabytki/zabytki.php";
        //myDb.insertData("Zamek 1", "Interesujący zamek", "-75.12", "27.756", "17");
        //myDb.deleteData("1");

        initControls();
    }
    //myDb.insertData("Katedra 1", "Zabytkowa katedra", "12.12", "111.756");
    //myDb.insertData("Zamek 1", "Interesujący zamek", "-75.12", "27.756");

    private void initControls() {
        wyswSz = (TextView) findViewById(R.id.txtSzerokosc);
        wyswDl = (TextView) findViewById(R.id.txtDlugosc);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000, 1, this);

        button = (Button) findViewById(R.id.buttonSzukaj);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String akSz = (String) wyswSz.getText();
                String akDl = (String) wyswDl.getText();

                new BackgroundTask().execute(Adres);
                Cursor res = myDb.getWybrane(akSz, akDl);
                //Cursor res = myDb.getAllData();
                if (res.getCount() == 0) {
                    showMessage("Błąd", "Nic nie znaleziono");

                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append(res.getString(0) + "\n");
                    buffer.append(res.getString(1) + "\n");
                    buffer.append("Opis :" + res.getString(2) + "\n\n");
                    buffer.append("Szerokość geo.:"+ res.getString(3) + "\n");
                    buffer.append("Długość geo.:"+ res.getString(4) + "\n");
                    buffer.append("Data aktualizacji:"+ res.getString(5) + "\n");
                }
                showMessage("Znalezione miejsca: ", buffer.toString());
            }
        });
    }

    private class BackgroundTask extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {


            try {
                return PobierzURL(params[0]);
            } catch (IOException e) {
                return null;
            }
        }


        private String[] PobierzURL(String BiezAdres) throws IOException {
            Document doc = Jsoup.connect(BiezAdres).get();
            log(doc.title());

            int i = 0;
            Element content = doc.getElementById("zabytki");
            Elements wiersz = content.getElementsByTag("p");
            for (Element w : wiersz) {
                tab[i] = w.text();
                i++;

            }
            return new String[] {tab[0], tab[1], tab[2], tab[3], tab[4] };
        }


        protected void onPostExecute(String[] wynik){

            showMessage("Proszę czekać.", "Sprawdzanie internetowej bazy danych");

            if (wyswSz.getText().equals("11.0") && wyswDl.getText().equals("11.0")){
                Cursor res = myDb.getAllData();
                if (res.getCount() == 0) {
                    myDb.insertData(wynik[0], wynik[1], wynik[2], wynik[3], wynik[4]);
                }
                while (res.moveToNext()) {
                    if (!res.getString(5).equals(wynik[4]))
                        myDb.updateData("1", wynik[0], wynik[1], wynik[2], wynik[3], wynik[4]);
                }
            }



        }
    }


    @Override
    public void onLocationChanged(Location location) {
        String msg1 = String.valueOf(location.getLatitude());
        String msg2 = String.valueOf(location.getLongitude());

        wyswSz.setText(msg1);
        wyswDl.setText(msg2);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    };

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    };



}