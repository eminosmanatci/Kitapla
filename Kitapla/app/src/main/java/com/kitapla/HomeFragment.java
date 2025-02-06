package com.kitapla;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private List<Book> filteredBookList; // Arama sonuçları için
    private FirebaseFirestore firestore;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();
        bookAdapter = new BookAdapter(getContext(), filteredBookList, false); // Filtrelenmiş listeyi kullanıyoruz
        recyclerView.setAdapter(bookAdapter);

        firestore = FirebaseFirestore.getInstance();
        loadBooks();

        setupSearchView();

        return view;
    }

    private void loadBooks() {
        firestore.collection("books").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            bookList.add(book);
                        }
                        filteredBookList.addAll(bookList); // Filtrelenmiş listeyi başlangıçta tüm kitaplarla doldur
                        bookAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Veriler alınamadı: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Arama gönderildiğinde
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Arama metni değiştikçe
                filterBooks(newText);
                return true;
            }
        });
    }

    private void filterBooks(String query) {
        filteredBookList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredBookList.addAll(bookList); // Eğer arama boşsa tüm kitapları göster
        } else {
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredBookList.add(book); // Başlığa göre filtrele
                }
            }
        }
        bookAdapter.notifyDataSetChanged(); // RecyclerView'ı güncelle
    }
}
