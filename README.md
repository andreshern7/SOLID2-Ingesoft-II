# Laboratorio L2: SOLID — Backend de Banco Andino

Ingeniería de Software II · Universidad Nacional de Colombia · Sede Bogotá · 2026

**Integrantes:**
- Jorge Andrés Hernández Garcia
- Juan Camilo

**Lenguaje elegido:** Java 21

## Cómo ejecutar

Con Maven:

```bash
mvn compile exec:java
```

Sin Maven (solo JDK):

```bash
javac -encoding UTF-8 -d out src/main/java/*.java
java -Dstdout.encoding=UTF-8 -cp out Main
```

## Bloque 0 — Arranque

Se copió el código base entregado por el docente (11 archivos) sin modificar su diseño.
La salida del programa principal quedó guardada en `salida_original.txt` y servirá como
prueba de caracterización durante la refactorización.
