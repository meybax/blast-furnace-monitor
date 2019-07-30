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

// remote/local switch
int remote = 23;
boolean remoteControl = false;

// booleans for status
boolean pistonUpOn = false;
boolean pistonDownOn = false;
boolean valveOpenOn = false;
boolean valveClosedOn = false;

// data monitoring storage
long oldTime = 0;
String pushData = "PUSH:";
String pullData = "PULL:";

// button signals
int insertSign = 5;
int removeSign = 6;
int pushSign = 7;
int pullSign = 8;
int openValveSign = 9;
int closeValveSign = 10;
int openScraperSign = 11;
int closeScraperSign = 12;

// sensor boundaries
int maxTemp = 55; // Celcius
double minPress = 0.4;  // MPa


void setup() {
  Serial.begin(9600);
  delay(100);
  Serial.println("START");
  Serial.flush();
  
  // control
  pinMode(r1, OUTPUT);
  pinMode(r2, OUTPUT);
  pinMode(r3, OUTPUT);
  pinMode(r4, OUTPUT);
  pinMode(r5, OUTPUT);
  digitalWrite(r1, HIGH);
  digitalWrite(r2, HIGH);
  digitalWrite(r3, HIGH);
  digitalWrite(r4, HIGH);
  digitalWrite(r5, HIGH);

  // feedback
  pinMode(r6, INPUT);
  pinMode(r7, INPUT);
  pinMode(r8, INPUT);
  pinMode(r9, INPUT);

  // remote/local switch
  pinMode(remote, INPUT);

  // sensors
  pinMode(A0, INPUT);  // temp
  pinMode(A1, INPUT);  // pressure
}


void loop() {
  if (Serial.available() > 0) {
    readSignal();
  }
  sendLightStatus();
  
  int tempRaw = analogRead(A0);
  int pressureRaw = analogRead(A1);
  int temp = processTemp(tempRaw);
  double pressure = processPressure(pressureRaw);
  if (temp >= maxTemp || pressure < minPress) {
    /*
    
    */
  }
  
  long currTime = millis();
  if (currTime > oldTime + 60000) {
    transmitData(currTime, temp, pressure);
  }

}


// reads incoming serial byte
void readSignal() {
  int currByte = Serial.read();
  Serial.println("MOVE:START");
  Serial.flush();
  if (currByte == removeSign) {
    if (digitalRead(r8) != HIGH) {
      closeScraper();
      pullPiston();
      while (digitalRead(r8) != HIGH) {
        sendLightStatus();
        delay(10);
      }
      closeValve();
      while (digitalRead(r7) != HIGH) {
        sendLightStatus();
        delay(10);
      }
      digitalWrite(r2, HIGH);
    }
  } else if (currByte == insertSign) {
    if (digitalRead(r9) != HIGH) {
      openValve();
      while (digitalRead(r6) != HIGH) {
        sendLightStatus();
        delay(10);
      }
      digitalWrite(r1, HIGH);
      pushPiston();
      while (digitalRead(r9) != HIGH) {
        sendLightStatus();
        delay(10);
      }
      openScraper();
    }
  } else if (currByte == pushSign) {
    pushPiston();
  } else if (currByte == pullSign) {
    pullPiston();
  } else if (currByte == openValveSign) {
    openValve();
  } else if (currByte == closeValveSign) {
    closeValve();
  } else if (currByte == openScraperSign) {
    openScraper();
  } else if (currByte == closeScraperSign) {
    closeScraper();
  }
  Serial.println("MOVE:END");
  Serial.flush();
}


// sends current status for lights
void sendLightStatus() {
  // send remote/local data
  if (digitalRead(remote) == HIGH && !remoteControl) {
    remoteControl = true;
    Serial.println("REMOTE:ON");
  } else if (digitalRead(remote) == LOW && remoteControl) {
    remoteControl = false;
    Serial.println("REMOTE:OFF");
  }
  Serial.flush(); 

  // send data on light status, only writing if a change was detected
  if (digitalRead(r6) == HIGH && !valveOpenOn) {
    digitalWrite(r1, HIGH);
    valveOpenOn = true;
    Serial.println("VALVE:OPEN:ON");
    Serial.flush();
  } else if (digitalRead(r6) == LOW && valveOpenOn) {
    valveOpenOn = false;
    Serial.println("VALVE:OPEN:OFF");
    Serial.flush();
  }
  if (digitalRead(r7) == HIGH && !valveClosedOn) {
    digitalWrite(r2, HIGH);
    valveClosedOn = true;
    Serial.println("VALVE:CLOSED:ON");
    Serial.flush();
  } else if (digitalRead(r7) == LOW && valveClosedOn) {
    valveClosedOn = false;
    Serial.println("VALVE:CLOSED:OFF");
    Serial.flush();
  }
  if (digitalRead(r8) == HIGH && !pistonUpOn) {
    digitalWrite(r3, HIGH);
    pistonUpOn = true;
    Serial.println("PISTON:UP:ON");
    Serial.flush();
  } else if (digitalRead(r8) == LOW && pistonUpOn) {
    pistonUpOn = false;
    Serial.println("PISTON:UP:OFF");
    Serial.flush();
  }
  if (digitalRead(r9) == HIGH && !pistonDownOn) {
    digitalWrite(r4, HIGH);
    pistonDownOn = true;
    Serial.println("PISTON:DOWN:ON");
    Serial.flush();
  } else if (digitalRead(r9) == LOW && pistonDownOn) {
    pistonDownOn = false;
    Serial.println("PISTON:DOWN:OFF");
    Serial.flush();
  }
}

void transmitData(long currTime, int temp, double pressure) {
    // transmits pull, push, temperature and pressure data
    Serial.println(pushData);
    Serial.flush();
    delay(10);
    Serial.println(pullData);
    Serial.flush();
    delay(10);
    Serial.println((String) "TEMP:" + currTime + ":" + temp + ":");
    Serial.flush();
    delay(10);
    Serial.println((String) "PRESS:" + currTime + ":" + pressure + ":");
    Serial.flush();
    delay(10);
    
    // redefines/initializes variables
    oldTime = currTime;
    pushData = "PUSH:";
    pullData = "PULL:";
}


int processTemp(int temp) {
  return temp;
}

double processPressure(int pressure) {
  return (double) pressure;
}


void pullPiston() {
  if (digitalRead(r6) == HIGH) {
    digitalWrite(r3, HIGH);
    digitalWrite(r4, LOW);
  }
}

void pushPiston() {
  if (digitalRead(r6) == HIGH) {
    digitalWrite(r4, HIGH);
    digitalWrite(r3, LOW);
  }
}


void openValve() {
  if (digitalRead(r8) == HIGH) {
    digitalWrite(r2, HIGH);
    digitalWrite(r1, LOW);
  }
}

void closeValve() {
  if (digitalRead(r8) == HIGH) {
    digitalWrite(r1, HIGH);
    digitalWrite(r2, LOW);
  }
}


void openScraper() {
  digitalWrite(r5, LOW);
}

void closeScraper() {
  digitalWrite(r5, HIGH);
}
