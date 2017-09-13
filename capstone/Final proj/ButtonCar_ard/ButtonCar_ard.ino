/*
 * ********Doorbell Basic Button *******
 *
 *
 *
 */
 
#define VERSION "1.00a0"

int BUTTON = 2;
int LED = 11;
int NORTH = 3;
int SOUTH = 4;
int WEST = 5;
int EAST = 6;

long nTime;
long sTime;
long eTime;
long wTime;

long lastRead = 0;

boolean pressed = false;
long thresh = 300; // milliseconds before it will register as a press/release.

int NSDir = -1;
int WEDir = -1;

void setup() {
  pinMode(BUTTON, INPUT);
  pinMode(LED, OUTPUT);
  pinMode(NORTH, OUTPUT);
  pinMode(SOUTH, OUTPUT);
  pinMode(EAST, OUTPUT);
  pinMode(WEST, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  //Send a capital P when the button FIRST enters the pressed state.
  if (digitalRead(BUTTON) == HIGH && !pressed) {
    if (millis() - lastRead >= thresh) { // If greater than threshhold, count as triggered
      lastRead = millis(); // set new lastread time.
      Serial.print('P'); // Send a pressed indicator 
      pressed = true;
    }
  }
  //Send a capital R when the button FIRST enters the released state.
  if (digitalRead(BUTTON) == LOW && pressed) {
    if (millis() - lastRead >= thresh) { // If greater than threshhold, count as triggered
      lastRead = millis(); // set new lastread time.
      Serial.print('R'); // Send a released indicator 
      pressed = false;
    }
  }
  
  // If a capital N,S,E,W is recieved back, light the N,S,E,W leds,
  // respectively, for a second.
  
  if (Serial.available() > 0) {
    int oldNSDir = NSDir;
    int oldWEDir = WEDir;
    char nextChar = Serial.read();
    if (nextChar == 'N') {
      NSDir = NORTH;
    } else if (nextChar == 'S') {
      NSDir = SOUTH;
    } else if (nextChar == 'E') {
      WEDir = EAST;
    } else if (nextChar == 'W') {
      WEDir = WEST;
    } else if (nextChar == '0') {
      digitalWrite(oldWEDir, LOW);
      WEDir = -1;
      return;
    } else if (nextChar == '1') {
      digitalWrite(oldNSDir, LOW);
      NSDir = -1;
      return;
    } else {
      return;
    }
    
    
    if (NSDir != oldNSDir) {
      digitalWrite(oldNSDir, LOW);
      digitalWrite(NSDir, HIGH);
    }
    if (WEDir != oldWEDir) {
      digitalWrite(oldWEDir, LOW);
      digitalWrite(WEDir, HIGH);
    }
  }
  
//  //Turn off the led only wehen direction changes.
//  if (millis() - nTime >= 100) {
//    digitalWrite(NORTH, LOW);
//  }
//  if (millis() - sTime >= 100) {
//    digitalWrite(SOUTH, LOW);
//  }
//  if (millis() - eTime >= 100) {
//    digitalWrite(EAST, LOW);
//  }
//  if (millis() - wTime >= 100) {
//    digitalWrite(WEST, LOW);
//  }
}
