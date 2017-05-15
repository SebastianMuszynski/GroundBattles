package futuremakers.groundbattles;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.MapFragmentCallback;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
    }

    private void initView() {
        initToolbar();
        initHyperTrackMap();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
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
}
