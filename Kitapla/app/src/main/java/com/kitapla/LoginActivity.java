package com.kitapla;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private Toast currentToast; // Tek bir Toast kontrolü

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Auth Başlatma
        auth = FirebaseAuth.getInstance();

        // View Referansları
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        // Giriş Yap Butonu İşlevi
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Alan Kontrolü
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showToast("Tüm alanları doldurun!");
                return;
            }

            // Firebase ile Giriş Yap
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Giriş Başarılı
                            showToast("Giriş Başarılı!");
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            // Giriş Başarısız
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Bilinmeyen bir hata oluştu!";
                            showToast("Giriş Başarısız: " + errorMessage);
                        }
                    });
        });

        // Kayıt Ekranına Yönlendirme
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Tek bir Toast göstermek için yardımcı metod
    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
