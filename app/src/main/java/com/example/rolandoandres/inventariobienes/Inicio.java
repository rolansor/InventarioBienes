package com.example.rolandoandres.inventariobienes;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;


public class Inicio extends ActionBarActivity {

    private Button btIngreso, btRevision, btAdministracion, btnSalir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);


        //Boton para el ingreso de bienes.
        btIngreso = (Button) findViewById(R.id.button_ingresar);
        btIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intIngreso = new Intent(Inicio.this, Ingreso.class);
                startActivity(intIngreso);
            }
        });

        //Boton para la revision de bienes.
        btRevision = (Button) findViewById(R.id.button_revision);
        btRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intIngreso = new Intent(Inicio.this, Revision.class);
                startActivity(intIngreso);
            }
        });

        //Boton para la administracion de las opciones de los menus.
        btAdministracion = (Button) findViewById(R.id.button_administracion);
        btAdministracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intIngreso = new Intent(Inicio.this, Ingreso.class);
                startActivity(intIngreso);
            }
        });

        //Boton para salir de la aplicacion.
        btnSalir = (Button) findViewById(R.id.button_salir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
