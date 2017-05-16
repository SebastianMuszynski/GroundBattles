package futuremakers.groundbattles;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
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
    private boolean hasReachedMinStartDistance = false;

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
                startTrackingBtn.setVisibility(View.INVISIBLE);
                stopTrackingBtn.setVisibility(View.VISIBLE);

                startTrackingUser();
            }
        });

        stopTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrackingBtn.setVisibility(View.VISIBLE);
                stopTrackingBtn.setVisibility(View.INVISIBLE);

                stopTrackingUser();
            }
        });

        startTrackingBtn.setVisibility(View.VISIBLE);
        stopTrackingBtn.setVisibility(View.INVISIBLE);
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
        public void onActionRefreshed(List<String> refreshedActionIds, List<Action> refreshedActions) {
            String polyline = refreshedActions.get(0).getEncodedPolyline();

            if (polyline != null) {
                List<LatLng> decodedPolyline = PolyUtil.decode(polyline);

                if (decodedPolyline.size() > 0) {

                    LatLng firstPoint = decodedPolyline.get(0);
                    LatLng lastPoint = decodedPolyline.get(decodedPolyline.size()-1);

                    if (decodedPolyline.size() > 1 && SphericalUtil.computeDistanceBetween(lastPoint, firstPoint) > 20) {
                        if (hasReachedMinStartDistance) {
                            landDrawer.drawPolygon();
                        }
                    } else {
                        hasReachedMinStartDistance = true;
                        landDrawer.drawPolyline(lastPoint);
                    }
                }

            }
        }
    };

    private void startTrackingUser() {
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                Toast.makeText(MapActivity.this, "Started tracking!", Toast.LENGTH_SHORT).show();

                if (ActionsData.getInstance().getActionIds().size() < 1) {
                    assignActionToUser();
                }
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
        HyperTrack.trackAction(ActionsData.getInstance().getActionIds(), null);
    }

    private void stopTrackingUser() {
        if (ActionsData.getInstance().getActionIds().size() > 0) {
            HyperTrack.completeAction(ActionsData.getInstance().getActionIds().get(0));
            ActionsData.getInstance().clearActions();
        }

        HyperTrack.stopTracking();
    }
}
