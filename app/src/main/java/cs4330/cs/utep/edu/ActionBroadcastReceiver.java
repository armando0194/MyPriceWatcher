package cs4330.cs.utep.edu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionBroadcastReceiver extends BroadcastReceiver {

    /**
     * When the custom action in the custom chrome tab is clicked,
     * the url of the tab is sent through a action intent
     * @param ctx
     * @param intent
     */
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);

            Intent test = new Intent(shareIntent);
            test.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(test);
        }
    }
}
