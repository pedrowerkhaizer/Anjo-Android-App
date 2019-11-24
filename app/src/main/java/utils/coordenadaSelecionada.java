package utils;

public class coordenadaSelecionada {
    public static Double lat;
    public static Double lng;
    public static String end;

    public static Double getLat() {
        return lat;
    }

    public static void setLat(Double lat) {
        coordenadaSelecionada.lat = lat;
    }

    public static Double getLng() {
        return lng;
    }

    public static void setLng(Double lng) {
        coordenadaSelecionada.lng = lng;
    }

    public static String getEnd() {
        return end;
    }

    public static void setEnd(String end) {
        coordenadaSelecionada.end = end;
    }
}
