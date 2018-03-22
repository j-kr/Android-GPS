package com.example.jakub.zadanie1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private TextView wyswSz;
    private TextView wyswDl;
    private TextView test;
    private EditText editSzukaj;

    private Button button;
    Database myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new Database(this);


        //myDb.insertData("Posąg", "A.Mickiewicz", "10.00", "100.00");
        //myDb.insertData("Fontanna", "Fontanna Trytona", "0.00", "0.00");
        //myDb.insertData("Nic", "Czarna dziura", "-50.00", "10.00");

        wyswSz = (TextView) findViewById(R.id.txtSzerokosc);
        wyswDl = (TextView) findViewById(R.id.txtDlugosc);
        editSzukaj = (EditText) findViewById(R.id.editSzukaj);

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

                double promienSzukania = Double.parseDouble(String.valueOf(editSzukaj.getText()));
                double szukaneSzMAX = (Double.parseDouble((String) wyswSz.getText()) + promienSzukania);
                double szukaneSzMIN = Double.parseDouble((String) wyswSz.getText()) - promienSzukania;
                double szukaneDlMAX = Double.parseDouble((String) wyswDl.getText()) + promienSzukania;
                double szukaneDlMIN = Double.parseDouble((String) wyswDl.getText()) - promienSzukania;

                //Cursor res = myDb.getAllData();
                Cursor res = myDb.getWybrane(szukaneSzMIN, szukaneSzMAX, szukaneDlMIN, szukaneDlMAX);
                if (res.getCount() == 0) {
                    showMessage("Błąd", "Nic nie znaleziono");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("\n\nID :" + res.getString(0) + "\n");
                    buffer.append("Nazwa :" + res.getString(1) + "\n");
                    buffer.append("Opis :" + res.getString(2) + "\n");
                    buffer.append("Szerokość :" + res.getString(3) + "\n");
                    buffer.append("Długość :" + res.getString(4) + "\n");
                }
                showMessage("Znalezione miejsca: ", buffer.toString());
            }
        });

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

}