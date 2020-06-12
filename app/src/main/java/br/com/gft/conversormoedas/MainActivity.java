package br.com.gft.conversormoedas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private EditText editValue;
    private TextView textDolar;
    private TextView textEuro;
    private Button btnCalcular;
    private double euro = 1;
    private double dolar = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCalcular = findViewById(R.id.button_calculate);
        editValue = findViewById(R.id.edit_value);
        textDolar = findViewById(R.id.text_dolar);
        textEuro = findViewById(R.id.text_euro);

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTask task = new MyTask();
                String urlApi = "https://economia.awesomeapi.com.br/all/USD-BRL,EUR-BRL";
                task.execute(urlApi);
            }
        });
    }

    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            StringBuffer buffer = null;
            final InputStream inputStream;
            InputStreamReader inputStreamReader;


            try {
                URL url = new URL(stringUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //recupera dados em bytes
                inputStream = conn.getInputStream();

                //decodifica os dados de bytes para caracteres.
                inputStreamReader = new InputStreamReader(inputStream);

                //realiza a leitura dos caracteres
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String linha = "";

                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            try {
                JSONObject jsonObject = new JSONObject(resultado);
                String objetoDolar = jsonObject.getString("USD");
                String objetoEuro = jsonObject.getString("EUR");

                JSONObject jsonObjectDolar = new JSONObject(objetoDolar);
                dolar = Double.parseDouble(jsonObjectDolar.getString("ask"));

                JSONObject jsonObjectEuro = new JSONObject(objetoEuro);
                euro = Double.parseDouble(jsonObjectEuro.getString("ask"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Double real = Double.valueOf(editValue.getText().toString());
            textDolar.setText(String.format("%.2f", (real / Double.valueOf(dolar))));
            textEuro.setText(String.format("%.2f", (real / euro)));

        }
    }
}
