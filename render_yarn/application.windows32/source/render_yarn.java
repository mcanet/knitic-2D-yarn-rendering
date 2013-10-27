import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import javax.swing.JOptionPane; 
import javax.swing.ImageIcon; 
import processing.pdf.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class render_yarn extends PApplet {







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

public void setup() {

  size(1600, 600);
  smooth();
  loadNewpattern("sample.png");
  noStroke();
  setupGui();
  centerX = 0;
  centerY = 0;  
  cursor(HAND);
}

public void draw() {
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

public void keyPressed() {
  switch(key) {
    case(' '):
    selectInput("Select a file to process:", "fileSelected");
    break;
  }
}

public void mousePressed(){
  offsetX = mouseX-centerX;
  offsetY = mouseY-centerY;
}

public void loadNewpattern(String imagePath) {
  println("load:"+imagePath);
  imgSelected = loadImage(imagePath);
  myYarnRenderFBO = new yarnRenderFBO();
  myYarnRenderFBO.loadPattern(imgSelected);
}
RadioButton r;

public void setupGui() {
  cp5 = new ControlP5(this);
  cp = cp5.addColorPicker("picker")
    .setPosition(150, height-60)
      .setColorValue(color(255, 128, 0, 128))
        ;

  cp5.addButton("Open pattern image", 1, 10, height-60, 100, 20);

  cp5.addSlider("Yarn thickness")
    .setPosition(450, height-60)
      .setRange(1, 20)
        .setValue(2)
          ;

  cp5.addSlider("Stitch Size")
    .setPosition(450, height-40)
      .setRange(1, 100)
        .setValue(7)
          ;

  cp5.addButton("Export render", 1, 850, height-60, 100, 20);
  cp5.addToggle("Export transparent background")
    .setPosition(650, height-35)
      .setSize(15, 15)
        .setColorActive(color(255))
          .setValue(true)
            ;

  r = cp5.addRadioButton("radioButton")
    .setPosition(650, height-60)
      .setSize(40, 20)
        .setColorForeground(color(120))
          .setColorActive(color(255))
            .setColorLabel(color(255))
              .setItemsPerRow(5)
                .setSpacingColumn(50)
                  .addItem("PDF", 1)
                    .addItem("PNG", 2)
                      ;
  r.activate(0);
}

public void guiDraw() {
  noStroke();
  fill(100);
  rect(0, height-70, width, height);
}

public void controlEvent(ControlEvent c) {
  // when a value change from a ColorPicker is received, extract the ARGB values
  // from the controller's array value
  if (c.isFrom(cp)) {
    int r = PApplet.parseInt(c.getArrayValue(0));
    int g = PApplet.parseInt(c.getArrayValue(1));
    int b = PApplet.parseInt(c.getArrayValue(2));
    int a = PApplet.parseInt(c.getArrayValue(3));
    int col = color(r, g, b, a);
    println("event\talpha:"+a+"\tred:"+r+"\tgreen:"+g+"\tblue:"+b+"\tcol"+col);
  }

  if (c.getName()=="Open pattern image") {
    selectInput("Select a file to process:", "fileSelected");
  }
  if (c.getName()=="Export render") {
    if (exportType=="image") {
      String fileName = "";
      fileName = JOptionPane.showInputDialog(frame, "Write filename to export", "export.png");
      myYarnRenderFBO.exportImage(fileName);
    }
    else if (exportType=="pdf") {
      String fileName = "";
      fileName = JOptionPane.showInputDialog(frame, "Write filename to export", "export.pdf");
      myYarnRenderPDF = new yarnRenderPDF();
      if (fileName.substring(fileName.length()-4, fileName.length()).equals(".pdf")) {
        myYarnRenderPDF.loadPatternAndExport(imgSelected, fileName);
      }
      else {
        myYarnRenderPDF.loadPatternAndExport(imgSelected, fileName+".pdf");
      }
    }
  }
  if (c.getName()=="Export transparent background") {
    if (c.getValue()==0.0f) {
      exportTransparentPattern = false;
    }
    else {
      exportTransparentPattern = true;
    }
  }
  if (c.getName()=="Yarn thickness") {
    println(c.getValue());
    myYarnRenderFBO.yarnthickness = ceil(c.getValue());
    myYarnRenderFBO.render();
  }
  if (c.getName()=="Stitch Size") {
    myYarnRenderFBO.stitchSize = ceil(c.getValue());
    myYarnRenderFBO.render();
  }
  if (c.isFrom(r)){
    if(c.getValue()==1.0f){
      println("choose image");
       exportType = "image";
    }
    if(c.getValue()==2.0f){
       println("choose pdf");
       exportType = "pdf";
    }
  }
}

// color information from ColorPicker 'picker' are forwarded to the picker(int) function
public void picker(int col) {
  println("picker\talpha:"+alpha(col)+"\tred:"+red(col)+"\tgreen:"+green(col)+"\tblue:"+blue(col)+"\tcol"+col);
}

public void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } 
  else {
    loadNewpattern(selection.getAbsolutePath());
  }
}

