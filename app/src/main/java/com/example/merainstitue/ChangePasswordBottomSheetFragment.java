package com.example.merainstitue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthException;

public class ChangePasswordBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password_bottom_sheet, container, false);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        currentPasswordEditText = rootView.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = rootView.findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = rootView.findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = rootView.findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Validate input
            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // Re-authenticate the user to change password
                reauthenticateUser(user, currentPassword, newPassword);
            }
        });

        return rootView;
    }

    private void reauthenticateUser(FirebaseUser user, String currentPassword, String newPassword) {
        // Create AuthCredential with the user's current password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        // Re-authenticate the user
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // After successful re-authentication, update the password
                updatePassword(user, newPassword);
            } else {
                // Handle failure
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getContext(), "Invalid current password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Password updated successfully
                Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                dismiss(); // Close the bottom sheet
            } else {
                // Handle failure
                if (task.getException() instanceof FirebaseAuthException) {
                    Toast.makeText(getContext(), "Password change failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
