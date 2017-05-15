package futuremakers.groundbattles;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hypertrack.lib.HyperTrack;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private SignInButton googleSignInButton;
    private GoogleSignInAccount googleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHyperTrack();
        initGoogleSignIn();
    }

    private void initGoogleSignIn() {
        getGoogleSignInOptions();
        getGoogleApiClient();

        googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(this);
    }

    private void getGoogleSignInOptions() {
        // Configure sign-in to request the user's ID, email address, and basic
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    private void getGoogleApiClient() {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by googleSignInOptions.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void handleGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            handleSignInSuccess(result);
        } else {
            handleSignInError(result);
        }
    }

    private void handleSignInSuccess(GoogleSignInResult result) {
        googleAccount = result.getSignInAccount();
        // TODO: Do something useful after signing in
        Toast.makeText(getApplicationContext(), "Google user signed in", Toast.LENGTH_SHORT).show();

    }

    private void handleSignInError(GoogleSignInResult result) {
        Toast.makeText(getApplicationContext(), "Google sign in error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Google connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sign_in_button:
                handleGoogleSignIn();
            break;
        }
    }

    private void initHyperTrack() {
        HyperTrack.initialize(this, "pk_f04d294b0576604faf34b6dcd51aed573531dc2f");
    }

    public void onLoginButtonClick(View view) {
        ensureLocationSettingsAndContinue();
    }

    private void ensureLocationSettingsAndContinue() {
        if (!HyperTrack.checkLocationPermission(this)) {
            HyperTrack.requestPermissions(this);
            return;
        }

        if (!HyperTrack.checkLocationServices(this)) {
            HyperTrack.requestLocationServices(this, null);
        }

        // TODO:
        // 1. Login/create HyperTrack User
        // 2. Redirect user to the Map Activity
    }

    /**
     * Handle on Grant Location Permissions request accepted/denied result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ensureLocationSettingsAndContinue();
            } else {
                Toast.makeText(this, "Location Permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handle on Enable Location Services request accepted/denied result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                ensureLocationSettingsAndContinue();
            } else {
                Toast.makeText(this, "Location Services denied.", Toast.LENGTH_SHORT).show();
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}
