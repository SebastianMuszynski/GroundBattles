package futuremakers.groundbattles;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
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
        return R.drawable.ic_hero_maker;
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

    @Override
    public boolean showPlaceSelectorView() {

        return false;
    }

    @Override
    public boolean enableLiveLocationSharingView() {
        return false;
    }

    @Override
    public boolean showActionSummaryForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return false;
    }

    @Override
    public CameraUpdate getMapFragmentInitialState(HyperTrackMapFragment hyperTrackMapFragment) {
        if (UserData.getInstance().getUser().getLastLocation() != null) {
            LatLng latLng = new LatLng(54.5189, 18.5305);
            return CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
        }
        return super.getMapFragmentInitialState(hyperTrackMapFragment);
    }
}
