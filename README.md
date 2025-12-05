🚦 Simulador de Tráfico Inteligente

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![License](https://img.shields.io/badge/License-MIT-blue)
![Status](https://img.shields.io/badge/Status-Activo-brightgreen)

Un simulador de tráfico urbano desarrollado en Java que modela el comportamiento de vehículos en un cruce de cuatro vías, implementando detección de colisiones y reglas de prioridad.


### 🎮 Funcionalidades Principales
- **Simulación en tiempo real** de tráfico vehicular
- **Detección de colisiones** con áreas direccionales (75x90 N/S, 90x75 E/O)
- **Sistema de reglas de prioridad** en cruces sin semáforos
- **Generación automática** de vehículos con comportamientos diversos
- **Interfaz gráfica** con Java Swing
- **Controles en tiempo real** para manipular la simulación

### 🛠️ Características Técnicas
- Arquitectura modular y extensible
- Sistema de sprites con 4 tipos de vehículos
- Mapa urbano detallado con calles y edificios
- Estadísticas en tiempo real
- Modo debug para visualizar áreas de colisión
- Totalmente portable (funciona en cualquier PC)

## 🖼️ Pantalla

<img width="1477" height="997" alt="image" src="https://github.com/user-attachments/assets/5f83813d-9b12-4a8f-ac1d-e7678b9274f9" />


## ⚙️ Requisitos

### Requisitos Mínimos del Sistema
- **Sistema Operativo**: Windows 10/11, macOS 10.14+, Linux
- **Java**: JDK 17 o superior
- **Memoria RAM**: 2 GB mínimo
- **Espacio en disco**: 50 MB

### Dependencias
- Java SE Development Kit 17+
- No se requieren bibliotecas externas

## 📥 Instalación

### Método 1: Clonar y Compilar
```bash
# 1. Clonar el repositorio
git clone [https://github.com/tuusuario/simulador-trafico.git](https://github.com/Skyne-dev/simulador-de-trafico-Inteligente)

# 2. Navegar al directorio
cd simulador-trafico

# 3. Compilar (si usas NetBeans/Eclipse)
# El proyecto ya incluye los archivos .class compilados
Método 2: Ejecutar JAR Directamente
Descarga el archivo SimuladorTrafico.jar desde Releases

Crea una carpeta llamada sprites en el mismo directorio

Coloca los archivos PNG de sprites en la carpeta sprites/

Ejecuta: java -jar SimuladorTrafico.jar

Estructura de Carpetas Requerida
text
simulador-trafico/
├── SimuladorTrafico.jar
└── sprites/
    ├── verde.png
    ├── camioneta.png
    ├── rojo.png
    └── rosa.png
🎮 Uso
Controles del Teclado
Tecla	Función
A	Agregar 3 vehículos nuevos
C	Limpiar todos los vehículos
H	Mostrar/ocultar áreas de colisión
S	Activar/desactivar reglas de cruce
SPACE	Resetear reglas del cruce
R	Resetear vehículo en colisión
P	Pausar/Reanudar simulación
Iniciar la Simulación
Ejecuta el programa

Los vehículos comenzarán a generarse automáticamente

Usa los controles para manipular la simulación

Observa las estadísticas en tiempo real

📁 Estructura del Proyecto
text
src/
├── com/mycompany/mavenproject8/
│   ├── Mavenproject8.java      # Clase principal
│   ├── Vehiculo.java           # Modelo de vehículo
│   ├── ReglasCruce.java        # Sistema de reglas
│   └── MapaPanel.java          # Renderizado del mapa
sprites/                        # Recursos gráficos
├── verde.png
├── camioneta.png
├── rojo.png
└── rosa.png
README.md                       # Este archivo
SimuladorTrafico.jar            # Ejecutable
Descripción de Clases Principales
Mavenproject8: Clase principal que gestiona la simulación

Vehiculo: Representa cada vehículo con propiedades y comportamiento

ReglasCruce: Implementa las reglas de prioridad en intersecciones

MapaPanel: Se encarga del renderizado del entorno urbano

🛠️ Tecnologías
https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
https://img.shields.io/badge/Java_Swing-4A90E2?style=for-the-badge
https://img.shields.io/badge/Java_AWT-FF6B6B?style=for-the-badge

Lenguaje: Java 17

Interfaz Gráfica: Java Swing, AWT

Paradigma: Programación Orientada a Objetos

Gestión de Proyecto: Maven (opcional)


👤 Autores
Orlando Cabrera,  Samuel Gonzales

GitHub: @Skyne-dev

🙏 Agradecimientos
A los profesores por la orientación académica

A la comunidad de Java por los recursos de aprendizaje

⭐ Si este proyecto te resultó útil, ¡dale una estrella en GitHub!
