package com.kitapla;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> favoriteBooks;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteBooks = new ArrayList<>();
        bookAdapter = new BookAdapter(getContext(), favoriteBooks, true); // Favori işlemi için "true"
        recyclerView.setAdapter(bookAdapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("favorites").document(userId).collection("books").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteBooks.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            favoriteBooks.add(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Favoriler yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
