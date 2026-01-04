# Discrete Curvature Flow Visualizer

A Java-based visualization tool for **Discrete Curve Evolution** (also known as Curve Shortening Flow). This project demonstrates how 2D closed curves evolve under mean curvature flow, gradually smoothing out irregularities and shrinking to a round point.

![Demo](demo.gif)
*(Note: Replace `demo.gif` with a screenshot or GIF of your running application)*

## 📖 Overview

**Curvature Flow** is a geometric process where each point on a curve moves in the direction of its inward normal vector with a speed proportional to its curvature.

This project implements this process mathematically using the **Turning Angle Method**. It allows users to load polygon data (from `.vert` files) and watch the curve evolve in real-time.

**Key Concepts:**
* **Smoothing:** High-curvature regions (sharp corners) move faster, smoothing the curve.
* **Shrinking:** The curve's perimeter decreases over time.
* **Roundness:** Any non-self-intersecting closed curve eventually becomes convex and circular before vanishing.

## 🚀 Features

* **Maven Project Structure**: Easy dependency management and building.
* **Real-time Evolution**: discrete time-step integration to simulate the flow.
* **Geometric Visualization**:
    * Draws the curve dynamically.
    * Visualizes **Tangent** (Blue) and **Normal** (Green) vectors.
* **Robust Curvature Calculation**:
    * Uses the **Turning Angle** method ($\kappa = \theta / L$).
    * Implements `Math.atan2` and Cross Product for robust signed angle calculation (handling convex and concave regions correctly).
* **Auto-Scaling & Centering**: The viewport automatically adjusts to keep the shrinking curve centered and visible.

## 🛠️ Tech Stack

* **Language**: Java (JDK 8+)
* **GUI**: Java Swing (Graphics2D)
* **Build Tool**: Maven
* **IDE**: Compatible with VS Code, IntelliJ IDEA, Eclipse

## ⚙️ How It Works (The Math)

The evolution follows the geometric heat equation:

$$\frac{\partial C}{\partial t} = \kappa \cdot \vec{N}$$

Where:
* $\kappa$ (Kappa) is the discrete curvature.
* $\vec{N}$ is the unit inward normal vector.

In this discrete implementation:
1.  **Turning Angle ($\theta$)**: Calculated using the difference between the angles of adjacent segments (using `atan2` to preserve sign).
2.  **Discrete Curvature**: Approximate curvature as $\kappa \approx \theta / \text{AverageEdgeLength}$.
3.  **Update Rule**: $P_{new} = P_{old} + \Delta t \cdot \kappa \cdot \vec{N}$

## 📦 Installation & Usage

### Prerequisites
* Java Development Kit (JDK) installed.
* Maven installed (optional, if running from an IDE).

### Build and Run

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/YourUsername/CurvatureFlow-Java.git](https://github.com/YourUsername/CurvatureFlow-Java.git)
    cd CurvatureFlow-Java
    ```

2.  **Open in your IDE (VS Code / IntelliJ)**
    * Since this is a Maven project, your IDE should automatically detect the `pom.xml`.

3.  **Run the Main Class**
    * Locate `src/main/java/com/local/Main.java`.
    * Run the file.

### How to use
* The application loads the default `.vert` file (e.g., `riderr.vert`) on startup.
* The evolution starts automatically after a short delay.
* Watch the curve smooth out and shrink!

## 📂 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── local/
│   │           ├── Main.java           # Entry point & Timer logic
│   │           ├── DrawCurves.java     # Rendering & Evolution logic
│   │           └── VertFileReader.java # File I/O
│   └── resources/
│       └── *.vert                      # Data files
