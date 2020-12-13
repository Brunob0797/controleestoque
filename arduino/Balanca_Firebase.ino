// INCLUSÃO DE BIBLIOTECAS
#include <HX711.h>
#include <WiFi.h>
#include <FirebaseESP32.h>

// DEFINIÇÕES DE PINOS
#define pinDT  13
#define pinSCK  12

//Credenciais Firebase
#define FIREBASE_HOST "https://controleestoque-90543.firebaseio.com/"
#define FIREBASE_AUTH "e78h9sQRX0PkOwoXPashyoTEMtxGFJLx7DgUCyMG"

// INSTANCIANDO OBJETOS
HX711 scale;

//Conectando WIFI
const char* ssid = "***";
const char* password = "***";

//acessando o DB especifico
String node = "/devices";

//Objetos do firebase
FirebaseData firebaseData;
FirebaseJson json;

// DECLARAÇÃO DE VARIÁVEIS
float medida = 0;
int medida_anterior = 0;
int medida_int;


void setup() {
  Serial.begin(115200);

  //Faz a conexão no wifi
  WiFi.begin(ssid,password);

  while(WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(300);
    }

  //Conecta no firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  scale.begin(pinDT, pinSCK); // CONFIGURANDO OS PINOS DA BALANÇA
  scale.set_scale(-359500); // LIMPANDO O VALOR DA ESCALA

  delay(2000);
  scale.tare(); // ZERANDO A BALANÇA PARA DESCONSIDERAR A MASSA DA ESTRUTURA

  Serial.println("Balança Zerada");
  leitura();
  json.set("/device1/massadev", medida_int);
  Firebase.updateNode(firebaseData, node, json);
}

void loop() {

  int contador = 0;
  leitura();

  if(medida_int - medida_anterior > 15 || medida_int - medida_anterior < -15){
    while(contador < 2){
      leitura();
      contador++;
    }
    
    json.set("/device1/massadev", medida_int);
    Firebase.updateNode(firebaseData, node, json);
    }
  Serial.println(medida_int);  
  medida_anterior = medida_int;
  
}

void leitura(){
  medida = scale.get_units(20); // SALVANDO NA VARIAVEL O VALOR DA MÉDIA DE 20 MEDIDAS
      medida_int = medida*1000;
    
      scale.power_down(); // DESLIGANDO O SENSOR
      delay(200); // AGUARDA 200 ms
      scale.power_up(); // LIGANDO O SENSOR
  }
