package ch.unil.eda.activmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.models.UserStatus;
import io.matchmore.sdk.Matchmore;

public class SignInActivity extends ActivMatchActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 1;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        findViewById(R.id.signin_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.sign_in_button).setOnClickListener(this::onSignInClick);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Launch main activity
            toMainActivity(account);
            return;
        }
        findViewById(R.id.signin_layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void onSignInClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            default:
                throw new IllegalArgumentException("Bad view passed to onSignInClick");
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account == null) {
                handleSignInFailure();
                return;
            }
            // Signed in successfully, show authenticated UI.
            toMainActivity(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            handleSignInFailure();
        }
    }

    private void handleSignInFailure() {
        Toast.makeText(this, "The sign-in failed.", Toast.LENGTH_LONG).show();
    }


    private void toMainActivity(final GoogleSignInAccount a) {
        storeUserLocally(a);
        startActivity(new Intent(this, MainActivity.class));
    }

    private void storeUserLocally(final GoogleSignInAccount a) {
        User u = accountToUser(a);
        storage.setUser(u);
    }

    private User accountToUser(final GoogleSignInAccount a) {
        return new User(a.getId(), a.getDisplayName(), UserStatus.AVAILABLE);
    }
}
