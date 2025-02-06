package com.kitapla;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private boolean isFavorites;

    public BookAdapter(Context context, List<Book> bookList, boolean isFavorites) {
        this.context = context;
        this.bookList = bookList;
        this.isFavorites = isFavorites;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        Glide.with(context).load(book.getImageUrl()).into(holder.bookImageView);

        // Kitaba tıklandığında
        holder.itemView.setOnClickListener(v -> {
            if (!isFavorites) {
                // Favori olmayan kitaplar için detay ekranına git
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("title", book.getTitle());
                intent.putExtra("author", book.getAuthor());
                intent.putExtra("imageUrl", book.getImageUrl());
                intent.putExtra("description", book.getDescription());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Bu kitap favorilerde!", Toast.LENGTH_SHORT).show();
            }
        });

        // Favorilerdeki kitaplar için çıkar butonu görünür
        if (isFavorites) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(v -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection("favorites").document(userId)
                        .collection("books").document(book.getTitle())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            bookList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, bookList.size());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Kitap silinirken hata oluştu.", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView;
        ImageView bookImageView;
        Button removeButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            bookImageView = itemView.findViewById(R.id.bookImageView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
