class yarnStitch {
  boolean gridEnabled = false;
  boolean debug = false;
  float sizeCub = 10;
  color yarn0 = color (255, 255, 255);
  color yarn1 = color (255, 255, 255);
  color colorDebug = color (255, 102, 0);

  yarnStitch() {
  }

  yarnStitch( int _sizeCub) {
    sizeCub = _sizeCub;
  }

  void drawPieceModelUnit(int x, int y, color _yarn0, color _yarn1, Boolean firstRow, Boolean lastRow) {
    yarn0 = _yarn0;
    yarn1 = _yarn1;

    fbo.pushMatrix();
    fbo.translate(x*(sizeCub*2), y*(sizeCub*2));
    if (gridEnabled)drawGrid(sizeCub);
    fbo.strokeWeight(2);
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

  void drawGrid(float sizeCub) {
    // draw grid
    fbo.stroke(30);
    fbo.strokeWeight(1);
    for (int x=0; x<3;x++) {
      fbo.line(float(x)*sizeCub, 0, float(x)*sizeCub, sizeCub*2 );
    }
    for (int y=0; y<3;y++) {
      fbo.line(0, float(y)*sizeCub, sizeCub*2, float(y)*sizeCub );
    }
  }

  void drawSec1() {
    float x1 = sizeCub*0.4;
    float y1 = 0;
    float x2 = sizeCub*0.6;
    float y2 = sizeCub*0.5;
    float x3 = sizeCub*0.7;
    float y3 = sizeCub*0.3;
    float x4 = sizeCub*0.75;
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

  void drawSec2() {
    float x1 = sizeCub*1.6;
    float y1 = 0;
    float x2 = sizeCub*1.4;
    float y2 = sizeCub*0.5;
    float x3 = sizeCub*1.3;
    float y3 = sizeCub*0.3;
    float x4 = sizeCub*1.25;
    float y4 = sizeCub;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  void drawSec3() {
    float x1 = sizeCub*0.75;
    float y1 = sizeCub;
    float x2 = sizeCub*0.75;
    float y2 = sizeCub*1.7;
    float x3 = sizeCub*0.2;
    float y3 = sizeCub*1.8;
    float x4 = 0;
    float y4 = sizeCub*1.8;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);

    if (debug) {

      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  void drawSec4() {
    float x1 = sizeCub*1.25;
    float y1 = sizeCub;
    float x2 = sizeCub*1.25;
    float y2 = sizeCub*1.7;
    float x3 = sizeCub*1.8;
    float y3 = sizeCub*1.8;
    float x4 = sizeCub*2;
    float y4 = sizeCub*1.8;
    fbo.noFill();
    fbo.stroke(yarn0);
    fbo.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    if (debug) {
      fbo.stroke(colorDebug);
      fbo.line(x1, y1, x2, y2);
      fbo.line(x3, y3, x4, y4);
    }
  }

  void drawSec_1() {
    float x1 = sizeCub;
    float y1 = sizeCub*0.2;
    float x2 = sizeCub*0.25;
    float y2 = sizeCub*0.25;
    float x3 = sizeCub*0.25;
    float y3 = sizeCub*0.8;
    float x4 = sizeCub*0.25;
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

  void drawSec_2() {
    float x1 = sizeCub;
    float y1 = sizeCub*0.2;
    float x2 = sizeCub*1.75;
    float y2 = sizeCub*0.25;
    float x3 = sizeCub*1.75;
    float y3 = sizeCub*0.8;
    float x4 = sizeCub*1.75;
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

  void drawSec_3() {
    float x1 = sizeCub*0.25;
    float y1 = sizeCub;
    float x2 = sizeCub*0.3;
    float y2 = sizeCub*1.7;
    float x3 = sizeCub*0.3;
    float y3 = sizeCub*1.8;
    float x4 = sizeCub*0.4;
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

  void drawSec_4() {
    float x1 = sizeCub*1.75;
    float y1 = sizeCub;
    float x2 = sizeCub*1.7;
    float y2 = sizeCub*1.7;
    float x3 = sizeCub*1.7;
    float y3 = sizeCub*1.8;
    float x4 = sizeCub*1.6;
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
