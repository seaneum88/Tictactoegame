package jkeum.tictactoe.android;

import java.io.Serializable;
import java.util.List;

import jkeum.gameengine.GameEngine;
import jkeum.gameengine.Grid;
import jkeum.gameengine.GridPosition;
import jkeum.gameengine.Move;
import jkeum.gameengine.StateChangeNotification;
import jkeum.gameengine.ai.minimax.MinimaxAI;
import jkeum.gameengine.ai.minimax.MinimaxPruningAI;
import jkeum.gameengine.interfaces.IMove;
import jkeum.gameengine.interfaces.IPlayer;
import jkeum.gameengine.interfaces.IState;
import jkeum.gameengine.interfaces.IStateChangeNotificationHandler;
import jkeum.gameengine.tictactoe.TicTacToeMoveGenerator;
import jkeum.gameengine.tictactoe.TicTacToePiece;
import jkeum.gameengine.tictactoe.TicTacToeState;
import jkeum.gameengine.tictactoe.TicTacToeStateEvaluator;
import jkeum.tictactoe.R;
import jkeum.tictactoe.android.TicTacToeView.IGridClickEventHandler;
import android.app.Activity;
import android.os.Bundle;

public class TicTacToeActivity extends Activity implements IPlayer,
		IGridClickEventHandler, IStateChangeNotificationHandler {

	/** Start player. Must be 1 or 2. Default is 1. */
	public static final String EXTRA_START_PLAYER = TicTacToeActivity.class
			.getName() + "EXTRA_START_PLAYER";

	private TicTacToeView mGameView;

	private GridPosition userMove = new GridPosition(0, 0);

	private GameEngine engine;

	// private TextView mInfoView;

	// private Button mButtonNext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.game_activity);

		mGameView = (TicTacToeView) findViewById(R.id.game_view);
		// mInfoView = (TextView) findViewById(R.id.info_turn);
		// mButtonNext = (Button) findViewById(R.id.next_turn);

		mGameView.setFocusable(true);
		mGameView.setFocusableInTouchMode(true);

		// mButtonNext.setOnClickListener(new MyButtonListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Grid<TicTacToePiece> grid = new Grid<TicTacToePiece>(3, 3);
		TicTacToeState state = new TicTacToeState(grid);
		engine = new GameEngine(state);
		engine.addStateChangeNotificationHandler(this);
		mGameView.setGrid(grid);
		mGameView.setGridClickEventHandler(this);

		MinimaxAI ai = new MinimaxPruningAI(new TicTacToeStateEvaluator(),
				new TicTacToeMoveGenerator());
		ai.setMaxDepth(9);
		Serializable startPlayerPiece = getIntent().getSerializableExtra(
				EXTRA_START_PLAYER);

		state.setPlayerPiece(this, TicTacToePiece.O);
		state.setPlayerPiece(ai, TicTacToePiece.X);

		if (startPlayerPiece == null
				|| ((TicTacToePiece) startPlayerPiece) == TicTacToePiece.O) {
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

	}

	@Override
	public IMove computeNextMove(IState state) {
		Move<TicTacToePiece, GridPosition> move = null;
		TicTacToeState currentState = (TicTacToeState) state;
		Grid<TicTacToePiece> grid = currentState.getGrid();
		try {
			// get a move from user
			do {
				synchronized (userMove) {
					userMove.wait();
					if (grid.getPiece(userMove.row, userMove.col) == null) {
						move = new Move<TicTacToePiece, GridPosition>(
								(TicTacToePiece) currentState
										.getPlayerPiece(this),
								new GridPosition(userMove.row, userMove.col));
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

	private void updateUI() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TicTacToeActivity.this.mGameView.invalidate();
			}
		});
	}

	@Override
	public void notifyStateChange(IState source,
			StateChangeNotification notification) {

		TicTacToeState state = (TicTacToeState) source;
		switch (notification) {
		case MOVE_MADE:
			updateUI();
			break;
		case TERMINATED:
			List<List<GridPosition>> winnings = state.getWinningPositions();
			if (winnings == null) {
				if (state.checkTie()) {
					// we have a tie
				} else {
					throw new RuntimeException(
							"game over but no winner and not tie");
				}
			} else {
				// we have a winner!
				mGameView.setWinningPositions(winnings);
				updateUI();
			}
			break;
		case TURN_CHANGED:
			IPlayer currentPlayer = state.getTurnManager().currentPlayer();
			if (currentPlayer == this) {
				TicTacToePiece piece = (TicTacToePiece) state
						.getPlayerPiece(currentPlayer);
				mGameView.setPreviewPiece(piece);
			} else {
				mGameView.setPreviewPiece(null);
			}
			break;
		}
	}
}
