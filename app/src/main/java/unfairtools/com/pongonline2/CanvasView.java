package unfairtools.com.pongonline2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by brianroberts on 9/16/16.
 */
public class CanvasView extends View {
    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;

    volatile float xball;
    volatile float oppPaddle = 0f;
    volatile float yball;

    public int player;


    public float getPaddleY(){
        if( mY == 0)
            return .5f;
        return mY/this.getHeight();
    }



    public CanvasView(Context c, AttributeSet attrs){
        super(c, attrs);

        mPath = new Path();

//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.BLACK);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeWidth(4f);

          mPaint = new Paint();
          mPaint.setStyle(Paint.Style.FILL);



    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        super.onDraw(canvas);
        float xballs = xball*canvas.getWidth();
        float yballs = yball*canvas.getHeight();




        //mCanvas.drawOval(xballs-5.05f, yballs-5.05f,xballs+5.05f,yballs+5.05f,mPaint);

        int x = getWidth();
        int y = getHeight();
        int radius;
        radius = 100;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#CD5C5C"));
        canvas.drawCircle(xballs,yballs, 20, paint);

        float widthPaddle = canvas.getWidth()/20;
        float halfHeightPaddle = canvas.getHeight()/10;

        float oppPaddler = oppPaddle * canvas.getHeight();

       // Log.e("Player is ", player + "");

       // Log.e("Drawing ", 0f + "," + (mY- halfHeightPaddle) + "," + (widthPaddle) + "," + (mY + halfHeightPaddle));
        if(player==1) {
            canvas.drawRect(0f,mY- halfHeightPaddle, widthPaddle, mY + halfHeightPaddle,paint);

            canvas.drawRect(canvas.getWidth() - widthPaddle,oppPaddler - halfHeightPaddle, canvas.getWidth(),
                    oppPaddler + halfHeightPaddle, paint);

        }else{
            canvas.drawRect(0f,oppPaddler- halfHeightPaddle, widthPaddle, oppPaddler + halfHeightPaddle,paint);
            canvas.drawRect(canvas.getWidth() - widthPaddle,mY - halfHeightPaddle, canvas.getWidth(),
                    mY + halfHeightPaddle, paint);
        }

        //canvas.drawPath(mPath, mPaint);
    }

    private void startTouch(float x, float y){
        //mPath.moveTo(x,y);
        mX = x;
        mY = y;
    }

    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {

        mPath.reset();

        invalidate();

    }



            // when ACTION_UP stop touch

    private void upTouch() {
        mPath.lineTo(mX, mY);
    }


              //override the onTouchEvent

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }
}



