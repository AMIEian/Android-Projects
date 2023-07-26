/**
 * This example turns the ESP32 into a Bluetooth LE keyboard that writes the words, presses Enter, presses a media key and then Ctrl+Alt+Delete
 */
#include <BleKeyboard.h>

BleKeyboard bleKeyboard;

bool started = false;

void setup()
  {
    Serial.begin(115200);
    Serial.println("Starting BLE work!");
    bleKeyboard.begin();
    started = true;
  }

void loop()
  {
    if(bleKeyboard.isConnected()) 
      {
        Serial.println("Sending 'Hello world'...");
        bleKeyboard.print("Hello world");
    
        delay(1000);
    
        Serial.println("Sending Enter key...");
        bleKeyboard.write(KEY_RETURN);
    
        delay(1000);
        started = false;
      }
    else
      {
        if(started == false)
          {
            ESP.restart();
          }
      }
    Serial.println("Waiting 5 seconds...");
    delay(5000);
  }
