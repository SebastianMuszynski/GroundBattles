package futuremakers.groundbattles;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;
import com.hypertrack.lib.models.User;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    // UI Elements
    private SignInButton googleSignInButton;
    private TextView welcomeText;

    // Objects
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount googleAccount;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHyperTrack();
        initGoogleSignIn();
        initView();
    }

    private void initView() {
        welcomeText = (TextView) findViewById(R.id.welcomeText);
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
        if (result.isSuccess()) {
            handleSignInSuccess(result);
        } else {
            handleSignInError(result);
        }
    }

    private void handleSignInSuccess(GoogleSignInResult result) {
        googleAccount = result.getSignInAccount();
        ensureLocationSettingsAndContinue();
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
        HyperTrack.initialize(this, "pk_test_1305b4b0cfd67a5743605023bc9c85d71f84fcca");
    }

    private void ensureLocationSettingsAndContinue() {
        if (!HyperTrack.checkLocationPermission(this)) {
            HyperTrack.requestPermissions(this);
            return;
        }

        if (!HyperTrack.checkLocationServices(this)) {
            HyperTrack.requestLocationServices(this, null);
        }

        createOrLoginHypertrackUser();
    }

    private void createOrLoginHypertrackUser() {
        HyperTrack.getOrCreateUser(googleAccount.getDisplayName(), null, googleAccount.getId(),
            new HyperTrackCallback() {
                @Override
                public void onSuccess(@NonNull SuccessResponse successResponse) {
                    onHypertrackUserLoginSuccess(successResponse);
                }

                @Override
                public void onError(@NonNull ErrorResponse errorResponse) {
                    onHypertrackUserLoginError(errorResponse);
                }
            });
    }

    private void onHypertrackUserLoginSuccess(SuccessResponse successResponse) {
        user = (User) successResponse.getResponseObject();
        welcomeText.setText("Hi " + user.getName());

        Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(mapIntent);
    }

    private void onHypertrackUserLoginError(ErrorResponse errorResponse) {
        Toast.makeText(getApplicationContext(), "Hypertrack create or login user error", Toast.LENGTH_SHORT).show();
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
                welcomeText.setText("GroundBattles needs your location permisions");
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
                welcomeText.setText("GroundBattles needs your location permisions");
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}
