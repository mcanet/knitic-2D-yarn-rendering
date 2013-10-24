void setupGui() {
  cp5 = new ControlP5(this);
  cp = cp5.addColorPicker("picker")
    .setPosition(150, height-60)
      .setColorValue(color(255, 128, 0, 128))
        ;

  cp5.addButton("Open pattern image", 1, 10, height-60, 100, 20);
  cp5.addButton("Export image", 1, 450, height-60, 100, 20);
  cp5.addToggle("Export transparent background")
    .setPosition(450, height-35)
      .setSize(15, 15)
        .setValue(true)
          ;
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
  if (c.getName()=="Export image") {
    String fileName = "";
    fileName = JOptionPane.showInputDialog(frame, "Write filename to export", "export.png");
    myYarnRender.exportImage(fileName);
  }
  if (c.getName()=="Export transparent background") {
    if (c.getValue()==0.0) {
      exportTransparentPattern = false;
    }
    else {
      exportTransparentPattern = true;
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
