package futuremakers.groundbattles;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.MapFragmentCallback;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;

public class MapActivity extends AppCompatActivity {

    private Button startTrackingBtn;
    private Button stopTrackingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
    }

    private void initView() {
        initToolbar();
        initBtns();
        initHyperTrackMap();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    private void initBtns() {
        startTrackingBtn = (Button) findViewById(R.id.startTrackingBtn);
        stopTrackingBtn = (Button) findViewById(R.id.stopTrackingBtn);

        startTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrackingUser();
            }
        });

        stopTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTrackingUser();
            }
        });
    }

    private void initHyperTrackMap() {
        HyperTrackMapFragment htMap = (HyperTrackMapFragment) getSupportFragmentManager().findFragmentById(R.id.htMapFragment);
        htMap.setMapFragmentCallback(htMapCallback);
    }

    private MapFragmentCallback htMapCallback = new MapFragmentCallback() {
        @Override
        public void onMapReadyCallback(HyperTrackMapFragment hyperTrackMapFragment, GoogleMap map){
            Toast.makeText(getApplicationContext(), "On Map Ready callback", Toast.LENGTH_SHORT).show();
        }
    };

    private void startTrackingUser() {
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                Toast.makeText(MapActivity.this, "Started tracking!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(MapActivity.this, "Stopped tracking!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopTrackingUser() {
        HyperTrack.stopTracking();
    }
}
