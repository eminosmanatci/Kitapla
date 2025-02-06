package com.kitapla;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private EditText nameEditText, ageEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private ImageView avatarImageView;
    private Button updateButton, logoutButton;

    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // View'ları bağlama
        nameEditText = view.findViewById(R.id.nameEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        maleRadioButton = view.findViewById(R.id.maleRadioButton);
        femaleRadioButton = view.findViewById(R.id.femaleRadioButton);
        avatarImageView = view.findViewById(R.id.avatarImageView);
        updateButton = view.findViewById(R.id.updateButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Kullanıcı bilgilerini Firebase'den doldur
        fetchUserData();

        // Cinsiyet seçimi dinleyicisi
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.maleRadioButton) {
                avatarImageView.setImageResource(R.drawable.ic_male_avatar);
            } else if (checkedId == R.id.femaleRadioButton) {
                avatarImageView.setImageResource(R.drawable.ic_female_avatar);
            }
        });

        // Güncelle butonu işlevi
        updateButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String ageText = ageEditText.getText().toString().trim();
            String gender;

            if (genderRadioGroup.getCheckedRadioButtonId() == R.id.maleRadioButton) {
                gender = "Erkek";
            } else if (genderRadioGroup.getCheckedRadioButtonId() == R.id.femaleRadioButton) {
                gender = "Kadın";
            } else {
                gender = null;
            }

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "İsim boş olamaz!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(ageText)) {
                Toast.makeText(getContext(), "Yaş boş olamaz!", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = Integer.parseInt(ageText);

            // Firebase güncellemeleri
            databaseReference.child(currentUser.getUid()).child("name").setValue(name);
            databaseReference.child(currentUser.getUid()).child("age").setValue(age);
            databaseReference.child(currentUser.getUid()).child("gender").setValue(gender);

            Toast.makeText(getContext(), "Profil güncellendi!", Toast.LENGTH_SHORT).show();
        });

        // Çıkış yap butonu işlevi
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kullanıcı bilgilerini fragment yeniden açıldığında çek
        fetchUserData();
    }

    // Kullanıcı bilgilerini Firebase'den çekme
    private void fetchUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();
                    String name = snapshot.child("name").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    Integer age = snapshot.child("age").getValue(Integer.class);

                    if (name != null) {
                        nameEditText.setText(name);
                    }

                    if ("Erkek".equals(gender)) {
                        maleRadioButton.setChecked(true);
                        avatarImageView.setImageResource(R.drawable.ic_male_avatar);
                    } else if ("Kadın".equals(gender)) {
                        femaleRadioButton.setChecked(true);
                        avatarImageView.setImageResource(R.drawable.ic_female_avatar);
                    } else {
                        genderRadioGroup.clearCheck();
                        avatarImageView.setImageResource(R.drawable.ic_default_avatar);
                    }

                    if (age != null) {
                        ageEditText.setText(String.valueOf(age));
                    }
                }
            });
        }
    }
}
