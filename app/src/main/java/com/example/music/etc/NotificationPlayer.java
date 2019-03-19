package com.example.music.etc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.service.MusicService;
import com.squareup.picasso.Picasso;

public class NotificationPlayer {
    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private MusicService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;

    public NotificationPlayer(MusicService service) {
        mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void updateNotificationPlayer() {
        cancel();
        mNotificationManagerBuilder = new NotificationManagerBuilder();
        mNotificationManagerBuilder.execute();
    }

    public void removeNotificationPlayer() {
        cancel();
        mService.stopForeground(true);
        isForeground = false;
    }

    private void cancel() {
        if (mNotificationManagerBuilder != null) {
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainActivity = new Intent(mService, MainActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            mMainPendingIntent = PendingIntent.getActivity(mService, 0, mainActivity, 0);
            mRemoteViews = createRemoteView(R.layout.notification_player);
            mNotificationBuilder = new NotificationCompat.Builder(mService);
            mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setSmallIcon(R.drawable.ic_music_player)
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);

            Notification notification = mNotificationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendingIntent;
            if (!isForeground) {
                isForeground = true;
                // 서비스를 Foreground 상태로 만든다
                mService.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... params) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private RemoteViews createRemoteView(int layoutId) {
            RemoteViews remoteView = new RemoteViews(mService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSE);
            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);

            remoteView.setOnClickPendingIntent(R.id.btn_noti_play_pause, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.btn_noti_forward, forward);
            remoteView.setOnClickPendingIntent(R.id.btn_noti_rewind, rewind);
            remoteView.setOnClickPendingIntent(R.id.btn_noti_close, close);

            return remoteView;
        }

        private void updateRemoteView(RemoteViews remoteViews, Notification notification) {
            if (mService.isPlaying()) {
                remoteViews.setImageViewResource(R.id.btn_noti_play_pause, R.drawable.ic_pause_button);
            } else {
                remoteViews.setImageViewResource(R.id.btn_noti_play_pause, R.drawable.ic_play);
            }

            String title = mService.getMusicItem().getTitle();
            String artist = mService.getMusicItem().getArtist();
            remoteViews.setTextViewText(R.id.tv_noti_title, title);
            remoteViews.setTextViewText(R.id.tv_noti_artist, artist);
            String cover = mService.getMusicItem().getCover();

            NotificationTarget notificationTarget = new NotificationTarget(
                                mService,
                    R.id.iv_noti_cover,
                    remoteViews,
                                notification,
                                NOTIFICATION_PLAYER_ID);

            Glide
                    .with(mService)
                    .asBitmap()
                    .load(cover)
                    .into(notificationTarget);

            //Picasso.with(mService).load(cover).error(R.drawable.default_album).into(remoteViews, R.id.iv_noti_cover, NOTIFICATION_PLAYER_ID, notification);
        }
    }
}
