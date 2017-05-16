package futuremakers.groundbattles;

import android.content.Context;

import com.hypertrack.lib.HyperTrackMapAdapter;
import com.hypertrack.lib.HyperTrackMapFragment;

class MyMapAdapter extends HyperTrackMapAdapter {
    public Context mContext;

    public MyMapAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public int getSourceMarkerIconForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return R.drawable.marker_cat;
    }

    @Override
    public boolean enableLiveLocationSharingView() {
        return false;
    }

    @Override
    public boolean rotateHeroMarker(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return true;
    }

    @Override
    public boolean showTrailingPolyline() {
        return true;
    }
}
