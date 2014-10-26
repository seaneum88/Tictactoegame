package jkeum.tictactoe.android;

import java.util.Random;

import jkeum.gameengine.Grid;
import jkeum.gameengine.GridPosition;
import jkeum.gameengine.tictactoe.TicTacToePiece;
import jkeum.tictactoe.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TicTacToeView extends View {

	public static final long FPS_MS = 1000 / 2;

	private static final int MARGIN = 4;
	private static final int MSG_BLINK = 1;

	// private final Handler mBlinkHandler = new Handler(new BlinkHandler());

	private final Rect mSrcRect = new Rect();
	private final Rect mDstRect = new Rect();

	private Grid<TicTacToePiece> grid;
	private int mCellSize;
	private int mOffetX;
	private int mOffetY;
	private Paint mWinPaint;
	private Paint mLinePaint;
	private Paint mBmpPaint;
	private Bitmap mBmpX;
	private Bitmap mBmpO;
	private Drawable mDrawableBg;

	// private ICellListener mCellListener;

	// private GridPosition mSelectedCell;
	// private TicTacToePiece mSelectedPiece = null;
	// private IFixedPiecePlayer mCurrentPlayer = null;

	// private boolean mBlinkDisplayOff;
	// private final Rect mBlinkRect = new Rect();

	private IGridClickEventHandler gridClickEventHandler;

	public static interface IGridClickEventHandler {
		void onGridClickEvent(GridPosition position);
	}

	public void setGrid(Grid<TicTacToePiece> grid) {
		this.grid = grid;
	}

	public void setGridClickEventHandler(IGridClickEventHandler handler) {
		this.gridClickEventHandler = handler;
	}

	// public interface ICellListener {
	// abstract void onCellSelected();
	// }

	public TicTacToeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		requestFocus();

		mDrawableBg = getResources().getDrawable(R.drawable.lib_bg);
		setBackgroundDrawable(mDrawableBg);

		mBmpX = getResBitmap(R.drawable.lib_cross);
		mBmpO = getResBitmap(R.drawable.lib_circle);

		if (mBmpX != null) {
			mSrcRect.set(0, 0, mBmpX.getWidth() - 1, mBmpX.getHeight() - 1);
		}

		mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mLinePaint = new Paint();
		mLinePaint.setColor(0xFFFFFFFF);
		mLinePaint.setStrokeWidth(5);
		mLinePaint.setStyle(Style.STROKE);

		mWinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mWinPaint.setColor(0xFFFF0000);
		mWinPaint.setStrokeWidth(10);
		mWinPaint.setStyle(Style.STROKE);

		if (isInEditMode()) {
			// In edit mode (e.g. in the Eclipse ADT graphical layout editor)
			// we'll use some random data to display the state.
			Random rnd = new Random();
			grid = new Grid<TicTacToePiece>(3, 3);
			for (int col = 0; col < 3; col++) {
				for (int row = 0; row < 3; row++) {
					grid.setPiece(row, col,
							rnd.nextBoolean() ? TicTacToePiece.O
									: TicTacToePiece.X);
				}
			}
		}
	}

	// public void setCell(int cellIndex, State value) {
	// mData[cellIndex] = value;
	// invalidate();
	// }
	//
	// public GridPosition getSelection() {
	// if (mSelectedPiece != null) {
	// if (mSelectedPiece.equals(mCurrentPlayer.getPiece())) {
	// return mSelectedCell;
	// }
	// }
	// return null;
	// }
	//
	// public IFixedPiecePlayer getCurrentPlayer() {
	// return mCurrentPlayer;
	// }
	//
	// public void setCurrentPlayer(IFixedPiecePlayer player) {
	// mCurrentPlayer = player;
	// mSelectedCell = null;
	// }

	// -----------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int cellSize = mCellSize;
		int cellSize3 = cellSize * 3;
		int offsetX = mOffetX;
		int offsetY = mOffetY;

		for (int i = 0, k = cellSize; i < 2; i++, k += cellSize) {
			canvas.drawLine(offsetX, offsetY + k, offsetX + cellSize3 - 1,
					offsetY + k, mLinePaint);
			canvas.drawLine(offsetX + k, offsetY, offsetX + k, offsetY
					+ cellSize3 - 1, mLinePaint);
		}

		for (int row = 0, y = offsetY; row < 3; row++, y += cellSize) {
			for (int col = 0, x = offsetX; col < 3; col++, x += cellSize) {
				mDstRect.offsetTo(MARGIN + x, MARGIN + y);

				TicTacToePiece piece;
				// if (mSelectedCell != null && mSelectedCell.col == col
				// && mSelectedCell.row == row) {
				// if (mBlinkDisplayOff) {
				// continue;
				// }
				// piece = mSelectedPiece;
				// } else {
				piece = grid.getPiece(row, col);
				// }

				if (piece != null) {
					switch (piece) {
					case X:
						if (mBmpX != null) {
							canvas.drawBitmap(mBmpX, mSrcRect, mDstRect,
									mBmpPaint);
						}
						break;
					case O:
						if (mBmpO != null) {
							canvas.drawBitmap(mBmpO, mSrcRect, mDstRect,
									mBmpPaint);
						}
						break;
					}
				}
			}
		}
		//
		// if (mWinRow >= 0) {
		// int y = offsetY + mWinRow * cellSize + cellSize / 2;
		// canvas.drawLine(offsetX + MARGIN, y, offsetX + cellSize3 - 1
		// - MARGIN, y, mWinPaint);
		//
		// } else if (mWinCol >= 0) {
		// int x = offsetX + mWinCol * cellSize + cellSize / 2;
		// canvas.drawLine(x, offsetY + MARGIN, x, offsetY + cellSize3 - 1
		// - MARGIN, mWinPaint);
		//
		// } else if (mWinDiag == 0) {
		// // diagonal 0 is from (0,0) to (2,2)
		//
		// canvas.drawLine(offsetX + MARGIN, offsetY + MARGIN, offsetX
		// + cellSize3 - 1 - MARGIN, offsetY + cellSize3 - 1 - MARGIN,
		// mWinPaint);
		//
		// } else if (mWinDiag == 1) {
		// // diagonal 1 is from (0,2) to (2,0)
		//
		// canvas.drawLine(offsetX + MARGIN, offsetY + cellSize3 - 1 - MARGIN,
		// offsetX + cellSize3 - 1 - MARGIN, offsetY + MARGIN,
		// mWinPaint);
		// }
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Keep the view squared
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
		setMeasuredDimension(d, d);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int sx = (w - 2 * MARGIN) / 3;
		int sy = (h - 2 * MARGIN) / 3;

		int size = sx < sy ? sx : sy;

		mCellSize = size;
		mOffetX = (w - 3 * size) / 2;
		mOffetY = (h - 3 * size) / 2;

		mDstRect.set(MARGIN, MARGIN, size - MARGIN, size - MARGIN);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN) {
			return true;

		} else if (action == MotionEvent.ACTION_UP) {
			int col = (int) event.getX();
			int row = (int) event.getY();

			int cellSize = mCellSize;
			col = (col - MARGIN) / cellSize;
			row = (row - MARGIN) / cellSize;

			if (isEnabled() && col >= 0 && col < 3 && row >= 0 & row < 3) {
				GridPosition cell = new GridPosition(row, col);

				// this.mSelectedCell = cell;
				if (this.gridClickEventHandler != null) {
					this.gridClickEventHandler.onGridClickEvent(cell);
				} else {
					System.out.println("click event was not propagated");
				}

			}

			return true;
		}

		return false;
	}

	// public void stopBlink() {
	// boolean hadSelection = mSelectedCell != null && mSelectedPiece != null;
	// mSelectedCell = null;
	// mSelectedPiece = null;
	// if (!mBlinkRect.isEmpty()) {
	// invalidate(mBlinkRect);
	// }
	// mBlinkDisplayOff = false;
	// mBlinkRect.setEmpty();
	// mBlinkHandler.removeMessages(MSG_BLINK);
	// if (hadSelection && mCellListener != null) {
	// mCellListener.onCellSelected();
	// }
	// }

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle b = new Bundle();

		Parcelable s = super.onSaveInstanceState();
		b.putParcelable("gv_super_state", s);

		b.putBoolean("gv_en", isEnabled());

		b.putSerializable("gv_grid", this.grid);

		// b.putSerializable("gv_sel_cell", mSelectedCell);
		// b.putSerializable("gv_sel_val", mSelectedPiece);
		// b.putSerializable("gv_curr_play", mCurrentPlayer);
		// b.putInt("gv_winner", mWinner.getValue());

		// b.putInt("gv_win_col", mWinCol);
		// b.putInt("gv_win_row", mWinRow);
		// b.putInt("gv_win_diag", mWinDiag);

		// b.putBoolean("gv_blink_off", mBlinkDisplayOff);
		// b.putParcelable("gv_blink_rect", mBlinkRect);

		return b;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {

		if (!(state instanceof Bundle)) {
			// Not supposed to happen.
			super.onRestoreInstanceState(state);
			return;
		}

		Bundle b = (Bundle) state;
		Parcelable superState = b.getParcelable("gv_super_state");

		setEnabled(b.getBoolean("gv_en", true));

		int[] data = b.getIntArray("gv_data");
		// if (data != null && data.length == mData.length) {
		// for (int i = 0; i < data.length; i++) {
		// mData[i] = State.fromInt(data[i]);
		// }
		// }

		// mSelectedCell = b.getInt("gv_sel_cell", -1);
		// mSelectedPiece = TicTacToePiece
		// .valueOf(b.getString("gv_sel_val", null));
		// mCurrentPlayer = State.fromInt(b.getInt("gv_curr_play",
		// State.EMPTY.getValue()));
		// mWinner = State.fromInt(b.getInt("gv_winner",
		// State.EMPTY.getValue()));

		// mWinCol = b.getInt("gv_win_col", -1);
		// mWinRow = b.getInt("gv_win_row", -1);
		// mWinDiag = b.getInt("gv_win_diag", -1);
		//
		// mBlinkDisplayOff = b.getBoolean("gv_blink_off", false);
		// Rect r = b.getParcelable("gv_blink_rect");
		// if (r != null) {
		// mBlinkRect.set(r);
		// }

		// let the blink handler decide if it should blink or not
		// mBlinkHandler.sendEmptyMessage(MSG_BLINK);

		super.onRestoreInstanceState(superState);
	}

	// -----

	// private class BlinkHandler implements Callback {
	// public boolean handleMessage(Message msg) {
	// if (msg.what == MSG_BLINK) {
	// if (mSelectedCell != null && mSelectedPiece != null
	// && mBlinkRect.top != 0) {
	// mBlinkDisplayOff = !mBlinkDisplayOff;
	// invalidate(mBlinkRect);
	//
	// if (!mBlinkHandler.hasMessages(MSG_BLINK)) {
	// mBlinkHandler
	// .sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
	// }
	// }
	// return true;
	// }
	// return false;
	// }
	// }

	private Bitmap getResBitmap(int bmpResId) {
		Options opts = new Options();
		opts.inDither = false;

		Resources res = getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, bmpResId, opts);

		if (bmp == null && isInEditMode()) {
			// BitmapFactory.decodeResource doesn't work from the rendering
			// library in Eclipse's Graphical Layout Editor. Use this workaround
			// instead.

			Drawable d = res.getDrawable(bmpResId);
			int w = d.getIntrinsicWidth();
			int h = d.getIntrinsicHeight();
			bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas c = new Canvas(bmp);
			d.setBounds(0, 0, w - 1, h - 1);
			d.draw(c);
		}

		return bmp;
	}
}
