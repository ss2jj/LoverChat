package com.xujia.loverchat.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

import com.nineoldandroids.view.ViewHelper;
import com.xujia.loverchat.R;


public class DragLayout extends FrameLayout {
private GestureDetectorCompat gesDector;
private ViewDragHelper dragHelper;
private int range;
private int width;
private int height;
private int mainLeft = 0;
private ImageView iv_shadow;
private RelativeLayout vg_left;
private boolean isShowShadow = true;
private MyRelativeLayout vg_main;
private DragListener dragListener;
private Status status = Status.Close;  
private Context context;
    public enum Status {
        Drag, Open, Close
    }
    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        gesDector = new GestureDetectorCompat(context, new MyGesDetector());
        dragHelper = ViewDragHelper.create(this, dragHelperCallback);
    }

    class MyGesDetector extends SimpleOnGestureListener  {
        @Override
     public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
         // TODO Auto-generated method stub
         return Math.abs(distanceY) <= Math.abs(distanceX); 
     }
    }
   
    
    ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback()   {

        @Override
        /*
         * (non-Javadoc)
         * @see android.support.v4.widget.ViewDragHelper.Callback#clampViewPositionHorizontal(android.view.View, int, int)
         * 处理横向滑动view
         */
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 边界处理
            if(mainLeft+dx < 0) {
                return mainLeft;
            }else   if(mainLeft+dx>range)   {
                return range;
            }else   {
                return left;
            }          
        }

        @Override
        //对可移动的view返回true
        public boolean tryCaptureView(View arg0, int arg1) {
            // TODO Auto-generated method stub
            return true;
        }
        //返回可移动的最大距离
        public int getViewHorizontalDragRange(View child) {
            return width;
        };
        //view释放后 锁定view最后的位置
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                open();
            } else if (xvel < 0) {
                close();
            } else if (releasedChild == vg_main && mainLeft > range * 0.3) {
                open();
            } else if (releasedChild == vg_left && mainLeft > range * 0.7) {
                open();
            } else {
                close();
            }
            
        };
        //view位置移动时 重新layout 并对view进行缩放处理
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == vg_main) {
                mainLeft = left;
            } else {
                mainLeft = mainLeft + left;
            }
            if (mainLeft < 0) {
                mainLeft = 0;
            } else if (mainLeft > range) {
                mainLeft = range;
            }

            if (isShowShadow) {
                iv_shadow.layout(mainLeft, 0, mainLeft + width, height);
            }
            if (changedView == vg_left) {
                vg_left.layout(0, 0, width, height);
                vg_main.layout(mainLeft, 0, mainLeft + width, height);
            }

            dispatchDragEvent(mainLeft);
        };
       
   };
   
   protected void onFinishInflate() {
       super.onFinishInflate();
       if (isShowShadow) {
           iv_shadow = new ImageView(context);
           iv_shadow.setImageResource(R.drawable.shadow);
           LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                   LayoutParams.MATCH_PARENT);
           addView(iv_shadow, 1, lp);
       }
      vg_left = (RelativeLayout)getChildAt(0);
      vg_main = (MyRelativeLayout)getChildAt(1);
      vg_main.setDragLayout(this);
      vg_left.setClickable(true);
      vg_main.setClickable(true);
   };
   
   @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        width = vg_left.getMeasuredWidth();
        height = vg_left.getMeasuredHeight();
        range = (int)(width*0.6f);
    }
   
   @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
       vg_left.layout(0, 0, width, height);
       vg_main.layout(mainLeft,0, mainLeft + width, height);
    }

   @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
       return dragHelper.shouldInterceptTouchEvent(ev)
               && gesDector.onTouchEvent(ev);
    }
   
   @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
       try {
           dragHelper.processTouchEvent(event);
       } catch (Exception ex) {
           ex.printStackTrace();
       }
       return false;
    }
   public void setDragListener(DragListener dragListener) {
       this.dragListener = dragListener;
   }
   //分发滑动事件 给外界处理 同时进行动画处理
   private void dispatchDragEvent(int mainLeft) {
       if(dragListener == null) return;
       float percent = mainLeft / (float) range;
       animateView(percent);
       dragListener.onDrag(percent);
       Status lastStatus = status;
       if (lastStatus != getStatus() && status == Status.Close) {
           dragListener.onClose();
       } else if (lastStatus != getStatus() && status == Status.Open) {
           dragListener.onOpen();
       }
   }
   private void animateView(float percent) {
       float f1 = 1 - percent * 0.3f;
       ViewHelper.setScaleX(vg_main, f1);
       ViewHelper.setScaleY(vg_main, f1);
       ViewHelper.setTranslationX(vg_left, -vg_left.getWidth() / 2.3f
               + vg_left.getWidth() / 2.3f * percent);
       ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent);
       ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent);
       ViewHelper.setAlpha(vg_left, percent);
       if (isShowShadow) {
           ViewHelper.setScaleX(iv_shadow, f1 * 1.4f * (1 - percent * 0.12f));
           ViewHelper.setScaleY(iv_shadow, f1 * 1.85f * (1 - percent * 0.12f));
       }
       getBackground().setColorFilter(
               evaluate(percent, Color.BLACK, Color.TRANSPARENT),
               Mode.SRC_OVER);
   }
   private Integer evaluate(float fraction, Object startValue, Integer endValue) {
       int startInt = (Integer) startValue;
       int startA = (startInt >> 24) & 0xff;
       int startR = (startInt >> 16) & 0xff;
       int startG = (startInt >> 8) & 0xff;
       int startB = startInt & 0xff;
       int endInt = (Integer) endValue;
       int endA = (endInt >> 24) & 0xff;
       int endR = (endInt >> 16) & 0xff;
       int endG = (endInt >> 8) & 0xff;
       int endB = endInt & 0xff;
       return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
               | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
               | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
               | (int) ((startB + (int) (fraction * (endB - startB))));
   }

   @Override
   public void computeScroll() {
       if (dragHelper.continueSettling(true)) {
           ViewCompat.postInvalidateOnAnimation(this);
       }
   }
   public Status getStatus() {
       if (mainLeft == 0) {
           status = Status.Close;
       } else if (mainLeft == range) {
           status = Status.Open;
       } else {
           status = Status.Drag;
       }
       return status;
   }
   public void open() {
       open(true);
   }

   public void open(boolean animate) {
       if (animate) {
           if (dragHelper.smoothSlideViewTo(vg_main, range, 0)) {
               ViewCompat.postInvalidateOnAnimation(this);
           }
       } else {
           vg_main.layout(range, 0, range * 2, height);
           dispatchDragEvent(range);
       }
   }

   public void close() {
       close(true);
   }

   public void close(boolean animate) {
       if (animate) {
           if (dragHelper.smoothSlideViewTo(vg_main, 0, 0)) {
               ViewCompat.postInvalidateOnAnimation(this);
           }
       } else {
           vg_main.layout(0, 0, width, height);
           dispatchDragEvent(0);
       }
   }
}


