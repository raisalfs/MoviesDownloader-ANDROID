package com.rafslab.movie.dl.sticky;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private final StickyListener listener;
    private int headerHeight;

    public StickyHeaderDecoration(StickyListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }
        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION){
            return;
        }
        int headerPosition = listener.getHeaderPositionItem(topChildPosition);
        View currentHeader = getHeaderViewItem(headerPosition, parent);
        fixLayoutSize(parent, currentHeader);
        int contactPoint = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPoint, headerPosition);
        if (childInContact != null && listener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact);
            return;
        }
        drawHeader(c, currentHeader);
    }
    private View getHeaderViewItem(int headerPosition, RecyclerView parent){
        int layoutResId = listener.getHeaderLayout(headerPosition);
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        listener.bindHeaderData(header, headerPosition);
        return header;
    }
    private void drawHeader(Canvas canvas, View header){
        canvas.save();
        canvas.translate(0 , 0);
        header.draw(canvas);
        canvas.restore();
    }
    private void moveHeader(Canvas canvas, View currentHeader, View nextHeader){
        canvas.save();
        canvas.translate(0, nextHeader.getTop() - currentHeader.getHeight());
        currentHeader.draw(canvas);
        canvas.restore();
    }
    private View getChildInContact(RecyclerView parent, int contactPoint, int currentHeaderPosition){
        View childInContact = null;
        for(int i = 0; i < parent.getChildCount(); i++){
            int heightTolerance = 0;
            View child = parent.getChildAt(i);

            if (currentHeaderPosition != i) {
                boolean isChildHeader = listener.isHeader(parent.getChildAdapterPosition(child));
                if (isChildHeader) {
                    heightTolerance = headerHeight - child.getHeight();
                }
            }
            int childBottomPosition ;
            if (child.getTop() > 0) {
                childBottomPosition = child.getBottom() + heightTolerance;
            } else {
                childBottomPosition = child.getBottom();
            }
            if (childBottomPosition > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    childInContact = child;
                    break;
                }
            }
        }
        return childInContact;
    }
    private void fixLayoutSize(ViewGroup parent, View view){
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);
        view.measure(childWidthSpec, childHeightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), headerHeight = view.getMeasuredHeight());
    }

    public interface StickyListener {
        int getHeaderPositionItem(int itemPosition);
        int getHeaderLayout(int headerPosition);
        void bindHeaderData(View view, int headerPosition);
        boolean isHeader(int itemPosition);
    }
}
