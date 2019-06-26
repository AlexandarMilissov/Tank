
struct Track {
   int pin1;
   int pin2;
   int pinEnable;
};

void setup() {
  //definition of left track  
  struct Track LeftTrack;
  LeftTrack.pin1 = 9; //
  LeftTrack.pin2 = 10;
  LeftTrack.pinEnable = 11;
  pinMode(LeftTrack.pin1,OUTPUT);
  pinMode(LeftTrack.pin2,OUTPUT);
  pinMode(LeftTrack.pinEnable,OUTPUT);

  //definition of right track
  struct Track RightTrack;
  RightTrack.pin1 = 8;
  RightTrack.pin2 = 7;
  RightTrack.pinEnable = 6;
  pinMode(RightTrack.pin1,OUTPUT);
  pinMode(RightTrack.pin2,OUTPUT);
  pinMode(RightTrack.pinEnable,OUTPUT);

  MoveTrack(LeftTrack,255,true);
}

void loop() {
}


void MoveTrack(struct Track track,int speedValue,boolean forward)
{
  if(speedValue == 0)
  {
    digitalWrite(track.pin1,LOW);
    digitalWrite(track.pin2,LOW);
    return;
  }
  else if(forward)
  {
    digitalWrite(track.pin1,LOW);
    digitalWrite(track.pin2,HIGH);
  }
  else if(!forward)
  {
    digitalWrite(track.pin1,HIGH);
    digitalWrite(track.pin2,LOW);
  }

  analogWrite(track.pinEnable,speedValue);
}
