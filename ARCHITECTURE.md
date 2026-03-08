# Guía de Arquitectura y Mejores Prácticas

## 📚 Documentación del Proyecto

Este microservicio implementa las siguientes mejores prácticas de desarrollo:

### 1. ✅ Documentación Clara del Código

#### JavaDoc Completo
- **Todas las clases públicas** tienen documentación JavaDoc
- **Todos los métodos públicos** están documentados con:
  - Descripción del propósito
  - Parámetros con `@param`
  - Valor de retorno con `@return`
  - Excepciones con `@throws` cuando aplica

#### Archivos Documentados
- `ProductServiceController.java` - Endpoints REST
- `ProductService.java` y `ProductServiceImpl.java` - Lógica de negocio
- `InventoryClient.java` - Cliente HTTP reactivo
- `GlobalExceptionHandler.java` - Manejo centralizado de errores
- `ProductMapper.java` - Conversión entre entidades y DTOs
- `ProductEntity.java` - Entidad JPA
- `ProductRepository.java` - Acceso a datos
- `WebClientConfig.java` - Configuración de clientes HTTP
- `InventoryResponseDto.java` - DTO con JavaDoc en record

#### Configuración Documentada
- `application.properties` - Configuración base con comentarios explicativos
- `application-dev.properties` - Perfil de desarrollo
- `application-test.properties` - Perfil de pruebas
- `application-prod.properties` - Perfil de producción

---

### 2. ✅ Buenas Prácticas de Desarrollo

#### Separación de Responsabilidades (Layered Architecture)
```
Controller → Service → Repository
              ↓
           Client (externo)
```

#### Principios SOLID
- **Single Responsibility**: Cada clase tiene una única responsabilidad
- **Open/Closed**: Interfaces permiten extensión sin modificación
- **Dependency Inversion**: Inyección de dependencias por constructor

#### Constantes Centralizadas
```java
private static final String MSG_PRODUCT_NAME_REQUIRED = "Product name is required";
private static final String MSG_PRODUCT_PRICE_REQUIRED = "Product price must be greater than 0";
```

#### Logging Estructurado
- **INFO**: Operaciones principales (creación, consulta de productos)
- **DEBUG**: Detalles de flujo (inicio de operaciones)
- **WARN**: Situaciones anormales pero manejables (producto no encontrado)
- **ERROR**: Errores críticos (fallos de servicio externo, errores inesperados)

#### Validaciones Robustas
- Uso de `Optional` para manejo seguro de null
- Validación de entrada con `Consumer` y `Predicate`
- Mensajes de error descriptivos

#### Programación Funcional
- Uso de `Supplier`, `Consumer`, `Predicate`
- Lambdas en lugar de clases anónimas
- Streams para manipulación de colecciones
- Method references (`productMapper::toResponse`)

---

### 3. ✅ Manejo Apropiado de Excepciones

#### Excepciones Personalizadas
- `ProductNotFoundException` - Cuando un producto no existe
- `InventoryServiceException` - Cuando hay problemas con el servicio de inventario

#### Jerarquía de Manejo
```
GlobalExceptionHandler (@RestControllerAdvice)
├── ProductNotFoundException → 404 NOT_FOUND
├── InventoryServiceException → 503 SERVICE_UNAVAILABLE
├── IllegalArgumentException → 400 BAD_REQUEST
├── IllegalStateException → 500 INTERNAL_SERVER_ERROR
└── Throwable → 500 INTERNAL_SERVER_ERROR (fallback)
```

#### Manejo en Flujos Reactivos
```java
.onErrorMap(this::mapUnexpectedError)  // Transforma errores inesperados
.doOnError(error -> LOGGER.error(...)) // Log de errores
```

#### Respuestas de Error Estandarizadas
```json
{
  "message": "Product not found with id: 999",
  "status": 404,
  "timestamp": "2026-03-08T10:30:00Z"
}
```

---

### 4. ✅ Estructura de Proyecto Organizada

