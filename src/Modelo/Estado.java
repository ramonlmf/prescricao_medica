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
public class Estado {
    private String id;
    private String estado;

    // Construtores
    public Estado(String id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return estado; // Para exibir o nome do estado no JComboBox
    }
}