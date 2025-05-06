package com.bro.contactsapp.ui.contact

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bro.contactsapp.R
import com.bro.contactsapp.domain.model.Contact

@Composable
fun ContactScreen(viewModel: ContactViewModel) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.values.all { it }
            if (granted) {
                viewModel.loadContacts()
            } else {
                viewModel.updatePermissionDenied()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.CALL_PHONE
        ))
    }

    when {
        state.permissionDenied -> {
            PermissionDeniedScreen(
                onOpenSettings = {
                    context.startActivity(
                        Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:${context.packageName}"))
                    )
                }
            )
        }
        state.error != null -> ErrorScreen(
            errorMessage = state.error ?: stringResource(R.string.ErrorScreenUnknownErrorMessage),
            onRetry = { viewModel.loadContacts() }
        )
        state.isLoading -> CircularProgressIndicator()
        else -> {
            ContactListScreen(
                contacts = state.contacts,
                onContactClick = { phone ->
                    if (androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CALL_PHONE
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        makeCall(context, phone)
                    } else {
                        androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.ContactListScreenAlertDialogTitle))
                            .setMessage(context.getString(R.string.ContactListScreenAlertDialogMessage))
                            .setPositiveButton("ОК", null)
                            .show()
                    }
                }
            )
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = errorMessage, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.ErrorScreenRepeatButton))
        }
    }
}

@Composable
fun ContactListScreen(
    contacts: List<Contact>,
    onContactClick: (String) -> Unit
) {
    if (contacts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.ContactListScreenNoContact),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    } else {
        LazyColumn {
            items(contacts) { contact ->
                ContactItem(contact = contact, onContactClick = onContactClick)
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact, onContactClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onContactClick(contact.phoneNumber) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = contact.name, fontWeight = FontWeight.Bold)
            Text(text = contact.phoneNumber)
        }
    }
}

@Composable
fun PermissionDeniedScreen(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.PermissionDeniedScreenCause))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onOpenSettings) {
            Text(stringResource(R.string.PermissionDeniedScreenOpenSettings))
        }
    }
}

private fun makeCall(context: android.content.Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}