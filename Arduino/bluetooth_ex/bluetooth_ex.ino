/*
 * Bluetooth-Modul anschliessen: +5, GND, 
 * Tx an Arduino Rx(Pin 0)
 * Rx an Arduino Tx(Pin 1)
 * 
 * Per Handy mit App 'ArduDroid by Techbitar':
 * -Menü 'Connect me to a Bluetooth device
 * -Send Data "A" oder "B" schaltet die LED auf Pin 13 aus bzw. an
 * und gibt Rückmeldung 'LED:on' oder 'LEF:off'.
 * 
 * https://www.youtube.com/watch?v=sXs7S048eIo
 * 
 * Alternativ mit JavaCode: projekt 'MrBlue'
 */

int ledPin = 13;
int cmd = -1;
int flag = 0;

void setup() {
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  Serial.begin(9600);
}

void loop() {
  if (Serial.available() > 0) {
    cmd = Serial.read();
    flag = 1;
  }

  if (flag == 1) {
    if (cmd == '0') {
      digitalWrite(ledPin, LOW);
      Serial.println("LED: off");
    } else if (cmd == '1') {
      digitalWrite(ledPin, HIGH);
      Serial.println("LED: on");
    } else {
      Serial.print("unknown command: ");
      Serial.write(cmd);
      Serial.print(" (");
      Serial.print(cmd, DEC);
      Serial.print(")");

      Serial.println();
    }

    flag = 0;    
    cmd = 65;
  }

  Serial.flush();
  delay(100);
}
