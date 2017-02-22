package cross.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;

import cross.model.PathArea;
import cross.pathview.R;


/**
 * Created by cross on 17/1/20.
 */

public class PathView extends View {
    private Context mContext;
    private int mWidth, mHeigh;//控件的宽高
    private GestureDetector mGestureDetector = null;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private Paint paintArea;//区域画笔
    private Paint paintBoundary;//区域边界画笔
    private List<PathArea> ListData;//画path数据
    private float pWidth, pHeigh, top, bottom, left, right;//所画区域的矩阵位置和宽高
    private float scale;//缩放的比例
    private float InitSclale;//初始化缩放比例（最小缩放比例）
    private Matrix mScaleMatrix;    // 缩放矩阵
    private final float[] matrixValues = new float[9];// 用于存放矩阵的9个值
    private int Grivity;//绘制区域在画布的位置
    private int boundary_color;//区域边框颜色
    private OnAreaLoadedCallback mOnAreaLoadedCallback;
    private OnAreaClick mOnAreaClick;
    public PathView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initattrs(attrs);
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initattrs(AttributeSet attrs) {
        TypedArray typearray = mContext.obtainStyledAttributes(attrs,
                R.styleable.PathView);
        Grivity = typearray.getInt(R.styleable.PathView_gravity, 0);
        boundary_color=typearray.getColor(R.styleable.PathView_boundary_color,0xffffffff);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeigh = getMeasuredHeight();
        mOnAreaLoadedCallback.onAreaLoaded();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new Gesturelistener());
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        paintArea = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArea.setStyle(Paint.Style.FILL);
        paintBoundary = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBoundary.setStyle(Paint.Style.STROKE);
        paintBoundary.setColor(boundary_color);
        paintBoundary.setStrokeWidth(1);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ListData != null && ListData.size() > 0) {
            DrawArea(canvas);//画区域
        }
    }

    private void DrawArea(Canvas canvas) {
        for (int i = 0; i < ListData.size(); i++) {
            paintArea.setColor(ListData.get(i).getAreaColor());
            canvas.drawPath(ListData.get(i).getPath(), paintArea);
            canvas.drawPath(ListData.get(i).getPath(), paintBoundary);
        }
    }

    public void setListData(List<PathArea> ListData) {
        this.ListData = ListData;
        if (ListData != null && ListData.size() > 0) {
            mScaleMatrix = new Matrix();
            for (int i = 0; i < ListData.size(); i++) {
                RectF rectF = new RectF();
                ListData.get(i).getPath().computeBounds(rectF, true);
                if (top == 0 || top > rectF.top) {
                    top = rectF.top;
                }
                if (bottom == 0 || bottom < rectF.bottom) {
                    bottom = rectF.bottom;
                }
                if (left == 0 || left > rectF.left) {
                    left = rectF.left;
                }
                if (right == 0 || right < rectF.right) {
                    right = rectF.right;
                }
            }
            pWidth = right - left;
            pHeigh = bottom - top;
            int width = mWidth - getPaddingLeft() - getPaddingRight();
            int heigh = mHeigh - getPaddingTop() - getPaddingBottom();
            scale = InitSclale= (width / pWidth) > (heigh / pHeigh) ? (heigh / pHeigh) : (width / pWidth);
            initMove();
            mScaleMatrix.postScale(scale, scale);
            areaScale(scale, scale, 0, 0);
        }
    }

    private void initMove() {
        float dx = 0;
        float dy = 0;
        switch (Grivity) {
            case 0:
                dx = (mWidth - right * scale - left * scale) / 2;
                dy = (mHeigh - bottom * scale - top * scale) / 2;
                break;
        }
        mScaleMatrix.postTranslate(dx, dy);
        areaTranslate(dx, dy);
    }

    private void areaScale(float sx, float sy, float px, float py) {
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy, px, py);
        for (int i = 0; i < ListData.size(); i++) {
            ListData.get(i).getPath().transform(matrix);
        }
        invalidate();
    }

    private void areaTranslate(float deltaX, float deltaY) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(deltaX, deltaY);
        for (int i = 0; i < ListData.size(); i++) {
            ListData.get(i).getPath().transform(matrix);
        }
        invalidate();
    }

    // 获得当前的缩放比例
    private final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class Gesturelistener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            AreaOnClick(motionEvent);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {
            mScaleMatrix.postTranslate(dx, dy);
            areaTranslate(-dx, -dy);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    private class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            scale = getScale();
            if ((scale < 3 && scaleFactor > 1.0f)
                    || (scale > InitSclale && scaleFactor < 1.0f)) {
                // 最大值最小值判断
                if (scaleFactor * scale < InitSclale) {
                    scaleFactor = InitSclale / scale;
                }
                if (scaleFactor * scale > 3) {
                    scaleFactor = 3 / scale;
                }
                mScaleMatrix.postScale(scaleFactor, scaleFactor,
                        scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                areaScale(scaleFactor, scaleFactor,
                        scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }
    }

    private void AreaOnClick(MotionEvent pMotionEvent) {
        for (int i = 0; i < ListData.size(); i++) {
            Region re = new Region();
            re.setPath(ListData.get(i).getPath(), new Region((int) (left),
                    (int) (top), (int) (right), (int) (bottom)));
            if (re.contains((int) pMotionEvent.getX(), (int) pMotionEvent.getY())) {
                mOnAreaClick.OnClick(ListData.get(i));
            }
        }
    }

    public void setOnAreaLoadedCallback(OnAreaLoadedCallback mOnAreaLoadedCallback){
        this.mOnAreaLoadedCallback=mOnAreaLoadedCallback;
    }
    public interface  OnAreaLoadedCallback{
        void onAreaLoaded();
    }
    public void addOnAreaClick(OnAreaClick mOnAreaClick){
         this.mOnAreaClick=mOnAreaClick;
    }
    public interface  OnAreaClick{
          void OnClick(PathArea pathArea);
    }
}
