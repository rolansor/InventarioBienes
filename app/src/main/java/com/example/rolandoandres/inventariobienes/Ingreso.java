package com.example.rolandoandres.inventariobienes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Ingreso extends ActionBarActivity {
    private String[] listaElementos;
    private Connection conexionMySQL;
    private Spinner spnTipos, spnDepartamento,spnEstado,spnCustodio,spnProveedor;
    private ArrayAdapter adaptador;
    private static final int CAMERA_PIC_REQUEST = 0;
    private File photoFile;
    private ImageView imagenBien;
    private Switch switchPadre;
    private TextView codigopadrelabel;
    private EditText espadretext;
    private static final String textIP = "192.168.1.100";
    private static final String textPuerto = "3306";
    private static final String textContrasena = "root";
    private static final String textUsuario = "root";
    private static final String catalogoMySQL = "inventario";
    private static final String[] listaEstado = new String[]{"Bueno", "Malo", "Desuso"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_ingreso);




        spnTipos = (Spinner)findViewById(R.id.tipospinner);
        obtenerListaTablas("select nombreTipo from tipo");
        spnTipos.setAdapter(adaptador);

        spnDepartamento = (Spinner)findViewById(R.id.departamentospinner);
        obtenerListaTablas("select nombreDep from departamento");
        spnDepartamento.setAdapter(adaptador);

        spnEstado = (Spinner)findViewById(R.id.estadospinner);
        adaptador = new ArrayAdapter(Ingreso.this,
                android.R.layout.simple_list_item_1, listaEstado);

        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spnEstado.setAdapter(adaptador);

        spnCustodio = (Spinner)findViewById(R.id.custodiospinner);
        obtenerListaTablas("select nombreEmp from empleado");
        spnCustodio.setAdapter(adaptador);

        spnProveedor = (Spinner)findViewById(R.id.proveedorspinner);
        obtenerListaTablas("select nombrePro from proveedor");
        spnProveedor.setAdapter(adaptador);

        imagenBien = (ImageView) findViewById(R.id.imageview);

        imagenBien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                        photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(getApplicationContext(),
                                "El directorio es inaccesible favor intenta de nuevo.",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
                    }
                }
            }
        });

        switchPadre = (Switch) findViewById(R.id.espadreswitch);
        codigopadrelabel = (TextView) findViewById(R.id.codigopadrelabel);
        espadretext = (EditText) findViewById(R.id.espadretext);
        switchPadre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    codigopadrelabel.setVisibility(View.VISIBLE);
                    espadretext.setVisibility(View.VISIBLE);
                }
                else{
                    codigopadrelabel.setVisibility(View.GONE);
                    espadretext.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingreso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.probarconexion) {
            conectarBDMySQL();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case CAMERA_PIC_REQUEST:
                if(resultCode==RESULT_OK){
                    if(photoFile!=null)
                        imagenBien.setImageURI(Uri.fromFile(photoFile));
                    else
                        Toast.makeText(getApplicationContext(),
                                "La imagen no fue almacenada correctamente.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        String storageDir = Environment.getExternalStorageDirectory().toString() + "/InventarioBienes/ImagenBienes";
        File imagesFolder = new File(storageDir);
        File image=null;
        if (imagesFolder.exists())
            {
                image = new File(imagesFolder, imageFileName);
            }
        else
        {
            imagesFolder.mkdirs();
            image = new File(imagesFolder, imageFileName);
        }
        return image;
    }



    public void obtenerListaTablas(String sql)
    {
        try
        {
            conectarBDMySQL();
            Statement st = conexionMySQL.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.last();
            Integer numFilas = 0;
            numFilas = rs.getRow();

            listaElementos = new String[numFilas];
            Integer j = 0;
            for (int i = 1; i <= numFilas; i++)
            {
                listaElementos [j] = rs.getObject(1).toString();
                j++;
                rs.previous();
            }
            rs.close();

            adaptador = new ArrayAdapter(Ingreso.this,
                            android.R.layout.simple_list_item_1, listaElementos);

            adaptador.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void conectarBDMySQL ()
    {
            String urlConexionMySQL = "";
            if (catalogoMySQL != "")
                urlConexionMySQL = "jdbc:mysql://" + textIP + ":" +
                        textPuerto + "/" + catalogoMySQL;
            else
                urlConexionMySQL = "jdbc:mysql://" + textIP + ":" + textPuerto;
            try
                {
                    Class.forName("com.mysql.jdbc.Driver");
                    conexionMySQL =	DriverManager.getConnection(urlConexionMySQL,
                            textUsuario, textContrasena);
                }
                catch (ClassNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                catch (SQLException e)
                {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
    }

    public void ejecutarSql (String sql, Context context)
    {
        try
        {
            // consulta SQL de modificación de
            // datos (CREATE, DROP, INSERT, UPDATE)

                conectarBDMySQL();
                Statement st = conexionMySQL.createStatement();
                st.executeUpdate(sql);
                st.close();
                Toast.makeText(context,
                    "Se ha guardado exitosamente el registro.",
                    Toast.LENGTH_SHORT).show();
        }

        catch (Exception e)
        {
            Toast.makeText(context,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}

//Revisar conexiones.
//Nueva clase para conexiones y consultas
//Comentar
