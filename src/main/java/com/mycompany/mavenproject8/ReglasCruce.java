/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ReglasCruce.java - Control por SEMÁFOROS (manteniendo prohibición de vuelta en U)
package com.mycompany.mavenproject8;

import java.awt.BasicStroke;
import java.awt.Color; // Necesario para la aleatoriedad
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class ReglasCruce {

    // ReglasCruce.java - Sistema intuitivo sin semáforos

    // Área del cruce central
    private static final int CRUCE_X1 = 350;
    private static final int CRUCE_X2 = 450;
    private static final int CRUCE_Y1 = 250;
    private static final int CRUCE_Y2 = 350;

    // Distancias para detección
    private static final int DISTANCIA_APROXIMACION = 120;
    private static final int DISTANCIA_PARADA = 30;

    // Vehículos actualmente en el cruce
    private List<Vehiculo> vehiculosEnCruce;
    //Semaforos del cruce
    public Map<String, Semaforo> getSemaforos() {
        return semaforos;
    }
    // Vehículos que han tomado decisión de giro
    private Map<Vehiculo, Boolean> decisionesTomadas;

    // Vehículos esperando en cada dirección
    private Map<String, Queue<Vehiculo>> colasEspera;

    // Tiempo de espera para evitar deadlocks
    private Map<Vehiculo, Integer> tiemposEspera;

    // Registro de quién llegó primero
    private Map<String, Long> tiemposLlegada;
    private Random random;
    private java.util.Map<String, Semaforo> semaforos;
    private int fase; // 0: EW_GREEN, 1: EW_YELLOW, 2: NS_GREEN, 3: NS_YELLOW
    private int contadorFase;
    private static final int DURACION_VERDE = 300; // frames (~5s at 60fps)
    private static final int DURACION_AMARILLO = 60; // frames (~1s)

    public ReglasCruce() {
        this.vehiculosEnCruce = new ArrayList<>();
        this.decisionesTomadas = new HashMap<>();
        this.tiemposEspera = new HashMap<>();
        this.tiemposLlegada = new HashMap<>();
        this.random = new Random();
        this.semaforos = new HashMap<>();
        // Initialize semaphores and start with East-West green
        semaforos.put("este", new Semaforo("este", Semaforo.Estado.VERDE));
        semaforos.put("oeste", new Semaforo("oeste", Semaforo.Estado.VERDE));
        semaforos.put("norte", new Semaforo("norte", Semaforo.Estado.ROJO));
        semaforos.put("sur", new Semaforo("sur", Semaforo.Estado.ROJO));
        this.fase = 0; // EW green
        this.contadorFase = DURACION_VERDE;

        // Inicializar colas de espera por dirección
        this.colasEspera = new HashMap<>();
        colasEspera.put("este", new LinkedList<>());
        colasEspera.put("oeste", new LinkedList<>());
        colasEspera.put("norte", new LinkedList<>());
        colasEspera.put("sur", new LinkedList<>());
    }

    public void actualizar() {
        // Limpiar vehículos que ya salieron del cruce
        vehiculosEnCruce.removeIf(v -> !estaEnCruce(v.getX(), v.getY(), v.getDireccion()));

        // Actualizar tiempos de espera
        for (Vehiculo v : new ArrayList<>(tiemposEspera.keySet())) {
            tiemposEspera.put(v, tiemposEspera.get(v) + 1);

            // Si espera demasiado (4 segundos), se le da prioridad
            if (tiemposEspera.get(v) > 240) {
                tiemposEspera.put(v, 0);
            }
        }

        // Simple, robust 4-phase FSM: EW_GREEN -> EW_YELLOW -> NS_GREEN -> NS_YELLOW ->
        // repeat
        contadorFase--;
        if (contadorFase <= 0) {
            avanzarFase();
        }

        // Anti-deadlock: si algún vehículo espera demasiado, forzar fase
        for (Vehiculo v : new ArrayList<>(tiemposEspera.keySet())) {
            if (tiemposEspera.getOrDefault(v, 0) > 300) { // ~5 seconds
                forzarFaseParaDireccion(v.getDireccion());
                break;
            }
        }
    }

    private void avanzarFase() {
        switch (fase) {
            case 0: // EW_GREEN -> EW_YELLOW
                semaforos.get("este").setEstado(Semaforo.Estado.AMARILLO);
                semaforos.get("oeste").setEstado(Semaforo.Estado.AMARILLO);
                semaforos.get("norte").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("sur").setEstado(Semaforo.Estado.ROJO);
                fase = 1;
                contadorFase = DURACION_AMARILLO;
                break;
            case 1: // EW_YELLOW -> NS_GREEN
                semaforos.get("este").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("oeste").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("norte").setEstado(Semaforo.Estado.VERDE);
                semaforos.get("sur").setEstado(Semaforo.Estado.VERDE);
                fase = 2;
                contadorFase = DURACION_VERDE;
                break;
            case 2: // NS_GREEN -> NS_YELLOW
                semaforos.get("norte").setEstado(Semaforo.Estado.AMARILLO);
                semaforos.get("sur").setEstado(Semaforo.Estado.AMARILLO);
                semaforos.get("este").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("oeste").setEstado(Semaforo.Estado.ROJO);
                fase = 3;
                contadorFase = DURACION_AMARILLO;
                break;
            case 3: // NS_YELLOW -> EW_GREEN
            default:
                semaforos.get("norte").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("sur").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("este").setEstado(Semaforo.Estado.VERDE);
                semaforos.get("oeste").setEstado(Semaforo.Estado.VERDE);
                fase = 0;
                contadorFase = DURACION_VERDE;
                break;
        }
    }

    public boolean estaEnZonaDeReglas(Vehiculo vehiculo) {
        int x = vehiculo.getX();
        int y = vehiculo.getY();
        String direccion = vehiculo.getDireccion();

        switch (direccion) {
            case "este":
                return x >= (CRUCE_X1 - DISTANCIA_APROXIMACION) && x < CRUCE_X2;
            case "oeste":
                return x <= (CRUCE_X2 + DISTANCIA_APROXIMACION) && x > CRUCE_X1;
            case "norte":
                return y <= (CRUCE_Y2 + DISTANCIA_APROXIMACION) && y > CRUCE_Y1;
            case "sur":
                return y >= (CRUCE_Y1 - DISTANCIA_APROXIMACION) && y < CRUCE_Y2;
        }
        return false;
    }

    public boolean estaEnCruce(int x, int y, String direccion) {
        Rectangle areaCruce = new Rectangle(CRUCE_X1, CRUCE_Y1,
                CRUCE_X2 - CRUCE_X1,
                CRUCE_Y2 - CRUCE_Y1);
        // Use vehicle-size 100x100 to match sprites
        Rectangle areaVehiculo = new Rectangle(x, y, 100, 100);

        return areaCruce.intersects(areaVehiculo);
    }

    public void tomarDecisionDeGiro(Vehiculo vehiculo) {
        if (decisionesTomadas.containsKey(vehiculo)) {
            return;
        }

        if (!estaEnZonaDeReglas(vehiculo) && !estaEnCruce(vehiculo.getX(), vehiculo.getY(), vehiculo.getDireccion())) {
            return;
        }

        String dirActual = vehiculo.getDireccion();
        String[] posiblesDirecciones = getPosiblesDirecciones(dirActual);

        if (posiblesDirecciones.length > 0) {
            // Distribución de probabilidad más realista
            double randomValue = random.nextDouble();
            String nuevaDir;

            // 60% seguir recto, 25% derecha, 15% izquierda
            if (randomValue < 0.60) {
                nuevaDir = dirActual; // Recto
            } else if (randomValue < 0.85) {
                nuevaDir = getGiroDerecha(dirActual); // Derecha
            } else {
                nuevaDir = getGiroIzquierda(dirActual); // Izquierda
            }

            // Asegurar que sea una dirección válida
            if (esDireccionValida(nuevaDir, posiblesDirecciones)) {
                vehiculo.setSiguienteDireccion(nuevaDir);
                decisionesTomadas.put(vehiculo, true);

                // Registrar tiempo de llegada si es el primero en su dirección
                String key = vehiculo.getDireccion() + "_" + nuevaDir;
                if (!tiemposLlegada.containsKey(key)) {
                    tiemposLlegada.put(key, System.currentTimeMillis());
                }
            }
        }
    }

    // Sistema principal de intuición y decisión
    public boolean puedePasar(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();

        // Si ya está dentro del cruce, puede continuar
        if (estaEnCruce(vehiculo.getX(), vehiculo.getY(), direccion)) {
            if (!vehiculosEnCruce.contains(vehiculo)) {
                vehiculosEnCruce.add(vehiculo);
            }
            return true;
        }

        Semaforo s = semaforos.get(direccion);
        if (s != null && !s.esVerde()) {
            agregarACola(vehiculo);
            return false;
        }

        // 1. Verificar si hay conflicto con vehículos en el cruce
        if (hayConflictoConCruce(vehiculo)) {
            agregarACola(vehiculo);
            return false;
        }

        // 2. Regla de la derecha
        if (tieneDerechaPrioritaria(vehiculo)) {
            return true;
        }

        // 3. Verificar si llegó primero
        if (llegoPrimero(vehiculo)) {
            return true;
        }

        // 4. Vía principal (Este-Oeste) tiene prioridad sobre vía secundaria
        if (esViaPrincipal(direccion) && !hayVehiculoEnViaPrincipalConflicto()) {
            return true;
        }

        // 5. Si ya esperó mucho tiempo (anti-deadlock)
        if (tiemposEspera.getOrDefault(vehiculo, 0) > 180) { // 3 segundos
            tiemposEspera.put(vehiculo, 0);
            return true;
        }

        // 6. Si hay espacio y no viene nadie del lado derecho
        if (caminoDespejado(vehiculo) && !vieneVehiculoDeLaDerecha(vehiculo)) {
            return true;
        }

        // Si no pasa ninguna regla, debe esperar
        agregarACola(vehiculo);
        return false;
    }

    // REGLA 1: Verificar conflictos con vehículos en el cruce
    private boolean hayConflictoConCruce(Vehiculo vehiculo) {
        for (Vehiculo otro : vehiculosEnCruce) {
            if (seCruzan(vehiculo.getDireccion(), vehiculo.getSiguienteDireccion(),
                    otro.getDireccion(), otro.getSiguienteDireccion())) {
                return true;
            }
        }
        return false;
    }

    // REGLA 2: El de la derecha tiene prioridad
    private boolean tieneDerechaPrioritaria(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();
        String siguienteDir = vehiculo.getSiguienteDireccion();

        // Determinar qué direcciones están a la derecha
        String[] direccionesDerecha = getDireccionesDerecha(direccion, siguienteDir);

        for (String dirDerecha : direccionesDerecha) {
            Queue<Vehiculo> cola = colasEspera.get(dirDerecha);
            if (cola != null && !cola.isEmpty()) {
                // Hay alguien a la derecha esperando
                Vehiculo derecho = cola.peek();
                if (derecho != null && !derecho.equals(vehiculo)) {
                    return false; // Debe ceder el paso
                }
            }
        }

        return true;
    }

    // REGLA 3: Quién llegó primero
    private boolean llegoPrimero(Vehiculo vehiculo) {
        String key = vehiculo.getDireccion() + "_" + vehiculo.getSiguienteDireccion();

        // Verificar todas las otras direcciones que podrían tener conflicto
        for (String otraKey : tiemposLlegada.keySet()) {
            if (seCruzan(
                    vehiculo.getDireccion(), vehiculo.getSiguienteDireccion(),
                    otraKey.split("_")[0], otraKey.split("_")[1])) {
                if (tiemposLlegada.get(otraKey) < tiemposLlegada.getOrDefault(key, Long.MAX_VALUE)) {
                    return false; // Alguien llegó antes
                }
            }
        }

        return true;
    }

    // REGLA 4: Prioridad de vía principal
    private boolean esViaPrincipal(String direccion) {
        return direccion.equals("este") || direccion.equals("oeste");
    }

    private boolean hayVehiculoEnViaPrincipalConflicto() {
        for (Vehiculo v : vehiculosEnCruce) {
            if (esViaPrincipal(v.getDireccion())) {
                return true;
            }
        }
        return false;
    }

    // REGLA 6: Verificar si viene vehículo de la derecha
    private boolean vieneVehiculoDeLaDerecha(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();
        int x = vehiculo.getX();
        int y = vehiculo.getY();
        // Determine an observation rectangle located on the right-hand approach
        // relative to the intersection (use CRUCE_* constants and approach distance)
        Rectangle zonaObservacion = null;

        int d = DISTANCIA_APROXIMACION; // how far to look from the cross
        int laneWidth = CRUCE_X2 - CRUCE_X1; // approx cross width

        switch (direccion) {
            case "este": // coming from west to east, right is vehicles coming from south
                // Look in the south approach just below the intersection
                zonaObservacion = new Rectangle(CRUCE_X1, CRUCE_Y2, CRUCE_X2 - CRUCE_X1, d);
                break;
            case "oeste": // coming from east to west, right is north
                zonaObservacion = new Rectangle(CRUCE_X1, CRUCE_Y1 - d, CRUCE_X2 - CRUCE_X1, d);
                break;
            case "norte": // coming from south to north, right is east
                zonaObservacion = new Rectangle(CRUCE_X2, CRUCE_Y1, d, CRUCE_Y2 - CRUCE_Y1);
                break;
            case "sur": // coming from north to south, right is west
                zonaObservacion = new Rectangle(CRUCE_X1 - d, CRUCE_Y1, d, CRUCE_Y2 - CRUCE_Y1);
                break;
            default:
                return false;
        }

        // Check vehicles in that approach area (use their collision boxes)
        for (Vehiculo otro : vehiculos) {
            if (otro == vehiculo)
                continue;
            if (otro.getAreaColision().intersects(zonaObservacion)) {
                return true;
            }
        }

        return false;
    }

    // Métodos auxiliares
    private boolean seCruzan(String dir1, String sigDir1, String dir2, String sigDir2) {
        // Simplificado: se cruzan si vienen de direcciones diferentes
        // y al menos uno va recto o gira a donde va el otro
        if (dir1.equals(dir2))
            return false; // Misma dirección

        // Casos de conflicto común
        return (dir1.equals("este") && dir2.equals("norte") && sigDir1.equals("sur")) ||
                (dir1.equals("este") && dir2.equals("sur") && sigDir1.equals("norte")) ||
                (dir1.equals("oeste") && dir2.equals("norte") && sigDir1.equals("sur")) ||
                (dir1.equals("oeste") && dir2.equals("sur") && sigDir1.equals("norte")) ||
                (dir1.equals("norte") && dir2.equals("este") && sigDir1.equals("oeste")) ||
                (dir1.equals("norte") && dir2.equals("oeste") && sigDir1.equals("este")) ||
                (dir1.equals("sur") && dir2.equals("este") && sigDir1.equals("oeste")) ||
                (dir1.equals("sur") && dir2.equals("oeste") && sigDir1.equals("este"));
    }

    private void agregarACola(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();
        Queue<Vehiculo> cola = colasEspera.get(direccion);

        if (cola != null && !cola.contains(vehiculo)) {
            cola.offer(vehiculo);

            // Registrar tiempo de espera
            if (!tiemposEspera.containsKey(vehiculo)) {
                tiemposEspera.put(vehiculo, 0);
            }
        }

    }

    private void cambiarFase() {
        if (fase == 0) {
            semaforos.get("este").setEstado(Semaforo.Estado.AMARILLO);
            semaforos.get("oeste").setEstado(Semaforo.Estado.AMARILLO);
            contadorFase = DURACION_AMARILLO;
            fase = 2;
            return;
        }

        if (fase == 1) {
            semaforos.get("norte").setEstado(Semaforo.Estado.AMARILLO);
            semaforos.get("sur").setEstado(Semaforo.Estado.AMARILLO);
            contadorFase = DURACION_AMARILLO;
            fase = 2;
            return;
        }

        if (fase == 2) {
            if (semaforos.get("este").esAmarillo() || semaforos.get("oeste").esAmarillo()) {
                semaforos.get("este").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("oeste").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("norte").setEstado(Semaforo.Estado.VERDE);
                semaforos.get("sur").setEstado(Semaforo.Estado.VERDE);
                fase = 1;
                contadorFase = DURACION_VERDE;
                return;
            }

            if (semaforos.get("norte").esAmarillo() || semaforos.get("sur").esAmarillo()) {
                semaforos.get("norte").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("sur").setEstado(Semaforo.Estado.ROJO);
                semaforos.get("este").setEstado(Semaforo.Estado.VERDE);
                semaforos.get("oeste").setEstado(Semaforo.Estado.VERDE);
                fase = 0;
                contadorFase = DURACION_VERDE;
                return;
            }
        }
    }

    private void forzarFaseParaDireccion(String direccion) {
        if (direccion.equals("este") || direccion.equals("oeste")) {
            semaforos.get("este").setEstado(Semaforo.Estado.VERDE);
            semaforos.get("oeste").setEstado(Semaforo.Estado.VERDE);
            semaforos.get("norte").setEstado(Semaforo.Estado.ROJO);
            semaforos.get("sur").setEstado(Semaforo.Estado.ROJO);
            fase = 0;
            contadorFase = DURACION_VERDE;
        } else {
            semaforos.get("norte").setEstado(Semaforo.Estado.VERDE);
            semaforos.get("sur").setEstado(Semaforo.Estado.VERDE);
            semaforos.get("este").setEstado(Semaforo.Estado.ROJO);
            semaforos.get("oeste").setEstado(Semaforo.Estado.ROJO);
            fase = 1;
            contadorFase = DURACION_VERDE;
        }
    }

    private void quitarDeCola(Vehiculo vehiculo) {
        String direccion = vehiculo.getDireccion();
        Queue<Vehiculo> cola = colasEspera.get(direccion);

        if (cola != null) {
            cola.remove(vehiculo);
            tiemposEspera.remove(vehiculo);
        }
    }

    private String[] getDireccionesDerecha(String direccion, String siguienteDir) {
        // Basado en la dirección y el giro, determinar qué está a la derecha
        if (siguienteDir.equals(direccion)) {
            // Va recto
            switch (direccion) {
                case "este":
                    return new String[] { "sur" };
                case "oeste":
                    return new String[] { "norte" };
                case "norte":
                    return new String[] { "este" };
                case "sur":
                    return new String[] { "oeste" };
            }
        } else if (siguienteDir.equals(getGiroDerecha(direccion))) {
            // Gira a la derecha
            switch (direccion) {
                case "este":
                    return new String[] { "sur", "oeste" };
                case "oeste":
                    return new String[] { "norte", "este" };
                case "norte":
                    return new String[] { "este", "sur" };
                case "sur":
                    return new String[] { "oeste", "norte" };
            }
        } else {
            // Gira a la izquierda - tiene que ceder a todos
            return new String[] { "este", "oeste", "norte", "sur" };
        }
        return new String[] {};
    }

    private boolean caminoDespejado(Vehiculo vehiculo) {
        // Verificar que no haya vehículos justo delante
        String direccion = vehiculo.getDireccion();
        int x = vehiculo.getX();
        int y = vehiculo.getY();

        Rectangle areaDelante = null;

        switch (direccion) {
            case "este":
                areaDelante = new Rectangle(x + vehiculo.getAncho(), y - 10, 40, vehiculo.getAlto());
                break;
            case "oeste":
                areaDelante = new Rectangle(x - 40, y - 10, 40, vehiculo.getAlto());
                break;
            case "norte":
                areaDelante = new Rectangle(x - 10, y - 40, vehiculo.getAncho(), 40);
                break;
            case "sur":
                areaDelante = new Rectangle(x - 10, y + vehiculo.getAlto(), vehiculo.getAncho(), 40);
                break;
        }

        if (areaDelante == null)
            return true;

        for (Vehiculo otro : vehiculos) {
            if (otro != vehiculo && otro.getAreaColision().intersects(areaDelante)) {
                return false;
            }
        }

        return true;
    }

    // Métodos para el manejo del cruce
    public void liberarCruceSiSale(Vehiculo vehiculo) {
        if (vehiculo.haPasadoElCruce()) {
            vehiculosEnCruce.remove(vehiculo);
            decisionesTomadas.remove(vehiculo);
            quitarDeCola(vehiculo);

            // Limpiar tiempo de llegada
            String key = vehiculo.getDireccion() + "_" + vehiculo.getSiguienteDireccion();
            tiemposLlegada.remove(key);
        }
    }

    public void incrementarEspera(Vehiculo vehiculo) {
        if (tiemposEspera.containsKey(vehiculo)) {
            tiemposEspera.put(vehiculo, tiemposEspera.get(vehiculo) + 1);
        }
    }

    // Métodos auxiliares de dirección
    private String[] getPosiblesDirecciones(String dirActual) {
        switch (dirActual) {
            case "este":
                return new String[] { "este", "norte", "sur" };
            case "oeste":
                return new String[] { "oeste", "norte", "sur" };
            case "norte":
                return new String[] { "norte", "este", "oeste" };
            case "sur":
                return new String[] { "sur", "este", "oeste" };
            default:
                return new String[] { dirActual };
        }
    }

    private String getGiroDerecha(String dirActual) {
        switch (dirActual) {
            case "este":
                return "sur";
            case "oeste":
                return "norte";
            case "norte":
                return "este";
            case "sur":
                return "oeste";
            default:
                return dirActual;
        }
    }

    private String getGiroIzquierda(String dirActual) {
        switch (dirActual) {
            case "este":
                return "norte";
            case "oeste":
                return "sur";
            case "norte":
                return "oeste";
            case "sur":
                return "este";
            default:
                return dirActual;
        }
    }

    private boolean esDireccionValida(String dir, String[] posibles) {
        for (String posible : posibles) {
            if (posible.equals(dir)) {
                return true;
            }
        }
        return false;
    }

    // Variables para dibujado
    private List<Vehiculo> vehiculos = new ArrayList<>();

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    // Dibujar información del cruce
    public void dibujar(Graphics2D g) {
        // Dibujar zona del cruce
        g.setColor(new Color(255, 255, 0, 30));
        g.fillRect(CRUCE_X1, CRUCE_Y1, CRUCE_X2 - CRUCE_X1, CRUCE_Y2 - CRUCE_Y1);
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));
        g.drawRect(CRUCE_X1, CRUCE_Y1, CRUCE_X2 - CRUCE_X1, CRUCE_Y2 - CRUCE_Y1);

        

        int luzTam = 12;
        Semaforo se = semaforos.get("este");
        Semaforo so = semaforos.get("oeste");
        Semaforo sn = semaforos.get("norte");
        Semaforo ss = semaforos.get("sur");

        int cx = (CRUCE_X1 + CRUCE_X2) / 2;
        int cy = (CRUCE_Y1 + CRUCE_Y2) / 2;

        if (se != null) {
            if (se.esVerde())
                g.setColor(Color.GREEN);
            else if (se.esAmarillo())
                g.setColor(Color.YELLOW);
            else
                g.setColor(Color.RED);
            g.fillOval(CRUCE_X1 - 20, cy - 40, luzTam, luzTam);
        }
        if (so != null) {
            if (so.esVerde())
                g.setColor(Color.GREEN);
            else if (so.esAmarillo())
                g.setColor(Color.YELLOW);
            else
                g.setColor(Color.RED);
            g.fillOval(CRUCE_X2 + 8, cy + 20, luzTam, luzTam);
        }
        if (sn != null) {
            if (sn.esVerde())
                g.setColor(Color.GREEN);
            else if (sn.esAmarillo())
                g.setColor(Color.YELLOW);
            else
                g.setColor(Color.RED);
            g.fillOval(cx + 20, CRUCE_Y1 - 20, luzTam, luzTam);
        }
        if (ss != null) {
            if (ss.esVerde())
                g.setColor(Color.GREEN);
            else if (ss.esAmarillo())
                g.setColor(Color.YELLOW);
            else
                g.setColor(Color.RED);
            g.fillOval(cx - 32, CRUCE_Y2 + 8, luzTam, luzTam);
        }

    }

   
    // En ReglasCruce.java, añadir este método

    public void dibujarPanelLateral(Graphics2D g, int x, int y) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));

        // Estado del cruce
        String estadoCruce = vehiculosEnCruce.isEmpty() ? "LIBRE" : "OCUPADO";
        Color colorEstado = vehiculosEnCruce.isEmpty() ? Color.GREEN : Color.YELLOW;

        g.setColor(colorEstado);
        g.drawString("Estado: " + estadoCruce, x, y);
        y += 20;

        g.setColor(Color.WHITE);
        g.drawString("Vehículos en cruce: " + vehiculosEnCruce.size(), x, y);
        y += 20;
        g.setColor(Color.WHITE);
        g.drawString("Semáforos:", x, y);
        y += 18;
        Semaforo se = semaforos.get("este");
        Semaforo so = semaforos.get("oeste");
        Semaforo sn = semaforos.get("norte");
        Semaforo ss = semaforos.get("sur");
        g.drawString("  Este: " + (se != null ? se.getEstado().name() : "NA"), x + 10, y);
        y += 16;
        g.drawString("  Oeste: " + (so != null ? so.getEstado().name() : "NA"), x + 10, y);
        y += 16;
        g.drawString("  Norte: " + (sn != null ? sn.getEstado().name() : "NA"), x + 10, y);
        y += 16;
        g.drawString("  Sur: " + (ss != null ? ss.getEstado().name() : "NA"), x + 10, y);
        y += 20;
        
        
        // Colas de espera
        x = 400; 
        y = 650;
        g.setColor(Color.CYAN);
        g.drawString("Colas de espera:", x, y);
        y += 20;

        g.setColor(Color.WHITE);
        g.drawString("  Este: " + colasEspera.get("este").size(), x + 10, y);
        y += 18;
        g.drawString("  Oeste: " + colasEspera.get("oeste").size(), x + 10, y);
        y += 18;
        g.drawString("  Norte: " + colasEspera.get("norte").size(), x + 10, y);
        y += 18;
        g.drawString("  Sur: " + colasEspera.get("sur").size(), x + 10, y);
        
}
}
