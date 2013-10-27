import controlP5.*;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import processing.pdf.*;


// global variables
ControlP5 cp5;
ColorPicker cp;

yarnRenderFBO myYarnRenderFBO;
yarnRenderPDF myYarnRenderPDF;
PGraphics fbo;
PGraphicsPDF pdf;
Boolean exportTransparentPattern = true;
int centerX = 0, centerY = 0, offsetX = 0, offsetY = 0;
String exportType = "pdf";
PImage imgSelected;

void setup() {

  size(1600, 600);
  smooth();
  loadNewpattern("sample.png");
  noStroke();
  setupGui();
  centerX = 0;
  centerY = 0;  
  cursor(HAND);
}

void draw() {
  if (mousePressed == true) {
    centerX = mouseX-offsetX;
    centerY = mouseY-offsetY;
  } 
  pushMatrix();
  translate(centerX,centerY);
  
  background(cp.getColorValue());
  image(fbo, 0, 0);
  popMatrix();
  guiDraw();
}

void keyPressed() {
  switch(key) {
    case(' '):
    selectInput("Select a file to process:", "fileSelected");
    break;
  }
}

void mousePressed(){
  offsetX = mouseX-centerX;
  offsetY = mouseY-centerY;
}

void loadNewpattern(String imagePath) {
  println("load:"+imagePath);
  imgSelected = loadImage(imagePath);
  myYarnRenderFBO = new yarnRenderFBO();
  myYarnRenderFBO.loadPattern(imgSelected);
}
