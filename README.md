# challenge-product-service

Microservicio de productos construido con Java 17, Spring Boot 3 y WebFlux.

## 🚀 Características

- ✅ API REST reactiva con Spring WebFlux
- ✅ Programación funcional con Optional, Supplier, Consumer, Predicate
- ✅ Contract-First con OpenAPI 3.0
- ✅ WebClient para comunicación HTTP no bloqueante
- ✅ Manejo robusto de excepciones customizadas
- ✅ Logging estructurado en todas las capas
- ✅ Cobertura de código con JaCoCo
- ✅ Análisis estático con Checkstyle
- ✅ Tests unitarios y de integración

## 📁 Estructura del proyecto

- `src/main/java/com/challenge/product/controller`: endpoints reactivos
- `src/main/java/com/challenge/product/service`: contrato de negocio
- `src/main/java/com/challenge/product/service/impl`: implementacion del servicio
- `src/main/java/com/challenge/product/repository`: acceso a datos JPA
- `src/main/java/com/challenge/product/client`: clientes HTTP externos
- `src/main/java/com/challenge/product/exception`: excepciones y manejo global
- `src/main/java/com/challenge/product/config`: configuraciones Spring
- `src/main/resources/openapi/product-api.yaml`: contrato OpenAPI
- `build/generated`: codigo generado por OpenAPI (no editar manualmente)

## ✅ Criterios de calidad aplicados

### 1. Documentacion clara del codigo
- JavaDoc en clases y metodos clave de `src/main/java` y pruebas principales
- Comentarios explicativos en `application.properties`
- Documentación de arquitectura en `ARCHITECTURE.md`

### 2. Buenas practicas de desarrollo
- Mensajes de validacion centralizados como constantes
- Controlador alineado al contrato OpenAPI (`POST /products` responde 201)
- Separacion por capas (controller, service, repository, mapper, exception, client)
- Inyección de dependencias por constructor
- Uso de Lombok para reducir boilerplate
- Logging estructurado con SLF4J en todas las capas (Controller, Service, Client)
- Configuración por perfiles (dev, test, prod)
- Tests unitarios y de integración con alta cobertura

### 3. Manejo apropiado de excepciones
- `GlobalExceptionHandler` mapea errores de negocio a 400/404/503
- Excepciones customizadas: `ProductNotFoundException`, `InventoryServiceException`
- Errores internos se registran y responden con mensaje generico 500
- Flujos reactivos con `onErrorMap` para encapsular errores inesperados
- Logging diferenciado por severidad (INFO, WARN, ERROR)

### 4. Estructura organizada
- Paquetes por responsabilidad y OpenAPI como fuente del contrato
- Separación clara entre capa de presentación, negocio y persistencia
- DTOs separados para clientes externos

### 5. Configuracion de dependencias
- `spring-boot-starter-webflux` para endpoints reactivos
- `spring-boot-starter-data-jpa` para persistencia
- `mapstruct` para mapeo eficiente de objetos
- `reactor-test` para testing reactivo
- `jacoco` para cobertura de código (70% mínimo)
- `checkstyle` para análisis estático de código
- Configuracion de `javadoc` enfocada en codigo fuente propio

## 🏗️ Tecnologías Utilizadas

- **Java 17** con características modernas (Records, var, etc.)
- **Spring Boot 3.5.11** con WebFlux
- **Project Reactor** para programación reactiva
- **MapStruct 1.5.5** para mapeo declarativo
- **Lombok** para reducir boilerplate
- **H2 Database** para persistencia en memoria
- **OpenAPI Generator** para contract-first development
- **JaCoCo** para cobertura de código
- **Checkstyle** para calidad de código
- **JUnit 5** y **Mockito** para testing

## 🔧 Ejecucion local

### Compilar y ejecutar tests
```powershell
Set-Location 'D:\reto\idm\challenge-product-service'
.\gradlew.bat clean build --no-daemon
```

### Generar reporte de cobertura
```powershell
.\gradlew.bat test jacocoTestReport
```

### Ejecutar análisis de código
```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

### Generar documentación JavaDoc
```powershell
.\gradlew.bat javadoc --no-daemon
```

### Ejecutar aplicación con perfil específico
```powershell
# Desarrollo
.\gradlew.bat bootRun --args='--spring.profiles.active=dev'

# Producción
.\gradlew.bat bootRun --args='--spring.profiles.active=prod'
```

## 📊 Reportes Generados

- **JavaDoc**: `build/docs/javadoc/index.html`
- **JaCoCo Coverage**: `build/reports/jacoco/test/html/index.html`
- **Checkstyle**: `build/reports/checkstyle/main.html`
- **Test Results**: `build/reports/tests/test/index.html`

## 🧪 Tests Incluidos

1. **ProductServiceImplTest** - Tests unitarios de lógica de negocio
2. **ProductServiceControllerTest** - Tests de integración de endpoints
3. **GlobalExceptionHandlerTest** - Tests de manejo de excepciones
4. **InventoryClientTest** - Tests de cliente HTTP

## 📖 Documentación Adicional

Ver `ARCHITECTURE.md` para detalles completos sobre:
- Arquitectura del proyecto
- Patrones de diseño implementados
- Guías de desarrollo
- Mejores prácticas aplicadas
