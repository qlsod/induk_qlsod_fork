package com.example.figma.controller.myinformation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.figma.R;
import com.example.figma.controller.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyInfChPW extends Activity {
    private EditText  ch_password1, password1;
    private ImageButton backButton;
    private Button CompleteChangeButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser; //안드로이드와 파이어베이스 사이의 인증을 확인하기 위한 인스턴스 선언



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_inf_change_pw);

        ch_password1 = findViewById(R.id.ch_password1); // 새 비밀번호
        password1 = findViewById(R.id.password1); //비밀번호 확인
        CompleteChangeButton = findViewById(R.id.CompleteChangeButton);
        backButton = findViewById(R.id.backButton);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfChPW.this, MyInfDetails.class);
                startActivity(intent);
            }
        });

        CompleteChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = ch_password1.getText().toString();
                String confirmPassword = password1.getText().toString();
                if(newPassword.equals(confirmPassword)){
                    DatabaseReference signUpRef = FirebaseDatabase.getInstance().getReference("signUp");
                    Query query = signUpRef.orderByChild("idToken").equalTo(uid);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("password").setValue(newPassword);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                   mAuth.sendPasswordResetEmail(mUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               Toast.makeText(MyInfChPW.this, "비밀번호 변경 확인 메일이 발송되었습니다.", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(MyInfChPW.this, Login.class);
                               startActivity(intent);
                               finish();
                           }
                       }
                   });
                }else {
                    Toast.makeText(MyInfChPW.this,"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}