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
    private String siguienteDireccion; // Nueva propiedad para la dirección al salir del cruce**
    private int x, y;
    private int velocidad;
    private int ancho = 80;  // Ancho del sprite
    private int alto = 80;   // Alto del sprite
    private int tiempoRecuperacion = 0;
    
    public Vehiculo(String tipo, String direccion, int x, int y) {
        this.tipo = tipo;
        this.direccion = direccion;
        this.  siguienteDireccion = direccion; // Inicialmente sigue recto**
        this.x = x;
        this.y = y;
        this.velocidad = 2 + (int) (Math.random() * 3);
    }

     public void resetearGiro() {
        this.siguienteDireccion = this.direccion;
    }
     
    public void mover() {
     if (tiempoRecuperacion > 0) {
            return;
        }
        
        switch (direccion) {
            case "este":
                x += velocidad;
                break;
            case "oeste":
                x -= velocidad;
                break;
            case "norte":
                y -= velocidad;
                break;
            case "sur":
                y += velocidad;
                break;
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
    
    public Rectangle getAreaColisionFutura() {
    int futuroX = x;
    int futuroY = y;
    
    // Calcular posición futura según velocidad y dirección
    switch (direccion) {
        case "este":
            futuroX += velocidad;
            break;
        case "oeste":
            futuroX -= velocidad;
            break;
        case "norte":
            futuroY -= velocidad;
            break;
        case "sur":
            futuroY += velocidad;
            break;
    }
    
    // Calcular área de colisión futura
    return calcularAreaColision(futuroX, futuroY);
}

/**
 * Calcula el área de colisión en una posición específica
 */
private Rectangle calcularAreaColision(int posX, int posY) {
    int colisionAncho, colisionAlto;
    int offsetX, offsetY;

    if (direccion.equals("norte") || direccion.equals("sur")) {
        // Para norte/sur: 75x90 (más alto que ancho)
        colisionAncho = 50;
        colisionAlto = 70;
        offsetX = (ancho - colisionAncho) / 2;
        offsetY = (alto - colisionAlto) / 4;
    } else {
        // Para este/oeste: 90x75 (más ancho que alto)
        colisionAncho = 70;
        colisionAlto = 50;
        offsetX = (ancho - colisionAncho) / 4;
        offsetY = (alto - colisionAlto) / 2;
    }

    return new Rectangle(posX + offsetX, posY + offsetY, colisionAncho, colisionAlto);
}

/**
 * Obtiene el "punto frontal" del vehículo (parte que choca primero)
 */
public int[] getPuntoFrontal() {
    int frontalX = x + ancho / 2;
    int frontalY = y + alto / 2;
    
    switch (direccion) {
        case "este":
            frontalX = x + ancho; // Parte derecha
            frontalY = y + alto / 2; // Centro vertical
            break;
        case "oeste":
            frontalX = x; // Parte izquierda
            frontalY = y + alto / 2;
            break;
        case "norte":
            frontalX = x + ancho / 2;
            frontalY = y; // Parte superior
            break;
        case "sur":
            frontalX = x + ancho / 2;
            frontalY = y + alto; // Parte inferior
            break;
    }
    
    return new int[]{frontalX, frontalY};
}

     
    public boolean haPasadoElCruce() {
        final int limiteSalida = 480; // Coordinada de salida del cruce

        switch (direccion) {
            case "este":
                return x > limiteSalida; // Sale a la derecha (450 + 30)
            case "oeste":
                return x < (800 - limiteSalida) - ancho; // Sale a la izquierda (350 - 30 - ancho)
            case "sur":
                return y > limiteSalida; // Sale abajo (450 + 30)
            case "norte":
                return y < (600 - limiteSalida) - alto; // Sale arriba (250 - 30 - alto)
            default:
                return false;
        }
    }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public String getSiguienteDireccion() {
        return siguienteDireccion;
    }

    public void setSiguienteDireccion(String siguienteDireccion) {
        this.siguienteDireccion = siguienteDireccion;
    }
    
    public void actualizarRecuperacion() {
        if (tiempoRecuperacion > 0) {
            tiempoRecuperacion--;
        }
    }
     public boolean puedeMoverse() {
        return tiempoRecuperacion == 0 && velocidad > 0;
    }
       // Getters y Setters para tiempoRecuperacion
    public int getTiempoRecuperacion() {
        return tiempoRecuperacion;
    }
    
    public void setTiempoRecuperacion(int tiempo) {
        this.tiempoRecuperacion = tiempo;
    }

}
