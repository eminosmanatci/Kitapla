package com.kitapla;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kullanıcı oturumunu kontrol et
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Kullanıcı giriş yapmışsa, HomeActivity'ye yönlendir
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            // Kullanıcı giriş yapmamışsa, LoginActivity'ye yönlendir
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        // MainActivity'yi kapat
        finish();
    }
}
