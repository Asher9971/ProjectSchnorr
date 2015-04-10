package com.example.wolfgang.projectschnorr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AddActivity extends ActionBarActivity {
    ListView lv;
    Cursor cursor1;
    Intent intent;
    ArrayList <String> allnames = new ArrayList<String>();
    ArrayAdapter <String> arrayadapter;
    int clickedName = 0;
    TextView tv; //hadfhaefowwoeefoq   TESTWIEDERLÖSCHEN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        lv = (ListView) findViewById(R.id.listView);
        tv = (TextView) findViewById(R.id.textView);        //TEST WIEDER LÖSCHEN!!!!!
        fillListWithContacts();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                clickedName = position;
                tv.setText(allnames.get(position));
                showAddDialog();
                
            }
        });
    }

    public void fillListWithContacts(){

        Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            allnames.add(name);
        }
        arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allnames);
        lv.setAdapter(arrayadapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private void showAddDialog(){
        final EditText txtName = new EditText(this);
        final boolean ok = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(allnames.get(clickedName)+" Schuldet Ihnen: ")
                .setCancelable(true)
                .setView(txtName)
                .setPositiveButton("OK.", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                       String newName = txtName.getText().toString();
                       setIntent(newName);
                       startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setIntent(String input){
        intent = new Intent(this, MainActivity.class);
        intent.putExtra("Schulden", input);
        intent.putExtra("Name", allnames.get(clickedName));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
