#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27,16,2);

int led = 2;
const String fingerprint = "48a29b7dcf0559676bde7ddf0ce8e96404a35011d3eeb18debf0896d8eca59b0";

bool isActive = false;
bool isPerforming = false;

const String a1 = "0010";


void setup() {
  pinMode(led, OUTPUT);
  pinMode(4, OUTPUT);
  lcd.init();
  lcd.backlight();
  Serial.begin(115200);
  lcd.setCursor(3,0);
  lcd.print("SPARKLE DI");
  lcd.setCursor(0,1);
  lcd.print("Initializing...");
  digitalWrite(led, HIGH);
  for(int i=0; i<=10; i++){
    digitalWrite(led, LOW);
    delay(250);
    digitalWrite(led, HIGH);
    delay(250);
  }
  delay(1000);
  digitalWrite(led, LOW);
  Serial.println("Started...");
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("No Connection.");
}

void loop() {
  
  if(Serial.available()){
    String cmd = Serial.readString();
    if(cmd.equals("1")){
      Serial.println(a1);
      delay(1000);
      getVerification();
      delay(1500);
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Connected.");
    }
    else if(cmd.equals("2")){
      disconnect();
    }else if(cmd.length() > 1){
      if(isActive == true && isPerforming == false){
        String opreatorCommand = cmd.substring(0,1);
        if(opreatorCommand.equals("3")){
          int l = cmd.length();
          String number = cmd.substring(2,l);
          isPerforming = true;
          perfromAction(number);
          isPerforming = false;
        }
      }
    }
  }
}

void perfromAction(String number){
  String toPrint = "Current " + number;
  lcd.setCursor(0, 1);
  lcd.print(toPrint);
  digitalWrite(4,HIGH);
  digitalWrite(led, HIGH);
  delay(10000);
  digitalWrite(4, LOW);
  digitalWrite(led, LOW);
  lcd.setCursor(0,1);
  lcd.print("                ");
}

void getVerification(){
  if(Serial.available()){
    String cmd = Serial.readString();
    if(cmd == fingerprint ){
      isActive = true;
      Serial.println("1110011");
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Connection");
      lcd.setCursor(0, 1);
      lcd.print("Success");
    }else{
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Cannot make");
      lcd.setCursor(0, 1);
      lcd.print("Connection");
    }
  }
}

void disconnect(){
  isActive = false;
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Disconnecting...");
  delay(1500);
  lcd.setCursor(0, 0);
  lcd.print("Disconnected...");
  delay(1500);
  lcd.setCursor(0, 0);
  lcd.print("No Connection.  ");
}