```
com.challenge.product/
├── client/                    # Clientes HTTP externos
│   ├── dto/                  # DTOs de comunicación externa
│   └── InventoryClient.java
├── config/                    # Configuraciones Spring
│   └── WebClientConfig.java
├── controller/                # Controllers REST (capa de presentación)
│   └── ProductServiceController.java
├── entity/                    # Entidades JPA (capa de persistencia)
│   └── ProductEntity.java
├── exception/                 # Excepciones y handlers globales
│   ├── GlobalExceptionHandler.java
│   ├── InventoryServiceException.java
│   └── ProductNotFoundException.java
├── mapper/                    # Mappers (MapStruct)
│   └── ProductMapper.java
├── model/                     # DTOs generados por OpenAPI
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   ├── ProductWithStockResponse.java
│   └── ErrorResponse.java
├── repository/                # Repositorios JPA (acceso a datos)
│   └── ProductRepository.java
├── service/                   # Interfaces de servicio (contratos)
│   ├── ProductService.java
│   └── impl/                 # Implementaciones de servicios
│       └── ProductServiceImpl.java
└── ProductServiceApplication.java  # Clase principal
```

---

### 5. ✅ Configuración Adecuada de Dependencias

#### Dependencias Principales
```gradle
// Framework reactivo
spring-boot-starter-webflux

// Persistencia JPA
spring-boot-starter-data-jpa

// Validación
spring-boot-starter-validation

// Documentación OpenAPI
springdoc-openapi-starter-webflux-ui

// Mapeo de objetos
mapstruct

// Base de datos H2
h2

// Reducción de boilerplate
lombok

// Testing reactivo
reactor-test
```

#### Herramientas de Calidad de Código
- **JaCoCo**: Cobertura de código (mínimo 70%)
- **Checkstyle**: Análisis estático de código
- **JUnit 5**: Framework de pruebas unitarias
- **Mockito**: Mocking para pruebas

#### Generación de Código
- **OpenAPI Generator**: Genera modelos y API contracts desde YAML

---

## 🔧 Comandos Útiles

### Compilar y Ejecutar Tests
```powershell
cd D:\reto\idm\challenge-product-service
.\gradlew.bat clean build
```

### Generar Reporte de Cobertura
```powershell
.\gradlew.bat test jacocoTestReport
# Ver en: build/reports/jacoco/test/html/index.html
```

### Ejecutar Checkstyle
```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

### Generar JavaDoc
```powershell
.\gradlew.bat javadoc
# Ver en: build/docs/javadoc/index.html
```

### Ejecutar con Perfil Específico
```powershell
# Desarrollo
.\gradlew.bat bootRun --args='--spring.profiles.active=dev'

# Producción
.\gradlew.bat bootRun --args='--spring.profiles.active=prod'
```

---

## 📊 Cobertura de Tests

El proyecto incluye:
- ✅ Tests unitarios de servicios (`ProductServiceImplTest.java`)
- ✅ Tests de integración de controllers (`ProductServiceControllerTest.java`)
- ✅ Tests de exception handler (`GlobalExceptionHandlerTest.java`)
- ✅ Tests de cliente externo (`InventoryClientTest.java`)

**Objetivo**: Mantener cobertura de código > 70%

---

## 🎯 Características Implementadas

### Programación Reactiva (WebFlux)
- ✅ Endpoints no bloqueantes con `Mono` y `Flux`
- ✅ WebClient para llamadas HTTP asíncronas
- ✅ Schedulers para operaciones bloqueantes (JPA)

### Programación Funcional (Java 17+)
- ✅ Optional para manejo seguro de null
- ✅ Supplier, Consumer, Predicate
- ✅ Streams con filter, map, toList
- ✅ Lambdas y method references
- ✅ Records para DTOs inmutables

### Contract-First con OpenAPI
- ✅ Especificación en `product-api.yaml`
- ✅ Generación automática de modelos
- ✅ Documentación Swagger UI en `/swagger-ui.html`

---

## 🔐 Seguridad y Mejores Prácticas

1. **No exponer detalles internos** en mensajes de error 500
2. **Logging apropiado** sin exponer información sensible
3. **Validación de entrada** antes de procesar
4. **Manejo de errores en cascada** sin propagar excepciones sensibles
5. **Configuración por perfiles** para diferentes ambientes
6. **Separación de concerns** en capas bien definidas

---

## 📈 Métricas de Calidad

- ✅ Cobertura de código: >70% (JaCoCo)
- ✅ Análisis estático: Checkstyle configurado
- ✅ JavaDoc: 100% en clases públicas
- ✅ Tests: Unitarios + Integración
- ✅ Logging: Niveles apropiados en todas las capas

