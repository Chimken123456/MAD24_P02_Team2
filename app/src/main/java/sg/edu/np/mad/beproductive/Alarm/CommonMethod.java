package sg.edu.np.mad.beproductive.Alarm;

import android.content.Context;
import android.media.MediaPlayer;

public class CommonMethod {
    public static MediaPlayer player;
    public static void SoundPlayer(Context ctx, int raw_id)
    {
        player = MediaPlayer.create(ctx,raw_id);
        player.start();
    }

}
