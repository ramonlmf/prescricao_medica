/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author ramon
 */
public class Cidade {
    private String estadoId;
    private String cidade;

    // Construtores
    public Cidade(String estadoId, String cidade) {
        this.estadoId = estadoId;
        this.cidade = cidade;
    }

    // Getters
    public String getEstadoId() {
        return estadoId;
    }

    public String getCidade() {
        return cidade;
    }

    @Override
    public String toString() {
        return cidade; // Para exibir a cidade no JComboBox
    }
}
