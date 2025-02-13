Proyecto Divisa 

Julio César Lara Lerma
Juan Vazquez Paniagua



Descripción de la App.
Actividad # 1
En este proyecto deberás crear un aplicación Android  que sincroniza de manera automática, cada hora en un día, el tipo de cambio de divisas haciendo uso del servicio web (https://www.exchangerate-api.com/docs/free).
Los datos sincronizados diariamente deberán almacenarse en una base de datos de SQLite. 

Parte # 2
La aplicación deberá implementar un componente ContentProvider que permita acceder a la información de la base de datos.  
Diseñar un cliente, una app Android con interfaz de usuario, que liste el tipo de cambio por día registrado. Los datos que mostrará la app deberán ser obtenidos a través del componente ContentProvider diseñado en el paso anterior.

Especificaciones técnicas:
La sincronización de datos debe implementarse haciendo uso de las librerías  WokManager, Retrofit.
La compartición de datos se deberá  implementar  con un componente ContentProvider seguro (manejo de permisos).

Nota: El proyecto podrá realizarse en pareja.
Al corte de la entrega de la actividad, solo se entrega la actividad # 1
