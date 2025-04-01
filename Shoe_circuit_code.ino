#define TRIG_PIN 9
#define ECHO_PIN 10
#define BUZZER_PIN 3  // Buzzer connected to pin 3

void setup() {
    Serial.begin(9600);  // Initialize Serial Monitor
    pinMode(TRIG_PIN, OUTPUT);
    pinMode(ECHO_PIN, INPUT);
    pinMode(BUZZER_PIN, OUTPUT);
}

void loop() {
    long duration;
    float distance_cm;

    // Send a 10-microsecond pulse to trigger pin
    digitalWrite(TRIG_PIN, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIG_PIN, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIG_PIN, LOW);

    // Measure the echo duration
    duration = pulseIn(ECHO_PIN, HIGH);

    // Convert time to distance (speed of sound = 343m/s or 0.0343 cm/µs)
    distance_cm = (duration * 0.0343) / 2;

    // Print the distance to Serial Monitor
    Serial.print("Distance: ");
    Serial.print(distance_cm);
    Serial.println(" cm");

    // Buzzer alert if object is closer than 10 cm
    if (distance_cm > 0 && distance_cm < 10) {
        digitalWrite(BUZZER_PIN, HIGH);  // Turn ON buzzer
        delay(200);  // Beep duration
        digitalWrite(BUZZER_PIN, LOW);   // Turn OFF buzzer
    }

    delay(500);  // Wait before next reading
}