#include <DHT11.h>    

int pin=3; 
DHT11 dht11(pin);        
int delay_time = 3000;

int Vo = A0;
int V_LED = 2;

float Vo_value = 0;
float Voltage = 0;
float dustDensity = 0;
float dustCleanVoltage = 0.44;
int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;

void setup() {
   Serial.begin(9600); 

  pinMode(V_LED, OUTPUT);
  pinMode(Vo, INPUT);
}
 
void loop() {
  temp();
  dust();
  Serial.println();
  delay(delay_time);                  
}

void temp() {
  int err;
  float temp, humi;
  if((err=dht11.read(humi, temp))==0)
  {
    //Serial.print("temperature:");
    //Serial.print(temp);
    //Serial.print(" humidity:");
    //Serial.print(humi);
    //Serial.println();
    Serial.print("t<");
    Serial.print(temp);
    Serial.print(">");
    Serial.print("h<");
    Serial.print(humi);
    Serial.print(">");
  } else              
  {
    Serial.println();
    Serial.print("Error No :");
    Serial.print(err);
    Serial.println();    
  }
}


void dust(){
  digitalWrite(V_LED,LOW); //ired 'on'
  delayMicroseconds(samplingTime);
  Vo_value = analogRead(Vo); //read the dust valu6e
  delayMicroseconds(deltaTime);// pulse width 0.32 - 0.28 = 0.04 msec
  
  digitalWrite(V_LED,HIGH); //ired 'off'
  delayMicroseconds(sleepTime);
  
  
  Voltage = Vo_value * (5.0 / 1024.0);
  
  dustDensity = (Voltage - dustCleanVoltage)/0.005;
  
  if(dustDensity < 0) {
    dustDensity = 0;
  }
  
    Serial.print("d<");
    Serial.print(dustDensity );
    Serial.print(">");
  /*
  Serial.print(" Raw Signal Value (0-1023):");
  Serial.print(Vo_value);
  Serial.print(" | Volatage:");
  Serial.print(Voltage);
  Serial.print(" | Dust Density:");
  Serial.print(dustDensity);
  Serial.print("[ug/m3]");
  if(dustDensity<31){
  Serial.println(" => [Great(~30)]");
  } else if(dustDensity>30 && dustDensity<81){
  Serial.println(" => [Good(31~80)]");
  } else if(dustDensity>81 && dustDensity<151){
  Serial.println(" => [Bad(81~150)]");
  } else{
  Serial.println(" => [Too Bad(151~)]");
  }
  
  // set the cursor to column 0, line 1
  // (note: line 1 is the second row, since counting begins with 0):
  if(dustDensity<31){
  Serial.print(": Great(~30)");
  } else if(dustDensity>30 && dustDensity<81){
  Serial.print(": Good(31~80)");
  } else if(dustDensity>81 && dustDensity<151){
  Serial.print(": Bad(81~150)");
  } else{
  Serial.print(": Too Bad(151~)");
  }
  Serial.println(" ");*/
}

