/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ReglasCruce.java - Control por reglas de prioridad
package com.mycompany.mavenproject8;

import java.awt.BasicStroke;
import java.awt.Color; // Necesario para la aleatoriedad
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReglasCruce {

    // Área del cruce central
    private static final int CRUCE_X1 = 350; // Ajustado al inicio de la calle vertical
    private static final int CRUCE_X2 = 450; // Ajustado al fin de la calle vertical
    private static final int CRUCE_Y1 = 250; // Ajustado al inicio de la calle horizontal
    private static final int CRUCE_Y2 = 350; // Ajustado al fin de la calle horizontal

    // Zonas de aproximación al cruce (antes de detenerse)
    private static final int APROX_DISTANCIA = 100; // Distancia antes del cruce para aplicar reglas
    private static final int PARADA_DISTANCIA = 30; // Distancia exacta para detenerse

    // Tiempo que un vehículo "ocupa" el cruce después de pasar
    private int[] tiempoOcupacion = new int[4]; // índices: 0=norte, 1=sur, 2=este, 3=oeste
    private boolean[] cruceOcupado = new boolean[4];
    private int tiempoEspera = 0;
    private Random random = new Random(); // Para la decisión aleatoria
    private Map<Vehiculo, Boolean> decisionesTomadas = new HashMap<>();
    
    public ReglasCruce() {
        for (int i = 0; i < 4; i++) {
            tiempoOcupacion[i] = 0;
            cruceOcupado[i] = false;
        }
    }

  

    // Determinar si un vehículo está en la zona de influencia del cruce
    public boolean estaEnZonaDeReglas(Vehiculo vehiculo) {
        int x = vehiculo.getX();
        int y = vehiculo.getY();
        String direccion = vehiculo.getDireccion();
        int ancho = vehiculo.getAncho();
        int alto = vehiculo.getAlto();

        switch (direccion) {
            case "este":
                // Desde la izquierda, si está cerca del borde izquierdo del cruce (350)
                return x >= (CRUCE_X1 - APROX_DISTANCIA) && x < CRUCE_X2;
            case "oeste":
                // Desde la derecha, si está cerca del borde derecho del cruce (450)
                return x <= (CRUCE_X2 + APROX_DISTANCIA) && x > CRUCE_X1;
            case "norte":
                // Desde abajo, si está cerca del borde superior del cruce (250)
                return y <= (CRUCE_Y2 + APROX_DISTANCIA) && y > CRUCE_Y1;
            case "sur":
                // Desde arriba, si está cerca del borde inferior del cruce (350)
                return y >= (CRUCE_Y1 - APROX_DISTANCIA) && y < CRUCE_Y2;
        }
        return false;
    }

    // Determinar si un vehículo está **dentro** del cruce
    public boolean estaEnCruce(int x, int y, String direccion) {
        // Esta función solo debe verificar si el vehículo está físicamente DENTRO del área de intersección.

        // El vehículo se considera en el cruce si su área de colisión se intersecta con el área central (350x250 a 450x350)
        Rectangle areaCentral = new Rectangle(CRUCE_X1, CRUCE_Y1, CRUCE_X2 - CRUCE_X1, CRUCE_Y2 - CRUCE_Y1);

        // Crear un Rectangle temporal para el vehículo (aproximación, ya que no tenemos acceso al método completo sin la clase Vehiculo)
        // Usaremos las coordenadas del sprite como un aproximado para esta verificación simple.
        // Mejorar con las coordenadas reales de las calles
        int calleHorizontalY1 = 250;
        int calleHorizontalY2 = 350;
        int calleVerticalX1 = 350;
        int calleVerticalX2 = 450;

        if (direccion.equals("este") || direccion.equals("oeste")) {
            return (y + 80 >= calleHorizontalY1 && y <= calleHorizontalY2 && x >= calleVerticalX1 && x <= calleVerticalX2);
        } else {
            return (x + 80 >= calleVerticalX1 && x <= calleVerticalX2 && y >= calleHorizontalY1 && y <= calleHorizontalY2);
        }
    }

    /**
     * **NUEVO: Lógica de decisión aleatoria de giro** Al entrar en la zona de
     * reglas, decide aleatoriamente el siguiente movimiento.
     *
     * @param vehiculo El vehículo para el que se toma la decisión.
     */
    public void tomarDecisionDeGiro(Vehiculo vehiculo) {
    // Solo decidir si:
    // 1. Está en el CRUCE (no solo en zona de reglas)
    // 2. No ha tomado ya una decisión para este cruce
    
    if (decisionesTomadas.containsKey(vehiculo) && decisionesTomadas.get(vehiculo)) {
        return; // Ya tomó decisión para este cruce
    }
    
    // Solo decidir cuando está físicamente en el cruce
    if (!estaEnCruce(vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion())) {
        return;
    }
    
    // Solo decidir si todavía no ha cambiado de dirección
    if (!vehiculo.getDireccion().equals(vehiculo.getSiguienteDireccion())) {
        return;
    }
    
    String dirActual = vehiculo.getDireccion();
    String[] posiblesDirecciones = getPosiblesDirecciones(dirActual);
    
    if (posiblesDirecciones.length > 0) {
        String nuevaDir = posiblesDirecciones[random.nextInt(posiblesDirecciones.length)];
        vehiculo.setSiguienteDireccion(nuevaDir);
        decisionesTomadas.put(vehiculo, true); // Marcar que ya tomó decisión
    }
}

// Método para limpiar la decisión cuando el vehículo sale del cruce
public void limpiarDecision(Vehiculo vehiculo) {
    if (vehiculo.haPasadoElCruce()) {
        decisionesTomadas.remove(vehiculo);
        // También resetear la siguiente dirección a la actual
        vehiculo.setSiguienteDireccion(vehiculo.getDireccion());
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


    private String[] getPosiblesDirecciones(String dirActual) {
        return switch (dirActual) {
            case "este" ->
                new String[]{"este", "norte", "sur"}; // Recto, arriba, abajo
            case "oeste" ->
                new String[]{"oeste", "norte", "sur"}; // Recto, arriba, abajo
            case "norte" ->
                new String[]{"norte", "este", "oeste"}; // Recto, derecha, izquierda
            case "sur" ->
                new String[]{"sur", "este", "oeste"}; // Recto, derecha, izquierda
            default ->
                new String[]{dirActual};
        };
    }

    // Verificar si un vehículo puede pasar según las reglas
    public boolean puedePasar(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();
        int idx = getIndiceDireccion(direccion);

        // 1. Si ya está ocupando el cruce (ya entró), puede continuar
        if (estaEnCruce(vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion())) {
            return true;
        }

        // 2. Si el cruce está físicamente ocupado por un vehículo con conflicto
        if (hayConflicto(direccion)) {
            return false;
        }

        // 3. Reglas de Prioridad (simplificadas)
        // REGLA 1: El de la derecha tiene prioridad
        if (tieneDerechaLibre(direccion)) {
            return true;
        }

        // REGLA 2: Vía principal (este-oeste) tiene prioridad sobre norte-sur
        if (esViaPrincipal(direccion) && !hayVehiculoEnViaPrincipalConflicto()) {
            return true;
        }

        // REGLA 3: Si ya esperó mucho tiempo, puede pasar (para evitar deadlocks)
        if (tiempoEspera >= 240) { // Aproximadamente 4 segundos a 60 FPS
            tiempoEspera = 0;
            return true;
        }

        return false;
    }

    // Implementación más realista de la regla "de la derecha" (muy simplificada)
    private boolean tieneDerechaLibre(String direccion) {
        // En un cruce de 4 vías, el de la derecha tiene prioridad si su camino
        // de salida no está ocupado.

        String dirDerecha = switch (direccion) {
            case "este" ->
                "sur"; // La dirección a la derecha del este es el sur
            case "oeste" ->
                "norte";
            case "norte" ->
                "este";
            case "sur" ->
                "oeste";
            default ->
                "";
        };

        if (dirDerecha.isEmpty()) {
            return false;
        }

        int idxDerecha = getIndiceDireccion(dirDerecha);
        return !cruceOcupado[idxDerecha]; // Si la dirección a la derecha no está ocupada, se asume que tengo la prioridad
    }

    private boolean hayVehiculoEnViaPrincipalConflicto() {
        // Verifica si un vehículo N/S está tratando de pasar (o al revés)
        return (cruceOcupado[2] || cruceOcupado[3]) && (cruceOcupado[0] || cruceOcupado[1]);
    }

    // Registrar que un vehículo está ocupando el cruce
    public void ocuparCruce(String direccion) {
        int idx = getIndiceDireccion(direccion);
        cruceOcupado[idx] = true;
        tiempoOcupacion[idx] = 60; // 1 segundo de ocupación (para que no lo detenga inmediatamente después de pasar)

        // Reiniciar espera cuando alguien pasa
        tiempoEspera = 0;
    }

    // Liberar el cruce si el vehículo sale
    public void liberarCruceSiSale(Vehiculo vehiculo) {
        // La liberación se basará en si el vehículo ha pasado completamente el cruce, usando el nuevo método en Vehiculo
        if (vehiculo.haPasadoElCruce()) {
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
        return switch (direccion) {
            case "norte" ->
                0;
            case "sur" ->
                1;
            case "este" ->
                2;
            case "oeste" ->
                3;
            default ->
                -1;
        };
    }

    private String getDireccionPorIndice(int idx) {
        return switch (idx) {
            case 0 ->
                "norte";
            case 1 ->
                "sur";
            case 2 ->
                "este";
            case 3 ->
                "oeste";
            default ->
                "";
        };
    }

    private boolean seCruzan(String dir1, String dir2) {
        // Direcciones que se cruzan (ej: norte vs este, sur vs oeste)
        if (dir1.equals("norte") && (dir2.equals("este") || dir2.equals("oeste"))) {
            return true;
        }
        if (dir1.equals("sur") && (dir2.equals("este") || dir2.equals("oeste"))) {
            return true;
        }
        if (dir1.equals("este") && (dir2.equals("norte") || dir2.equals("sur"))) {
            return true;
        }
        if (dir1.equals("oeste") && (dir2.equals("norte") || dir2.equals("sur"))) {
            return true;
        }
        return false;
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
        g.drawString("1. Evitar conflictos de cruce", 610, y + 25);
        g.drawString("2. El de la derecha tiene prioridad (Simplificado)", 610, y + 40);
        g.drawString("3. Via principal (E-O) tiene prioridad", 610, y + 55);
        g.drawString("4. Máximo 4s de espera (Anti-Deadlock)", 610, y + 70);
    }
}