class yarnRenderFBO {

  yarnStitchFBO[] myYarn;
  int stitchSize;
  int widthYarn;
  int yarnthickness;
  PImage img;

  yarnRenderFBO() {
    stitchSize = 7;
    yarnthickness = 2;
  }

  public void setStitchSize(int _stitchSize) {
    stitchSize = _stitchSize;
    render();
  }
  
  public void render(){
    loadPattern(img);
  }

  public void loadPattern(PImage _img) {
    noLoop(); 
    img = _img;
    img.loadPixels();
    int totalStitch = img.height*img.width;
    
    widthYarn = img.width;
    myYarn = new yarnStitchFBO[totalStitch];
    for (int i=0; i<myYarn.length;i++) {
      myYarn[i] = new yarnStitchFBO(stitchSize, yarnthickness);
    }

    fbo = createGraphics(img.width*stitchSize*2, (img.height+1)*stitchSize*2);
    //pdf = (PGraphicsPDF) createGraphics(width, height, PDF, "pause-resume.pdf");
    draw();
    loop(); 
  }

  public void exportImage(String fileName) {
    if(!exportTransparentPattern){
      PGraphics fbo2 = createGraphics(fbo.width, fbo.height);
      fbo2.beginDraw();
      fbo2.background(cp.getColorValue());
      fbo2.image(fbo,0,0);
      fbo2.save( fileName);
    }else{
      fbo.save( fileName);
    }
  }

  public void draw() {
    fbo.beginDraw();
    img.loadPixels();
    for (int y = 0; y < img.height; y++) {
      for (int x = 0; x < img.width; x++) {
        int loc = x + y*img.width;

        // posicion in garment
        Boolean lastRow = false;
        Boolean firstRow = false;
        if ((loc/widthYarn)==0) firstRow = true;
        if (ceil(loc/widthYarn)==ceil((myYarn.length-1)/widthYarn)) lastRow = true;

        // The functions red(), green(), and blue() pull out the 3 color components from a pixel.
        int c1 = img.pixels[loc];
        
        int c2 = color(0,0,0);
        
        if (!lastRow) {
          c2 = img.pixels[loc+widthYarn];
        }
        if (firstRow) {
          myYarn[loc].drawPieceModelUnit(loc%widthYarn, loc/widthYarn, c1, c1, true, lastRow);
          myYarn[loc].drawPieceModelUnit((loc%widthYarn), (loc/widthYarn)+1, c1, c2, false, lastRow);
        }
        else {
          myYarn[loc].drawPieceModelUnit((loc%widthYarn), (loc/widthYarn)+1, c1, c2, firstRow, lastRow);
        }
      }
    }
    fbo.endDraw();
  }
}

class yarnRenderPDF {

  yarnStitchPDF[] myYarn;
  int stitchSize;
  int widthYarn;
  int yarnthickness;
  PImage img;

  yarnRenderPDF() {
    stitchSize = 7;
    yarnthickness = 2;
  }

