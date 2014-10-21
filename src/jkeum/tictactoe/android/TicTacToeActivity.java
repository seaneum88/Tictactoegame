package jkeum.tictactoe.android;

import java.io.Serializable;
import java.util.Random;

import jkeum.gameengine.GameEngine;
import jkeum.gameengine.GenericGrid;
import jkeum.gameengine.GridPosition;
import jkeum.gameengine.Move;
import jkeum.gameengine.StateChangeNotification;
import jkeum.gameengine.ai.minimax.MinimaxAI;
import jkeum.gameengine.ai.minimax.MinimaxPruningAI;
import jkeum.gameengine.interfaces.IFixedPiecePlayer;
import jkeum.gameengine.interfaces.IMove;
import jkeum.gameengine.interfaces.IPiece;
import jkeum.gameengine.interfaces.IPlayer;
import jkeum.gameengine.interfaces.IState;
import jkeum.gameengine.interfaces.IStateChangeNotificationHandler;
import jkeum.gameengine.tictactoe.TicTacToeMinimaxAICallback;
import jkeum.gameengine.tictactoe.TicTacToePiece;
import jkeum.gameengine.tictactoe.TicTacToeState;
import jkeum.tictactoe.R;
import jkeum.tictactoe.android.TicTacToeView.IGridClickEventHandler;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TicTacToeActivity extends Activity implements IFixedPiecePlayer,
		IGridClickEventHandler, IStateChangeNotificationHandler {

	/** Start player. Must be 1 or 2. Default is 1. */
	public static final String EXTRA_START_PLAYER = TicTacToeActivity.class
			.getName() + "EXTRA_START_PLAYER";

	private Random mRnd = new Random();
	private TicTacToeView mGameView;
	private TextView mInfoView;

	// private Button mButtonNext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.game_activity);

		mGameView = (TicTacToeView) findViewById(R.id.game_view);
		mInfoView = (TextView) findViewById(R.id.info_turn);
		// mButtonNext = (Button) findViewById(R.id.next_turn);

		mGameView.setFocusable(true);
		mGameView.setFocusableInTouchMode(true);

		// mButtonNext.setOnClickListener(new MyButtonListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		GenericGrid<TicTacToePiece> grid = new GenericGrid<TicTacToePiece>(3, 3);
		TicTacToeState state = new TicTacToeState(grid);
		engine = new GameEngine(state);
		state.addStateChangeNotificationHandler(this);
		this.myPiece = TicTacToePiece.O;
		mGameView.setGrid(grid);
		mGameView.setGridClickEventHandler(this);

		MinimaxAI ai = new MinimaxPruningAI(TicTacToePiece.X,
				new TicTacToeMinimaxAICallback());
		ai.setMaxDepth(9);
		Serializable startPlayer = getIntent().getSerializableExtra(
				EXTRA_START_PLAYER);

		if (startPlayer == null || ((TicTacToePiece) startPlayer) == myPiece) {
			engine.start(this, ai);
		} else {
			engine.start(ai, this);
		}

	}

	@Override
	public void onGridClickEvent(GridPosition position) {

		synchronized (userMove) {
			userMove.col = position.col;
			userMove.row = position.row;
			userMove.notify();
		}
		// int sxy = mCellSize;
		// mBlinkRect.set(MARGIN + position.col * sxy, MARGIN + position.row
		// * sxy, MARGIN + (position.col + 1) * sxy, MARGIN
		// + (position.row + 1) * sxy);

		// if (piece != State.EMPTY) {
		// // Start the blinker
		// mBlinkHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
		// }

		// if (mCellListener != null) {
		// mCellListener.onCellSelected();
		// }

	}

	//
	// private class MyButtonListener implements OnClickListener {
	//
	// public void onClick(View v) {
	// if (v == mButtonNext) {
	// synchronized (userMove) {
	// userMove.col = mGameView.getSelection().col;
	// userMove.row = mGameView.getSelection().row;
	// userMove.notify();
	// }
	// }
	// }
	// }

	private GridPosition userMove = new GridPosition(0, 0);

	private TicTacToePiece myPiece;

	private GameEngine engine;

	@Override
	public IMove computeMove(IState state) {
		Move<TicTacToePiece, GridPosition> move = null;
		TicTacToeState currentState = (TicTacToeState) state;
		GenericGrid<TicTacToePiece> grid = currentState.getGrid();
		try {
			// get a move from user
			do {
				synchronized (userMove) {
					userMove.wait();
					if (grid.getPiece(userMove.row, userMove.col) == null) {
						move = new Move<TicTacToePiece, GridPosition>(
								this.myPiece, new GridPosition(userMove.row,
										userMove.col));
					} else {
						// invalid move
					}
				}
			} while (move == null);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return move;
	}

	@Override
	public IPiece getPiece() {
		return this.myPiece;
	}

	@Override
	public void notifyStateChange(IState source,
			StateChangeNotification notification) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TicTacToeActivity.this.mGameView.invalidate();
			}
		});
	}
}
