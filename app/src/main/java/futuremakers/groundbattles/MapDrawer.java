package futuremakers.groundbattles;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MapDrawer {
    GoogleMap map;
    int colorLine;
    int colorBackground;
    List<LatLng> coords = new ArrayList<LatLng>();

    public MapDrawer(GoogleMap googleMapInstance) {
        map = googleMapInstance;
        // Todo: get way to set color
        colorLine = 0xff000000;
        colorBackground = 0x55000000;
    }

    public void addPoint(LatLng p) {
        coords.add(p);
    }

    public Polyline drawPolyline() {
        PolylineOptions options = new PolylineOptions();
        options.color(colorLine);
        for (LatLng point : coords)
            options.add(point);

        return map.addPolyline(options);
    }

    public Polyline drawPolyline(LatLng point) {
        addPoint(point);
        return drawPolyline();
    }

    public Polygon drawPolygon() {
        PolygonOptions options = new PolygonOptions();
        options.strokeColor(colorLine);
        options.fillColor(colorBackground);

        for (LatLng point : coords)
            options.add(point);

        return map.addPolygon(options);
    }

    public Double getPolygonArea(Polygon p) {
        return SphericalUtil.computeArea(p.getPoints());
    }

    public Double getPolygonArea() {
        return SphericalUtil.computeArea(coords);
    }

    public Double getPolylineLength(Polyline p) {
        return SphericalUtil.computeLength(p.getPoints());
    }

    public Double getPolylineLength() {
        return SphericalUtil.computeLength(coords);
    }
}