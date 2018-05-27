package tw.com.team13.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * @author Chun-Kai Kao on 2018/5/27 11:10
 * @github http://github.com/cckaron
 */

public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {

        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * set the (height = width) so that the image will be square-like
     * @param widthMeasureSpec
     * @param widthMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
