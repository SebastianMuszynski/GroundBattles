package futuremakers.groundbattles;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHyperTrack();
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
    }
}
