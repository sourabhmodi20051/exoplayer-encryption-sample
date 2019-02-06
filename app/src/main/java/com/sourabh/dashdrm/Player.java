package com.sourabh.dashdrm;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.grepsale.shakaplayerdemo.R;

public class Player extends AppCompatActivity {


    private static final String DRM_LICENSE_URL = "https://widevine-proxy.appspot.com/proxy";
    private static final String USER_AGENT = "shakaplayer-demo";
    private static final String DRM_DASH_URL = "http://3.87.145.73/sample.mpd";
    private PlayerView playerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        SimpleExoPlayer player = null;
        try {
            player = getPlayer();
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private SimpleExoPlayer getPlayer() throws  Exception {
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(DRM_LICENSE_URL,
                new DefaultHttpDataSourceFactory(USER_AGENT));
        DefaultDrmSessionManager drmSessionManager = new DefaultDrmSessionManager(C.WIDEVINE_UUID,
                FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), drmCallback, null
                ,new Handler(), null);

        RenderersFactory renderersFactory =  new DefaultRenderersFactory(this, drmSessionManager);

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(renderersFactory, new DefaultTrackSelector(), new DefaultLoadControl());

        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, bandwidthMeter,
               new DefaultHttpDataSourceFactory(USER_AGENT, bandwidthMeter)
        );


        MediaSource mediaSource =
            createDashSource(DRM_DASH_URL, dataSourceFactory);

        player.prepare(mediaSource);

        return player;

    }


    private DashMediaSource createDashSource(String url, DefaultDataSourceFactory dataSourceFactory ) {
        return new DashMediaSource
                .Factory(new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
                .createMediaSource(Uri.parse(url));
    }
}
