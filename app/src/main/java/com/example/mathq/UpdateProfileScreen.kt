package com.example.mathq.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun UpdateProfileScreen(userId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val ref = FirebaseDatabase.getInstance().getReference("User").child(userId)

    LaunchedEffect(Unit) {
        ref.get().addOnSuccessListener {
            name = it.child("name").getValue(String::class.java) ?: ""
            email = it.child("email").getValue(String::class.java) ?: ""
            password = it.child("password").getValue(String::class.java) ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            ref.child("name").setValue(name)
            ref.child("email").setValue(email)
            ref.child("password").setValue(password)

            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
            onBack()
        }) {
            Text("Save")
        }
    }
}
