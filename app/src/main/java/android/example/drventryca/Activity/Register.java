package android.example.drventryca.Activity;

import android.app.Activity;
import android.content.Intent;
import android.example.drventryca.Model.Data;
import android.example.drventryca.R;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Variable yang akan merefers Firebase Realtime Database.
    DatabaseReference root; // untuk mengambil rootnya
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    FirebaseUser user; // untuk mengambil user

     TextView toHome, goLogin;

    // Variable fields EditText dan Button
    private Button btSubmitDB;
    EditText etNamaDepan, etNamaBelakang, etBeratBadan, etTinggiBadan, etUsia, etUserName, etPassword;
    Spinner goldar;
    RadioGroup gender;
    RadioButton jenisKelamin;

    String gen, spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.AUTOFILL_FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);



        // inisialisasi
        etNamaDepan = findViewById(R.id.namdep_edit);
        etNamaBelakang = findViewById(R.id.nambel_edit);
        etBeratBadan = findViewById(R.id.edit_mass);
        etTinggiBadan = findViewById(R.id.edit_ting);
        etUsia = findViewById(R.id.edit_usia);
        btSubmitDB = findViewById(R.id.bt_update);
        toHome = findViewById(R.id.goHome);
        etUserName = findViewById(R.id.edit_email);
        etPassword = findViewById(R.id.edit_pass);
        gender = findViewById(R.id.rg_kelamin);
        goldar = findViewById(R.id.spin_goldar);


        // Radio Group dan Radio Button
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                jenisKelamin = gender.findViewById(i);

                switch (i) {
                    case R.id.rb_laki:
                        gen = jenisKelamin.getText().toString().trim();
                        break;

                    case R.id.rb_perempuan:
                        gen = jenisKelamin.getText().toString().trim();
                        break;

                    default:
                }
            }
        });

        Spinner spinner = findViewById(R.id.spin_goldar);


        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spn = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.goldar, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (spinner != null) {
            spinner.setAdapter(adapter);
        }




        // Ranah Firebase
        auth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();

        btSubmitDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String namaDepan, namaBelakang, username, password;
                final int massaBadan, tinggiBadan, usia;

                namaDepan = etNamaDepan.getText().toString().trim();
                namaBelakang = etNamaBelakang.getText().toString().trim();
                username = etUserName.getText().toString().trim();
                password = etPassword.getText().toString().trim();


                massaBadan = Integer.parseInt(etBeratBadan.getText().toString().trim());
                tinggiBadan = Integer.parseInt(etTinggiBadan.getText().toString().trim());
                usia = Integer.parseInt(etUsia.getText().toString().trim());

                auth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user = auth.getCurrentUser(); // dia mengambil user yang terbaru;
                                    Data data = new Data(namaDepan, namaBelakang, String.valueOf(massaBadan), String.valueOf(tinggiBadan), String.valueOf(usia), String.valueOf(gen), String.valueOf(spn));
                                    Log.d("Laki laki", data.getGender() + data.getGolonganDarah());
                                    root.child("User:").child(user.getUid()).setValue(data)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(), "Sekarang anda sudah terdaftar, silahkan lakukan login", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                                    finish();
                                                }
                                            });
                                }
                            }
                        });
            }

        });





    }

    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);

    }


    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, Landing.class);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
        String spinnerLabel = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void toLogin(View view){
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
