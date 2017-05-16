package futuremakers.groundbattles;

import android.content.Context;
import android.view.View;

import com.hypertrack.lib.HyperTrackMapAdapter;
import com.hypertrack.lib.HyperTrackMapFragment;

class MyMapAdapter extends HyperTrackMapAdapter {
    public Context mContext;

    public MyMapAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

//    @Override
//    public int getSourceMarkerIconForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
//        return R.drawable.ic_ht_hero_marker_car;
//    }

    @Override
    public int getHeroMarkerIconForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return R.drawable.ic_ht_hero_marker_car;
    }

//    @Override
//    public View getHeroMarkerViewForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
//        return null;
//    }

    @Override
    public boolean enableLiveLocationSharingView() {
        return true;
    }

    @Override
    public boolean rotateHeroMarker(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return true;
    }

    @Override
    public boolean showTrailingPolyline() {
        return false;
    }
}
