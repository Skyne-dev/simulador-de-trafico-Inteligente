
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Semaforo.java - Sistema de semáforos para el cruce
package com.mycompany.mavenproject8;

class Semaforo {
    public enum Estado {
        VERDE, AMARILLO, ROJO
    }

    private String direccion;
    private Estado estado;

    public Semaforo(String direccion, Estado estadoInicial) {
        this.direccion = direccion;
        this.estado = estadoInicial;
    }

    public String getDireccion() {
        return direccion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean esVerde() {
        return this.estado == Estado.VERDE;
    }

    public boolean esAmarillo() {
        return this.estado == Estado.AMARILLO;
    }

    public boolean esRojo() {
        return this.estado == Estado.ROJO;
    }
}
