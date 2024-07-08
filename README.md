# CoDriving

## Descripción
El proyecto Codrive, propone desarrollar una aplicación móvil para dispositivos Android que gestione el acceso y uso de vehículos ofrecidos en préstamo. La aplicación tiene como objetivo mejorar la movilidad al ofrecer una plataforma segura y eficiente para compartir vehículos, promoviendo un modelo de transporte sostenible. Se utilizará una metodología ágil para el diseño, enfocada en la experiencia del usuario, la seguridad y la facilidad de uso. Los objetivos clave incluyen facilitar el acceso a vehículos compartidos, optimizar su uso para reducir tiempos de inactividad, contribuir a una movilidad más sostenible y ofrecer una experiencia segura mediante tecnologías avanzadas y protocolos estrictos.

## Tecnologías utilizadas

* Kotlin
* Android
* Firebase
* SDK Android
* Dagger/Hilt

## Contenido

* Presentación
* Funciones.
* Ejecución.
* Tecnologías

## Presentación

El objetivo principal de CoDrive como propuesta de Trabajo Fin de Grado es desarrollar una aplicación móvil para gestionar préstamos de vehículos, integrando habilidades en programación, diseño de sistemas, y experiencia de usuario. A diferencia de otras plataformas mencionadas anteriormente, CoDrive se enfoca en promover prácticas eco-amigables y garantizar la accesibilidad.

## Funciones

* Registro de servicios (alquileres)
* CRUD de publicaciones
* CRUD de automóviles
* Historial de alquileres activos e inactivos.
* Sistema de mensajería en tiempo real.
* Sistema de notificaciones interactivas.
* Búsqueda y filtro
* Autocompletado de localizaciones.
* Sistema de reviews seguros.
* Sistema de scroll infinito.

## Ejecución

Si queremos ejecutarlo solo desde Android Studios, necesitamos hacer los siguientes pasos:

* Instalamos en la web oficinal Android Studios el [IDE Giraffe](https://developer.android.com/studio/releases/past-releases/as-giraffe-release-notes?hl=es-419) (en mi caso descargué Giraffe)
* Tenemos que clonar este repositorio
* En build.gradle.kts coopiamos Y pegamos lo siguiente.
```kotlin
//Fuera de android{}
val geocode: Stirng by project

//dentro de android{}

val geocodeApiKey = project.findProperty("geocode")+
buildConfigField("String", "GEOCODE_API_KEY", "\"${geocodeApiKey}\"")

```
* Luego compilamos y ejecutamos
## Objetivos Personales
La creación de CoDriving ha permitido la adquisición de valiosas experiencias en el establecimiento de criterios para la toma de decisiones durante todo el ciclo de desarrollo de una aplicación de software. Este proyecto, diseñado para ser completamente exportable a otras plataformas, ha incrementado mi comprensión del ecosistema de desarrollo, abarcando herramientas como Android Studio, Jetpack y los Frameworks empleados.

Este README proporciona una visión general del proyecto CODriving, detallando su propósito, funcionalidades, tecnologías utilizadas y cómo ejecutarlo. 
