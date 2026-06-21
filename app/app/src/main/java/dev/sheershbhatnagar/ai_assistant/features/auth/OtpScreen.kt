package dev.sheershbhatnagar.ai_assistant.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sheershbhatnagar.ai_assistant.R

@Composable
fun OtpScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit
) {
    val otp by viewModel.otp.collectAsState()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToHome()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0x10FFFFFF), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Verification",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Text(
                text = "Enter the secure login code sent to your email",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(40.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0x0AFFFFFF),
                border = BorderStroke(1.dp, Color(0x15FFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "ONE-TIME CODE",
                        color = Color(0x80FFFFFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { viewModel.updateOtp(it) },
                        placeholder = { Text("6-digit code", color = Color(0x40FFFFFF)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color(0x20FFFFFF),
                            focusedContainerColor = Color(0x05FFFFFF),
                            unfocusedContainerColor = Color(0x05FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.verifyOtp(onNavigateToHome) },
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Verify & Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = Color(0xFFF87171),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
