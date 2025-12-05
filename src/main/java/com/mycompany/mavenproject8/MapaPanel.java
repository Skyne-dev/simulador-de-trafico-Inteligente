/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject8;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Carlos
 */
public class MapaPanel {
    
    
    public void dibujarMapa(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Mejorar la calidad del renderizado
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        dibujarEscenario(g2d);
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
            if((i<360) || (i>440)){
                g.fillRect(i, 295, 20, 5);  // Línea central horizontal
            }
        }
        
        for (int i = 0; i < 600; i += 40) {
            if((i<220) || (i>340)){
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
    
}
