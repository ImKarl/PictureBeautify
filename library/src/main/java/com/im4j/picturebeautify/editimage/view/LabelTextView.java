package com.im4j.picturebeautify.editimage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.im4j.picturebeautify.editimage.utils.PointUtils;

import java.util.LinkedHashMap;

/**
 * 标签操作控件
 * @author panyi
 */
public class LabelTextView extends TextView {
	private static int STATUS_IDLE = 0;
	private static int STATUS_MOVE = 1;// 移动状态
	private static int STATUS_DELETE = 2;// 删除状态
	private static int STATUS_ROTATE = 3;// 图片旋转状态

	private int imageCount;// 已加入照片的数量
	private int currentStatus;// 当前状态
	private TextItem currentItem;// 当前操作的贴图数据
	private float oldx, oldy;


	private LinkedHashMap<Integer, TextItem> bank = new LinkedHashMap<>();// 存贮每层贴图数据

	public LabelTextView(Context context) {
		super(context);
		init(context);
	}

	public LabelTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		currentStatus = STATUS_IDLE;
	}

	public void addText(Typeface typeface, String text) {
		TextItem item = new TextItem(this.getContext());
		item.init(text, typeface, this);
		if (currentItem != null) {
			currentItem.isDrawHelpTool = false;
		}
		bank.put(++imageCount, item);
		this.invalidate();// 重绘视图
	}

	/**
	 * 绘制客户页面
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// System.out.println("on draw!!~");
		for (Integer id : bank.keySet()) {
			TextItem item = bank.get(id);
			item.draw(canvas);
		}// end for each
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// System.out.println(w + "   " + h + "    " + oldw + "   " + oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:

				int deleteId = -1;
				for (Integer id : bank.keySet()) {
					TextItem item = bank.get(id);
					// 矫正触摸位置，适应旋转后的文字
					PointF point = new PointF(x, y);
					point = PointUtils.rotatePoint(point, new PointF(item.targetRect.centerX, item.targetRect.centerY), -item.targetRect.rotate);
					if (item.generateDeleteRect().contains(point.x, point.y)) {  // 删除模式
						// ret = true;
						deleteId = id;
						currentStatus = STATUS_DELETE;
					} else if (item.generateRotateRect().contains(point.x, point.y)) {// 点击了旋转按钮
						ret = true;
						if (currentItem != null) {
							currentItem.isDrawHelpTool = false;
						}
						currentItem = item;
						currentItem.isDrawHelpTool = true;
						currentStatus = STATUS_ROTATE;
						oldx = x;
						oldy = y;
					} else if (item.targetRect.contains(point.x, point.y)) {// 移动模式
						// 被选中一张贴图
						ret = true;
						if (currentItem != null) {
							currentItem.isDrawHelpTool = false;
						}
						currentItem = item;
						currentItem.isDrawHelpTool = true;
						currentStatus = STATUS_MOVE;
						oldx = x;
						oldy = y;
					}// end if
				}// end for each

				if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
					currentItem.isDrawHelpTool = false;
					currentItem = null;
					invalidate();
				}

				if (deleteId > 0 && currentStatus == STATUS_DELETE) {// 删除选定贴图
					bank.remove(deleteId);
					currentStatus = STATUS_IDLE;// 返回空闲状态
					invalidate();
				}// end if

				break;
			case MotionEvent.ACTION_MOVE:
				ret = true;
				if (currentStatus == STATUS_MOVE) {// 移动贴图
					float dx = x - oldx;
					float dy = y - oldy;
					if (currentItem != null) {
						currentItem.updatePos(dx, dy);
						invalidate();
					}// end if
					oldx = x;
					oldy = y;
				} else if (currentStatus == STATUS_ROTATE) {// 旋转 缩放图片操作
					// System.out.println("旋转");
					float dx = x - oldx;
					float dy = y - oldy;
					if (currentItem != null) {
						currentItem.updateRotateAndScale(dx, dy);// 旋转
						invalidate();
					}// end if
					oldx = x;
					oldy = y;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				ret = false;
				currentStatus = STATUS_IDLE;
				break;
		}// end switch
		return ret;
	}

	public LinkedHashMap<Integer, TextItem> getBank() {
		return bank;
	}

	public void clear() {
		bank.clear();
		this.invalidate();
	}
}// end class
