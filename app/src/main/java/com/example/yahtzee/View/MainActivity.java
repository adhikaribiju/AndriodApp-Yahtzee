package com.example.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.View.TurnDecideActivity;


import com.example.yahtzee.R;

public class MainActivity extends AppCompatActivity {

    public static Tournament tournament = new Tournament();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Reference the buttons using their IDs
        Button buttonStartGame = findViewById(R.id.btnStartGame);
        Button buttonLoadGame = findViewById(R.id.btnLoadGame);

        // Set an OnClickListener for the "Start a Game" button
        buttonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to the StartGame activity
                Intent intent = new Intent(MainActivity.this, TurnDecideActivity.class);
                intent.putExtra("gameType", "Start");
                // Start the StartGame activity
                startActivity(intent);
            }
        });

        // Set an OnClickListener for the "Load a Game" button

        buttonLoadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dialog to open a file picker
               // LoadGame(view);
            }
        });


        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */


    }
}