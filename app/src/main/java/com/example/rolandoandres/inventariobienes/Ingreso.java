package com.example.rolandoandres.inventariobienes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Ingreso extends ActionBarActivity {
    private String[] listaElementos;
    private Connection conexionMySQL;

    private Spinner tipospinner, departamentospinner,zonaspinner, areaspinner, estadospinner,custodiospinner,proveedorspinner;
    private EditText codigotext, espadretext,marcatext,modelotext,numpartetext,numserietext,colortext,caracteristicastext;

    private int numtipo, numdepartamento,numzona, numarea,numpadre ,numcustodio,numproveedor,numserie,numparte;
    private String txtcodigo,txtestado,txtmarca,txtmodelo,txtcolor,txtcaracteristicas;
    private String txtSwitch ="false";

    private Button guardarButton,cancelarButton;
    private TextView codigolabel, espadrelabel,imagenlabel,codigopadrelabel,numpartelabel,numserielabel,colorlabel,custodiolabel,estadolabel,caracteristicaslabel,proveedorlabel, modelolabel,tipolabel,departamentolabel,arealabel,zonalabel,marcalabel ;
    private Switch espadreswitch;
    private ImageView imagenBien;


    private ArrayAdapter adaptador;
    private static final int CAMERA_PIC_REQUEST = 0;
    private File photoFile;




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

        conectarBDMySQL();

        tipospinner = (Spinner)findViewById(R.id.tipospinner);
        departamentospinner = (Spinner)findViewById(R.id.departamentospinner);
        zonaspinner = (Spinner)findViewById(R.id.zonaspinner);
        areaspinner = (Spinner)findViewById(R.id.areaspinner);
        estadospinner = (Spinner)findViewById(R.id.estadospinner);
        custodiospinner = (Spinner)findViewById(R.id.custodiospinner);
        proveedorspinner = (Spinner)findViewById(R.id.proveedorspinner);

        espadretext = (EditText) findViewById(R.id.espadretext);
        codigotext = (EditText) findViewById(R.id.codigotext);
        marcatext = (EditText) findViewById(R.id.marcatext);
        modelotext = (EditText) findViewById(R.id.modelotext);
        numpartetext = (EditText) findViewById(R.id.numpartetext);
        numserietext = (EditText) findViewById(R.id.numserietext);
        colortext = (EditText) findViewById(R.id.colortext);
        caracteristicastext = (EditText) findViewById(R.id.caracteristicastext);

        codigopadrelabel = (TextView) findViewById(R.id.codigopadrelabel);
        espadreswitch = (Switch) findViewById(R.id.espadreswitch);
        imagenBien = (ImageView) findViewById(R.id.imagenBien);

        guardarButton = (Button) findViewById(R.id.guardarButton);
        cancelarButton = (Button) findViewById(R.id.cancelarButton);

        //
        obtenerListaTablas("select nombreTipo from tipo");
        tipospinner.setAdapter(adaptador);
        //
        obtenerListaTablas("select nombreDep from departamento");
        departamentospinner.setAdapter(adaptador);
        //
        obtenerListaTablas("select nombreZona from Zona");
        zonaspinner.setAdapter(adaptador);
        //
        obtenerListaTablas("select nombreArea from Area");
        areaspinner.setAdapter(adaptador);
        //
        adaptador = new ArrayAdapter(Ingreso.this,
                    android.R.layout.simple_list_item_1, listaEstado);
        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        estadospinner.setAdapter(adaptador);
        //
        obtenerListaTablas("select nombreEmp from empleado");
        custodiospinner.setAdapter(adaptador);
        //
        obtenerListaTablas("select nombrePro from proveedor");
        proveedorspinner.setAdapter(adaptador);

        //
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
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
                    }
                }
            }
        });
        //
        espadreswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    codigopadrelabel.setVisibility(View.VISIBLE);
                    espadretext.setVisibility(View.VISIBLE);
                    txtSwitch ="true";
                } else {
                    codigopadrelabel.setVisibility(View.GONE);
                    espadretext.setVisibility(View.GONE);
                    txtSwitch ="false";
                }
            }
        });

        //
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                txtcodigo = codigotext.getText().toString();
                numpadre = Integer.parseInt(espadretext.getText().toString());
                numtipo = consultarid("tipo", "idTipo", "nombreTipo", tipospinner.getItemAtPosition(tipospinner.getSelectedItemPosition()).toString());
                numdepartamento = consultarid("departamento","idDepartamento", "nombreDep",departamentospinner.getItemAtPosition(departamentospinner.getSelectedItemPosition()).toString());
                numarea = consultarid("area", "idArea","nombreArea",areaspinner.getItemAtPosition(areaspinner.getSelectedItemPosition()).toString());
                numzona = consultarid("zona", "idZona","nombreZona",zonaspinner.getItemAtPosition(zonaspinner.getSelectedItemPosition()).toString());
                txtmarca = marcatext.getText().toString();
                txtmodelo = modelotext.getText().toString();
                numparte = Integer.parseInt(numpartetext.getText().toString());
                numserie = Integer.parseInt(numserietext.getText().toString());
                txtestado = estadospinner.getItemAtPosition(estadospinner.getSelectedItemPosition()).toString();
                txtcolor = colortext.getText().toString();
                numcustodio = consultarid("empleado", "idEmpleado", "nombreEmp", custodiospinner.getItemAtPosition(custodiospinner.getSelectedItemPosition()).toString());
                numproveedor = consultarid("proveedor", "idProveedor","nombrePro",proveedorspinner.getItemAtPosition(proveedorspinner.getSelectedItemPosition()).toString());
                txtcaracteristicas = caracteristicastext.getText().toString();
                guardarArticulo(getApplicationContext());



            }
        });

        //
        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingreso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.probarconexion) {
           // conectarBDMySQL();
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
                urlConexionMySQL = "jdbc:mysql://" + textIP + ":" +textPuerto + "/" + catalogoMySQL;
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

    public int consultarid(String tabla, String columna1, String columna2, String descripcion ) {
        int id=0;
        try {
            Statement st = conexionMySQL.createStatement();
            String sql ="select " + columna1 + " from " + tabla + " where " + columna2 + " = \"" + descripcion + "\"";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next())
                id = rs.getInt(1);
            st.close();
            rs.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public void guardarArticulo(Context context)
    {

        String articuloDetalle="(codigoArt,marcaArt,modeloArt,colorArt,numeroparteArt,numeroserieArt,estadoArt,imagenArt,jerarquiaArt,articuloPadre,idDepartamento,idArea,idZona,idTipo,fk_idProveedor,fk_idEmpleado)";
        String sqlSentence;
        sqlSentence = "insert into articulo"+articuloDetalle+" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        FileInputStream imagestream = null;
        PreparedStatement ps = null;
        try {
            conexionMySQL.setAutoCommit(false);
            imagestream = new FileInputStream(this.photoFile);
            ps = conexionMySQL.prepareStatement(sqlSentence);
            ps.setString(1, txtcodigo);
            ps.setString(2, txtmarca);
            ps.setString(3, txtmodelo);
            ps.setString(4, txtcolor);
            ps.setInt(5, numparte);
            ps.setInt(6, numserie);
            ps.setString(7, txtestado);
            ps.setBinaryStream(8, imagestream, (int) this.photoFile.length());
            ps.setString(9, txtSwitch);
            ps.setInt(10, numpadre);
            ps.setInt(11, numdepartamento);
            ps.setInt(12, numarea);
            ps.setInt(13, numzona);
            ps.setInt(14, numtipo);
            ps.setInt(15, numproveedor);
            ps.setInt(16, numcustodio);
            ps.executeUpdate();
            conexionMySQL.commit();
            ps.close();
            Toast.makeText(getApplicationContext(),
                    "Se ha guardado exitosamente el registro",
                    Toast.LENGTH_SHORT).show();
            limpiarcampos();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void limpiarcampos(){

    }
}

//Revisar conexiones.
//Nueva clase para conexiones y consultas
//Comentar
