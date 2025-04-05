# VisionSole: A Smart Assistive System for the Visually Impaired

**VisionSole** is an innovative wearable assistive technology designed to support visually impaired individuals by combining obstacle detection, object recognition, and voice-guided navigation in real time.

## Project Description

This system integrates a **smart shoe**, an **ESP32-CAM object detection module**, and a **voice-assisted Android app** to enhance the safety and independence of the user.

### 1. Smart Shoe Module

- Built using **Arduino UNO**, **ultrasonic sensor**, and a **buzzer**
- Detects obstacles within **10 cm** range
- Provides instant **audio alerts** via buzzer, allowing the user to avoid obstacles while walking
- Powered using a **portable power bank** for flexibility and mobility

### 2. Object Detection Module

- Developed using an **ESP32-CAM** and a custom-trained **YOLOv4** model
- Performs real-time object detection for classes like **person**, **vehicle**, etc.
- Provides **audio feedback** such as *"person detected"*, improving the user's spatial awareness
- Designed to be lightweight and **body-wearable**

### 3. HELP Android App

- Developed in **Kotlin** using **Android Studio** and integrated with the **Google Maps API**
- Allows users to give **voice commands** for:
  - **Navigation**: Speak the destination to get voice-guided directions
  - **Emergency Calls**: Say the command *"call"* or *"emergency"*, and the app will:
    - Automatically **call a pre-set emergency contact**
    - **Send the live GPS location** via SMS
- Ensures hands-free, intuitive operation tailored for the visually impaired

## Key Highlights

- Fully integrated **voice interaction** — no need for manual input
- **Offline-ready hardware** (shoe + object detector) with real-time feedback
- Android app bridges navigation, safety, and emergency communication
- Designed for **portability**, **ease of use**, and **practical real-world application**
