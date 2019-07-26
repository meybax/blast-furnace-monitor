// relays
int r1 = 4;
int r2 = 5;
int r3 = 6;
int r4 = 7;
int r5 = 8;
int r6 = 9;
int r7 = 10;
int r8 = 11;
int r9 = 12;

// button values
bool openPiston = false;
bool closePiston = false;

// data monitoring storage
int oldTime = 0;
String pushData = "PUSH:";
String pullData = "PULL:";
String tempData = "TEMP:";
String pressureData = "PRESSURE:";

void setup() {
  // control
  pinMode(r1, OUTPUT);
  pinMode(r2, OUTPUT);
  pinMode(r3, OUTPUT);
  pinMode(r4, OUTPUT);
  pinMode(r5, OUTPUT);

  // feedback
  pinMode(r6, INPUT);
  pinMode(r7, INPUT);
  pinMode(r8, INPUT);
  pinMode(r9, INPUT);

  // sensors
  pinMode(A0, INPUT);  // temp
  pinMode(A1, INPUT);  // pressure

}

void loop() {
  // update button booleans *
  
  int temp = analogRead(A0);
  double pressure = analogRead(A1);
  // process raw data (???) *
  int curr = millis();
  if (millis() % 10 < 3) {
    // record data and time stamp
    tempData += (String) temp + ";" + curr + ":";
    pressureData += (String) (int) pressure + ";" + curr + ":";
  }

  if (openPiston) {
    // record time stamp for push
    pushData += millis() + ":";
    
    // open ball-valve
    digitalWrite(r1, HIGH);
    while (digitalRead(r6) != HIGH) {
      delay(10);
    }
    digitalWrite(r1, LOW);

    // push piston
    digitalWrite(r3, HIGH);
    while(digitalRead(r8) != HIGH) {
      delay(10);
    }
    digitalWrite(r3, LOW);

    // set scraper to open
    digitalWrite(r5, HIGH);

    openPiston = false;
  }

  if (closePiston || temp > 55 || pressure < 0.4) {
    // record time stamp for pull
    pullData += millis() + ":";

    // pull piston
    digitalWrite(r4, HIGH);
    while(digitalRead(r9) != HIGH) {
      delay(10);
    }
    digitalWrite(r4, LOW);

    // close ball-valve
    digitalWrite(r2, HIGH);
    while (digitalRead(r7) != HIGH) {
      delay(10);
    }
    digitalWrite(r2, LOW);

    // set scraper to closed
    digitalWrite(r5, LOW);

    closePiston = false;
  }

  int currTime = millis();
  if (currTime > oldTime + 60) {
    // send pushData + pullData + tempData + pressureData;
    
    // redefines/initializes variables
    oldTime = currTime;
    String pushData = "PUSH:";
    String pullData = "PULL:";
    String tempData = "TEMP:";
    String pressureData = "PRESSURE:";
  }
}
