/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject8;
import java.awt.Rectangle;
/**
 *
 * @author Carlos
 */
// Clase para representar un vehículo

// Vehiculo.java actualizada




class Vehiculo {
    private String tipo;
    private String direccion;
    private int x, y;
    private int velocidad;
    private int ancho = 80;  // Ancho del sprite
    private int alto = 80;   // Alto del sprite
    
    public Vehiculo(String tipo, String direccion, int x, int y) {
        this.tipo = tipo;
        this.direccion = direccion;
        this.x = x;
        this.y = y;
        this.velocidad = 2 + (int)(Math.random() * 3);
    }
    
    public void mover() {
        switch(direccion) {
            case "este": x += velocidad; break;
            case "oeste": x -= velocidad; break;
            case "norte": y -= velocidad; break;
            case "sur": y += velocidad; break;
        }
    }
    
    // Método para obtener el área de colisión (bounding box)
     // Método para obtener el área de colisión según la dirección
    public Rectangle getAreaColision() {
        int colisionAncho, colisionAlto;
        int offsetX, offsetY;
        
        if (direccion.equals("norte") || direccion.equals("sur")) {
            // Para norte/sur: 75x90 (más alto que ancho)
            colisionAncho = 50;
            colisionAlto = 70;
            offsetX = (ancho - colisionAncho) / 2;  // Centrar horizontalmente
            offsetY = (alto - colisionAlto) / 4;    // Un poco más arriba/abajo
        } else {
            // Para este/oeste: 90x75 (más ancho que alto)
            colisionAncho = 70;
            colisionAlto = 50;
            offsetX = (ancho - colisionAncho) / 4;  // Un poco más a los lados
            offsetY = (alto - colisionAlto) / 2;    // Centrar verticalmente
        }
        
        return new Rectangle(x + offsetX, y + offsetY, colisionAncho, colisionAlto);
    }
    
   // Obtener las dimensiones reales del sprite
    public Rectangle getAreaCompleta() {
        return new Rectangle(x, y, ancho, alto);
    }
    
    // Verificar colisión con otro vehículo
    public boolean colisionaCon(Vehiculo otro) {
        return this.getAreaColision().intersects(otro.getAreaColision());
    }
    
    // Getters y Setters
    public String getTipo() { return tipo; }
    public String getDireccion() { return direccion; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
    public int getVelocidad() { return velocidad; }
}