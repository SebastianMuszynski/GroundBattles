package futuremakers.groundbattles;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.MapFragmentCallback;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.List;

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
        htMap.setHTMapAdapter(new MyMapAdapter(MapActivity.this));
        htMap.setMapFragmentCallback(htMapCallback);
    }

    private MapFragmentCallback htMapCallback = new MapFragmentCallback() {
        @Override
        public void onMapReadyCallback(HyperTrackMapFragment hyperTrackMapFragment, GoogleMap map){
            Toast.makeText(getApplicationContext(), "Prepare yourself!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onHeroMarkerAdded(HyperTrackMapFragment hyperTrackMapFragment, String actionID, Marker heroMarker) {
            Toast.makeText(getApplicationContext(), "Hero Marker Added callback", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onActionStatusChanged(List<String> changedStatusActionIds, List<Action> changedStatusActions) {
            super.onActionStatusChanged(changedStatusActionIds, changedStatusActions);
        }

        @Override
        public void onActionRefreshed(List<String> refreshedActionIds, List<Action> refreshedActions) {
            super.onActionRefreshed(refreshedActionIds, refreshedActions);
        }
    };

    private void trackActions() {
        System.out.println(ActionsData.getInstance().getActionIds());
        HyperTrack.trackAction(ActionsData.getInstance().getActionIds(), new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                List<Action> actionsList = (List<Action>) response.getResponseObject();
                System.out.println(actionsList);

//                // Start Activity containing HyperTrackMapFragment
//                // ActionId can also be passed along as intent extras

//                Intent intent = new Intent(this, TrackingActivity.class);
//                startActivity(intent);
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(MapActivity.this, "Tracking action problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTrackingUser() {
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                Toast.makeText(MapActivity.this, "Started tracking!", Toast.LENGTH_SHORT).show();

                assignActionToUser();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(MapActivity.this, "Stopped tracking!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignActionToUser() {
        ActionParams actionParams = new ActionParams("testUserId", null, null, null, null, null);

        HyperTrack.createAndAssignAction(actionParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                if (successResponse.getResponseObject() != null) {
                    Action action = (Action) successResponse.getResponseObject();
                    ActionsData.getInstance().addAction(action.getId());

                    trackActions();
                } else {
                    Toast.makeText(getApplicationContext(), "Could not assign an action to a user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), "Could not assign an action to a user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopTrackingUser() {
        HyperTrack.stopTracking();

        HyperTrack.removeActions(null);
    }
}
