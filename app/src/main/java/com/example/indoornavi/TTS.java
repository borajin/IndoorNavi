package com.example.indoornavi;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TTS implements TextToSpeech.OnInitListener{
    Context mContext;
    TextToSpeech tts;

    public TTS(Context mContext) {
        this.mContext = mContext;

        tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            tts.setPitch(1.0f);//목소리 톤1.0
            tts.setSpeechRate(1.0f);//목소리 속도
        } else {
            ToastMsg("tts 초기화 실패");
        }
    }

    //음성 메세지 출력용
    public void startTTS(String OutMsg) {
        if (OutMsg.length() < 1) return;

        if(!tts.isSpeaking()) {
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void endTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    private void ToastMsg(final String msg) {
        final String message = msg;
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
