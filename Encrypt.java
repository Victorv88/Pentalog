package com.example.android.quadracrypto;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Encrypt extends AppCompatActivity implements View.OnClickListener{

    TextView cheie;
    EditText text_introdus;
    TextView mesajCriptat;
    String key;
    String aux;
    private ValueEventListener mPostListener;
    private DatabaseReference mDatabase;
    String ran;
    String MesajIntrodus;
    Button btn;
    String mail_utilizator;
    String mesajIntrodus;
    String Binary;
    private DatabaseReference get_data;
    Boolean gasit_baza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        text_introdus = findViewById(R.id.MesajIntrodus);
        cheie = findViewById(R.id.Cheie);
        btn = findViewById(R.id.btn);
        gasit_baza=false;
        btn.setOnClickListener(this);
        mesajCriptat = findViewById(R.id.MesajCriptat);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mail_utilizator=getIntent().getStringExtra("EMAIL");

    }

    @Override
    public void onClick(View view) {

            key = cheie.getText().toString();
            ran = UUID.randomUUID().toString();
            aux = ran.substring(0,7);
            key += aux;

        MesajIntrodus = text_introdus.getText().toString();

            char Xor = aux.charAt(aux.length() - 1);

            Boolean[] BinText = new Boolean[1700];

        for(int i = 1; i <= MesajIntrodus.length(); i++)
        {
            char letter = MesajIntrodus.charAt(i - 1);

            int AsciiLetter = (int) letter;

            for(int j = 8 * i - 1; j >= 8 * (i - 1) && AsciiLetter != 0; j--, AsciiLetter /= 2)
            {
                if(AsciiLetter % 2 == 0)
                    BinText[j] = false;
                else
                    BinText[j] = true;

            }
        }

        for(int i = 0; i < MesajIntrodus.length() * 8; i++)
        {
            if(BinText[i] == true)
            {
                Binary = Binary.concat("1");

            }
            else
            {
                Binary = Binary.concat("0");
            }

        }

            Log.d("MUIEPSD", "" + Binary);

            get_data= FirebaseDatabase.getInstance().getReference().child("Text").child(aux);
            cheie.setText(key);
            btn.setEnabled(false);




            Incercare_Adaugare();
            gasit_baza=false;
    }

   public void Incercare_Adaugare()
   {
       ValueEventListener postListener=new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(!dataSnapshot.exists())
              {
                  gasit_baza=true;
                   Adaugare_mesaj(aux, MesajIntrodus, mail_utilizator);
                   Log.d("ABC","a intrat pe prima");
                   return;
              }
              else if (gasit_baza==false)
              {
                  Log.d("ABC", "a intrat pe a doua");
                  Toast.makeText(Encrypt.this,"Please try again",Toast.LENGTH_SHORT);
                  btn.setEnabled(true);
                  key=key.substring(0,12);
                  cheie.setText(key);
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("ABC","onCanceleld");
           }
       };
       get_data.addValueEventListener(postListener);
   }


    private void Adaugare_mesaj(String key, String mesaj_cryptat,String mail)
    {
        Mesaj date=new Mesaj(mesaj_cryptat,mail);

        mDatabase.child("Text").child(key).setValue(date);
    }
}
