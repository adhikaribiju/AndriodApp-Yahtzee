package com.example.yahtzee.View;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.View.TurnDecideActivity;


import com.example.yahtzee.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
                // Fetch the file names from the Downloads directory
                List<String> fileNames = getFilesFromDownloads();
                if (fileNames.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "No files found in Downloads.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] fileArray = fileNames.toArray(new String[0]);

                // Show file picker dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose a File")
                        .setItems(fileArray, (dialog, which) -> {
                            String selectedFile = fileArray[which];

                            // Read the file content from Downloads
                            String fileContent = readFileFromDownloads(selectedFile);
                            if (!fileContent.isEmpty()) {
                                Log.d("FileContent", "Content of " + selectedFile + ":\n" + fileContent);
                                //Toast.makeText(MainActivity.this, "File Content Loaded:\n" + fileContent, Toast.LENGTH_SHORT).show();
                            }

                            // Optionally, pass the selected file to PlayRoundActivity
                            Intent intent = new Intent(MainActivity.this, PlayRoundActivity.class);
                            intent.putExtra("selectedFile", selectedFile);
                            intent.putExtra("gameType", "Load");
                            startActivity(intent);
                        })
                        .show();
            }
        });







    }



    private List<String> getFilesFromDownloads() {
        List<String> fileNames = new ArrayList<>();
        File downloadsDir = new File(getFilesDir(), "Downloads");

        // Ensure the directory exists
        if (downloadsDir.exists() && downloadsDir.isDirectory()) {
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName()); // Add file names to the list
                    }
                }
            }
        }

        return fileNames;
    }
    private String readFileFromDownloads(String fileName) {
        File downloadsDir = new File(getFilesDir(), "Downloads");
        File file = new File(downloadsDir, fileName);
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("ReadFile", "Failed to read file: " + fileName, e);
        }

        return fileContent.toString();
    }







}