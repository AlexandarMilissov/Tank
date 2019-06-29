#include <SoftwareSerial.h>

byte remote_data;

SoftwareSerial Bluetooth(13, 12); // RX, TX on Arduino, connected to TX, RX on Bluetooth Module

struct Track {
  int pin1;
  int pin2;
  int enable;
};

void MoveTrack(struct Track, int, bool);

struct Track left_track;
struct Track right_track;


void setup() {

  Serial.begin(9600);
  Serial.println("var init");


  // left_track definition
  left_track.pin1 = 9;
  left_track.pin2 = 10;
  left_track.enable = 11;

  pinMode(left_track.pin1, OUTPUT);
  pinMode(left_track.pin2, OUTPUT);
  pinMode(left_track.enable, OUTPUT); // pwm

  //right_track definition
  right_track.pin1 = 8;
  right_track.pin2 = 7;
  right_track.enable = 6;

  pinMode(right_track.pin1, OUTPUT);
  pinMode(right_track.pin2, OUTPUT);
  pinMode(right_track.enable, OUTPUT); // pwm

  Bluetooth.begin(9600);
}

void loop() {

  if (Bluetooth.available()) {

    remote_data = Bluetooth.read();

    switch (remote_data) {
      case 'a':
        Serial.println("stop left track");
        MoveTrack(left_track, 0, false);
        break;
      case 'b':
        Serial.println("left track forward");
        MoveTrack(left_track, 255, true);
        break;
      case 'c':
        Serial.println("left track backward");
        MoveTrack(left_track, 255, false);
        break;
      case 'd':
        Serial.println("stop right track");
        MoveTrack(right_track, 0, false);
        break;
      case 'e':
        Serial.println("right track forward");
        MoveTrack(right_track, 255, true);
        break;
      case 'f':
        Serial.println("right track backward");
        MoveTrack(right_track, 255, false);
        break;
      default:
        Serial.println("unknown command received");
        break;
    }
  }
}

void MoveTrack(struct Track track, int speed_val, bool forward) {

  if (speed_val == 0) // no movement
  {
    digitalWrite(track.pin1, LOW);
    digitalWrite(track.pin2, LOW);
    return; // because analogWrite?
  }
  else if (forward) // move track forwards
  {
    digitalWrite(track.pin1, LOW);
    digitalWrite(track.pin2, HIGH);
  }
  else if (!forward) // move track backwards
  {
    digitalWrite(track.pin1, HIGH);
    digitalWrite(track.pin2, LOW);
  }

  analogWrite(track.enable, speed_val);
}
