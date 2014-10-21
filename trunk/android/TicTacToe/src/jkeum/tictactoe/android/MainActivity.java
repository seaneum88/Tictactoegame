package jkeum.tictactoe.android;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import jkeum.gameengine.tictactoe.TicTacToePiece;
import jkeum.tictactoe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		findViewById(R.id.start_player).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						startGame(true);
					}
				});

		findViewById(R.id.start_comp).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startGame(false);
			}
		});
	}

	private void startGame(boolean startWithHuman) {
		Intent i = new Intent(this, TicTacToeActivity.class);
		i.putExtra(TicTacToeActivity.EXTRA_START_PLAYER,
				startWithHuman ? TicTacToePiece.O : TicTacToePiece.X);
		startActivity(i);
	}
}