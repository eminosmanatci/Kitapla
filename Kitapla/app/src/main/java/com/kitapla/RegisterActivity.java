package com.kitapla;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Auth Başlatma
        auth = FirebaseAuth.getInstance();

        // View Referansları
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        // Kayıt Butonu İşlevi
        registerButton.setOnClickListener(v -> {
            // Kullanıcı girişlerini alma
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Alan Kontrolleri
            if (TextUtils.isEmpty(username)) {
                showToast("Kullanıcı adı boş olamaz!");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                showToast("E-posta boş olamaz!");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                showToast("Şifre boş olamaz!");
                return;
            }

            if (password.length() < 6) {
                showToast("Şifre en az 6 karakter olmalı!");
                return;
            }

            // Firebase ile Kayıt Olma
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Kayıt başarılı!");
                            finish(); // Giriş ekranına geri dön
                        } else {
                            handleFirebaseError(task.getException());
                        }
                    });
        });
    }

    private void handleFirebaseError(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();

            switch (errorCode) {
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    showToast("Bu e-posta zaten kullanılıyor!");
                    break;
                case "ERROR_INVALID_EMAIL":
                    showToast("Geçersiz e-posta adresi!");
                    break;
                case "ERROR_WEAK_PASSWORD":
                    showToast("Şifre çok zayıf!");
                    break;
                default:
                    showToast("Hata: " + exception.getMessage());
                    break;
            }
        } else {
            showToast("Bilinmeyen bir hata oluştu.");
        }
    }

    // Tek Toast Gösterimi için Yardımcı Metod
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
