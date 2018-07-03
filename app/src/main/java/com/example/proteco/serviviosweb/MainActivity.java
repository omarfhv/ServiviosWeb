package com.example.proteco.serviviosweb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnActualizar, btnBorrar, btnInsertar;
    EditText nombre, direccion, Id;

    String IP = "http://protecoandroid.esy.es";

    String INSERT = IP + "/insertar_alumno.php";
    String GET = IP + "/obtener_alumnos.php";
    String DELETE = IP + "/borrar_alumno.php";
    String UPDATE = IP + "/actualizar_alumno.php";

    ObtenerWebService hiloConexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnActualizar = (Button) findViewById(R.id.btnActualizar);
        btnBorrar = (Button) findViewById(R.id.btnBorrar);
        btnInsertar = (Button) findViewById(R.id.btnInsertar);


        nombre = (EditText) findViewById(R.id.edtNombre);
        direccion = (EditText) findViewById(R.id.edtDireccion);
        Id = (EditText) findViewById(R.id.edtIdentificador);


        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiloConexion = new ObtenerWebService();
                hiloConexion.execute(INSERT, "3", nombre.getText().toString(), direccion.getText().toString(), Id.getText().toString());


            }
        });

        //cuando uso execute tengo que usar doInBackground
    }

    public class ObtenerWebService extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String[] params) {
            String cadena = params[0];
            URL url = null;
            String devuelve = "";
            if (params[1] == "3") {
                try {
                    DataOutputStream printout;
                    DataInputStream printint;
//WEB SERVICE
                    HttpURLConnection urlConn;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);

                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");

                    urlConn.connect();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("nombre", params[2]);
                    jsonParam.put("direccion", params[3]);

                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
             //
                    int respuesta = urlConn.getResponseCode();
                    StringBuilder resultado = new StringBuilder();
                    if (respuesta == HttpURLConnection.HTTP_OK){
                        BufferedReader br =  new BufferedReader(new InputStreamReader( urlConn.getInputStream()));
                        String linea;
                        while ((linea=br.readLine())!=null){
                            resultado.append(linea);

                        }
                        JSONObject respuestaJSON = new JSONObject(resultado.toString());
                        String resultadoJson = respuestaJSON.getString("estado");


                        if(resultadoJson == "1"){
                            devuelve= "la insecion fue correcta";
                            Toast.makeText(getApplicationContext(),devuelve,Toast.LENGTH_SHORT).show();
                        }else if (resultadoJson =="0"){
                            devuelve="la insercion fue incorrecta";
                        }


                    }


                } catch (MalformedURLException me) {
                    me.printStackTrace();
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }
                catch (JSONException je){
                    je.printStackTrace();
                }
            }


            return devuelve;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getApplicationContext(),"cancelado",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"PostExecute",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"PreExecute",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"onProgressUpdate",Toast.LENGTH_SHORT).show();

        }
    }
}

