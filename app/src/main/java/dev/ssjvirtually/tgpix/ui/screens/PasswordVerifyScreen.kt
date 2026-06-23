package dev.ssjvirtually.tgpix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ssjvirtually.tgpix.ui.theme.TelePhotosTheme
import dev.ssjvirtually.tgpix.telegram.AuthManager
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.util.Log
import org.drinkless.tdlib.TdApi

@Composable
fun PasswordVerifyScreen() {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize().background(TelePhotosTheme.Background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = TelePhotosTheme.Surface),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Key / lock verification icon styled with a Google Photos color ring!
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(
                                    TelePhotosTheme.GoogleBlue,
                                    TelePhotosTheme.GoogleGreen,
                                    TelePhotosTheme.GoogleYellow,
                                    TelePhotosTheme.GoogleRed,
                                    TelePhotosTheme.GoogleBlue
                                )
                             ),
                             shape = CircleShape
                        )
                        .padding(3.dp)
                        .background(TelePhotosTheme.Surface, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock, // Elegant key lock
                        contentDescription = null,
                        tint = TelePhotosTheme.AccentBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Enter Password",
                    color = TelePhotosTheme.TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your Telegram account is protected by a Two-Factor Authentication (2FA) cloud password. Please enter it to authorize this session.",
                    color = TelePhotosTheme.TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Cloud Password", color = TelePhotosTheme.TextSecondary) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description, tint = TelePhotosTheme.TextSecondary)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TelePhotosTheme.AccentBlue,
                        unfocusedBorderColor = TelePhotosTheme.SurfaceVariant,
                        focusedLabelColor = TelePhotosTheme.AccentBlue,
                        unfocusedLabelColor = TelePhotosTheme.TextSecondary,
                        focusedTextColor = TelePhotosTheme.TextPrimary,
                        unfocusedTextColor = TelePhotosTheme.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        isLoading = true
                        AuthManager.verifyPassword(password) { result ->
                            isLoading = false
                            if (result is TdApi.Error) {
                                Log.e("TGPix", "Failed to verify 2FA password: [${result.code}] ${result.message}")
                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Log.i("TGPix", "2FA password verified successfully, result: ${result::class.java.simpleName}")
                            }
                        }
                    },
                    enabled = password.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = TelePhotosTheme.AccentBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Log In", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
