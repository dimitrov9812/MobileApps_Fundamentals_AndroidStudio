package com.example.sqlite_3_basics;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected EditText editName,editPhone,editEmail;
    protected Button btnInsert;
    protected ListView simpleList;
    protected void initDB() throws SQLException{
        SQLiteDatabase db = null;

        db = SQLiteDatabase.openOrCreateDatabase(
                getFilesDir().getPath()+"/"+"contacts.db",
                null
        );

        String q = "CREATE TABLE if not exists";
        q+= " ID integer primary key AUTOINCREMENT ";
        q+= " name text not null";
        q+= " phone text not null";
        q+= " email text not null";
        q+= " unique(name, phone)";

        db.execSQL(q);
        db.close();
    }

    public void selectDB() throws SQLException {
        SQLiteDatabase db = null;

        db = SQLiteDatabase.openOrCreateDatabase(
                getFilesDir().getPath()+"/"+"contacts.db",
                null
        );
        simpleList.clearChoices();
        ArrayList<String> listResults = new ArrayList<String>();
        String q = "SELECT * FROM CONTACTS ORDER BY name";
        Cursor c = db.rawQuery(q, null);

        while (c.moveToNext()){
            String name = c.getString(c.getColumnIndex("name"));
            String email = c.getString(c.getColumnIndex("email"));
            String phone = c.getString(c.getColumnIndex("phone"));
            String ID = c.getString(c.getColumnIndex("ID"));
            listResults.add(ID+"\t" + name + "\t" + email + "\t" + phone);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.activity_list_view,
                R.id.textView,
                listResults
        );

        simpleList.setAdapter(arrayAdapter);

        db.execSQL(q);
        db.close();
    }

    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        try{
            selectDB();
        }
        catch(Exception e)
        {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName =findViewById(R.id.editName);
        editPhone =findViewById(R.id.editPhone);
        editEmail =findViewById(R.id.editEmail);
        btnInsert =findViewById(R.id.btnInsert);

        simpleList = findViewById(R.id.simpleList);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = "";
                TextView clickedText = view.findViewById(R.id.textView);
                selected = clickedText.getText().toString();

                String[] elements = selected.split("\t");
                String ID = elements[0];
                String Name = elements[1];
                String Email = elements[2];
                String Phone = elements[3];

                Intent intent = new Intent(MainActivity.this, Update.class);
                Bundle b = new Bundle();
                b.putString("ID",ID);
                b.putString("name",Name);
                b.putString("email",Email);
                b.putString("phone",Phone);

                intent.putExtras(b);
                startActivityForResult(intent,200,b);


            }
        });


        try{
            initDB();
            selectDB();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }

        btnInsert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SQLiteDatabase db = null;
                try{
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath()+"/"+"contacts.db",
                            null
                    );

                    String name = editName.getText().toString();
                    String phone = editPhone.getText().toString();
                    String email = editEmail.getText().toString();

                    //Make insert query

                    String q = "INSERT INTO CONTACTS (name,phone,email) VALUES (?,?,?)";

                    db.execSQL(q,new Object[]{name,phone,email});

                    Toast.makeText(getApplicationContext(),"Insert successful",Toast.LENGTH_LONG).show();


                    db.close();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    if(db!=null){
                        db.close();
                        db = null;
                    }

                }

                try{
                    selectDB();
                }catch (Exception e){

                }
            }
        });
    }
}
