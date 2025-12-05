/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */


//Version prueba
// Mavenproject8.java - Solo reglas de prioridad
package com.mycompany.mavenproject8;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Mavenproject8 extends JPanel implements ActionListener {
    private MapaPanel mapaPanel;
    private Timer timer;
    private Map<String, Image> sprites = new HashMap<>();
    private List<Vehiculo> vehiculos = new ArrayList<>();
    private List<Vehiculo> colisionesActivas = new ArrayList<>();
    private ReglasCruce reglasCruce;
    private Random random = new Random();
    private boolean mostrarAreasColision = false;
    private boolean reglasActivadas = true;
    private int vehiculosAgregados = 0;
    
    private File getProjectFolder() {
        try {
            // Intento 1: Buscar desde la ubicación de la clase compilada
            URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
            File classPath = new File(url.toURI());
            
            // Si estamos en NetBeans, subir hasta la raíz del proyecto
            File currentDir = classPath;
            while (currentDir != null) {
                // Buscar carpeta "src" o "build"
                if (currentDir.getName().equals("build") || currentDir.getName().equals("src")) {
                    return currentDir.getParentFile();
                }
                currentDir = currentDir.getParentFile();
            }
            
            // Si no se encuentra, usar el directorio de trabajo actual
            return new File(".");
            
        } catch (Exception e) {
            System.out.println("Error buscando carpeta del proyecto: " + e.getMessage());
            return new File(".");
        }
    }
    
    public void cargarSprites() {
      String[] nombresSprites = {"verde", "camioneta", "rojo", "rosado"};
        String[] archivosSprites = {"verde.png", "camioneta.png", "rojo.png", "rosa.png"};
        
        String[] puntosCardinales = {"norte", "sur", "este", "oeste"};
        int[][] coordenadas = {
            {0, 200},   // norte
            {200, 0},   // sur  
            {0, 0},     // este
            {100, 100}  // oeste
        };
        
        // Obtener carpeta del proyecto
        File proyectoDir = getProjectFolder();
        System.out.println("Carpeta del proyecto: " + proyectoDir.getAbsolutePath());
        
        // Intentar diferentes ubicaciones posibles para los sprites
        String[] posiblesRutas = {
            proyectoDir.getAbsolutePath() + File.separator + "sprites",
            proyectoDir.getAbsolutePath() + File.separator + "src" + File.separator + "sprites",
            proyectoDir.getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator + "mycompany" + File.separator + "sprites",
            proyectoDir.getAbsolutePath() + File.separator + "resources",
            proyectoDir.getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "resources"
        };
        
        String carpetaSprites = encontrarCarpetaSprites(posiblesRutas);
        
        if (carpetaSprites == null) {
            System.err.println("ERROR: No se encontró la carpeta de sprites");
            System.err.println("Creando sprites de emergencia...");
            
            return;
        }
        
        System.out.println("Carpeta de sprites encontrada: " + carpetaSprites);
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String nombre = nombresSprites[i] + puntosCardinales[j];
                String rutaCompleta = carpetaSprites + File.separator + archivosSprites[i];
                
                System.out.println("Intentando cargar: " + rutaCompleta);
                cargarSpriteDeArchivo(nombre, rutaCompleta, coordenadas[j][0], coordenadas[j][1], 100, 100);
            }
        }
        
        // Verificar que se cargaron sprites
        if (sprites.isEmpty()) {
            System.err.println("ADVERTENCIA: No se pudieron cargar sprites de archivos");
         
        } else {
            System.out.println("Sprites cargados exitosamente: " + sprites.size());
        }
    }
    
    // Buscar carpeta de sprites en diferentes ubicaciones
    private String encontrarCarpetaSprites(String[] rutas) {
        for (String ruta : rutas) {
            File carpeta = new File(ruta);
            if (carpeta.exists() && carpeta.isDirectory()) {
                // Verificar que haya al menos un archivo PNG
                File[] archivos = carpeta.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".png"));
                if (archivos != null && archivos.length > 0) {
                    return ruta;
                }
            }
        }
        return null;
    }
    
    
     private void cargarSpriteDeArchivo(String nombre, String ruta, int x, int y, int ancho, int alto) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                System.err.println("Archivo no encontrado: " + ruta);
                return;
            }
            
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage();
            
            BufferedImage bufferedImage = new BufferedImage(
                imagen.getWidth(null), 
                imagen.getHeight(null), 
                BufferedImage.TYPE_INT_ARGB
            );
            
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(imagen, 0, 0, null);
            g2d.dispose();
           
            sprites.put(nombre, bufferedImage.getSubimage(x, y, ancho, alto));
            System.out.println("Sprite cargado: " + nombre + " desde " + ruta);
            
        } catch (Exception e) {
            System.err.println("Error cargando sprite " + nombre + " desde " + ruta + ": " + e.getMessage());
        }
    }
    
    private void generarVehiculosAutomaticos(int cantidad) {
        String[] tiposVehiculos = {"camioneta", "verde", "rojo", "rosado"};
        
        for (int i = 0; i < cantidad; i++) {
            String tipo = tiposVehiculos[random.nextInt(tiposVehiculos.length)];
            String direccion = generarDireccionAleatoria();
            int[] posicion = generarPosicionInicial(direccion);
            
            Vehiculo nuevoVehiculo = new Vehiculo(tipo, direccion, posicion[0], posicion[1]);
            
            if (!hayColisionInicial(nuevoVehiculo)) {
                vehiculos.add(nuevoVehiculo);
                vehiculosAgregados++;
            }
        }
    }
    
    private boolean hayColisionInicial(Vehiculo nuevo) {
        for (Vehiculo existente : vehiculos) {
            if (nuevo.colisionaCon(existente)) {
                return true;
            }
        }
        return false;
    }
    
    private String generarDireccionAleatoria() {
        String[] direcciones = {"este", "oeste", "norte", "sur"};
        return direcciones[random.nextInt(direcciones.length)];
    }
    
    private int[] generarPosicionInicial(String direccion) {
        int x = 0, y = 0;
        
        switch(direccion) {
            case "este":
                x = -100;
                y = 270;
                break;
            case "oeste":
                x = 800;
                y = 215;
                break;
            case "norte":
                x = 375;
                y = 600;
                break;
            case "sur":
                x = 322;
                y = -100;
                break;
        }
        
        return new int[]{x, y};
    }
    
    // Manejar reglas del cruce para un vehículo
    private void manejarReglasCruce(Vehiculo vehiculo) {
        if (!reglasActivadas) return;
        
        boolean enCruce = reglasCruce.estaEnCruce(
            vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion());
        
        if (enCruce) {
            // Verificar si puede pasar según las reglas
            boolean puedePasar = reglasCruce.puedePasar(vehiculo);
            
            if (!puedePasar) {
                // Detener y esperar
                vehiculo.setVelocidad(0);
                reglasCruce.incrementarEspera();
                return;
            } else {
                // Ocupar el cruce
                reglasCruce.ocuparCruce(vehiculo.getDireccion());
            }
        } else {
            // Liberar el cruce si sale de él
            reglasCruce.liberarCruceSiSale(vehiculo);
        }
        
        // Si puede pasar o no está en el cruce, mantener velocidad normal
        if (vehiculo.getVelocidad() == 0) {
            vehiculo.setVelocidad(2 + (int)(Math.random() * 3));
        }
    }
    
    // Detectar colisiones físicas
    private void detectarColisiones() {
        colisionesActivas.clear();
        
        for (int i = 0; i < vehiculos.size(); i++) {
            Vehiculo v1 = vehiculos.get(i);
            
            for (int j = i + 1; j < vehiculos.size(); j++) {
                Vehiculo v2 = vehiculos.get(j);
                
                if (v1.colisionaCon(v2)) {
                    if (!colisionesActivas.contains(v1)) colisionesActivas.add(v1);
                    if (!colisionesActivas.contains(v2)) colisionesActivas.add(v2);
                    
                    manejarColision(v1, v2);
                }
            }
            
            // Aplicar reglas del cruce
            manejarReglasCruce(v1);
        }
    }
    
    private void manejarColision(Vehiculo v1, Vehiculo v2) {
        v1.setVelocidad(0);
        v2.setVelocidad(0);
        retrocederVehiculo(v1);
        retrocederVehiculo(v2);
    }
    
    private void retrocederVehiculo(Vehiculo v) {
        int retroceso = 8;
        switch(v.getDireccion()) {
            case "este": v.setX(v.getX() - retroceso); break;
            case "oeste": v.setX(v.getX() + retroceso); break;
            case "norte": v.setY(v.getY() + retroceso); break;
            case "sur": v.setY(v.getY() - retroceso); break;
        }
    }
    
    
    public Mavenproject8() {
        // Inicializar componentes
         mapaPanel = new MapaPanel();
        reglasCruce = new ReglasCruce();
        generarVehiculosAutomaticos(12); // Más vehículos iniciales
        
        cargarSprites();
        
        // Timer para animación (60 FPS)
        timer = new Timer(16, this);
        timer.start();
        
        // Configurar controles del teclado
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_A -> generarVehiculosAutomaticos(3);
                    case KeyEvent.VK_C -> {
                        vehiculos.clear();
                        colisionesActivas.clear();
                        vehiculosAgregados = 0;
                        reglasCruce = new ReglasCruce();
                    }
                    case KeyEvent.VK_H -> mostrarAreasColision = !mostrarAreasColision;
                    case KeyEvent.VK_S -> reglasActivadas = !reglasActivadas;
                    case KeyEvent.VK_SPACE -> reglasCruce = new ReglasCruce();
                    case KeyEvent.VK_R -> {
                        // Resetear un vehículo en colisión
                        if (!colisionesActivas.isEmpty()) {
                            Vehiculo v = colisionesActivas.get(0);
                            reposicionarVehiculo(v);
                            colisionesActivas.remove(v);
                        }
                    }
                    case KeyEvent.VK_P -> {
                        // Pausar/reanudar simulación
                        if (timer.isRunning()) {
                            timer.stop();
                        } else {
                            timer.start();
                        }
                    }
                }
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar mapa
        mapaPanel.dibujarMapa(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Dibujar información del cruce
        reglasCruce.dibujar(g2d);
        
        // Dibujar todos los vehículos automáticos
        for (Vehiculo vehiculo : vehiculos) {
            Image spriteVehiculo = sprites.get(vehiculo.getTipo() + vehiculo.getDireccion());
            if (spriteVehiculo != null) {
                // Efecto visual según estado
                boolean enCruce = reglasCruce.estaEnCruce(
                    vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion());
                boolean esperando = enCruce && vehiculo.getVelocidad() == 0;
                boolean enColision = colisionesActivas.contains(vehiculo);
                
                // Aplicar efectos visuales
                if (esperando) {
                    // Efecto naranja para vehículos esperando
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2d.setColor(new Color(255, 165, 0, 100));
                    g2d.fillRect(vehiculo.getX(), vehiculo.getY(), 100, 100);
                } else if (enColision) {
                    // Efecto rojo para vehículos en colisión
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.setColor(new Color(255, 0, 0, 150));
                    g2d.fillRect(vehiculo.getX(), vehiculo.getY(), 100, 100);
                }
                
                // Dibujar sprite del vehículo
                g2d.drawImage(spriteVehiculo, vehiculo.getX(), vehiculo.getY(), this);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                
                // Dibujar área de colisión si está activado
                if (mostrarAreasColision) {
                    Rectangle area = vehiculo.getAreaColision();
                    
                    // Color según estado
                    if (enColision) {
                        g2d.setColor(Color.RED);
                    } else if (esperando) {
                        g2d.setColor(Color.ORANGE);
                    } else {
                        g2d.setColor(Color.GREEN);
                    }
                    
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(area.x, area.y, area.width, area.height);
                }
            }
        }
        
        // Dibujar estadísticas e información
        dibujarEstadisticas(g2d);
    }
    
    private void dibujarEstadisticas(Graphics2D g2d) {
        // Fondo semitransparente para estadísticas
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, 10, 250, 180);
        
        // Estadísticas de la simulación
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("SIMULADOR DE TRÁFICO", 20, 30);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Vehículos activos: " + vehiculos.size(), 20, 55);
        g2d.drawString("Total agregados: " + vehiculosAgregados, 20, 75);
        g2d.drawString("Colisiones activas: " + colisionesActivas.size(), 20, 95);
        g2d.drawString("Reglas de cruce: " + (reglasActivadas ? "ACTIVADAS" : "DESACTIVADAS"), 20, 115);
        g2d.drawString("Áreas colisión (H): " + (mostrarAreasColision ? "ON" : "OFF"), 20, 135);
        
        // Distribución de vehículos por dirección
        int[] contadoresDir = contarVehiculosPorDireccion();
        g2d.drawString("Distribución direcciones:", 20, 160);
        g2d.drawString("Este: " + contadoresDir[0] + " | Oeste: " + contadoresDir[1], 30, 180);
        g2d.drawString("Norte: " + contadoresDir[2] + " | Sur: " + contadoresDir[3], 30, 200);
        
        // Controles
        g2d.setColor(Color.YELLOW);
        g2d.drawString("Controles:", 300, 30);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("A: Agregar 3 vehículos", 300, 50);
        g2d.drawString("C: Limpiar todos los vehículos", 300, 70);
        g2d.drawString("H: Mostrar/ocultar áreas de colisión", 300, 90);
        g2d.drawString("S: Activar/desactivar reglas de cruce", 300, 110);
        g2d.drawString("SPACE: Resetear reglas del cruce", 300, 130);
        g2d.drawString("R: Resetear vehículo en colisión", 300, 150);
        g2d.drawString("P: Pausar/Reanudar simulación", 300, 170);
    }
    
    private int[] contarVehiculosPorDireccion() {
        int[] contadores = new int[4]; // 0:este, 1:oeste, 2:norte, 3:sur
        
        for (Vehiculo v : vehiculos) {
            switch(v.getDireccion()) {
                case "este": contadores[0]++; break;
                case "oeste": contadores[1]++; break;
                case "norte": contadores[2]++; break;
                case "sur": contadores[3]++; break;
            }
        }
        return contadores;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Actualizar reglas del cruce
        reglasCruce.actualizar();
        
        // Mover vehículos y aplicar reglas
        for (Vehiculo vehiculo : vehiculos) {
            manejarReglasCruce(vehiculo);
            
            if (vehiculo.getVelocidad() > 0) {
                vehiculo.mover();
            }
        }
        
        // Detectar colisiones
        detectarColisiones();
        
        // Reanudar vehículos que puedan moverse
        for (Vehiculo vehiculo : vehiculos) {
            if (!colisionesActivas.contains(vehiculo) && vehiculo.getVelocidad() == 0) {
                boolean enCruce = reglasCruce.estaEnCruce(
                    vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion());
                
                if (!enCruce || reglasCruce.puedePasar(vehiculo)) {
                    vehiculo.setVelocidad(2 + (int)(Math.random() * 3));
                }
            }
            
            // Reposicionar si sale de la pantalla
            if (vehiculo.getX() < -200 || vehiculo.getX() > 1000 || 
                vehiculo.getY() < -200 || vehiculo.getY() > 800) {
                reposicionarVehiculo(vehiculo);
            }
        }
        
        repaint();
    }
    
    private void reposicionarVehiculo(Vehiculo vehiculo) {
        String nuevaDireccion = generarDireccionAleatoria();
        vehiculo.setDireccion(nuevaDireccion);
        
        int[] nuevaPosicion = generarPosicionInicial(nuevaDireccion);
        vehiculo.setX(nuevaPosicion[0]);
        vehiculo.setY(nuevaPosicion[1]);
        vehiculo.setVelocidad(2 + (int)(Math.random() * 3));
        
        // Remover de colisiones activas
        colisionesActivas.remove(vehiculo);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulador de Tráfico Automático");
        Mavenproject8 simulador = new Mavenproject8();
        frame.add(simulador);
        
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Mensaje inicial
        System.out.println("Simulador de Tráfico iniciado");
        System.out.println("Controles:");
        System.out.println("A - Agregar 3 vehículos");
        System.out.println("C - Limpiar todos los vehículos");
        System.out.println("H - Mostrar/ocultar áreas de colisión");
        System.out.println("S - Activar/desactivar reglas de cruce");
        System.out.println("SPACE - Resetear reglas del cruce");
        System.out.println("R - Resetear vehículo en colisión");
        System.out.println("P - Pausar/Reanudar simulación");
    }
}