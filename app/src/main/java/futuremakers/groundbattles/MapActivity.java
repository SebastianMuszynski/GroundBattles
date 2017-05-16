package futuremakers.groundbattles;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.MapFragmentCallback;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.HyperTrackLocation;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private HyperTrackMapFragment htMap;
    private LandDrawer landDrawer;
    private Button startTakingLandBtn;
    private Button stopTakingLandBtn;
    private Button logoutBtn;

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
        startTakingLandBtn = (Button) findViewById(R.id.startTakingLandBtn);
        stopTakingLandBtn = (Button) findViewById(R.id.stopTakingLandBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        startTakingLandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTakingLand();
            }
        });

        stopTakingLandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTakingLand();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HyperTrack.removeActions(null);
                HyperTrack.stopTracking();

                TaskStackBuilder.create(MapActivity.this)
                        .addNextIntentWithParentStack(new Intent(MapActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                finish();
            }
        });
    }

    private void initHyperTrackMap() {
        htMap = (HyperTrackMapFragment) getSupportFragmentManager().findFragmentById(R.id.htMapFragment);
        htMap.setHTMapAdapter(new MyMapAdapter(MapActivity.this));
        htMap.setMapFragmentCallback(htMapCallback);
    }

    private void startTakingLand() {
        HyperTrack.getCurrentLocation(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                Toast.makeText(MapActivity.this, "Assigning user an action", Toast.LENGTH_SHORT).show();

                assignActionToUser();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(MapActivity.this, "startTakingLand problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignActionToUser() {
        String userId = UserData.getInstance().getUser().getId();
        String expectedPlaceId = null;
        Place expectedPlace = null;
        String lookupId = null;
        String type = Action.ACTION_TYPE_VISIT;
        String expectedAt = null;

        Toast.makeText(getApplicationContext(), "Trying to assign an action", Toast.LENGTH_SHORT).show();


        ActionParams actionParams = new ActionParams(
            userId,
            expectedPlaceId,
            expectedPlace,
            type,
            lookupId,
            expectedAt
        );

        HyperTrack.createAndAssignAction(actionParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                if (successResponse.getResponseObject() != null) {
                    Action action = (Action) successResponse.getResponseObject();
                    ActionsData.getInstance().addAction(action.getId());
                    Toast.makeText(getApplicationContext(), "Assigned an action to user!", Toast.LENGTH_SHORT).show();

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

    private void trackActions() {
        HyperTrack.trackAction(ActionsData.getInstance().getActionIds(), new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                List<Action> actionsList = (List<Action>) response.getResponseObject();
                HyperTrack.getAction(actionsList.get(0).getId(), new HyperTrackCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse response) {
                        Action actionResponse = (Action) response.getResponseObject();
                        System.out.println(actionResponse);
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        Toast.makeText(MapActivity.this, "Tracking action problem!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(MapActivity.this, "Tracking action problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopTakingLand() {
        if (ActionsData.getInstance().getActionIds().size() > 0) {
            HyperTrack.completeAction(ActionsData.getInstance().getActionIds().get(0));
        }
    }

    private MapFragmentCallback htMapCallback = new MapFragmentCallback() {
        @Override
        public void onMapReadyCallback(HyperTrackMapFragment hyperTrackMapFragment, GoogleMap map){
            Toast.makeText(getApplicationContext(), "Prepare yourself!", Toast.LENGTH_SHORT).show();

            landDrawer = new LandDrawer(map);
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

            HyperTrackLocation lastPoint = refreshedActions.get(0).getUser().getLastLocation();
            LatLng lastPointLatLng = lastPoint.getGeoJSONLocation().getLatLng();
            landDrawer.drawPolyline(lastPointLatLng);

            System.out.println(landDrawer.getPolylineLength());
        }
    };
}
