package com.example.test.ecuaruta.Logic;

/**
 * Created by Diego on 6/18/2016.
 */
public class Denuncia {
    private String placa;
    private String compania;
    private String problema;
    private String email;

    public Denuncia(String placa, String compania, String problema, String email) {
        this.placa = placa;
        this.compania = compania;
        this.problema = problema;
        this.email = email;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
