package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText editA,editB,editC;
    Button btnCalc;
    TextView viewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editA = findViewById(R.id.editA);
        editB = findViewById(R.id.editB);
        editC = findViewById(R.id.editC);
        btnCalc = findViewById(R.id.btnCalc);
        viewResult = findViewById(R.id.viewResult);

        btnCalc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    double A = Double.parseDouble((editA.getText().toString()));
                    double B = Double.parseDouble((editB.getText().toString()));
                    double C = Double.parseDouble((editC.getText().toString()));

                    if(A==0){
                        viewResult.setText("edin koren x = " + (-C/B));
                    }
                    else{
                        double D = B*B - 4*A*C;
                        if(D<0){
                            viewResult.setText("nqma realni koreni");
                        }
                        else if(D==0){
                            viewResult.setText("edin koren: x = " +(-B/(2*A)));
                        }else{
                            viewResult.setText("dva korena: ");
                            D=Math.sqrt(D);
                            viewResult.append("X1 = "+ (-B-D)/(2*A) + "\n ");
                            viewResult.append("X2 = "+ (-B+D)/(2*A) + "\n ");
                        }
                    }

                }
                catch(Exception e){
                    viewResult.setText(e.getLocalizedMessage());
                }
            }
        }
        );
    }
}
