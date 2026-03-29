[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)
# PCCCS495 – Term II Project

## Project Title
EmpathAI  —  Mental Wellness Tracker
---

## Problem Statement (max 150 words)
University students and working professionals often fail to recognise early signs of burnout and mental fatigue. Subtle patterns — poor sleep, overwork, and rising stress — compound silently over days until they become serious. There is no simple, private, lightweight tool that tracks these daily behavioural signals and surfaces them as actionable insights. EmpathAI solves this by allowing users to log sleep hours, work hours, and self-reported stress levels each day. A rule-based scoring engine classifies each day as LOW, MODERATE, or HIGH risk. Weekly reports with visual score bars, risk summaries, and personalised recommendations help users detect patterns before they become crises. Data is saved and loaded across sessions, supporting multiple user profiles.
---

## Target User
University students managing academic pressure, early-career professionals in high-workload environments, and anyone who wants a simple, private daily wellness journal with automatic pattern detection
---

## Core Features

-•	Daily signal logging — record sleep hours, work hours, and self-rated stress (1–10) for any date
•	Automatic stress score calculation using rule-based thresholds per signal type
•	Risk classification per day: LOW (0–3), MODERATE (4–7), or HIGH (8+)
•	Weekly wellness report with ASCII score bars, risk summary, and recommendation
•	Single-day detailed report for any specific date on record
•	Save and load user profiles to/from plain text files across sessions
•	Multi-user support with in-memory user switching via a 10-option console menu
  OOP Concepts Used


- 
- 

---

## OOP Concepts Used

Concept	How it is used in EmpathAI
Abstraction	BehaviorSignal is declared abstract with two abstract methods: getStressContribution() and getSummary(). No instance can be created directly. Each subclass must provide its own scoring logic, hiding implementation details from the caller.
Inheritance	SleepSignal, WorkSession, and StressEntry all extend BehaviorSignal using the extends keyword. They inherit signalType, date, and toString() from the parent, and each provides its own version of the abstract methods.
Polymorphism	DailyRecord.getTotalStressScore() iterates over a List<BehaviorSignal> and calls getStressContribution() on each element. Java resolves the correct subclass implementation at runtime — no instanceof checks are needed.
Exception Handling	IllegalArgumentException is thrown by model constructors for invalid inputs (stress rating outside 1–10, empty name, invalid age). FileManager uses try-with-resources for automatic stream closure and throws IOException with line numbers. MainApp input helpers catch NumberFormatException and re-prompt without crashing.
Collections / Threads	HashMap<String, User> stores all loaded users for O(1) lookup by ID. ArrayList<DailyRecord> maintains ordered record history per user. ArrayList<BehaviorSignal> stores signals per day. FileManager.saveUser() can be wrapped in a background Thread for non-blocking file I/O during long sessions.


---

## Proposed Architecture Description

---EmpathAI follows a layered object-oriented architecture with four tiers: model, service, I/O, and entry point. Classes are decoupled — the model layer knows nothing about reporting or file handling.

Class Hierarchy
BehaviorSignal  (abstract)
  ├── SleepSignal
  ├── WorkSession
  └── StressEntry
DailyRecord  ▶ List<BehaviorSignal>
User  ▶ List<DailyRecord>	Layers
Entry Point:  MainApp
Service:       BehaviorAnalyzer
Reporting:    ReportGenerator (implements Reportable)
Persistence:  FileManager
Model:         User, DailyRecord, BehaviorSignal and subclasses



## How to Run
# Step 1 — Compile from inside src/
cd src
javac *.java
 
# Step 2 — Run from project root
cd ..
java -cp src MainApp
 

---

## Git Discipline Notes
Minimum 10 meaningful commits required.
