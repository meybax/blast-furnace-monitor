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
boolean pistonUp = false;
boolean valveOpen = false;

// data monitoring storage
int oldTime = 0;
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

  // initializes status booleans
  if (digitalRead(r6) == HIGH) {
    valveOpen = true;
    Serial.println("VALVE OPEN");
    Serial.flush();
  }
  if (digitalRead(r8) == HIGH) {
    pistonUp = true;
    Serial.println("PISTON UP");
    Serial.flush();
  }

  // remote/local switch
  pinMode(remote, INPUT);

  // sensors
  pinMode(A0, INPUT);  // temp
  pinMode(A1, INPUT);  // pressure
}

void loop() {
  // reads incoming signal
  if (Serial.available() > 0) {
    int currByte = Serial.read();
    Serial.println("MOVE START");
    Serial.flush();
    if (currByte == removeSign) {
      closeScraper();
      pullPiston();
      closeValve();
    } else if (currByte == insertSign) {
      openValve();
      pushPiston();
      openScraper();
    } else if (currByte == pushSign) {
      if (valveOpen) {
        pushPiston();
      }
    } else if (currByte == pullSign) {
      if (valveOpen) {
        pullPiston();
      }
    } else if (currByte == openValveSign) {
      if (pistonUp) {
        openValve();
      }
    } else if (currByte == closeValveSign) {
      if (pistonUp) {
        closeValve();
      }
    } else if (currByte == openScraperSign) {
      openScraper();
    } else if (currByte == closeScraperSign) {
      closeScraper();
    }
    Serial.println("MOVE END");
    Serial.flush();
  }

  // send remote/local data
  if (digitalRead(remote) == HIGH && !remoteControl) {
    remoteControl = true;
    Serial.println("REMOTE ON");
  } else if (digitalRead(remote) == LOW && remoteControl) {
    remoteControl = false;
    Serial.println("REMOTE OFF");
  }
  Serial.flush(); 

  
  int temp = analogRead(A0);
  int pressure = analogRead(A1);
  // process raw data
  /* for sending and processing data
  long curr = millis();
  if (millis() % 10 < 2) {
    // transmits data and time stamp
    Serial.println((String) "TEMP:" + curr + ":" + temp + ":");
    Serial.flush();
    Serial.println((String) "PRESS:" + curr + ":" + pressure + ":");
    Serial.flush();
  }

  if (temp >= maxTemp || pressure < minPress) {
    closeScraper();
    pullPiston();
    closeValve();
  }
  
  long currTime = millis();
  if (currTime > oldTime + 60000) {
    // transmits pull and push time stamps
    Serial.println(pushData);
    Serial.flush();
    Serial.println(pullData);
    Serial.flush();
    
    // redefines/initializes variables
    oldTime = currTime;
    String pushData = "PUSH:";
    String pullData = "PULL:";
  }
  */
}

void pullPiston() {
  digitalWrite(r3, LOW);
  while(digitalRead(r8) != HIGH) {
    delay(10);
  }
  digitalWrite(r3, HIGH);

  pistonUp = true;
  Serial.println("PISTON UP");
  Serial.flush(); 
}

void pushPiston() {
  digitalWrite(r4, LOW);
  while(digitalRead(r9) != HIGH) {
    delay(10);
  }
  digitalWrite(r4, HIGH);
  
  pistonUp = false;
  Serial.println("PISTON DOWN");
  Serial.flush(); 
}

void openValve() {
  digitalWrite(r1, LOW);
  while (digitalRead(r6) != HIGH) {
    delay(10);
  }
  digitalWrite(r1, HIGH);

  valveOpen = true;
  Serial.println("VALVE OPEN");
  Serial.flush(); 
}

void closeValve() {
  digitalWrite(r2, LOW);
  while (digitalRead(r7) != HIGH) {
    delay(10);
  }
  digitalWrite(r2, HIGH);

  valveOpen = false;
  Serial.println("VALVE CLOSED");
  Serial.flush(); 
}

void openScraper() {
  digitalWrite(r5, LOW);
}

void closeScraper() {
  digitalWrite(r5, HIGH);
}
