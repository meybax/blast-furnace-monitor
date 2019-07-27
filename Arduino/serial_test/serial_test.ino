int led = 13;
boolean led_stat;

int tempSign = 2;
int pressSign = 3;
int ledSign = 4;

void setup() {
  // put your setup code here, to run once:
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
  led_stat = false;
  
  Serial.begin(9600);
  delay(100);
  Serial.println("START");
  Serial.flush();
  Serial.println("REMOTE");
  Serial.flush();

  randomSeed(analogRead(0));
}

void loop() {
  // put your main code here, to run repeatedly
  if (Serial.available() > 0) {
    int currByte = Serial.read();
    if (currByte == ledSign) {
      if (led_stat) {
        digitalWrite(led, LOW);
        led_stat = false;
      } else {
        digitalWrite(led, HIGH);
        led_stat = true;
      }
    } else if (currByte == tempSign) {
      long newTemp = random(30, 100);
      Serial.println((String) "TEMP:" + millis() + ":" + newTemp + ":");
    } else if (currByte == pressSign) {
      long newPress = random(15);
      Serial.println((String) "PRESS:" + millis() + ":" + newPress + ":");
    } else {
      Serial.println("Unknown Arduino Signal: " + currByte);
    }
    Serial.flush();
  }
}
