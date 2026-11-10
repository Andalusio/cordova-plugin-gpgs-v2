# cordova-plugin-gpgs-v2
This plugin is designed to help coders implement Google Play Games v2 services into Cordova apps. This is ideal for anyone coding with JavaScript with moderate knowledge in Java.

## Installation
Run Command Prompt as Administrator, or equivalent, navigate to your Cordova project, and type the following command to install the plugin:

```console
cordova plugin add cordova-plugin-gpgs-v2 --variable APP_ID="your-app-id"
```

Alternatively, you can download this plugin as a zip file. Once it's downloaded, open the zip file, unload the root folder into your C drive, then type the following in Command Promot to install:

```console
cordova plugin add --link C:\cordova-plugin-gpgs-v2 --variable APP_ID="your-app-id"
```

* your-app-id is your project's "Project ID," which can be found on your Google Play Console. To obtain it there, choose your app, then navigate to the following on the left menu:

"Grow Users" -> "Play Games Services" -> "Setup and Management" -> "Configuration"

# Usage

In order to get the plugin to work, you'll need to initiaize it under "deviceready" first. For example:

```console
document.addEventListener('deviceready', async () => {
    await window.PlayGamesV2.initialize();
    console.log("Logged into Google Play Games v2 automatically!");
}, false)
```

To manually log into Google Play Games, you could type the following function that is to be assigned to a button press event (either via "click" or "mousedown"):

```console
function loginToGPG() {
  try{
      window.PlayGamesV2.signIn();
      console.log("Success: ", result);
      //Type optional follow-up commands below:
  }
  catch (e){
      console.warn("User failed or cancelled sign-in:", e);
      //Type optional follow-up commands below:
  }
}
```
The example function above loginToGPG() may also be called on its own without being assigned to a button press event.


# Achievements
```console
// Unlock an Achievement
window.PlayGamesV2.unlockAchievement("CgkI_ABCDEFG");

// Unlock an Achievement in steps
window.PlayGamesV2.incrementAchievement("CgkI_ABCDEFG", 1);

// Show all Achievements
window.PlayGamesV2.showAchievements();
```

# Leaderboards
```console
// Submit player's score to a particular leaderboard
await window.PlayGamesV2.submitScore("CgkI_12345678", 1000);

// Show a particular leaderboard
window.PlayGamesV2.showLeaderboard("CgkI_12345678");
```
