package utils;

public class selectedMedicData {
    private static String crm;
    private static String especialidade;
    private static String dia;
    private static String horario;
    private static String SimpleHorario;

    public static String getDia() {
        return dia;
    }

    public static void setDia(String dia) {
        selectedMedicData.dia = dia;
    }

    public static String getHorario() {
        return horario;
    }

    public static void setHorario(String horario) {
        selectedMedicData.horario = horario;
    }

    public static String getSimpleHorario() {
        return SimpleHorario;
    }

    public static void setSimpleHorario(String simpleHorario) {
        SimpleHorario = simpleHorario;
    }

    public static String getCrm() {
        return crm;
    }

    public static void setCrm(String crm) {
        selectedMedicData.crm = crm;
    }

    public static String getEspecialidade() {
        return especialidade;
    }

    public static void setEspecialidade(String especialidade) {
        selectedMedicData.especialidade = especialidade;
    }


}
