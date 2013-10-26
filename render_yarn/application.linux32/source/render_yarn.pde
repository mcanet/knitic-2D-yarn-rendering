import controlP5.*;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

// global variables
ControlP5 cp5;
ColorPicker cp;

yarnRender myYarnRender;
PGraphics fbo;
Boolean exportTransparentPattern = true;

void setup() {

  size(1600, 600);
  smooth();
  loadNewpattern("sample.png");
  noStroke();
  setupGui();
}

void draw() {
  background(cp.getColorValue());
  image(fbo, 0, 0);
  guiDraw();
}

void keyPressed() {
  switch(key) {
    case(' '):
    selectInput("Select a file to process:", "fileSelected");
    break;
  }
}

void loadNewpattern(String imagePath) {
  println("load:"+imagePath);
  PImage img = loadImage(imagePath);
  myYarnRender = new yarnRender();
  myYarnRender.loadPattern(img);
}
