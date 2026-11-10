package ng.com.omarkaj.plugins.gpgs;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;

import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;

import java.nio.charset.StandardCharsets;

public class PlayGamesV2Plugin extends CordovaPlugin {

    private static final int RC_UNUSED = 9002;

    @Override
    protected void pluginInitialize() {
        PlayGamesSdk.initialize(this.cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Activity activity = this.cordova.getActivity();

        if ("initialize".equals(action) || "isAuthenticated".equals(action)) {
            this.checkAuthentication(callbackContext);
            return true;
        } else if ("signIn".equals(action)) {
            this.signIn(callbackContext);
            return true;
        } else if ("submitScore".equals(action)) {
            String leaderboardId = args.getString(0);
            long score = args.getLong(1);
            PlayGames.getLeaderboardsClient(activity).submitScore(leaderboardId, score);
            callbackContext.success("Score submitted");
            return true;
        } else if ("showLeaderboard".equals(action)) {
            String lbId = args.getString(0);
            LeaderboardsClient lbClient = PlayGames.getLeaderboardsClient(activity);
            if (lbId == null || lbId.isEmpty()) {
                lbClient.getAllLeaderboardsIntent().addOnSuccessListener(intent -> activity.startActivityForResult(intent, RC_UNUSED));
            } else {
                lbClient.getLeaderboardIntent(lbId).addOnSuccessListener(intent -> activity.startActivityForResult(intent, RC_UNUSED));
            }
            callbackContext.success();
            return true;
        } else if ("unlockAchievement".equals(action)) {
            String achievementId = args.getString(0);
            PlayGames.getAchievementsClient(activity).unlock(achievementId);
            callbackContext.success("Achievement unlocked");
            return true;
        } else if ("incrementAchievement".equals(action)) {
            String achievementId = args.getString(0);
            int steps = args.getInt(1);
            PlayGames.getAchievementsClient(activity).increment(achievementId, steps);
            callbackContext.success("Achievement incremented");
            return true;
        } else if ("showAchievements".equals(action)) {
            PlayGames.getAchievementsClient(activity).getAchievementsIntent()
                    .addOnSuccessListener(intent -> activity.startActivityForResult(intent, RC_UNUSED));
            callbackContext.success();
            return true;
        } else if ("saveGame".equals(action)) {
            String saveName = args.getString(0);
            String data = args.getString(1);
            this.saveCloudGame(saveName, data, callbackContext);
            return true;
        } else if ("loadGame".equals(action)) {
            String saveName = args.getString(0);
            this.loadCloudGame(saveName, callbackContext);
            return true;
        }
        
        return false;
    }

    private void checkAuthentication(CallbackContext callbackContext) {
        GamesSignInClient client = PlayGames.getGamesSignInClient(this.cordova.getActivity());
        client.isAuthenticated().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isAuthenticated()) {
                callbackContext.success("Authenticated");
            } else {
                callbackContext.error("Not authenticated");
            }
        });
    }

    private void signIn(CallbackContext callbackContext) {
        GamesSignInClient client = PlayGames.getGamesSignInClient(this.cordova.getActivity());
        client.signIn().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isAuthenticated()) {
                callbackContext.success("Signed in successfully");
            } else {
                callbackContext.error("Sign-in failed or cancelled");
            }
        });
    }

    private void saveCloudGame(String saveName, String data, CallbackContext callbackContext) {
        SnapshotsClient snapshotsClient = PlayGames.getSnapshotsClient(this.cordova.getActivity());
        snapshotsClient.open(saveName, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED)
                .addOnSuccessListener(result -> {
                    Snapshot snapshot = result.getData();
                    snapshot.getSnapshotContents().writeBytes(data.getBytes(StandardCharsets.UTF_8));
                    
                    SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                            .setDescription("Autosave from Cordova Game")
                            .build();
                    
                    snapshotsClient.commitAndClose(snapshot, metadataChange)
                            .addOnSuccessListener(ritem -> callbackContext.success("Game saved successfully"))
                            .addOnFailureListener(e -> callbackContext.error("Failed to commit save: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callbackContext.error("Failed to open snapshot: " + e.getMessage()));
    }

    private void loadCloudGame(String saveName, CallbackContext callbackContext) {
        SnapshotsClient snapshotsClient = PlayGames.getSnapshotsClient(this.cordova.getActivity());
        snapshotsClient.open(saveName, false, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED)
                .addOnSuccessListener(result -> {
                    Snapshot snapshot = result.getData();
                    try {
                        byte[] rawData = snapshot.getSnapshotContents().readFully();
                        String jsonString = new String(rawData, StandardCharsets.UTF_8);
                        callbackContext.success(jsonString);
                    } catch (Exception e) {
                        callbackContext.error("Failed to read save data: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> callbackContext.error("Failed to load snapshot: " + e.getMessage()));
    }
}
