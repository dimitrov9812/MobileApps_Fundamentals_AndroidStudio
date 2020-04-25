package com.example.sqlite_3_basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Update extends AppCompatActivity {

    protected  String ID;
    protected EditText euName,euPhone,euEmail;
    protected Button btnUpdate,btnDelete;
    protected void CloseThisActivity(){
        finishActivity(200);
        Intent i = new Intent(Update.this, MainActivity.class );
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        euName = findViewById(R.id.euName);
        euPhone = findViewById(R.id.euPhone);
        euEmail = findViewById(R.id.euEmail);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);



        Bundle b = getIntent().getExtras();

        if(b != null){
            ID = b.getString("ID");
            euName.setText(b.getString("name"));
            euPhone.setText(b.getString("phone"));
            euEmail.setText(b.getString("email"));
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = null;
                try{
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath()+"/"+"contacts.db",
                            null
                    );

                    String name = euName.getText().toString();
                    String phone = euPhone.getText().toString();
                    String email = euEmail.getText().toString();

                    //Make insert query

                    String q = "UPDATE CONTACTS SET name = ?, phone = ?, email = ?";
                    q+= "Where ID= ?";

                    db.execSQL(q,new Object[]{name,phone,email, ID});

                    Toast.makeText(getApplicationContext(),"Update successful",Toast.LENGTH_LONG).show();


                    db.close();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    if(db!=null){
                        db.close();
                        db = null;
                    }
                }
                CloseThisActivity();
            }

        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = null;
                try{
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath()+"/"+"contacts.db",
                            null
                    );

                    //Make insert query

                    String q = "DELETE FROM CONTACTS WHERE";
                    q+= "ID= ?";

                    db.execSQL(q,new Object[]{ID});

                    Toast.makeText(getApplicationContext(),"Delete successful",Toast.LENGTH_LONG).show();


                    db.close();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    if(db!=null){
                        db.close();
                        db = null;
                    }
                }
                CloseThisActivity();
            }

        });


    }
}
