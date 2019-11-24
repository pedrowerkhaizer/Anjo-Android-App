package utils;

public class filtroMedicos {
    private static Boolean distancia;
    private static Boolean rating;
    private static String especialidade;

    public static String getEspecialidade() {
        return especialidade;
    }

    public static void setEspecialidade(String especialidade) {
        filtroMedicos.especialidade = especialidade;
    }

    public static Boolean getDistancia() {
        return distancia;
    }

    public static void setDistancia(Boolean distancia) {
        filtroMedicos.distancia = distancia;
    }

    public static Boolean getRating() {
        return rating;
    }

    public static void setRating(Boolean rating) {
        filtroMedicos.rating = rating;
    }
}
