/*
 * Copyright (C) 2016 Adrian Ulrich <adrian@blinkenlights.ch>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package space.rcmd.android.vanillaplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class PlaylistDialog extends DialogFragment
	implements DialogInterface.OnClickListener
{

	// Unicode of checkbox 
	String CHECKBOX_UNCHECKED = "\u2610";
	String CHECKBOX_CHECKED = "\u2611";

	Song mCurrentSong;

	/**
	 * Default constructor as required by Gradle Release Lint
	 */
	public PlaylistDialog() {
	}

	/**
	 * Creates a new playlist dialog to assemble a playlist using an intent.
	 * Uses a static constructor method to satisfy Gradle Release Lint.
	 */
	public static PlaylistDialog newInstance(Callback callback, Intent intent, LibraryAdapter allSource, Song currentSong) {
		PlaylistDialog pd = new PlaylistDialog();
		pd.mCallback = callback;
		pd.mData = pd.new Data();
		pd.mData.sourceIntent = intent;
		pd.mData.allSource = allSource;
		pd.mCurrentSong = currentSong;
		return pd;
	}

	/**
	 * For backward compatible
	 */
	public static PlaylistDialog newInstance(Callback callback, Intent intent, LibraryAdapter allSource) {
		return newInstance( callback, intent, allSource, null);
	}

	/**
	 * A class implementing our callback interface
	 */
	private Callback mCallback;
	/**
	 * The data passed to the callback
	 */
	private PlaylistDialog.Data mData;
	/**
	 * Array of all found playlist names
	 */
	private String[] mItemName;
	/**
	 * Array of all found playlist values
	 */
	private long[] mItemValue;
	/**
	 * Index of 'create playlist' button
	 */
	private final int BUTTON_CREATE_PLAYLIST = 0;
	/**
	 * Our callback interface
	 */
	public interface Callback {
		void updatePlaylistFromPlaylistDialog(PlaylistDialog.Data data);
	}
	/**
	 * Our data structure
	 */
	public class Data {
		public String name;
		public long id;
		public Intent sourceIntent;
		public LibraryAdapter allSource;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context ctx = getActivity();
		Cursor cursor = Playlist.queryPlaylists(ctx);
		if (cursor == null)
			return null;

		int count = cursor.getCount();
		mItemName = new String[1+count];
		mItemValue = new long[1+count];
		String[] displayItem = new String[count+1];

		// Index 0 is always 'New Playlist...'
		mItemName[0] = getResources().getString(R.string.new_playlist);
		mItemValue[0] = -1;
		displayItem[0] = mItemName[0];

		for (int i = 0 ; i < count; i++) {
			cursor.moveToPosition(i);
			mItemValue[1+i] = cursor.getLong(0);
			mItemName[1+i] = cursor.getString(1);
			if(mCurrentSong==null){
				displayItem[1+i] = mItemName[1+i];
			}
			else {
				if (Playlist.isInPlaylist(ctx, mItemValue[1+i], mCurrentSong)) {
					displayItem[1 + i] = CHECKBOX_CHECKED + " " + mItemName[1 + i];
				}
				else {
					displayItem[1 + i] = CHECKBOX_UNCHECKED + " " + mItemName[1 + i];
				}
			}
		}




		// All names are now known: we can show the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.add_to_playlist)
			.setItems(displayItem, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case BUTTON_CREATE_PLAYLIST:
				PlaylistInputDialog newDialog = PlaylistInputDialog.newInstance(new PlaylistInputDialog.Callback() {
					@Override
					public void onSuccess(String input) {
						mData.id = -1;
						mData.name = input;
						mCallback.updatePlaylistFromPlaylistDialog(mData);
					}
				}, "", R.string.create);
				newDialog.show(getFragmentManager(), "PlaylistInputDialog");
				break;
			default:
				mData.id = mItemValue[which];
				mData.name = mItemName[which];
				mCallback.updatePlaylistFromPlaylistDialog(mData);
		}
		dialog.dismiss();
	}
}
