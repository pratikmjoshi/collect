/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.odk.collect.android.PromptElement;
import org.odk.collect.android.R;
import org.odk.collect.android.SharedConstants;

import java.io.File;


/**
 * Widget that allows user to record audio clips and add them to the form.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class AudioWidget extends LinearLayout implements IQuestionWidget, IBinaryWidget {

    /*
     * TODO: This works, but needs to get cleaned up a bit TODO: Also need to
     * add features to move files to/from the answer folder, but that'll be in
     * the export in FormHandler. Audio clips are currently in /sdcard/ The
     * value in the answers.xml file is currently content://media/audio/4 or
     * something similar to that which only android understands. It needs to be
     * changed to a path (VideoWidget has an example that does it right).
     */
    private Button mCaptureButton;
    private Button mPlayButton;
    private String mStringAnswer;
    private TextView mDisplayText;
    private String mBinaryPath;


    public AudioWidget(Context context) {
        super(context);
    }


    public void clearAnswer() {
        mStringAnswer = null;
        mPlayButton.setEnabled(false);
        mCaptureButton.setText(getContext().getString(R.string.capture_audio));
        mDisplayText.setText(getContext().getString(R.string.no_capture));
    }


    public IAnswerData getAnswer() {
        if (mStringAnswer != null)
            return new StringData(mStringAnswer);
        else
            return null;
    }


    public void buildView(PromptElement prompt) {
        this.setOrientation(LinearLayout.VERTICAL);

        mCaptureButton = new Button(getContext());
        mCaptureButton.setText(getContext().getString(R.string.capture_audio));
        mCaptureButton.setTextSize(TypedValue.COMPLEX_UNIT_PT, SharedConstants.APPLICATION_FONTSIZE);
        mCaptureButton.setPadding(20, 20, 20, 20);
        mCaptureButton.setEnabled(!prompt.isReadonly());

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(android.provider.MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                ((Activity) getContext()).startActivityForResult(i, SharedConstants.AUDIO_CAPTURE);
            }
        });

        mPlayButton = new Button(getContext());
        mPlayButton.setText(getContext().getString(R.string.play_audio));
        mPlayButton.setTextSize(TypedValue.COMPLEX_UNIT_PT, SharedConstants.APPLICATION_FONTSIZE);
        mPlayButton.setPadding(20, 20, 20, 20);


        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent("android.intent.action.VIEW");
                i.setDataAndType(Uri.fromFile(new File(mStringAnswer)), "audio/3gpp");
                ((Activity) getContext()).startActivity(i);
            }
        });

        mStringAnswer = prompt.getAnswerText();
        mPlayButton.setEnabled(mStringAnswer != null);
        if (mStringAnswer != null) {
            mCaptureButton.setText(getContext().getString(R.string.replace_audio));
        }

        mDisplayText = new TextView(getContext());
        if (mStringAnswer == null) {
            mDisplayText.setText(getContext().getString(R.string.no_capture));
        } else {
            mDisplayText.setText(getContext().getString(R.string.one_capture));
        }

        // finish complex layout
        this.addView(mDisplayText);
        this.addView(mCaptureButton);
        this.addView(mPlayButton);
    }


    public void setBinaryData(Object answer) {
        mStringAnswer = (String) answer;
    }

    public void setBinaryPath(String path) {
        mBinaryPath = path;
    }

}