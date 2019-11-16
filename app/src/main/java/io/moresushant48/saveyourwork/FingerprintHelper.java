package io.moresushant48.saveyourwork;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FingerprintHelper {

    private FragmentActivity activity;
    private ExecutorService executors;

    FingerprintHelper(FragmentActivity activity) {
        this.activity = activity;
        executors = Executors.newSingleThreadExecutor();
    }

    public void startAuthenticationPrompt(){
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Authenticate yourself to use the app.")
                .setNegativeButtonText("Cancel")
                .build();

        new BiometricPrompt(activity, executors, new FingerprintHelperCallback()).authenticate(promptInfo);
    }

    public class FingerprintHelperCallback extends BiometricPrompt.AuthenticationCallback {

        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            activity.finish();
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }
    }
}
