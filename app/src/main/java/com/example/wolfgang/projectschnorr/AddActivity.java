package com.example.wolfgang.projectschnorr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AddActivity extends Activity
{
    ListView lv;
    Cursor cursor1;
    Intent intent;
    ArrayList <String> allnames = new ArrayList<String>();
    ArrayList <String> allnumbers = new ArrayList<String>();
    ArrayAdapter <String> arrayadapter;
    int clickedName = 0;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        lv = (ListView) findViewById(R.id.listView);
        fillListWithContacts();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                clickedName = position;
                showAddDialog();
                
            }
        });
    }

    public void fillListWithContacts(){

        Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            allnames.add(name);
            allnumbers.add(number);
        }
        arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allnames);
        lv.setAdapter(arrayadapter);
        cursor.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private void showAddDialog(){
        final EditText txtName = new EditText(this);
        final Spinner howto = new Spinner(this);
        final boolean ok = false;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final Spinner whoto = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whoto.setAdapter(spinnerAdapter);
        spinnerAdapter.add("Ich - Ihm");
        spinnerAdapter.add("Er - Mir");
        spinnerAdapter.notifyDataSetChanged();
        whoto.setPadding(20, 20, 20, 20);
        layout.addView(whoto);


        final EditText note = new EditText(this);
        note.setHint("Notiz");
        layout.addView(note);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(allnames.get(clickedName)+" Schuldet Ihnen: ")
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("OK.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newSchulden = note.getText().toString();
                        setIntent(newSchulden);
                    }
                })
                .setNegativeButton("Cancel.", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void setIntent(String input){
        intent = new Intent();
        intent.putExtra("schulden", input);
        intent.putExtra("name", allnames.get(clickedName));
        intent.putExtra("nummer", allnumbers.get(clickedName));
        setResult(Activity.RESULT_OK, intent);
        finish();
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
