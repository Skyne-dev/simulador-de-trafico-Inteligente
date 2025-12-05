/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */


//Version prueba
// Mavenproject8.java - Solo reglas de prioridad
package com.mycompany.mavenproject8;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

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
    //variables para stadisticas
    private boolean mostrarDetallesAvanzados = true;
    private boolean mostrarTendencias = true;
    private Map<String, Integer> estadisticasColisiones = new HashMap<>();
    private List<Integer> historicoVehiculos = new ArrayList<>();
    private List<Integer> historicoColisiones = new ArrayList<>();
    private List<Float> historicoFlujo = new ArrayList<>();
    private long tiempoInicio = System.currentTimeMillis();
    private int totalColisiones = 0;
    private int totalGiros = 0;
    private int totalVehiculosPasados = 0;
    private int frameCount = 0;

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
            {0, 200}, // norte (usar la parte de arriba)
            {200, 0}, // sur  (usar la parte de abajo)
            {0, 0}, // este (usar la parte derecha)
            {100, 100} // oeste (usar la parte izquierda)
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
                File[] archivos = carpeta.listFiles((dir, name)
                        -> name.toLowerCase().endsWith(".png"));
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

            // Los sprites están en una cuadrícula de 3x3 de 100x100
            // Usamos las coordenadas 0,0 (parte superior izquierda) como referencia
            // y luego se ajusta la subimagen.
            // Los sprites están en 3 filas y 3 columnas (ej: verde.png tiene 9 sprites)
            // En la clase Vehiculo se usa un tamaño de 80x80, pero el sprite es de 100x100
            // para que no se vean cortados. Usaremos 100x100.
            // Se asume que el archivo PNG contiene los sprites ordenados de una manera específica
            // (ej: fila 1: este, noreste, norte, etc.)
            // Las coordenadas dadas en cargarSprites son aproximadas y deberían ajustarse.
            // Para simplificar, asumiremos que las coordenadas en cargarSprites son correctas para el recorte.
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

        // Coordenadas ajustadas para que salgan del borde
        switch (direccion) {
            case "este":
                x = -150; // Sale de muy a la izquierda
                y = 270; // Carril Este
                break;
            case "oeste":
                x = 800 + 50; // Sale de muy a la derecha
                y = 215; // Carril Oeste
                break;
            case "norte":
                x = 375; // Carril Norte
                y = 600 + 50; // Sale de muy abajo
                break;
            case "sur":
                x = 322; // Carril Sur
                y = -150; // Sale de muy arriba
                break;
        }

        return new int[]{x, y};
    }

    // Manejar reglas del cruce para un vehículo
    private void manejarReglasCruce(Vehiculo vehiculo) {
    if (!reglasActivadas) {
        return;
    }
    
    // 1. Tomar decisión de giro al aproximarse
    if (reglasCruce.estaEnZonaDeReglas(vehiculo) || 
        reglasCruce.estaEnCruce(vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion())) {
        reglasCruce.tomarDecisionDeGiro(vehiculo);
    }
    
    boolean enCruce = reglasCruce.estaEnCruce(
            vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion());
    
    if (enCruce) {
        // Si ya está en el cruce, puede continuar
        // Ejecutar giro si corresponde
        ejecutarGiroSiCorresponde(vehiculo);
        
    } else if (reglasCruce.estaEnZonaDeReglas(vehiculo)) {
        // Está en zona de aproximación - aplicar reglas intuitivas
        boolean puedePasar = reglasCruce.puedePasar(vehiculo);
        
        if (!puedePasar) {
            // Detener y esperar
            vehiculo.setVelocidad(0);
            reglasCruce.incrementarEspera(vehiculo);
            return;
        }
        
        // Si puede pasar según las reglas
        if (caminoDespejado(vehiculo)) {
            vehiculo.setVelocidad(2 + (int) (Math.random() * 3));
        } else {
            // Aún no hay espacio
            vehiculo.setVelocidad(0);
            reglasCruce.incrementarEspera(vehiculo);
        }
    } else {
        // Fuera de la zona - liberar si ya pasó
        reglasCruce.liberarCruceSiSale(vehiculo);
    }
    
    // Reanudar si está detenido sin razón
    if (vehiculo.getVelocidad() == 0 && !reglasCruce.estaEnZonaDeReglas(vehiculo)) {
        vehiculo.setVelocidad(2 + (int) (Math.random() * 3));
    }
}

    /**
     * **NUEVO: Lógica de giro** Cambia la dirección del vehículo en el punto
     * central del cruce.
     */
    private void ejecutarGiroSiCorresponde(Vehiculo vehiculo) {
    String dirActual = vehiculo.getDireccion();
    String siguienteDir = vehiculo.getSiguienteDireccion();
    
    if (dirActual.equals(siguienteDir)) {
        return; // No hay giro
    }
    
    int x = vehiculo.getX();
    int y = vehiculo.getY();
    
    // Puntos de referencia del cruce (centro aprox)
    final int CRUCE_CENTRO_X = 400;
    final int CRUCE_CENTRO_Y = 300;
    final int TOLERANCIA = 10;
    
    // Solo cambia de dirección si está cerca del centro del cruce
    boolean cambiar = false;
    
    switch (dirActual) {
        case "este":
            if (x >= CRUCE_CENTRO_X - TOLERANCIA) {
                cambiar = true;
            }
            break;
        case "oeste":
            if (x <= CRUCE_CENTRO_X + TOLERANCIA) {
                cambiar = true;
            }
            break;
        case "norte":
            if (y >= CRUCE_CENTRO_Y - TOLERANCIA) {
                cambiar = true;
            }
            break;
        case "sur":
            if (y <= CRUCE_CENTRO_Y + TOLERANCIA) {
                cambiar = true;
            }
            break;
    }
    
    if (cambiar) {
        // Cambiar dirección
        vehiculo.setDireccion(siguienteDir);
        vehiculo.setVelocidad(2 + (int) (Math.random() * 3));
        
        // Ajustar posición para centrarlo en el nuevo carril
        switch (siguienteDir) {
            case "este":
                vehiculo.setY(270);
                break;
            case "oeste":
                vehiculo.setY(215);
                break;
            case "norte":
                vehiculo.setX(375);
                break;
            case "sur":
                vehiculo.setX(322);
                break;
        }
        
        // Limpiar la decisión tomada para este cruce
        // Esto se hará automáticamente cuando el vehículo salga del cruce
    }
}


      


    // Detectar colisiones físicas
   private void detectarColisiones() {
    // Limpiar colisiones activas
    colisionesActivas.clear();
    
    for (int i = 0; i < vehiculos.size(); i++) {
        Vehiculo v1 = vehiculos.get(i);
        
        for (int j = i + 1; j < vehiculos.size(); j++) {
            Vehiculo v2 = vehiculos.get(j);
            
            if (v1.colisionaCon(v2)) {
                // Determinar quién es el "chocador" y quién el "chocado"
                Vehiculo chocador = determinarChocador(v1, v2);
                Vehiculo chocado = (chocador == v1) ? v2 : v1;
                
                // Marcar ambos en colisiones activas
                if (!colisionesActivas.contains(chocador)) {
                    colisionesActivas.add(chocador);
                }
                if (!colisionesActivas.contains(chocado)) {
                    colisionesActivas.add(chocado);
                }
                
                // Solo el chocador retrocede
                manejarColision(chocador, chocado);
            }
        }
    }
}
   
   
   private Vehiculo determinarChocador(Vehiculo v1, Vehiculo v2) {
    // Caso 1: Si uno está quieto y el otro se mueve, el que se mueve es el chocador
    if (v1.getVelocidad() == 0 && v2.getVelocidad() > 0) {
        return v2;
    }
    if (v2.getVelocidad() == 0 && v1.getVelocidad() > 0) {
        return v1;
    }
    
    // Caso 2: Ambos se mueven - determinar basado en dirección relativa
    String dir1 = v1.getDireccion();
    String dir2 = v2.getDireccion();
    
    // Si vienen de direcciones opuestas
    if (sonDireccionesOpuestas(dir1, dir2)) {
        // Ambos son chocadores en este caso
        // Usar velocidad como tie-breaker (el más rápido)
        return v1.getVelocidad() >= v2.getVelocidad() ? v1 : v2;
    }
    
    // Caso 3: Verificar quién se mueve hacia el punto del otro
    int[] frontal1 = v1.getPuntoFrontal();
    int[] frontal2 = v2.getPuntoFrontal();
    
    double distancia1 = calcularDistancia(frontal1[0], frontal1[1], 
                                         v2.getX() + v2.getAncho()/2, 
                                         v2.getY() + v2.getAlto()/2);
    double distancia2 = calcularDistancia(frontal2[0], frontal2[1], 
                                         v1.getX() + v1.getAncho()/2, 
                                         v1.getY() + v1.getAlto()/2);
    
    // El que está más cerca del centro del otro (y moviéndose hacia él) es el chocador
    if (distancia1 < distancia2) {
        return v1;
    } else {
        return v2;
    }
}

   //* Verifica si dos direcciones son opuestas
 
