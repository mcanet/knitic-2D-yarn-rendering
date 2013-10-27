RadioButton r;

void setupGui() {
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
  r.activate(1);
}

void guiDraw() {
  noStroke();
  fill(100);
  rect(0, height-70, width, height);
}

public void controlEvent(ControlEvent c) {
  // when a value change from a ColorPicker is received, extract the ARGB values
  // from the controller's array value
  if (c.isFrom(cp)) {
    int r = int(c.getArrayValue(0));
    int g = int(c.getArrayValue(1));
    int b = int(c.getArrayValue(2));
    int a = int(c.getArrayValue(3));
    color col = color(r, g, b, a);
    println("event\talpha:"+a+"\tred:"+r+"\tgreen:"+g+"\tblue:"+b+"\tcol"+col);
  }

  if (c.getName()=="Open pattern image") {
    selectInput("Select a file to process:", "fileSelected");
  }
  if (c.getName()=="Export render") {
    println("export");
    if (exportType.equals("image")) {
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
    if (c.getValue()==0.0) {
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
    if(c.getValue()==1.0){
      println("choose pdf");
       exportType = "pdf";
    }
    if(c.getValue()==2.0){
       println("choose image");
       exportType = "image";
    }
  }
}

// color information from ColorPicker 'picker' are forwarded to the picker(int) function
void picker(int col) {
  println("picker\talpha:"+alpha(col)+"\tred:"+red(col)+"\tgreen:"+green(col)+"\tblue:"+blue(col)+"\tcol"+col);
}

void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } 
  else {
    loadNewpattern(selection.getAbsolutePath());
  }
}
