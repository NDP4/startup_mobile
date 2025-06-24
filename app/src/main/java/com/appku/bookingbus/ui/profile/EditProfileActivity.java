package com.appku.bookingbus.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appku.bookingbus.R;

// EditProfileActivity.java
public class EditProfileActivity extends AppCompatActivity {
    private TextView tvPhone;
    private TextView tvAddress;
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        // Set text dari tvPhone dan tvAddress
        tvPhone.setText(getIntent().getStringExtra("phone"));
        tvAddress.setText(getIntent().getStringExtra("address"));

        // tombol edit profile
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat dialog untuk edit profile
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                alertDialogBuilder.setTitle("Edit Profile");
                alertDialogBuilder.setMessage("Masukkan data baru");

                final EditText inputPhone = new EditText(EditProfileActivity.this);
                inputPhone.setHint("Nomor Telepon");

                final EditText inputAddress = new EditText(EditProfileActivity.this);
                inputAddress.setHint("Alamat");

                LinearLayout layout = new LinearLayout(EditProfileActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(inputPhone);
                layout.addView(inputAddress);

                alertDialogBuilder.setView(layout);

                alertDialogBuilder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update profile
                        String phone = inputPhone.getText().toString();
                        String address = inputAddress.getText().toString();

                        // Kirim request ke server untuk update profile
                        

                        // Tampilkan toast jika berhasil update profile
                        Toast.makeText(EditProfileActivity.this, "Profile berhasil diupdate", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();
            }
        });
    }
}