  public void loadPatternAndExport(PImage _img, String exportFileName) {
    noLoop(); 
    img = _img;
    img.loadPixels();
    int totalStitch = img.height*img.width;
    
    widthYarn = img.width;
    myYarn = new yarnStitchPDF[totalStitch];
    for (int i=0; i<myYarn.length;i++) {
      myYarn[i] = new yarnStitchPDF(stitchSize, yarnthickness);
    }

    pdf = (PGraphicsPDF) createGraphics(img.width*stitchSize*2, (img.height+1)*stitchSize*2, PDF, exportFileName);
    draw();
    loop(); 
  }

  public void draw() {
    pdf.beginDraw();
    if(!exportTransparentPattern){
      pdf.fill(cp.getColorValue());
      pdf.rect(0,0,img.width*stitchSize*2, (img.height+1)*stitchSize*2);
    }
    img.loadPixels();
    for (int y = 0; y < img.height; y++) {
      for (int x = 0; x < img.width; x++) {
        int loc = x + y*img.width;

        // posicion in garment
        Boolean lastRow = false;
        Boolean firstRow = false;
        if ((loc/widthYarn)==0) firstRow = true;
        if (ceil(loc/widthYarn)==ceil((myYarn.length-1)/widthYarn)) lastRow = true;

        // The functions red(), green(), and blue() pull out the 3 color components from a pixel.
        int c1 = img.pixels[loc];
        
        int c2 = color(0,0,0);
        
        if (!lastRow) {
          c2 = img.pixels[loc+widthYarn];
        }
        if (firstRow) {
          myYarn[loc].drawPieceModelUnit(loc%widthYarn, loc/widthYarn, c1, c1, true, lastRow);
          myYarn[loc].drawPieceModelUnit((loc%widthYarn), (loc/widthYarn)+1, c1, c2, false, lastRow);
        }
        else {
          myYarn[loc].drawPieceModelUnit((loc%widthYarn), (loc/widthYarn)+1, c1, c2, firstRow, lastRow);
        }
      }
    }
    pdf.dispose();
    pdf.endDraw();
  }
}
class yarnStitchFBO {
  boolean gridEnabled = false;
  boolean debug = false;
  float sizeCub;
  int yarnthickness;
  
  int yarn0 = color (255, 255, 255);
  int yarn1 = color (255, 255, 255);
  int colorDebug = color (255, 102, 0);
  
  yarnStitchFBO() {
  }

  yarnStitchFBO( int _sizeCub, int _yarnthickness) {
    sizeCub = _sizeCub;
    yarnthickness = _yarnthickness;
  }

  public void drawPieceModelUnit(int x, int y, int _yarn0, int _yarn1, Boolean firstRow, Boolean lastRow) {
    yarn0 = _yarn0;
    yarn1 = _yarn1;

    fbo.pushMatrix();
    fbo.translate(x*(sizeCub*2), y*(sizeCub*2));
    if (gridEnabled)drawGrid(sizeCub);
    fbo.strokeWeight(yarnthickness);
    fbo.stroke(255, 0, 0);
    if (!firstRow) {
      drawSec3();  
      drawSec4();
    }
    //
    if (!lastRow) {
      drawSec_1();
      drawSec_2();
      drawSec_3();
      drawSec_4();
    }
    if (!firstRow) {
      drawSec1();
      drawSec2();
    }
    fbo.popMatrix();
  }

  public void drawGrid(float sizeCub) {
    // draw grid
    fbo.stroke(30);
    fbo.strokeWeight(1);
    for (int x=0; x<3;x++) {
      fbo.line(PApplet.parseFloat(x)*sizeCub, 0, PApplet.parseFloat(x)*sizeCub, sizeCub*2 );
    }
    for (int y=0; y<3;y++) {
      fbo.line(0, PApplet.parseFloat(y)*sizeCub, sizeCub*2, PApplet.parseFloat(y)*sizeCub );
    }
  }

