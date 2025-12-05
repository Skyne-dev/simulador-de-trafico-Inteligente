/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ReglasCruce.java - Control por reglas de prioridad
package com.mycompany.mavenproject8;

import java.awt.*;

public class ReglasCruce {
    // Área del cruce central
    private static final int CRUCE_X1 = 320;
    private static final int CRUCE_X2 = 480;
    private static final int CRUCE_Y1 = 220;
    private static final int CRUCE_Y2 = 380;
    
    // Tiempo que un vehículo "ocupa" el cruce después de pasar
    private int[] tiempoOcupacion = new int[4]; // índices: 0=norte, 1=sur, 2=este, 3=oeste
    private boolean[] cruceOcupado = new boolean[4];
    private int tiempoEspera = 0;
    
    public ReglasCruce() {
        for (int i = 0; i < 4; i++) {
            tiempoOcupacion[i] = 0;
            cruceOcupado[i] = false;
        }
    }
    
    public void actualizar() {
        // Reducir tiempo de ocupación
        for (int i = 0; i < 4; i++) {
            if (tiempoOcupacion[i] > 0) {
                tiempoOcupacion[i]--;
                if (tiempoOcupacion[i] == 0) {
                    cruceOcupado[i] = false;
                }
            }
        }
        
        if (tiempoEspera > 0) {
            tiempoEspera--;
        }
    }
    
    // Determinar si un vehículo está en la zona del cruce
    public boolean estaEnCruce(int x, int y, String direccion) {
        switch(direccion) {
            case "este":
                return x >= CRUCE_X1-80 && x <= CRUCE_X2 && y >= 240 && y <= 360;
            case "oeste":
                return x >= CRUCE_X1  && x <= CRUCE_X2 && y >= 240 && y <= 360;
            case "norte":
                return y >= CRUCE_Y1 && y <= CRUCE_Y2 && x >= 340 && x <= 460;
            case "sur":
                return y >= CRUCE_Y1-80 && y <= CRUCE_Y2 && x >= 340 && x <= 460;
        }
        return false;
    }
    
    // Verificar si un vehículo puede pasar según las reglas
    public boolean puedePasar(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();
        int idx = getIndiceDireccion(direccion);
        
        // Si ya está ocupando el cruce, puede continuar
        if (cruceOcupado[idx]) {
            return true;
        }
        
        // Verificar si hay conflicto con otras direcciones
        for (int i = 0; i < 4; i++) {
            if (i != idx && cruceOcupado[i]) {
                // Direcciones que se cruzan
                if (seCruzan(direccion, getDireccionPorIndice(i))) {
                    return false; // Hay conflicto, debe esperar
                }
            }
        }
        
        // REGLA 1: El que llega primero tiene prioridad
        // (implementado por tiempo de ocupación)
        
        // REGLA 2: El de la derecha tiene prioridad
        if (tieneDerecha(direccion)) {
            return true; // Tiene prioridad por estar a la derecha
        }
        
        // REGLA 3: Si viene por la vía principal (este-oeste) tiene prioridad
        if (esViaPrincipal(direccion)) {
            return true;
        }
        
        // REGLA 4: Si ya esperó mucho tiempo, puede pasar
        if (tiempoEspera >= 180) { // 3 segundos
            tiempoEspera = 0;
            return true;
        }
        
        // Por defecto, no puede pasar si hay conflicto
        return !hayConflicto(direccion);
    }
    
    // Registrar que un vehículo está ocupando el cruce
    public void ocuparCruce(String direccion) {
        int idx = getIndiceDireccion(direccion);
        cruceOcupado[idx] = true;
        tiempoOcupacion[idx] = 60; // 1 segundo de ocupación
        
        // Reiniciar espera cuando alguien pasa
        tiempoEspera = 0;
    }
    
    // Liberar el cruce si el vehículo sale
    public void liberarCruceSiSale(Vehiculo vehiculo) {
        if (!estaEnCruce(vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion())) {
            int idx = getIndiceDireccion(vehiculo.getDireccion());
            if (cruceOcupado[idx]) {
                cruceOcupado[idx] = false;
                tiempoOcupacion[idx] = 0;
            }
        }
    }
    
    // Incrementar tiempo de espera
    public void incrementarEspera() {
        tiempoEspera++;
    }
    
    // Métodos auxiliares
    private int getIndiceDireccion(String direccion) {
        switch(direccion) {
            case "norte": return 0;
            case "sur": return 1;
            case "este": return 2;
            case "oeste": return 3;
            default: return -1;
        }
    }
    
    private String getDireccionPorIndice(int idx) {
        switch(idx) {
            case 0: return "norte";
            case 1: return "sur";
            case 2: return "este";
            case 3: return "oeste";
            default: return "";
        }
    }
    
    private boolean seCruzan(String dir1, String dir2) {
        // Direcciones que no se cruzan: misma dirección o sentido contrario
        if (dir1.equals(dir2)) return false;
        if ((dir1.equals("norte") && dir2.equals("sur")) ||
            (dir1.equals("sur") && dir2.equals("norte")) ||
            (dir1.equals("este") && dir2.equals("oeste")) ||
            (dir1.equals("oeste") && dir2.equals("este"))) {
            return false;
        }
        return true; // Se cruzan (ej: norte vs este)
    }
    
    private boolean tieneDerecha(String direccion) {
        // Suponemos que todos vienen por la derecha en esta simulación
        // En realidad habría que verificar posiciones relativas
        return true; // Simplificado
    }
    
    private boolean esViaPrincipal(String direccion) {
        // Consideramos este-oeste como vía principal
        return direccion.equals("este") || direccion.equals("oeste");
    }
    
    private boolean hayConflicto(String direccion) {
        int idx = getIndiceDireccion(direccion);
        for (int i = 0; i < 4; i++) {
            if (i != idx && cruceOcupado[i] && seCruzan(direccion, getDireccionPorIndice(i))) {
                return true;
            }
        }
        return false;
    }
    
    // Dibujar información del cruce
    public void dibujar(Graphics2D g) {
        // Dibujar zona del cruce
        g.setColor(new Color(255, 255, 0, 50));
        g.fillRect(CRUCE_X1, CRUCE_Y1, CRUCE_X2 - CRUCE_X1, CRUCE_Y2 - CRUCE_Y1);
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));
        g.drawRect(CRUCE_X1, CRUCE_Y1, CRUCE_X2 - CRUCE_X1, CRUCE_Y2 - CRUCE_Y1);
        
        // Dibujar información de ocupación
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("REGLAS DE CRUCE", 600, 120);
        
        // Mostrar estado de cada dirección
        int y = 140;
        for (int i = 0; i < 4; i++) {
            String dir = getDireccionPorIndice(i);
            String estado = cruceOcupado[i] ? "OCUPADO" : "LIBRE";
            Color color = cruceOcupado[i] ? Color.RED : Color.GREEN;
            
            g.setColor(color);
            g.drawString(dir.toUpperCase() + ": " + estado, 600, y);
            y += 20;
        }
        
        // Dibujar reglas aplicadas
        g.setColor(Color.YELLOW);
        g.drawString("Reglas:", 600, y + 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("1. El primero en llegar pasa", 610, y + 25);
        g.drawString("2. El de la derecha tiene prioridad", 610, y + 40);
        g.drawString("3. Via principal (E-O) tiene prioridad", 610, y + 55);
        g.drawString("4. Máximo 3s de espera", 610, y + 70);
    }
}
