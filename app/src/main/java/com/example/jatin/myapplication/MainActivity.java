package com.example.jatin.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
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

    //declaring ui components
    private TextView temptextview;
    private TextView humiditytextview;
    private TextView soilMoisturetextview;
    private TextView raintextview;
    private TextView firetextview;
    private TextView pumptextview;
    private TextView refreshbtn;
    private CardView card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing ui component
        temptextview = findViewById(R.id.tvtemp);
        humiditytextview = findViewById(R.id.tvhumidity);
        raintextview = findViewById(R.id.tvrain);
        soilMoisturetextview = findViewById(R.id.tvmoisture);
        firetextview = findViewById(R.id.tvfire);
        pumptextview = findViewById(R.id.tvpump);
        refreshbtn = findViewById(R.id.button);
        //Initializing Cardview
        card = findViewById(R.id.pumpcardview);
        //Initializing progressBar
        dialog = new SpotsDialog(MainActivity.this);

        //Refresh Button is pressed by the user to get data from Thingspeak Server
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Showing Progress Bar
                dialog.show();

                // Make a new Request Queue
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If data is received from the server dismis the Progres Bar
                        dialog.dismiss();
                        // Create a Gson object to parse Json received From Server
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        Thingspeak channel = gson.fromJson(response, Thingspeak.class);

                        //temperatue reading is stored in field1 of the received JSON
                        String tempp = channel.getField1();
                        temptextview.setText(tempp + "deg");

                        //humidity reading is stored in field2 of the received JSON
                        String humidityy = channel.getField2();
                        humiditytextview.setText(humidityy + "%");

                        //Flame reading is stored in field3 of the received JSON
                        String flamee = channel.getField3();
                        Float flame = Float.parseFloat(flamee);

                        //Test Condition to show user whether there is fire in the farm or everthing is normal
                        if (flame < 900.0) {
                            firetextview.setText("Fire");
                        } else {
                            firetextview.setText("Normal");
                        }

                        //Rain reading is stored in field4 of the received JSON
                        String rainn = channel.getField4();
                        Float rain = Float.parseFloat(rainn);
                        //Test Condition to show user whether there is Rain in the farm or everthing is normal
                        if (rain < 900.0) {
                            raintextview.setText("Rain");
                        } else {
                            raintextview.setText("Normal");
                        }

                        //Moisture reading is stored in field5 of the received JSON
                        String Moisture = channel.getField5();
                        Float moistureVal = Float.parseFloat(Moisture);
                        if (moistureVal < 500.0) {
                            soilMoisturetextview.setText("High");
                        } else if (moistureVal < 850.0) {
                            soilMoisturetextview.setText("Medium");
                        } else {
                            soilMoisturetextview.setText("Low");
                        }

                        //check status of pump
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
                        dialog.dismiss();
                        //if error occured while fetching data from thingspeak server then show toast
                        Toast.makeText(MainActivity.this, "Error has occured", Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(stringRequest);
            }
        });

        // if user click on the pump cardview then it should on/off the pump App do so by sneding a request to thing speak server
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
                        Log.d("pump", "Error while switch on the pump");
                        Toast.makeText(MainActivity.this, "Error while switch on the pump", Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(stringRequest);
            }
        });
    }
}
