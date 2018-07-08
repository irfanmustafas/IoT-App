package com.example.jatin.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private static String URL = "https://api.thingspeak.com/channels/YOUR_CHANNELID/feeds/last.json";
    private static String URL1 = "https://api.thingspeak.com/update?api_key=YOUR_API_KEY&field7=";
    android.app.AlertDialog dialog;
    private TextView temptextview;
    private TextView humiditytextview;
    private TextView soilMoisturetextview;
    private TextView raintextview;
    private TextView firetextview;
    private TextView pumptextview;
    //its a baad code
    private Button refreshbtn;
    private CardView card;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temptextview = findViewById(R.id.tvtemp);
        humiditytextview = findViewById(R.id.tvhumidity);
        raintextview = findViewById(R.id.tvrain);
        soilMoisturetextview = findViewById(R.id.tvmoisture);
        firetextview = findViewById(R.id.tvfire);
        pumptextview = findViewById(R.id.tvpump);
        refreshbtn = findViewById(R.id.button);
        card = findViewById(R.id.pumpcardview);

        dialog = new SpotsDialog(MainActivity.this);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        Thingspeak channel = gson.fromJson(response, Thingspeak.class);
                        String tempp = channel.getField1();
                        temptextview.setText(tempp + "deg");
                        String humidityy = channel.getField2();
                        humiditytextview.setText(humidityy + "%");

                        String flamee = channel.getField3();
                        Float flame = Float.parseFloat(flamee);
                        if (flame < 900.0) {
                            firetextview.setText("Fire");
                        } else {
                            firetextview.setText("Normal");
                        }
                        String rainn = channel.getField4();
                        Float rain = Float.parseFloat(rainn);
                        if (rain < 900.0) {
                            raintextview.setText("Rain");
                        } else {
                            raintextview.setText("Normal");
                        }
                        String Moisture = channel.getField5();
                        Float moisturee = Float.parseFloat(Moisture);
                        if (moisturee < 500.0) {
                            soilMoisturetextview.setText("High");
                        } else if (moisturee < 850.0) {
                            soilMoisturetextview.setText("Medium");
                        } else {
                            soilMoisturetextview.setText("Low");
                        }

                        String pumpp = channel.getField6();
                        int pump = Integer.parseInt(pumpp);
                        if (pump == 0) {
                            pumptextview.setText("Off");
                        } else {
                            pumptextview.setText("On");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error has occured", Toast.LENGTH_LONG).show();
                    }

                });
                requestQueue.add(stringRequest);

            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                int i = 0;
                String pumpstr = pumptextview.getText().toString();
                if (pumpstr == "On") {
                    i = 0;
                } else if (pumpstr == "Off") {
                    i = 1;
                }
                // wait for seven seconds
                new CountDownTimer(7000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        // You don't need anything here
                    }

                    public void onFinish() {
                        dialog.dismiss();
                    }
                }.start();

                String NURL = URL1 + i;
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                final int finalI = i;
                StringRequest stringRequest = new StringRequest(NURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dialog.dismiss();
                        if (finalI == 0) {
                            pumptextview.setText("Off");
                        } else {
                            pumptextview.setText("On");
                        }

                        Toast.makeText(MainActivity.this, "Pump state is reversed", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pump", "srro while switch on the pump");
                        Toast.makeText(MainActivity.this, "Error while switch on the pump", Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(stringRequest);
            }
        });
    }
}
