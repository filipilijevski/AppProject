package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

/**
 * The main entry activity, providing user login
 * and directing to registration or homepages based on role.
 */

public class MainActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private EditText username;
    private EditText password;
    private Button loginButton;
    private Button registerUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerUserButton = findViewById(R.id.registerUserButton);

        // Open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Handle login
        loginButton.setOnClickListener(v -> handleLogin());

        // Handle registration
        registerUserButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class))
        );

        // Adjust for system insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });
    }

    /**
     * Authenticates the user based on role, then navigates accordingly.
     */

    private void handleLogin() {
        String userInput = username.getText().toString();
        String passInput = password.getText().toString();

        User user = dbManager.getUser(userInput);

        if (user != null && user.getAccountPassword().equals(passInput)) {
            Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

            // Determine role
            String role = user.getRole();
            Intent intent;

            switch (role) {
                case "Landlord":
                    intent = new Intent(MainActivity.this, LandlordHomepageActivity.class);
                    break;

                case "Client":
                    intent = new Intent(MainActivity.this, ClientHomepageActivity.class);
                    break;

                case "Manager":
                    intent = new Intent(MainActivity.this, ManagerHomepageActivity.class);
                    break;

                default:
                    throw new IllegalStateException("Unexpected role: " + role);
            }

            intent.putExtra("email_address", user.getEmailAddress());
            startActivity(intent);

        } else {
            Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
