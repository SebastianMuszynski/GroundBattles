package futuremakers.groundbattles;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.SphericalUtil;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.MapFragmentCallback;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private HyperTrackMapFragment htMap;
    private LandDrawer landDrawer;
    private Button startTrackingBtn;
    private Button stopTrackingBtn;
    private double startCircleRadius = 10;
    private LatLng startPoint;
    private boolean hasStartedTakingLand = false;
    private boolean isCircleDrawn = false;
    private Circle startCircle;
    private Polyline userPath;

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
        htMap = (HyperTrackMapFragment) getSupportFragmentManager().findFragmentById(R.id.htMapFragment);
        htMap.setHTMapAdapter(new MyMapAdapter(MapActivity.this));
        htMap.setMapFragmentCallback(htMapCallback);
    }

    private MapFragmentCallback htMapCallback = new MapFragmentCallback() {
        @Override
        public void onMapReadyCallback(HyperTrackMapFragment hyperTrackMapFragment, GoogleMap map){
            Toast.makeText(getApplicationContext(), "Prepare yourself!", Toast.LENGTH_SHORT).show();

            landDrawer = new LandDrawer(map);
        }

        @Override
        public void onHeroMarkerAdded(HyperTrackMapFragment hyperTrackMapFragment, String actionID, Marker heroMarker) {
            Toast.makeText(getApplicationContext(), "Hero Marker added", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onActionStatusChanged(List<String> changedStatusActionIds, List<Action> changedStatusActions) {
            super.onActionStatusChanged(changedStatusActionIds, changedStatusActions);
        }

        @Override
        public void onActionRefreshed(List<String> refreshedActionIds, List<Action> refreshedActions) {
            super.onActionRefreshed(refreshedActionIds, refreshedActions);
            Toast.makeText(getApplicationContext(), "Action Refreshed", Toast.LENGTH_SHORT).show();

            HyperTrack.getCurrentLocation(new HyperTrackCallback() {
                @Override
                public void onSuccess(@NonNull SuccessResponse successResponse) {
                    Location location = (Location) successResponse.getResponseObject();

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    if(startPoint == null) {
                        startPoint = currentLocation;
                        userPath = landDrawer.drawPolyline(currentLocation);
                    } else {
                        if(hasStartedTakingLand) {
                            if(SphericalUtil.computeDistanceBetween(startPoint, currentLocation) <= startCircleRadius) {
                                landDrawer.drawPolygon();
                                userPath.remove();
                                stopTrackingUser();
                            } else {
                                userPath = landDrawer.drawPolyline(currentLocation);
                            }
                        } else {
                            if(!isCircleDrawn) {
                                startCircle = landDrawer.drawCircle(startPoint, startCircleRadius);
                                isCircleDrawn = true;
                            }
                            if(SphericalUtil.computeDistanceBetween(startPoint, currentLocation) > startCircleRadius) {
                                hasStartedTakingLand = true;
                                startCircle.remove();
                            }
                        }

                    }
                    Toast.makeText(MapActivity.this, String.valueOf(SphericalUtil.computeDistanceBetween(startPoint, currentLocation)), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull ErrorResponse errorResponse) {
                    Toast.makeText(getApplicationContext(), "On Action Refreshed error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

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
        String userId = UserData.getInstance().getUser().getId();
        String expectedPlaceId = null;
        Place expectedPlace = null;
        String lookupId = null;
        String type = Action.ACTION_TYPE_VISIT;
        String expectedAt = null;

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
        System.out.println(ActionsData.getInstance().getActionIds());
        HyperTrack.trackAction(ActionsData.getInstance().getActionIds(), new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                List<Action> actionsList = (List<Action>) response.getResponseObject();
                System.out.println(actionsList);

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

    private void stopTrackingUser() {
        HyperTrack.stopTracking();
        isCircleDrawn = false;
        HyperTrack.removeActions(null);
    }
}
