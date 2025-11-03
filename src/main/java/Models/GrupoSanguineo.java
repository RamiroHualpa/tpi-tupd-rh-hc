package Models;

public enum GrupoSanguineo {
    A_POSITIVO("A+"), A_NEGATIVO("A-"), B_POSITIVO("B+"), B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"), AB_NEGATIVO("AB-"), O_POSITIVO("O+"), O_NEGATIVO("O-");

    private final String simbolo;

    private GrupoSanguineo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }
}
