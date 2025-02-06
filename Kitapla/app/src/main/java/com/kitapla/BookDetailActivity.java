package com.kitapla;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookDetailActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ImageView bookImageView = findViewById(R.id.bookImageView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView authorTextView = findViewById(R.id.authorTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        Button favoriteButton = findViewById(R.id.favoriteButton);

        // Intent ile gelen verileri al
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String description = getIntent().getStringExtra("description");

        // Verileri ekrana yerleştir
        titleTextView.setText(title);
        authorTextView.setText(author);
        descriptionTextView.setText(description);
        Glide.with(this).load(imageUrl).into(bookImageView);

        // Favorilere ekle butonu
        favoriteButton.setOnClickListener(v -> {
            String userId = auth.getCurrentUser().getUid();
            Map<String, Object> favoriteBook = new HashMap<>();
            favoriteBook.put("title", title);
            favoriteBook.put("author", author);
            favoriteBook.put("imageUrl", imageUrl);
            favoriteBook.put("description", description);

            firestore.collection("favorites").document(userId)
                    .collection("books").document(title) // Başlık benzersiz olsun
                    .set(favoriteBook)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BookDetailActivity.this, "Favorilere eklendi!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BookDetailActivity.this, "Favorilere eklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
