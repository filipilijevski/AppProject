package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ClientHistoryActivity extends AppCompatActivity {
    private String clientEmail;
    private EditText messageEditText;
    private SeekBar urgencySeekBar;
    private TextView urgencyTextView;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_history);

        // Retrieve the client email from the intent
        clientEmail = getIntent().getStringExtra("email_address");

        // Initialize UI components
        messageEditText = findViewById(R.id.messageEditText);
        urgencySeekBar = findViewById(R.id.urgencySeekBar);
        urgencyTextView = findViewById(R.id.urgencyTextView);
        submitButton = findViewById(R.id.submitButton);

        // Set initial urgency text
        urgencyTextView.setText(String.valueOf(urgencySeekBar.getProgress()));

        // Set up the SeekBar listener
        urgencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                urgencyTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Handle when user starts to track the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Handle when user stops tracking the SeekBar
            }
        });

        // Set up the Submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });
    }

    /**
     * Handles the submit button click event.
     */
    private void handleSubmit() {
        String message = messageEditText.getText().toString().trim();
        int urgencyLevel = urgencySeekBar.getProgress();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement message handling logic (e.g., save to database or send to server)

        // Show confirmation toast
        Toast.makeText(this, "Ticket Submitted", Toast.LENGTH_SHORT).show();

        // Navigate back to ClientHomepageActivity
        Intent intent = new Intent(ClientHistoryActivity.this, ClientHomepageActivity.class);
        intent.putExtra("email_address", clientEmail);
        startActivity(intent);

        // Optional: Finish this activity so it's removed from the back stack
        finish();
    }
}
