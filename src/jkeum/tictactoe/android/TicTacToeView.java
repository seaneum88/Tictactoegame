package jkeum.tictactoe.android;

import java.util.List;
import java.util.Random;

import jkeum.gameengine.Grid;
import jkeum.gameengine.GridPosition;
import jkeum.gameengine.Move;
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
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
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

	private final Rect srcRect = new Rect();
	private final Rect dstRect = new Rect();

	private Grid<TicTacToePiece> grid;
	private int cellSize;
	private int offetX;
	private int offetY;
	private Paint winningPaint;
	private Paint linePaint;
	private Paint bmpPaint;
	private Bitmap bmpX;
	private Bitmap bmpO;
	private Drawable drawableBg;

	private IGridClickEventHandler gridClickEventHandler;

	private List<List<GridPosition>> winnings;

	private Path winningPath;

	private Move<TicTacToePiece, GridPosition> movePreview = new Move<TicTacToePiece, GridPosition>(
			null, new GridPosition(0, 0));

	private TicTacToePiece previewPiece;

	private Paint previewPaint;;

	public void setWinningPositions(List<List<GridPosition>> winnings) {
		this.winnings = winnings;
	}

	public static interface IGridClickEventHandler {
		void onGridClickEvent(GridPosition position);
	}

	public void setGrid(Grid<TicTacToePiece> grid) {
		this.grid = grid;
	}

	public void setGridClickEventHandler(IGridClickEventHandler handler) {
		this.gridClickEventHandler = handler;
	}

	public TicTacToeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		requestFocus();

		drawableBg = getResources().getDrawable(R.drawable.lib_bg);
		setBackgroundDrawable(drawableBg);

		bmpX = getResBitmap(R.drawable.lib_cross);
		bmpO = getResBitmap(R.drawable.lib_circle);

		if (bmpX != null) {
			srcRect.set(0, 0, bmpX.getWidth() - 1, bmpX.getHeight() - 1);
		}

		bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		previewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		linePaint = new Paint();
		linePaint.setColor(0xFFFFFFFF);
		linePaint.setStrokeWidth(5);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeJoin(Join.ROUND);
		linePaint.setStrokeCap(Cap.ROUND);

		winningPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		winningPaint.setColor(0xFFFF0000);
		winningPaint.setStrokeWidth(10);
		winningPaint.setStyle(Style.STROKE);
		winningPaint.setStrokeJoin(Join.ROUND);
		winningPaint.setStrokeCap(Cap.ROUND);
		winningPath = new Path();

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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int cellSize3 = cellSize * 3;
		int offsetX = offetX;
		int offsetY = offetY;

		for (int i = 0, k = cellSize; i < 2; i++, k += cellSize) {
			canvas.drawLine(offsetX, offsetY + k, offsetX + cellSize3 - 1,
					offsetY + k, linePaint);
			canvas.drawLine(offsetX + k, offsetY, offsetX + k, offsetY
					+ cellSize3 - 1, linePaint);
		}

		for (int row = 0, y = offsetY; row < 3; row++, y += cellSize) {
			for (int col = 0, x = offsetX; col < 3; col++, x += cellSize) {
				dstRect.offsetTo(MARGIN + x, MARGIN + y);

				TicTacToePiece piece = grid.getPiece(row, col);

				if (piece != null) {
					switch (piece) {
					case X:
						if (bmpX != null) {
							canvas.drawBitmap(bmpX, srcRect, dstRect, bmpPaint);
						}
						break;
					case O:
						if (bmpO != null) {
							canvas.drawBitmap(bmpO, srcRect, dstRect, bmpPaint);
						}
						break;
					}
				}
			}
		}
		if (movePreview.getPiece() != null) {
			dstRect.offsetTo(
					MARGIN + offsetX + cellSize * movePreview.getPosition().col,
					MARGIN + offsetY + cellSize * movePreview.getPosition().row);
			TicTacToePiece piece = movePreview.getPiece();
			switch (piece) {
			case X:
				if (bmpX != null) {
					canvas.drawBitmap(bmpX, srcRect, dstRect, previewPaint);
				}
				break;
			case O:
				if (bmpO != null) {
					canvas.drawBitmap(bmpO, srcRect, dstRect, previewPaint);
				}
				break;
			}
		}
		if (winnings != null) {
			for (List<GridPosition> winning : winnings) {
				winningPath.reset();
				for (int i = 0; i < winning.size(); i++) {
					float x = winning.get(i).col * cellSize;
					float y = winning.get(i).row * cellSize;
					if (i == 0) {
						winningPath.moveTo(x, y);
					} else {
						winningPath.lineTo(x, y);
					}
				}
				winningPath.offset(
						offsetX + cellSize / 2 + winningPaint.getStrokeWidth()
								/ 4,
						offsetY + cellSize / 2 + winningPaint.getStrokeWidth()
								/ 4);
				canvas.drawPath(winningPath, winningPaint);
			}
		}

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

		cellSize = size;
		offetX = (w - 3 * size) / 2;
		offetY = (h - 3 * size) / 2;

		dstRect.set(MARGIN, MARGIN, size - MARGIN, size - MARGIN);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_MOVE) {
			int col = (int) event.getX();
			int row = (int) event.getY();

			col = (col - MARGIN) / cellSize;
			row = (row - MARGIN) / cellSize;

			if (isEnabled() && col >= 0 && col < 3 && row >= 0 & row < 3) {
				if (grid.getPiece(row, col) == null) {
					movePreview.getPosition().row = row;
					movePreview.getPosition().col = col;
					movePreview.setPiece(this.previewPiece);
				} else {
					movePreview.setPiece(null);
				}
				this.invalidate();
			}
			return true;

		} else if (action == MotionEvent.ACTION_UP) {
			int col = (int) event.getX();
			int row = (int) event.getY();

			col = (col - MARGIN) / cellSize;
			row = (row - MARGIN) / cellSize;

			if (isEnabled() && col >= 0 && col < 3 && row >= 0 & row < 3) {
				GridPosition cell = new GridPosition(row, col);

				movePreview.setPiece(null);
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

	public void setPreviewPiece(TicTacToePiece piece) {
		this.previewPiece = piece;

	}
}
