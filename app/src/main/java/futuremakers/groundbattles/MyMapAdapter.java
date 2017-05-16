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

    @Override
    public View getSourceMarkerViewForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return null;
    }

    @Override
    public int getHeroMarkerIconForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return R.drawable.marker_cat;
    }

    @Override
    public View getHeroMarkerViewForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return null;
    }

    @Override
    public boolean showAddressInfoViewForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return false;
    }

    @Override
    public boolean showAddressInfoViewForMultipleActionsView(HyperTrackMapFragment hyperTrackMapFragment) {
        return false;
    }

    @Override
    public boolean showOrderStatusToolbar(HyperTrackMapFragment hyperTrackMapFragment) {
        return false;
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