private boolean sonDireccionesOpuestas(String dir1, String dir2) {
    return (dir1.equals("este") && dir2.equals("oeste")) ||
           (dir1.equals("oeste") && dir2.equals("este")) ||
           (dir1.equals("norte") && dir2.equals("sur")) ||
           (dir1.equals("sur") && dir2.equals("norte"));
}

/**
 * Calcula distancia entre dos puntos
 */
private double calcularDistancia(int x1, int y1, int x2, int y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
}


    
  private void manejarColision(Vehiculo chocador, Vehiculo chocado) {
     // Detener ambos vehículos inmediatamente
    chocador.setVelocidad(0);
    chocado.setVelocidad(0);
    
    // Solo el CHOCADOR retrocede (pero menos distancia)
    retrocederVehiculo(chocador, 15);
    
    // El chocado permanece en su posición (solo se detiene)
    // No retrocede
    
    // Añadir un "tiempo de recuperación" aleatorio a cada vehículo
    Random rand = new Random();
    
    // El chocador espera más tiempo (penalización)
    chocador.setTiempoRecuperacion(30 + rand.nextInt(60)); // 0.5-1.5 segundos
    chocado.setTiempoRecuperacion(10 + rand.nextInt(30));  // 0.17-0.67 segundos
    
    System.out.println("Colisión: " + chocador.getTipo() + " chocó a " + 
                      chocado.getTipo() + " (Direcciones: " + 
                      chocador.getDireccion() + " -> " + chocado.getDireccion() + ")");
}
  private boolean caminoDespejado(Vehiculo vehiculo) {
    String direccion = vehiculo.getDireccion();
    int x = vehiculo.getX();
    int y = vehiculo.getY();
    
    // Crear área de verificación delante
    Rectangle areaDelante = null;
    
    switch (direccion) {
        case "este":
            areaDelante = new Rectangle(x + 80, y + 10, 60, 60);
            break;
        case "oeste":
            areaDelante = new Rectangle(x - 60, y + 10, 60, 60);
            break;
        case "norte":
            areaDelante = new Rectangle(x + 10, y - 60, 60, 60);
            break;
        case "sur":
            areaDelante = new Rectangle(x + 10, y + 80, 60, 60);
            break;
    }
    
    if (areaDelante == null) return true;
    
    for (Vehiculo otro : vehiculos) {
        if (otro != vehiculo && otro.getAreaColision().intersects(areaDelante)) {
            return false;
        }
    }
    
    return true;
}

