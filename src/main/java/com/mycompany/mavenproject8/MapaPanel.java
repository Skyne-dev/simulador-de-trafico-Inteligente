/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject8;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

/**
 *
 * @author orlando
 * @author Samuel
 */
public class MapaPanel {

    private static final int LUZ_SIZE = 8;
    // Variable para acceder a los estados de los semáforos
    private ReglasCruce reglasCruce;

    /**
     * Setter para inyectar la dependencia de ReglasCruce desde Mavenproject8.
     *
     * @param reglasCruce
     */
    public void setReglasCruce(ReglasCruce reglasCruce) {
        this.reglasCruce = reglasCruce;
    }

    public void dibujarMapa(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Mejorar la calidad del renderizado
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        dibujarEscenario(g2d);

        // Llamar al nuevo método para dibujar los semáforos
        if (reglasCruce != null) {
            dibujarSemaforos(g2d);
        }
    }

    private void dibujarEscenario(Graphics2D g) {
        // Fondo del cielo
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, 800, 600);

        // Calles principales
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 250, 800, 100);  // Calle horizontal
        g.fillRect(350, 0, 100, 600);  // Calle vertical

        // Aceras
        g.setColor(new Color(150, 150, 150));
        g.fillRect(0, 240, 350, 10);   // Acera superior
        g.fillRect(450, 240, 800, 10);   // Acera superior
        g.fillRect(0, 350, 350, 10);   // Acera inferior
        g.fillRect(450, 350, 800, 10);   // Acera inferior
        g.fillRect(340, 0, 10, 240);   // Acera izquierda
        g.fillRect(340, 350, 10, 600);   // Acera izquierda
        g.fillRect(450, 0, 10, 240);   // Acera derecha
        g.fillRect(450, 350, 10, 600);   // Acera derecha

        // Césped en esquinas
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 0, 340, 240);    // Esquina superior izquierda
        g.fillRect(460, 0, 340, 240);  // Esquina superior derecha
        g.fillRect(0, 360, 340, 240);  // Esquina inferior izquierda
        g.fillRect(460, 360, 340, 240);// Esquina inferior derecha

        dibujarMarcasViales(g);
        dibujarEdificios(g);
        dibujarArboles(g);
    }

    private void dibujarMarcasViales(Graphics2D g) {
        g.setColor(Color.WHITE);

        // Líneas discontinuas en calles
        for (int i = 0; i < 800; i += 40) {
            if ((i < 360) || (i > 440)) {
                g.fillRect(i, 295, 20, 5);  // Línea central horizontal
            }
        }

        for (int i = 0; i < 600; i += 40) {
            if ((i < 220) || (i > 340)) {
                g.fillRect(395, i, 5, 20);  // Línea central vertical
            }
        }
        // Pasos de peatones
        g.setColor(Color.WHITE);
        for (int i = 1; i < 7; i++) {
            // Norte
            g.fillRect(345 + i * 15, 240, 5, 10);
            // Sur
            g.fillRect(345 + i * 15, 350, 5, 10);
            // Este
            g.fillRect(450, 245 + i * 15, 10, 5);
            // Oeste
            g.fillRect(340, 245 + i * 15, 10, 5);
        }
    }

    private void dibujarEdificios(Graphics2D g) {
        // Edificio noroeste
        g.setColor(new Color(180, 160, 140));
        g.fillRect(50, 50, 200, 150);
        g.setColor(Color.YELLOW);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                g.fillRect(70 + i * 40, 70 + j * 40, 15, 20);
            }
        }

        // Edificio noreste
        g.setColor(new Color(160, 140, 120));
        g.fillRect(550, 50, 200, 150);
        g.setColor(Color.ORANGE);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                g.fillRect(570 + i * 40, 70 + j * 40, 15, 20);
            }
        }

        // Edificio suroeste
        g.setColor(new Color(140, 120, 100));
        g.fillRect(50, 400, 200, 150);
        g.setColor(Color.CYAN);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                g.fillRect(70 + i * 40, 420 + j * 40, 15, 20);
            }
        }

        // Edificio sureste
        g.setColor(new Color(120, 100, 80));
        g.fillRect(550, 400, 200, 150);
        g.setColor(Color.PINK);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                g.fillRect(570 + i * 40, 420 + j * 40, 15, 20);
            }
        }
    }

    private void dibujarArboles(Graphics2D g) {
        // Árboles en las esquinas
        dibujarArbol(g, 100, 100);
        dibujarArbol(g, 700, 100);
        dibujarArbol(g, 100, 450);
        dibujarArbol(g, 700, 450);
        dibujarArbol(g, 200, 150);
        dibujarArbol(g, 600, 150);
        dibujarArbol(g, 200, 400);
        dibujarArbol(g, 600, 400);
    }

    private void dibujarArbol(Graphics2D g, int x, int y) {
        // Tronco
        g.setColor(new Color(101, 67, 33));
        g.fillRect(x, y, 8, 20);

        // Copa del árbol
        g.setColor(new Color(34, 139, 34));
        g.fillOval(x - 10, y - 15, 28, 25);
    }

    /**
     * Dibuja los semáforos en las cuatro esquinas del cruce.
     */
    private void dibujarSemaforos(Graphics2D g) {
        if (reglasCruce == null) {
            return;
        }

        // Obtener el mapa de semáforos
        Map<String, Semaforo> semaforos = reglasCruce.getSemaforos();

        g.setStroke(new BasicStroke(2));

        // TODOS los semáforos en posición VERTICAL estándar
        // (Rojo arriba, Amarillo medio, Verde abajo)
        // 1. SEMÁFORO ESTE (esquina noreste) - controla tráfico ESTE
        Semaforo semaforoEste = semaforos.get("este");
        if (semaforoEste != null) {
            int xEste = 460;  // Esquina noreste
            int yEste = 240;  // Al nivel de la acera superior
            dibujarSemaforoVertical(g, xEste, yEste, semaforoEste.getEstado(), "ESTE");
        }

        // 2. SEMÁFORO OESTE (esquina suroeste) - controla tráfico OESTE
        Semaforo semaforoOeste = semaforos.get("oeste");
        if (semaforoOeste != null) {
            int xOeste = 340;  // Esquina suroeste
            int yOeste = 360;  // Al nivel de la acera inferior
            dibujarSemaforoVertical(g, xOeste, yOeste, semaforoOeste.getEstado(), "OESTE");
        }

        // 3. SEMÁFORO NORTE (esquina noroeste) - controla tráfico NORTE
        Semaforo semaforoNorte = semaforos.get("norte");
        if (semaforoNorte != null) {
            int xNorte = 340;  // Esquina noroeste
            int yNorte = 240;  // Al nivel de la acera superior
            dibujarSemaforoVertical(g, xNorte, yNorte, semaforoNorte.getEstado(), "NORTE");
        }

        // 4. SEMÁFORO SUR (esquina sureste) - controla tráfico SUR
        Semaforo semaforoS = semaforos.get("sur");
        if (semaforoS != null) {
            int xSur = 460;    // Esquina sureste
            int ySur = 360;    // Al nivel de la acera inferior
            dibujarSemaforoVertical(g, xSur, ySur, semaforoS.getEstado(), "SUR");
        }
    }

    /**
     * Dibuja un semáforo vertical estandarizado
     */
    private void dibujarSemaforoVertical(Graphics2D g, int x, int y,
            Semaforo.Estado estado, String etiqueta) {
        // Poste del semáforo
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 6, y - 20, 3, 20);  // Poste que sube desde el semáforo

        // Cuerpo del semáforo
        g.setColor(Color.BLACK);
        g.fillRect(x, y, 15, 40);

        // Marco del semáforo (para mejor visibilidad)
        g.setColor(Color.GRAY);
        g.drawRect(x, y, 15, 40);

        // Dibujar luces (vertical estándar: Rojo arriba, Amarillo medio, Verde abajo)
        dibujarLuz(g, x + 4, y + 4, estado, Semaforo.Estado.ROJO);
        dibujarLuz(g, x + 4, y + 17, estado, Semaforo.Estado.AMARILLO);
        dibujarLuz(g, x + 4, y + 30, estado, Semaforo.Estado.VERDE);

        // Etiqueta del semáforo (dirección que controla)
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString(etiqueta, x - 10, y - 25);
    }

    /**
     * Función auxiliar para dibujar un círculo de luz mejorado
     */
    private void dibujarLuz(Graphics2D g, int x, int y,
            Semaforo.Estado estadoActual, Semaforo.Estado estadoLuz) {
        int size = LUZ_SIZE;

        // Luz encendida
        if (estadoActual == estadoLuz) {
            if (estadoLuz == Semaforo.Estado.ROJO) {
                g.setColor(Color.RED);
                g.fillOval(x, y, size, size);
                // Brillo interior para efecto 3D
                g.setColor(new Color(240, 0, 38));
                g.fillOval(x + 1, y + 1, size - 2, size - 2);

            } else if (estadoLuz == Semaforo.Estado.AMARILLO) {
                g.setColor(Color.YELLOW);
                g.fillOval(x, y, size, size);
                // Brillo interior para efecto 3D
                g.setColor(new Color(236, 240, 0));
                g.fillOval(x + 1, y + 1, size - 2, size - 2);

            } else if (estadoLuz == Semaforo.Estado.VERDE) {
                g.setColor(Color.GREEN);
                g.fillOval(x, y, size, size);
                // Brillo interior para efecto 3D
                g.setColor(new Color(16, 240, 0));
                g.fillOval(x + 1, y + 1, size - 2, size - 2);
            }

            // Borde blanco para luz encendida (más visible)
            g.setColor(Color.WHITE);
            g.drawOval(x, y, size, size);

        } // Luz apagada
        else {
            // Círculo gris oscuro
            g.setColor(new Color(40, 40, 40));
            g.fillOval(x, y, size, size);

            // Borde gris
            g.setColor(Color.DARK_GRAY);
            g.drawOval(x, y, size, size);

            // Pequeño punto gris claro en el centro para mostrar que está apagado
            g.setColor(new Color(80, 80, 80));
            g.fillOval(x + 2, y + 2, size - 4, size - 4);
        }
    }
}