  public void drawSec1() {
    float x1 = sizeCub*0.4f;
    float y1 = 0;
    float x2 = sizeCub*0.6f;
    float y2 = sizeCub*0.5f;
    float x3 = sizeCub*0.7f;
    float y3 = sizeCub*0.3f;
    float x4 = sizeCub*0.75f;
    float y4 = sizeCub;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec2() {
    float x1 = sizeCub*1.6f;
    float y1 = 0;
    float x2 = sizeCub*1.4f;
    float y2 = sizeCub*0.5f;
    float x3 = sizeCub*1.3f;
    float y3 = sizeCub*0.3f;
    float x4 = sizeCub*1.25f;
    float y4 = sizeCub;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec3() {
    float x1 = sizeCub*0.75f;
    float y1 = sizeCub;
    float x2 = sizeCub*0.75f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*0.2f;
    float y3 = sizeCub*1.8f;
    float x4 = 0;
    float y4 = sizeCub*1.8f;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);

    if (debug) {

      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec4() {
    float x1 = sizeCub*1.25f;
    float y1 = sizeCub;
    float x2 = sizeCub*1.25f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*1.8f;
    float y3 = sizeCub*1.8f;
    float x4 = sizeCub*2;
    float y4 = sizeCub*1.8f;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_1() {
    float x1 = sizeCub;
    float y1 = sizeCub*0.2f;
    float x2 = sizeCub*0.25f;
    float y2 = sizeCub*0.25f;
    float x3 = sizeCub*0.25f;
    float y3 = sizeCub*0.8f;
    float x4 = sizeCub*0.25f;
    float y4 = sizeCub;
    fbo.noFill();
    fbo.stroke(yarn1);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_2() {
    float x1 = sizeCub;
    float y1 = sizeCub*0.2f;
    float x2 = sizeCub*1.75f;
    float y2 = sizeCub*0.25f;
    float x3 = sizeCub*1.75f;
    float y3 = sizeCub*0.8f;
    float x4 = sizeCub*1.75f;
    float y4 = sizeCub;
    fbo.noFill();
    fbo.stroke(yarn1);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_3() {
    float x1 = sizeCub*0.25f;
    float y1 = sizeCub;
    float x2 = sizeCub*0.3f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*0.3f;
    float y3 = sizeCub*1.8f;
    float x4 = sizeCub*0.4f;
    float y4 = sizeCub*2;
    fbo.noFill();
    fbo.stroke(yarn1);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_4() {
    float x1 = sizeCub*1.75f;
    float y1 = sizeCub;
    float x2 = sizeCub*1.7f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*1.7f;
    float y3 = sizeCub*1.8f;
    float x4 = sizeCub*1.6f;
    float y4 = sizeCub*2;
    fbo.noFill();
    fbo.stroke(yarn1);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }
}

//--------------


class yarnStitchPDF {
  boolean gridEnabled = false;
  boolean debug = false;
  float sizeCub;
  int yarnthickness;
  
  int yarn0 = color (255, 255, 255);
  int yarn1 = color (255, 255, 255);
  int colorDebug = color (255, 102, 0);
  
  yarnStitchPDF() {
  }

  yarnStitchPDF( int _sizeCub, int _yarnthickness) {
    sizeCub = _sizeCub;
    yarnthickness = _yarnthickness;
  }

  public void drawPieceModelUnit(int x, int y, int _yarn0, int _yarn1, Boolean firstRow, Boolean lastRow) {
    yarn0 = _yarn0;
    yarn1 = _yarn1;

    pdf.pushMatrix();
    pdf.translate(x*(sizeCub*2), y*(sizeCub*2));
    if (gridEnabled)drawGrid(sizeCub);
    pdf.strokeWeight(yarnthickness);
    pdf.stroke(255, 0, 0);
    if (!firstRow) {
      drawSec3();  
      drawSec4();
    }
    //
    if (!lastRow) {
      drawSec_1();
      drawSec_2();
      drawSec_3();
      drawSec_4();
    }
    if (!firstRow) {
      drawSec1();
      drawSec2();
    }
    pdf.popMatrix();
  }

  public void drawGrid(float sizeCub) {
    // draw grid
    pdf.stroke(30);
    pdf.strokeWeight(1);
    for (int x=0; x<3;x++) {
      pdf.line(PApplet.parseFloat(x)*sizeCub, 0, PApplet.parseFloat(x)*sizeCub, sizeCub*2 );
    }
    for (int y=0; y<3;y++) {
      pdf.line(0, PApplet.parseFloat(y)*sizeCub, sizeCub*2, PApplet.parseFloat(y)*sizeCub );
    }
  }

  public void drawSec1() {
    float x1 = sizeCub*0.4f;
    float y1 = 0;
    float x2 = sizeCub*0.6f;
    float y2 = sizeCub*0.5f;
    float x3 = sizeCub*0.7f;
    float y3 = sizeCub*0.3f;
    float x4 = sizeCub*0.75f;
    float y4 = sizeCub;
    pdf.noFill();
    pdf.stroke(yarn0);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.stroke(colorDebug);
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec2() {
    float x1 = sizeCub*1.6f;
    float y1 = 0;
    float x2 = sizeCub*1.4f;
    float y2 = sizeCub*0.5f;
    float x3 = sizeCub*1.3f;
    float y3 = sizeCub*0.3f;
    float x4 = sizeCub*1.25f;
    float y4 = sizeCub;
    pdf.noFill();
    pdf.stroke(yarn0);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec3() {
    float x1 = sizeCub*0.75f;
    float y1 = sizeCub;
    float x2 = sizeCub*0.75f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*0.2f;
    float y3 = sizeCub*1.8f;
    float x4 = 0;
    float y4 = sizeCub*1.8f;
    pdf.noFill();
    pdf.stroke(yarn0);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);

    if (debug) {

      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec4() {
    float x1 = sizeCub*1.25f;
    float y1 = sizeCub;
    float x2 = sizeCub*1.25f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*1.8f;
    float y3 = sizeCub*1.8f;
    float x4 = sizeCub*2;
    float y4 = sizeCub*1.8f;
    pdf.noFill();
    pdf.stroke(yarn0);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.stroke(colorDebug);
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_1() {
    float x1 = sizeCub;
    float y1 = sizeCub*0.2f;
    float x2 = sizeCub*0.25f;
    float y2 = sizeCub*0.25f;
    float x3 = sizeCub*0.25f;
    float y3 = sizeCub*0.8f;
    float x4 = sizeCub*0.25f;
    float y4 = sizeCub;
    pdf.noFill();
    pdf.stroke(yarn1);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.stroke(colorDebug);
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_2() {
    float x1 = sizeCub;
    float y1 = sizeCub*0.2f;
    float x2 = sizeCub*1.75f;
    float y2 = sizeCub*0.25f;
    float x3 = sizeCub*1.75f;
    float y3 = sizeCub*0.8f;
    float x4 = sizeCub*1.75f;
    float y4 = sizeCub;
    pdf.noFill();
    pdf.stroke(yarn1);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.stroke(colorDebug);
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_3() {
    float x1 = sizeCub*0.25f;
    float y1 = sizeCub;
    float x2 = sizeCub*0.3f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*0.3f;
    float y3 = sizeCub*1.8f;
    float x4 = sizeCub*0.4f;
    float y4 = sizeCub*2;
    pdf.noFill();
    pdf.stroke(yarn1);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.stroke(colorDebug);
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }

  public void drawSec_4() {
    float x1 = sizeCub*1.75f;
    float y1 = sizeCub;
    float x2 = sizeCub*1.7f;
    float y2 = sizeCub*1.7f;
    float x3 = sizeCub*1.7f;
    float y3 = sizeCub*1.8f;
    float x4 = sizeCub*1.6f;
    float y4 = sizeCub*2;
    pdf.noFill();
    pdf.stroke(yarn1);
    pdf.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      pdf.stroke(colorDebug);
      pdf.line(x1, y1, x2, y2);
      pdf.line(x3, y3, x4, y4);
    }
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "render_yarn" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