/**
 * Retrocede un vehículo en su dirección opuesta
 */
private void retrocederVehiculo(Vehiculo v, int distancia) {
    switch (v.getDireccion()) {
        case "este":
            v.setX(v.getX() - distancia);
            break;
        case "oeste":
            v.setX(v.getX() + distancia);
            break;
        case "norte":
            v.setY(v.getY() + distancia);
            break;
        case "sur":
            v.setY(v.getY() - distancia);
            break;
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

        // Configurar tamaño del panel (ancho expandido para estadísticas)
    setPreferredSize(new Dimension(1400, 800)); // 400px adicionales para estadísticas
    
    // Configurar controles del teclado
    setFocusable(true);
    addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> generarVehiculosAutomaticos(3);
                case KeyEvent.VK_C -> {
                    vehiculos.clear();
                    colisionesActivas.clear();
                    vehiculosAgregados = 0;
                    estadisticasColisiones.clear();
                    totalColisiones = 0;
                    totalGiros = 0;
                    reglasCruce = new ReglasCruce();
                }
                case KeyEvent.VK_H -> mostrarAreasColision = !mostrarAreasColision;
                case KeyEvent.VK_S -> reglasActivadas = !reglasActivadas;
                case KeyEvent.VK_SPACE -> reglasCruce = new ReglasCruce();
                case KeyEvent.VK_R -> {
                    if (!colisionesActivas.isEmpty()) {
                        Vehiculo v = colisionesActivas.get(0);
                        reposicionarVehiculo(v);
                    }
                }
                case KeyEvent.VK_P -> {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
                case KeyEvent.VK_D -> mostrarDetallesAvanzados = !mostrarDetallesAvanzados;
                case KeyEvent.VK_T -> mostrarTendencias = !mostrarTendencias;
                case KeyEvent.VK_E -> exportarEstadisticas();
            }
            repaint();
        }
    }
    );
    
    // Inicializar estadísticas
    inicializarEstadisticas();
    }

    
   
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    Graphics2D g2d = (Graphics2D) g;
    
    // Dividir pantalla: 1000px para mapa, 400px para estadísticas
    int anchoMapa = 800;
    int anchoEstadisticas = 400;
    
    // Crear clip para el mapa (solo izquierda)
    g2d.setClip(0, 0, anchoMapa, getHeight());
    
    // Dibujar mapa en su área
    mapaPanel.dibujarMapa(g);
    
    // Dibujar todos los vehículos automáticos
    for (Vehiculo vehiculo : vehiculos) {
        Image spriteVehiculo = sprites.get(vehiculo.getTipo() + vehiculo.getDireccion());
        if (spriteVehiculo != null) {
            // Efectos visuales según estado
            boolean enCruce = reglasCruce.estaEnCruce(
                    vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion());
            boolean esperando = !enCruce && vehiculo.getVelocidad() == 0 && 
                               reglasCruce.estaEnZonaDeReglas(vehiculo);
            boolean enColision = colisionesActivas.contains(vehiculo);
            
            // Aplicar efectos visuales
            if (esperando) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2d.setColor(new Color(255, 165, 0, 100));
                g2d.fillRect(vehiculo.getX(), vehiculo.getY(), 100, 100);
            } else if (enColision) {
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
    
    // Quitar el clip para dibujar las estadísticas
    g2d.setClip(null);
    
    // Dibujar panel de estadísticas (lado derecho)
    dibujarPanelEstadisticas(g2d, anchoMapa, anchoEstadisticas);
}

private void dibujarPanelEstadisticas(Graphics2D g2d, int inicioX, int ancho) {
    // Fondo del panel de estadísticas
    g2d.setColor(new Color(20, 20, 30, 240));
    g2d.fillRect(inicioX, 0, ancho, getHeight());
    // Fondo del panel de estadísticas
    g2d.setColor(new Color(20, 20, 30, 240));
    g2d.fillRect(0, 600, 800, 800);
    // Borde del panel
    g2d.setColor(new Color(60, 60, 80));
    g2d.setStroke(new BasicStroke(3));
    g2d.drawRect(inicioX, 0, ancho - 1, getHeight() - 1);
    
    // Título del panel
    g2d.setColor(Color.CYAN);
    g2d.setFont(new Font("Arial", Font.BOLD, 24));
    g2d.drawString("PANEL DE CONTROL", inicioX + 20, 40);
    
    // Separador
    g2d.setColor(new Color(100, 100, 120));
    g2d.fillRect(inicioX + 20, 55, ancho - 40, 2);
    
    int yPos = 90;
    
    // Sección 1: ESTADO ACTUAL
    g2d.setColor(Color.YELLOW);
    g2d.setFont(new Font("Arial", Font.BOLD, 18));
    g2d.drawString("ESTADO ACTUAL", inicioX + 20, yPos);
    yPos += 30;
    
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.PLAIN, 14));
    g2d.drawString("Vehículos activos: " + vehiculos.size(), inicioX + 30, yPos);
    yPos += 25;
    g2d.drawString("Total agregados: " + vehiculosAgregados, inicioX + 30, yPos);
    yPos += 25;
    g2d.drawString("Colisiones activas: " + colisionesActivas.size(), inicioX + 30, yPos);
    yPos += 25;
    g2d.drawString("Reglas: " + (reglasActivadas ? "ACTIVADAS" : "DESACTIVADAS"), inicioX + 30, yPos);
    yPos += 25;
    g2d.drawString("Áreas colisión: " + (mostrarAreasColision ? "VISIBLE" : "OCULTO"), inicioX + 30, yPos);
    
    yPos += 40;
    
    // Sección 2: DISTRIBUCIÓN DE TRÁFICO
    g2d.setColor(Color.GREEN);
    g2d.setFont(new Font("Arial", Font.BOLD, 18));
    g2d.drawString("DISTRIBUCIÓN", inicioX + 20, yPos);
    yPos += 30;
    
    int[] contadoresDir = contarVehiculosPorDireccion();
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.PLAIN, 14));
    
    // Barras de distribución
    int barWidth = 150;
    int barHeight = 20;
    int barStartX = inicioX + 30;
    
    // Este
    g2d.setColor(new Color(0, 150, 255));
    g2d.fillRect(barStartX, yPos, (barWidth * contadoresDir[0]) / Math.max(1, vehiculos.size()), barHeight);
    g2d.setColor(Color.WHITE);
    g2d.drawString("Este: " + contadoresDir[0] + " vehículos", barStartX + 5, yPos + 15);
    yPos += 30;
    
    // Oeste
    g2d.setColor(new Color(255, 100, 0));
    g2d.fillRect(barStartX, yPos, (barWidth * contadoresDir[1]) / Math.max(1, vehiculos.size()), barHeight);
    g2d.setColor(Color.WHITE);
    g2d.drawString("Oeste: " + contadoresDir[1] + " vehículos", barStartX + 5, yPos + 15);
    yPos += 30;
    
    // Norte
    g2d.setColor(new Color(0, 200, 100));
    g2d.fillRect(barStartX, yPos, (barWidth * contadoresDir[2]) / Math.max(1, vehiculos.size()), barHeight);
    g2d.setColor(Color.WHITE);
    g2d.drawString("Norte: " + contadoresDir[2] + " vehículos", barStartX + 5, yPos + 15);
    yPos += 30;
    
    // Sur
    g2d.setColor(new Color(200, 0, 200));
    g2d.fillRect(barStartX, yPos, (barWidth * contadoresDir[3]) / Math.max(1, vehiculos.size()), barHeight);
    g2d.setColor(Color.WHITE);
    g2d.drawString("Sur: " + contadoresDir[3] + " vehículos", barStartX + 5, yPos + 15);
    
    yPos += 50;
    
    // Sección 3: ESTADÍSTICAS AVANZADAS
    if (mostrarDetallesAvanzados) {
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("ESTADÍSTICAS AVANZADAS", inicioX + 20, yPos);
        yPos += 30;
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        
        long tiempoTranscurrido = (System.currentTimeMillis() - tiempoInicio) / 1000;
        g2d.drawString("Tiempo simulación: " + tiempoTranscurrido + "s", inicioX + 30, yPos);
        yPos += 25;
        
        g2d.drawString("Total colisiones: " + totalColisiones, inicioX + 30, yPos);
        yPos += 25;
        
        g2d.drawString("Total giros: " + totalGiros, inicioX + 30, yPos);
        yPos += 25;
        
        g2d.drawString("Vehículos/hora: " + calcularFlujoPorHora(), inicioX + 30, yPos);
        yPos += 25;
        
        if (tiempoTranscurrido > 0) {
            g2d.drawString("Colisiones/min: " + String.format("%.2f", 
                (totalColisiones * 60.0) / tiempoTranscurrido), inicioX + 30, yPos);
            yPos += 25;
        }
    }
    
    yPos =620;
    
    // Sección 4: INFORMACIÓN DEL CRUCE
    g2d.setColor(Color.ORANGE);
    g2d.setFont(new Font("Arial", Font.BOLD, 18));
    g2d.drawString("INFORMACIÓN DEL CRUCE", 20, yPos);
    yPos += 20;
    
    // Dibujar reglas del cruce en el panel lateral
    reglasCruce.dibujarPanelLateral(g2d,  30, yPos);
    
    yPos =630;
    
    // Sección 5: TENDENCIAS (si está habilitado)
    if (mostrarTendencias && historicoVehiculos.size() > 10) {
        g2d.setColor(new Color(100, 200, 255));
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("TENDENCIAS",  400, yPos);
        yPos += 30;
        
        dibujarGraficoTendencias(g2d,  400, yPos, 350, 120);
    }
    
    yPos =590;
    
    // Sección 6: CONTROLES
    g2d.setColor(Color.GRAY);
    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    g2d.drawString("CONTROLES", inicioX + 20, yPos);
    yPos += 25;
    
    g2d.setColor(Color.LIGHT_GRAY);
    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
    g2d.drawString("A: Agregar 3 vehículos", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("C: Limpiar todos", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("H: Áreas colisión", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("S: Reglas ON/OFF", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("SPACE: Resetear reglas", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("R: Resetear vehículo", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("P: Pausar/Reanudar", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("D: Detalles ON/OFF", inicioX + 30, yPos);
    yPos += 20;
    g2d.drawString("T: Tendencias ON/OFF", inicioX + 30, yPos);
  
    g2d.drawString("E: Exportar datos", inicioX + 200, yPos);
}

private int calcularFlujoPorHora() {
    long tiempoSegundos = (System.currentTimeMillis() - tiempoInicio) / 1000;
    if (tiempoSegundos == 0) return 0;
    return (int)((vehiculosAgregados * 3600.0) / tiempoSegundos);
}

private void dibujarGraficoTendencias(Graphics2D g2d, int x, int y, int ancho, int alto) {
    // Fondo del gráfico
    g2d.setColor(new Color(30, 30, 40));
    g2d.fillRect(x, y, ancho, alto);
    
    // Borde
    g2d.setColor(new Color(80, 80, 100));
    g2d.drawRect(x, y, ancho, alto);
    
    if (historicoVehiculos.size() < 2) return;
    
    // Encontrar máximos para escalar
    int maxVehiculos = Collections.max(historicoVehiculos);
    int maxColisiones = historicoColisiones.isEmpty() ? 1 : Collections.max(historicoColisiones);
    int maxValor = Math.max(maxVehiculos, maxColisiones);
    if (maxValor == 0) maxValor = 1;
    
    // Dibujar líneas de referencia
    g2d.setColor(new Color(60, 60, 80));
    for (int i = 1; i <= 4; i++) {
        int lineaY = y + alto - (alto * i) / 5;
        g2d.drawLine(x, lineaY, x + ancho, lineaY);
    }
    
    // Dibujar tendencia de vehículos (línea azul)
    g2d.setColor(new Color(0, 150, 255, 200));
    g2d.setStroke(new BasicStroke(2));
    
    int puntos = Math.min(historicoVehiculos.size(), 50); // Últimos 50 puntos
    int inicio = Math.max(0, historicoVehiculos.size() - puntos);
    
    for (int i = 1; i < puntos; i++) {
        int x1 = x + ((i - 1) * ancho) / puntos;
        int y1 = y + alto - (historicoVehiculos.get(inicio + i - 1) * alto) / maxValor;
        int x2 = x + (i * ancho) / puntos;
        int y2 = y + alto - (historicoVehiculos.get(inicio + i) * alto) / maxValor;
        
        g2d.drawLine(x1, y1, x2, y2);
    }
    
    // Dibujar tendencia de colisiones (línea roja)
    if (!historicoColisiones.isEmpty()) {
        g2d.setColor(new Color(255, 50, 50, 200));
        
        int puntosCol = Math.min(historicoColisiones.size(), 50);
        int inicioCol = Math.max(0, historicoColisiones.size() - puntosCol);
        
        for (int i = 1; i < puntosCol; i++) {
            int x1 = x + ((i - 1) * ancho) / puntosCol;
            int y1 = y + alto - (historicoColisiones.get(inicioCol + i - 1) * alto) / maxValor;
            int x2 = x + (i * ancho) / puntosCol;
            int y2 = y + alto - (historicoColisiones.get(inicioCol + i) * alto) / maxValor;
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    // Leyenda
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
    g2d.drawString("Vehículos", x + 5, y + 15);
    g2d.setColor(new Color(255, 50, 50));
    g2d.drawString("Colisiones", x + 5, y + 30);
}
    
    private void inicializarEstadisticas() {
    estadisticasColisiones.put("leve", 0);
    estadisticasColisiones.put("moderada", 0);
    estadisticasColisiones.put("grave", 0);
}
    private void exportarEstadisticas() {
    System.out.println("=== EXPORTANDO ESTADÍSTICAS ===");
    System.out.println("Tiempo de simulación: " + ((System.currentTimeMillis() - tiempoInicio) / 1000) + "s");
    System.out.println("Total vehículos: " + vehiculosAgregados);
    System.out.println("Colisiones totales: " + totalColisiones);
    System.out.println("Giros realizados: " + totalGiros);
    System.out.println("Vehículos pasados por cruce: " + totalVehiculosPasados);
}
    


    private int[] contarVehiculosPorDireccion() {
        int[] contadores = new int[4]; // 0:este, 1:oeste, 2:norte, 3:sur

        for (Vehiculo v : vehiculos) {
            switch (v.getDireccion()) {
                case "este":
                    contadores[0]++;
                    break;
                case "oeste":
                    contadores[1]++;
                    break;
                case "norte":
                    contadores[2]++;
                    break;
                case "sur":
                    contadores[3]++;
                    break;
            }
        }
        return contadores;
    }

  
   @Override
public void actionPerformed(ActionEvent e) {
    frameCount++;
    
    // Actualizar reglas del cruce
    reglasCruce.actualizar();
    
    // Actualizar lista de vehículos en reglasCruce
    reglasCruce.setVehiculos(vehiculos);
    
    // Mover vehículos y aplicar reglas
    for (Vehiculo vehiculo : vehiculos) {
        manejarReglasCruce(vehiculo);
        
        if (vehiculo.getVelocidad() > 0) {
            vehiculo.mover();
            
            // Registrar cuando un vehículo pasa el cruce
            if (vehiculo.haPasadoElCruce()) {
                totalVehiculosPasados++;
            }
        }
    }
    
    // Detectar colisiones
    int colisionesPrevias = colisionesActivas.size();
    detectarColisiones();
    
    // Registrar nuevas colisiones
    if (colisionesActivas.size() > colisionesPrevias) {
        totalColisiones++;
    }
    
    // Registrar giros
    for (Vehiculo vehiculo : vehiculos) {
        if (!vehiculo.getDireccion().equals(vehiculo.getSiguienteDireccion())) {
            totalGiros++;
            vehiculo.setSiguienteDireccion(vehiculo.getDireccion()); // Reset
        }
    }
    
    // Actualizar estadísticas cada 60 frames (~1 segundo)
    if (frameCount % 60 == 0) {
        historicoVehiculos.add(vehiculos.size());
        historicoColisiones.add(colisionesActivas.size());
        
        // Mantener tamaño máximo del histórico
        if (historicoVehiculos.size() > 100) {
            historicoVehiculos.remove(0);
            historicoColisiones.remove(0);
        }
    }
    
    // Mantenimiento
    for (Vehiculo vehiculo : vehiculos) {
        vehiculo.actualizarRecuperacion();
        
        if (colisionesActivas.contains(vehiculo)) {
            continue;
        }
        
        // Intentar reanudar vehículos detenidos
        if (vehiculo.getVelocidad() == 0) {
            boolean enZonaReglas = reglasCruce.estaEnZonaDeReglas(vehiculo);
            
            if (!enZonaReglas) {
                vehiculo.setVelocidad(2 + (int) (Math.random() * 3));
            } else if (reglasCruce.puedePasar(vehiculo) && caminoDespejado(vehiculo)) {
                vehiculo.setVelocidad(2 + (int) (Math.random() * 3));
            }
        }
        
        // Reposicionar si sale de pantalla
        if (vehiculo.getX() < -200 || vehiculo.getX() > 1200 ||
            vehiculo.getY() < -200 || vehiculo.getY() > 1000) {
            reposicionarVehiculo(vehiculo);
        }
    }
    
    repaint();
}
    
    private boolean puedeReanudarDespuesColision(Vehiculo vehiculo) {
    // Verificar que no haya otro vehículo justo delante
    Rectangle areaFutura = vehiculo.getAreaColisionFutura();
    
    for (Vehiculo otro : vehiculos) {
        if (otro != vehiculo && areaFutura.intersects(otro.getAreaColision())) {
            return false; // Hay alguien delante, no reanudar
        }
    }
    
    // Verificar que el camino esté despejado
    return Math.random() < 0.05; // 5% de probabilidad por frame
}
    
    
    private void reposicionarVehiculo(Vehiculo vehiculo) {
        // Solo reposicionar si NO está en colisión activa
    if (colisionesActivas.contains(vehiculo)) {
        System.out.println("No se puede reposicionar vehículo en colisión activa");
        return;
    }
    
    String nuevaDireccion = generarDireccionAleatoria();
    vehiculo.setDireccion(nuevaDireccion);
    vehiculo.setSiguienteDireccion(nuevaDireccion);
    
    int[] nuevaPosicion = generarPosicionInicial(nuevaDireccion);
    vehiculo.setX(nuevaPosicion[0]);
    vehiculo.setY(nuevaPosicion[1]);
    vehiculo.setVelocidad(2 + (int) (Math.random() * 3));
    vehiculo.setTiempoRecuperacion(0); // Resetear recuperación
    
    // Remover de colisiones activas
    colisionesActivas.remove(vehiculo);
    
    System.out.println("Vehículo " + vehiculo.getTipo() + " reposicionado manualmente");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulador de Tráfico Inteligente - Panel Expandido");
    Mavenproject8 simulador = new Mavenproject8();
    frame.add(simulador);
    
    // Usar tamaño expandido
    frame.setSize(1200, 820);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    
    // Mensaje inicial
    System.out.println("Simulador de Tráfico con Panel de Estadísticas");
    System.out.println("==============================================");
    System.out.println("Controles principales:");
    System.out.println("A - Agregar 3 vehículos");
    System.out.println("C - Limpiar todos los vehículos");
    System.out.println("H - Mostrar/ocultar áreas de colisión");
    System.out.println("S - Activar/desactivar reglas de cruce");
    System.out.println("D - Mostrar/ocultar detalles avanzados");
    System.out.println("T - Mostrar/ocultar gráfico de tendencias");
    System.out.println("E - Exportar estadísticas a consola");
    System.out.println("==============================================");
    }
}